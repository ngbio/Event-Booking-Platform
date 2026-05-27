package com.group3.service.impl;

import com.group3.dto.request.BookingRequest;
import com.group3.dto.response.BookingResponse;
import com.group3.exceptions.BusinessException;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.Booking;
import com.group3.pojo.Event;
import com.group3.pojo.Payment;
import com.group3.pojo.StatusBooking;
import com.group3.pojo.StatusPay;
import com.group3.pojo.StatusTicket;
import com.group3.pojo.TicketDetail;
import com.group3.pojo.User;
import com.group3.repository.BookingRepository;
import com.group3.repository.EventRepository;
import com.group3.repository.TicketDetailRepository;
import com.group3.service.BookingService;
import com.group3.utils.DTOMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private static final int ROLE_ADMIN = 1;
    private static final int ROLE_ORGANIZER = 2;
    private static final int ROLE_ATTENDEE = 3;
    private static final int USER_ACTIVE = 2;

    private static final int EVENT_PUBLISHED = 2;
    private static final int EVENT_CANCELLED = 5;

    private static final int BOOKING_PENDING = 1;
    private static final int BOOKING_PAID = 2;
    private static final int BOOKING_CANCELLED = 5;

    private static final int PAYMENT_PENDING = 1;
    private static final int TICKET_VALID = 1;

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private TicketDetailRepository ticketDetailRepo;

    @Autowired
    private LocalSessionFactoryBean factory;

    private void validateAttendee(User attendee) {
        if (attendee == null || attendee.getRoleId() == null || attendee.getStatusId() == null) {
            throw new UnauthorizedException("Nguoi dung khong hop le");
        }
        if (attendee.getRoleId().getId() != ROLE_ATTENDEE || attendee.getStatusId().getId() != USER_ACTIVE) {
            throw new UnauthorizedException("Chi attendee ACTIVE moi duoc dat ve");
        }
    }

    private void validateBookableEvent(Event event) {
        if (event == null) {
            throw new ResourceNotFoundException("Khong tim thay su kien");
        }
        if (event.getStatusId() == null || event.getStatusId().getId() != EVENT_PUBLISHED) {
            throw new BusinessException("Su kien chua duoc mo ban");
        }
        if (event.getStatusId().getId() == EVENT_CANCELLED || event.getEndTime().before(new Date())) {
            throw new BusinessException("Su kien da ket thuc hoac da bi huy");
        }
    }

    private void validateQuantity(int quantity, Event event) {
        if (quantity <= 0) {
            throw new BusinessException("So luong ve phai lon hon 0");
        }

        int availableTickets = event.getTotalTickets() - event.getSoldTickets();
        if (quantity > availableTickets) {
            throw new BusinessException("So luong ve vuot qua so ve con lai");
        }
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRoleId() != null && user.getRoleId().getId() == ROLE_ADMIN;
    }

    private boolean isOrganizerOwner(Event event, User user) {
        return user != null
                && user.getRoleId() != null
                && user.getRoleId().getId() == ROLE_ORGANIZER
                && event.getOrganizerId() != null
                && event.getOrganizerId().getId().equals(user.getId());
    }

    private void createTickets(Booking booking, int quantity) {
        StatusTicket validStatus = getStatusTicket(TICKET_VALID);
        Date now = new Date();
        List<TicketDetail> tickets = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            TicketDetail ticket = new TicketDetail();
            ticket.setBookingId(booking);
            ticket.setStatusId(validStatus);
            ticket.setQrCode("E-TKT-" + UUID.randomUUID());
            ticket.setCreatedDate(now);
            ticket.setUpdatedDate(now);
            tickets.add(ticket);
        }
        tickets.forEach(this.ticketDetailRepo::addTicket);
    }

    @Override
    public BookingResponse createBooking(BookingRequest request, User attendee) {
        validateAttendee(attendee);

        Event event = this.eventRepo.getEventById(request.getEventId());
        validateBookableEvent(event);
        validateQuantity(request.getQuantity(), event);

        BigDecimal unitPrice = event.getPrice() != null ? event.getPrice() : BigDecimal.ZERO;
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));
        boolean freeEvent = unitPrice.compareTo(BigDecimal.ZERO) == 0;

        Date now = new Date();
        Booking booking = new Booking();
        booking.setEventId(event);
        booking.setUserId(attendee);
        booking.setQuantity(request.getQuantity());
        booking.setUnitPrice(unitPrice);
        booking.setTotalPrice(totalPrice);
        booking.setCreatedDate(now);
        booking.setUpdatedDate(now);
        // booking.setStatusId(getStatusBooking(freeEvent ? BOOKING_PAID : BOOKING_PENDING));
        booking.setStatusId(getStatusBooking(BOOKING_PAID));
        Booking savedBooking = this.bookingRepo.addBooking(booking);
        // Khi nào lam thanh toán thì mới tăng soldTickets 
            // và tạo payment để tránh trường hợp người dùng không thanh toán mà đã chiếm vé của người khác.
            // Note: For paid booking, we create tickets first to ensure QR codes are generated even 
            // if payment is not completed within 10 minutes.

        // if (freeEvent) {
        //     increaseSoldTickets(event, request.getQuantity());
        //     createTickets(savedBooking, request.getQuantity());
        // } else {
        //     createPendingPayment(savedBooking, attendee, totalPrice, request.getPaymentMethod(), now);
        // }

        increaseSoldTickets(event, request.getQuantity());
        createTickets(savedBooking, request.getQuantity());
        return DTOMapper.toBookingResponse(savedBooking);
    }

    @Override
    public List<BookingResponse> getMyBookings(User attendee, Map<String, String> params) {
        validateAttendee(attendee);
        return DTOMapper.toBookingResponseList(this.bookingRepo.getBookingsByUser(attendee.getId(), params));
    }

    @Override
    public BookingResponse getBookingDetail(Integer bookingId, User currentUser) {
        Booking booking = getRequiredBooking(bookingId);
        if (!canViewBooking(booking, currentUser)) {
            throw new UnauthorizedException("Ban khong co quyen xem booking nay");
        }
        return DTOMapper.toBookingResponse(booking);
    }

    @Override
    public boolean cancelBooking(Integer bookingId, User attendee) {
        validateAttendee(attendee);

        Booking booking = getRequiredBooking(bookingId);
        if (!booking.getUserId().getId().equals(attendee.getId())) {
            throw new UnauthorizedException("Chi chu booking moi duoc huy booking");
        }
        if (booking.getStatusId() == null || booking.getStatusId().getId() != BOOKING_PENDING) {
            throw new BusinessException("Chi booking dang cho thanh toan moi duoc huy");
        }

        booking.setStatusId(getStatusBooking(BOOKING_CANCELLED));
        booking.setUpdatedDate(new Date());
        this.bookingRepo.updateBooking(booking);
        return true;
    }

    @Override
    public List<BookingResponse> getEventBookings(Integer eventId, User organizer, Map<String, String> params) {
        Event event = this.eventRepo.getEventById(eventId);
        if (event == null) {
            throw new ResourceNotFoundException("Khong tim thay su kien");
        }
        if (!isAdmin(organizer) && !isOrganizerOwner(event, organizer)) {
            throw new UnauthorizedException("Ban khong co quyen xem danh sach booking cua su kien nay");
        }

        Map<String, String> paidParams = params != null ? new HashMap<>(params) : new HashMap<>();
        paidParams.put("statusId", String.valueOf(BOOKING_PAID));
        return DTOMapper.toBookingResponseList(this.bookingRepo.getBookingsByEvent(eventId, paidParams));
    }

    private Booking getRequiredBooking(Integer bookingId) {
        Booking booking = this.bookingRepo.getBookingById(bookingId);
        if (booking == null) {
            throw new ResourceNotFoundException("Khong tim thay booking");
        }
        return booking;
    }

    private boolean canViewBooking(Booking booking, User currentUser) {
        if (currentUser == null || currentUser.getRoleId() == null) {
            return false;
        }
        if (isAdmin(currentUser)) {
            return true;
        }
        if (booking.getUserId() != null && booking.getUserId().getId().equals(currentUser.getId())) {
            return true;
        }
        return booking.getEventId() != null && isOrganizerOwner(booking.getEventId(), currentUser);
    }


    private void increaseSoldTickets(Event event, int quantity) {
        event.setSoldTickets(event.getSoldTickets() + quantity);
        event.setUpdatedDate(new Date());
        this.eventRepo.updateEvent(event);
    }

    //hàm này để tạo payment với trạng thái pending, 
    // chờ thanh toán xong mới chuyển booking sang paid và tạo vé
    private void createPendingPayment(Booking booking, User attendee, BigDecimal totalPrice, String paymentMethod, Date now) {
        Payment payment = new Payment();
        payment.setBookingId(booking);
        payment.setUserId(attendee);
        payment.setStatusId(getStatusPay(PAYMENT_PENDING));
        payment.setAmount(totalPrice);
        payment.setMethod(normalizePaymentMethod(paymentMethod));
        payment.setCreatedDate(now);
        payment.setUpdatedDate(now);
        getCurrentSession().persist(payment);
    }

    

    private String normalizePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            return "VNPAY";
        }
        return paymentMethod.trim().toUpperCase();
    }

    private StatusBooking getStatusBooking(int id) {
        return getCurrentSession().get(StatusBooking.class, id);
    }

    private StatusPay getStatusPay(int id) {
        return getCurrentSession().get(StatusPay.class, id);
    }

    private StatusTicket getStatusTicket(int id) {
        return getCurrentSession().get(StatusTicket.class, id);
    }

    private Session getCurrentSession() {
        return this.factory.getObject().getCurrentSession();
    }
}

package com.group3.service.impl;

import com.group3.dto.request.BookingRequest;
import com.group3.dto.response.BookingResponse;
import com.group3.exceptions.BusinessException;
import com.group3.exceptions.ResourceNotFoundException;
import com.group3.exceptions.UnauthorizedException;
import com.group3.pojo.Attendee;
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
import com.group3.repository.UserRepository;
import com.group3.service.BookingService;
import com.group3.service.TicketEmailService;
import com.group3.utils.DTOMapper;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@PropertySource("classpath:configs.properties")
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
    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TicketEmailService ticketEmailService;

    @Autowired
    private Environment env;

    private void validateAttendee(User attendee) {
        if (attendee == null || attendee.getRoleId() == null || attendee.getStatusId() == null) {
            throw new UnauthorizedException("Người dùng không hợp lệ");
        }
        if (attendee.getRoleId().getId() != ROLE_ATTENDEE || attendee.getStatusId().getId() != USER_ACTIVE) {
            throw new UnauthorizedException("Chỉ tài khoản active của attendee mới đặt được vé");
        }
    }

    private void validateBookableEvent(Event event) {
        if (event == null) {
            throw new ResourceNotFoundException("Không tìm thấy sự kiện");
        }
        if (event.getStatusId() == null) {
            throw new BusinessException("Trạng thái sự kiện không hợp lệ");
        }

        int statusId = event.getStatusId().getId();
        Date now = new Date();

        if (statusId == EVENT_CANCELLED || event.getEndTime().before(now)) {
            throw new BusinessException("Sự kiện đã kết thúc hoặc đã bị hủy");
        }
        if (statusId != EVENT_PUBLISHED) {
            throw new BusinessException("Sự kiện chưa được mở bán");
        }
    }

    private void validateQuantity(int quantity, Event event) {
        if (quantity <= 0) {
            throw new BusinessException("Số lượng vé phải lớn hơn 0");
        }

        int availableTickets = event.getTotalTickets() - event.getSoldTickets();
        if (quantity > availableTickets) {
            throw new BusinessException("Số lượng vé vượt qua số vé còn lại");
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
                && event.getOrganizerId().getUserId().equals(user.getId());
    }

    private List<TicketDetail> createTickets(Booking booking, int quantity) {
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
        return tickets;
    }
    
    private User validateAndGetAttendee(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chưa đăng nhập hoặc token hết hạn");
        }
        User user = userRepo.findUserByEmail(principal.getName());
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }
        return user;
    }

    @Override
    public BookingResponse createBooking(BookingRequest request, Principal principal) {
        User attendee = validateAndGetAttendee(principal);
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
        booking.setAttendeeId(getRequiredAttendeeProfile(attendee));
        booking.setQuantity(request.getQuantity());
        booking.setUnitPrice(unitPrice);
        booking.setTotalPrice(totalPrice);
        booking.setCreatedDate(now);
        booking.setUpdatedDate(now);
        booking.setStatusId(getStatusBooking(freeEvent ? BOOKING_PAID : BOOKING_PENDING));
        Booking savedBooking = this.bookingRepo.addBooking(booking);

        if (freeEvent) {
            increaseSoldTickets(event, request.getQuantity());
            List<TicketDetail> tickets = createTickets(savedBooking, request.getQuantity());
            sendTicketsEmail(attendee, savedBooking, event, tickets);
        } else {
            createPendingPayment(savedBooking, totalPrice, now);
            sendPaymentReminderEmail(attendee, savedBooking, event);
        }
        return DTOMapper.toBookingResponse(savedBooking);
    }

    private void sendTicketsEmail(User attendee, Booking booking, Event event, List<TicketDetail> tickets) {
        List<String> qrCodes = tickets.stream()
                .map(TicketDetail::getQrCode)
                .toList();

        Runnable sendEmailTask = () -> this.ticketEmailService.sendTicketsEmail(
                attendee.getEmail(),
                attendee.getFullName(),
                booking.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                booking.getTotalPrice(),
                qrCodes);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendEmailTask.run();
                }
            });
        } else {
            sendEmailTask.run();
        }
    }

    @Override
    public List<BookingResponse> getMyBookings(Principal principal, Map<String, String> params) {
        User attendee = validateAndGetAttendee(principal);
        validateAttendee(attendee);
        return DTOMapper.toBookingResponseList(this.bookingRepo.getBookingsByUserId(attendee.getId(), params));
    }

    @Override
    public BookingResponse getBookingDetail(Integer bookingId, Principal principal) {
        User currentUser = validateAndGetAttendee(principal);
        Booking booking = getRequiredBooking(bookingId);
        if (!canViewBooking(booking, currentUser)) {
            throw new UnauthorizedException("Bạn không có quyền xem chi tiết đơn mua vé này");
        }
        return DTOMapper.toBookingResponse(booking);
    }

    @Override
    public boolean cancelBooking(Integer bookingId, Principal principal) {
        User attendee = validateAndGetAttendee(principal);
        validateAttendee(attendee);

        Booking booking = getRequiredBooking(bookingId);
        if (booking.getAttendeeId() == null
                || booking.getAttendeeId().getUser() == null
                || !booking.getAttendeeId().getUser().getId().equals(attendee.getId())) {
            throw new UnauthorizedException("Chỉ người đặt vé mới được hủy mua vé");
        }
        if (booking.getStatusId() == null || booking.getStatusId().getId() != BOOKING_PENDING) {
            throw new BusinessException("Chỉ đơn đặt vé đang trạng thái chờ thanh toán mới được hủy");
        }

        booking.setStatusId(getStatusBooking(BOOKING_CANCELLED));
        booking.setUpdatedDate(new Date());
        this.bookingRepo.updateBooking(booking);
        return true;
    }

    private Booking getRequiredBooking(Integer bookingId) {
        Booking booking = this.bookingRepo.getBookingById(bookingId);
        if (booking == null) {
            throw new ResourceNotFoundException("Không tìm thấy đơn đặt vé");
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
        if (booking.getAttendeeId() != null
                && booking.getAttendeeId().getUser() != null
                && booking.getAttendeeId().getUser().getId().equals(currentUser.getId())) {
            return true;
        }
        return booking.getEventId() != null && isOrganizerOwner(booking.getEventId(), currentUser);
    }


    private void increaseSoldTickets(Event event, int quantity) {
        event.setSoldTickets(event.getSoldTickets() + quantity);
        event.setUpdatedDate(new Date());
        this.eventRepo.updateEvent(event);
    }

    
    
    private void createPendingPayment(Booking booking, BigDecimal totalPrice, Date now) {
        Payment payment = new Payment();
        payment.setBookingId(booking);
        payment.setStatusId(getStatusPay(PAYMENT_PENDING));
        payment.setAmount(totalPrice);
        payment.setMethod("MOMO");
        payment.setCreatedDate(now);
        payment.setUpdatedDate(now);
        getCurrentSession().persist(payment);
    }

    private StatusBooking getStatusBooking(int id) {
        return getCurrentSession().get(StatusBooking.class, id);
    }

    private Attendee getRequiredAttendeeProfile(User user) {
        Attendee attendee = user != null ? user.getAttendee() : null;
        if (attendee == null && user != null && user.getId() != null) {
            attendee = getCurrentSession().get(Attendee.class, user.getId());
        }
        if (attendee == null) {
            throw new BusinessException("Tài khoản attendee chưa có thông tin profile");
        }
        return attendee;
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

    private void sendPaymentReminderEmail(User attendee, Booking booking, Event event) {
        Date paymentDeadline = new Date(booking.getCreatedDate().getTime() + getPaymentTimeoutMillis());
        Runnable sendEmailTask = () -> this.ticketEmailService.sendPaymentReminderEmail(
                attendee.getEmail(),
                attendee.getFullName(),
                booking.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getStartTime(),
                paymentDeadline,
                booking.getTotalPrice());

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sendEmailTask.run();
                }
            });
        } else {
            sendEmailTask.run();
        }
    }

    private long getPaymentTimeoutMillis() {
        long minutes = Long.parseLong(env.getProperty("booking.payment_timeout_minutes", "5"));
        return minutes * 60_000L;
    }
    
    @Override
    public int updateStatusByEventId(Integer eventId, Integer oldStatusId, Integer newStatusId) {
        return bookingRepo.updateStatusByEventId(eventId, oldStatusId, newStatusId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByEventId(Integer eventId, Map<String, String> params) {
        List<BookingResponse> bookings = DTOMapper.toBookingResponseList(this.bookingRepo.getBookingsByEventId(eventId, params));
        return bookings;
    }
}

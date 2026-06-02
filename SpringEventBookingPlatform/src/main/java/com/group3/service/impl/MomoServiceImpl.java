package com.group3.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.group3.dto.request.MomoCreatePaymentRequest;
import com.group3.dto.response.MomoCreatePaymentResponse;
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
import com.group3.repository.UserRepository;
import com.group3.service.MomoService;
import com.group3.service.TicketEmailService;
import jakarta.persistence.NoResultException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@PropertySource("classpath:configs.properties")
public class MomoServiceImpl implements MomoService {

    private static final int BOOKING_PENDING = 1;
    private static final int BOOKING_PAID = 2;
    private static final int BOOKING_CANCELLED = 5;
    private static final int PAYMENT_PENDING = 1;
    private static final int PAYMENT_SUCCESS = 2;
    private static final int PAYMENT_FAILED = 3;
    private static final int TICKET_VALID = 1;
    private static final String PAYMENT_METHOD = "MOMO";

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private TicketDetailRepository ticketDetailRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TicketEmailService ticketEmailService;

    @Autowired
    private LocalSessionFactoryBean factory;

    @Autowired
    private Environment env;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public MomoCreatePaymentResponse createPayment(MomoCreatePaymentRequest request, Principal principal) {
        User user = getCurrentUser(principal);
        Booking booking = getRequiredBooking(request.getBookingId());

        if (booking.getAttendeeId() == null
                || booking.getAttendeeId().getUser() == null
                || !booking.getAttendeeId().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Bạn không có quyền thanh toán booking này");
        }
        if (booking.getStatusId() == null || booking.getStatusId().getId() != BOOKING_PENDING) {
            throw new BusinessException("Chỉ booking đang chờ thanh toán mới có thể thanh toán qua MoMo");
        }
        if (isBookingExpired(booking)) {
            cancelExpiredBooking(booking, null);
            throw new BusinessException("Booking đã quá hạn thanh toán và đã bị hủy");
        }

        Payment payment = getRetryableMomoPayment(booking);
        long amount = toMomoAmount(payment.getAmount());
        String requestId = "REQ_" + booking.getId() + "_" + System.currentTimeMillis();
        String orderId = buildOrderId(booking.getId(), requestId);
        String orderInfo = "Thanh toan booking #" + booking.getId();
        String extraData = "";
        String requestType = getConfig("momo.requestType", "captureWallet");
        String redirectUrl = getRequiredConfig("momo.redirectUrl");
        String ipnUrl = getRequiredConfig("momo.ipnUrl");

        String rawSignature = "accessKey=" + getRequiredConfig("momo.accessKey")
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + getRequiredConfig("momo.partnerCode")
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        Map<String, Object> momoRequest = new LinkedHashMap<>();
        momoRequest.put("partnerCode", getRequiredConfig("momo.partnerCode"));
        momoRequest.put("partnerName", getConfig("momo.partnerName", "Test"));
        momoRequest.put("storeId", getConfig("momo.storeId", "MomoTestStore"));
        momoRequest.put("requestType", requestType);
        momoRequest.put("ipnUrl", ipnUrl);
        momoRequest.put("redirectUrl", redirectUrl);
        momoRequest.put("orderId", orderId);
        momoRequest.put("amount", String.valueOf(amount));
        momoRequest.put("orderInfo", orderInfo);
        momoRequest.put("requestId", requestId);
        momoRequest.put("extraData", extraData);
        momoRequest.put("lang", getConfig("momo.lang", "vi"));
        momoRequest.put("signature", hmacSha256(rawSignature, getRequiredConfig("momo.secretKey")));

        ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                getRequiredConfig("momo.endpoint"),
                momoRequest,
                JsonNode.class);

        JsonNode body = response.getBody();
        if (body == null) {
            throw new BusinessException("MoMo không trả dữ liệu thanh toán");
        }

        payment.setTransactionId(orderId);
        payment.setUpdatedDate(new Date());
        getCurrentSession().merge(payment);

        MomoCreatePaymentResponse result = new MomoCreatePaymentResponse();
        result.setBookingId(booking.getId());
        result.setOrderId(readText(body, "orderId", orderId));
        result.setRequestId(readText(body, "requestId", requestId));
        result.setAmount(readLong(body, "amount", amount));
        result.setResultCode(readInt(body, "resultCode", -1));
        result.setMessage(readText(body, "message", null));
        result.setPayUrl(readText(body, "payUrl", null));
        result.setDeeplink(readText(body, "deeplink", null));
        result.setQrCodeUrl(readText(body, "qrCodeUrl", null));

        if (result.getResultCode() == null || result.getResultCode() != 0 || result.getPayUrl() == null) {
            throw new BusinessException("Không tạo được giao dịch MoMo: " + result.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Object> handleIpn(Map<String, Object> payload) {
        if (!isValidMomoSignature(payload)) {
            throw new BusinessException("Chữ ký MoMo không hợp lệ");
        }

        String orderId = stringValue(payload.get("orderId"));
        int resultCode = intValue(payload.get("resultCode"), -1);
        Payment payment = getMomoPaymentByOrderId(orderId);
        Booking booking = payment.getBookingId();
        Date now = new Date();

        if (resultCode == 0) {
            long paidAmount = longValue(payload.get("amount"), -1L);
            if (paidAmount != toMomoAmount(payment.getAmount())) {
                throw new BusinessException("Số tiền MoMo không khớp booking");
            }
            completePaidBooking(booking, payment, now);
        } else if (payment.getStatusId() == null || payment.getStatusId().getId() == PAYMENT_PENDING) {
            payment.setStatusId(getStatusPay(PAYMENT_FAILED));
            payment.setUpdatedDate(now);
            getCurrentSession().merge(payment);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", 0);
        response.put("message", "Received");
        return response;
    }

    @Override
    public Map<String, Object> handleRedirect(Map<String, String> params) {
        Map<String, Object> payload = new HashMap<>(params);
        if (!isValidMomoSignature(payload)) {
            throw new BusinessException("Chữ ký redirect MoMo không hợp lệ");
        }

        String orderId = params.get("orderId");
        int resultCode = intValue(params.get("resultCode"), -1);
        Payment payment = getMomoPaymentByOrderId(orderId);
        if (resultCode == 0) {
            long paidAmount = longValue(params.get("amount"), -1L);
            if (paidAmount != toMomoAmount(payment.getAmount())) {
                throw new BusinessException("Số tiền MoMo không khớp booking");
            }
            completePaidBooking(payment.getBookingId(), payment, new Date());
        } else if (payment.getStatusId() == null || payment.getStatusId().getId() == PAYMENT_PENDING) {
            payment.setStatusId(getStatusPay(PAYMENT_FAILED));
            payment.setUpdatedDate(new Date());
            getCurrentSession().merge(payment);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("bookingId", payment.getBookingId().getId());
        response.put("orderId", orderId);
        response.put("resultCode", resultCode);
        response.put("message", params.get("message"));
        return response;
    }

    private void completePaidBooking(Booking booking, Payment payment, Date now) {
        if (payment.getStatusId() != null && payment.getStatusId().getId() == PAYMENT_SUCCESS
                && booking.getStatusId() != null && booking.getStatusId().getId() == BOOKING_PAID) {
            return;
        }
        if (booking.getStatusId() == null || booking.getStatusId().getId() != BOOKING_PENDING) {
            throw new BusinessException("Booking không còn ở trạng thái chờ thanh toán");
        }
        if (isBookingExpired(booking)) {
            cancelExpiredBooking(booking, payment);
            throw new BusinessException("Booking đã quá hạn thanh toán và đã bị hủy");
        }

        Event event = booking.getEventId();
        int availableTickets = event.getTotalTickets() - event.getSoldTickets();
        if (booking.getQuantity() > availableTickets) {
            throw new BusinessException("Sự kiện không còn đủ vé để hoàn tất booking");
        }

        payment.setStatusId(getStatusPay(PAYMENT_SUCCESS));
        payment.setUpdatedDate(now);
        getCurrentSession().merge(payment);

        booking.setStatusId(getStatusBooking(BOOKING_PAID));
        booking.setUpdatedDate(now);
        bookingRepo.updateBooking(booking);

        event.setSoldTickets(event.getSoldTickets() + booking.getQuantity());
        event.setUpdatedDate(now);
        eventRepo.updateEvent(event);

        List<TicketDetail> tickets = ticketDetailRepo.getTicketsByBooking(booking.getId());
        if (tickets == null || tickets.isEmpty()) {
            tickets = createTickets(booking, booking.getQuantity(), now);
            sendTicketsEmail(booking, event, tickets);
        }
    }

    private List<TicketDetail> createTickets(Booking booking, int quantity, Date now) {
        StatusTicket validStatus = getCurrentSession().get(StatusTicket.class, TICKET_VALID);
        List<TicketDetail> tickets = new java.util.ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            TicketDetail ticket = new TicketDetail();
            ticket.setBookingId(booking);
            ticket.setStatusId(validStatus);
            ticket.setQrCode("E-TKT-" + UUID.randomUUID());
            ticket.setCreatedDate(now);
            ticket.setUpdatedDate(now);
            tickets.add(ticketDetailRepo.addTicket(ticket));
        }
        return tickets;
    }

    private void sendTicketsEmail(Booking booking, Event event, List<TicketDetail> tickets) {
        User attendee = booking.getAttendeeId().getUser();
        List<String> qrCodes = tickets.stream().map(TicketDetail::getQrCode).toList();
        Runnable task = () -> ticketEmailService.sendTicketsEmail(
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
                    task.run();
                }
            });
        } else {
            task.run();
        }
    }

    private Payment getRetryableMomoPayment(Booking booking) {
        String hql = "SELECT p FROM Payment p "
                + "JOIN FETCH p.bookingId b "
                + "JOIN FETCH b.attendeeId a "
                + "JOIN FETCH a.user "
                + "JOIN FETCH b.eventId "
                + "JOIN FETCH p.statusId "
                + "WHERE b.id = :bookingId AND p.method = :method "
                + "ORDER BY p.id DESC";
        Query<Payment> query = getCurrentSession().createQuery(hql, Payment.class);
        query.setParameter("bookingId", booking.getId());
        query.setParameter("method", PAYMENT_METHOD);
        query.setMaxResults(1);
        List<Payment> payments = query.getResultList();
        if (payments.isEmpty()) {
            return createRetryPayment(booking);
        }

        Payment payment = payments.get(0);
        if (payment.getStatusId() == null) {
            payment.setStatusId(getStatusPay(PAYMENT_PENDING));
            payment.setUpdatedDate(new Date());
            return (Payment) getCurrentSession().merge(payment);
        }
        if (payment.getStatusId().getId() == PAYMENT_SUCCESS) {
            throw new BusinessException("Payment MoMo đã thanh toán thành công");
        }
        if (payment.getStatusId().getId() == PAYMENT_FAILED) {
            payment.setStatusId(getStatusPay(PAYMENT_PENDING));
            payment.setUpdatedDate(new Date());
            return (Payment) getCurrentSession().merge(payment);
        }
        return payment;
    }

    private Payment createRetryPayment(Booking booking) {
        Date now = new Date();
        Payment payment = new Payment();
        payment.setBookingId(booking);
        payment.setStatusId(getStatusPay(PAYMENT_PENDING));
        payment.setAmount(booking.getTotalPrice());
        payment.setMethod(PAYMENT_METHOD);
        payment.setCreatedDate(now);
        payment.setUpdatedDate(now);
        getCurrentSession().persist(payment);
        return payment;
    }

    private boolean isBookingExpired(Booking booking) {
        Date createdDate = booking.getCreatedDate();
        if (createdDate == null) {
            return false;
        }
        return System.currentTimeMillis() - createdDate.getTime() > getPaymentTimeoutMillis();
    }

    private void cancelExpiredBooking(Booking booking, Payment payment) {
        Date now = new Date();
        booking.setStatusId(getStatusBooking(BOOKING_CANCELLED));
        booking.setUpdatedDate(now);
        bookingRepo.updateBooking(booking);

        if (payment != null) {
            payment.setStatusId(getStatusPay(PAYMENT_FAILED));
            payment.setUpdatedDate(now);
            getCurrentSession().merge(payment);
        } else {
            getCurrentSession().createMutationQuery("UPDATE Payment p SET p.statusId = :failedStatus, p.updatedDate = :now "
                    + "WHERE p.bookingId.id = :bookingId AND p.statusId.id = :pendingStatus")
                    .setParameter("failedStatus", getStatusPay(PAYMENT_FAILED))
                    .setParameter("now", now)
                    .setParameter("bookingId", booking.getId())
                    .setParameter("pendingStatus", PAYMENT_PENDING)
                    .executeUpdate();
        }
    }

    private long getPaymentTimeoutMillis() {
        long minutes = Long.parseLong(env.getProperty("booking.payment_timeout_minutes", "5"));
        return minutes * 60_000L;
    }

    private Payment getMomoPaymentByOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new BusinessException("Thiếu orderId từ MoMo");
        }
        String hql = "SELECT p FROM Payment p "
                + "JOIN FETCH p.bookingId b "
                + "JOIN FETCH b.attendeeId a "
                + "JOIN FETCH a.user "
                + "JOIN FETCH b.eventId "
                + "JOIN FETCH b.statusId "
                + "JOIN FETCH p.statusId "
                + "WHERE p.transactionId = :orderId AND p.method = :method";
        try {
            Query<Payment> query = getCurrentSession().createQuery(hql, Payment.class);
            query.setParameter("orderId", orderId);
            query.setParameter("method", PAYMENT_METHOD);
            return query.getSingleResult();
        } catch (NoResultException ex) {
            throw new ResourceNotFoundException("Không tìm thấy payment MoMo");
        }
    }

    private Booking getRequiredBooking(Integer bookingId) {
        Booking booking = bookingRepo.getBookingById(bookingId);
        if (booking == null) {
            throw new ResourceNotFoundException("Không tìm thấy booking");
        }
        return booking;
    }

    private User getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new UnauthorizedException("Chưa đăng nhập hoặc token hết hạn");
        }
        User user = userRepo.findUserByEmail(principal.getName());
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }
        return user;
    }

    private boolean isValidMomoSignature(Map<String, Object> payload) {
        String signature = stringValue(payload.get("signature"));
        if (signature == null || signature.isBlank()) {
            return false;
        }

        String rawSignature = "accessKey=" + getRequiredConfig("momo.accessKey")
                + "&amount=" + stringValue(payload.get("amount"))
                + "&extraData=" + defaultString(payload.get("extraData"))
                + "&message=" + stringValue(payload.get("message"))
                + "&orderId=" + stringValue(payload.get("orderId"))
                + "&orderInfo=" + stringValue(payload.get("orderInfo"))
                + "&orderType=" + stringValue(payload.get("orderType"))
                + "&partnerCode=" + stringValue(payload.get("partnerCode"))
                + "&payType=" + stringValue(payload.get("payType"))
                + "&requestId=" + stringValue(payload.get("requestId"))
                + "&responseTime=" + stringValue(payload.get("responseTime"))
                + "&resultCode=" + stringValue(payload.get("resultCode"))
                + "&transId=" + stringValue(payload.get("transId"));

        return signature.equalsIgnoreCase(hmacSha256(rawSignature, getRequiredConfig("momo.secretKey")));
    }

    private long toMomoAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Số tiền thanh toán MoMo không hợp lệ");
        }
        return amount.setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private String buildOrderId(Integer bookingId, String requestId) {
        return "MOMO_BOOKING_" + bookingId + "_" + requestId.substring(requestId.lastIndexOf('_') + 1);
    }

    private StatusBooking getStatusBooking(int id) {
        return getCurrentSession().get(StatusBooking.class, id);
    }

    private StatusPay getStatusPay(int id) {
        return getCurrentSession().get(StatusPay.class, id);
    }

    private Session getCurrentSession() {
        return factory.getObject().getCurrentSession();
    }

    private String getRequiredConfig(String key) {
        String value = env.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new BusinessException("Thiếu cấu hình " + key);
        }
        return value.trim();
    }

    private String getConfig(String key, String defaultValue) {
        String value = env.getProperty(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private String hmacSha256(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new BusinessException("Không thể tạo chữ ký MoMo");
        }
    }

    private String readText(JsonNode node, String field, String defaultValue) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? defaultValue : value.asText();
    }

    private Integer readInt(JsonNode node, String field, Integer defaultValue) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? defaultValue : value.asInt();
    }

    private Long readLong(JsonNode node, String field, Long defaultValue) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? defaultValue : value.asLong();
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private String defaultString(Object value) {
        String text = stringValue(value);
        return text == null ? "" : text;
    }

    private int intValue(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private long longValue(Object value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}

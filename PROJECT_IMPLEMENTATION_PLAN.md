# Kế hoạch triển khai hệ thống bán vé sự kiện trực tuyến

Tài liệu này được lập dựa trên đề tài và cấu trúc project hiện tại:

- Backend: Spring MVC, Spring Security, Hibernate, MySQL.
- Admin web: Thymeleaf.
- API cho frontend React: REST JSON dưới prefix `/api`.
- Upload media: Cloudinary đã được cấu hình.
- Auth: JWT đã có cho API user, form login cho admin.

## 1. Hiện trạng project

### 1.1. Đã có nền tảng

- Entity/POJO: `User`, `Role`, `StatusUser`, `Event`, `Category`, `Booking`, `Payment`, `TicketDetail`, `EventFee`, các bảng status.
- Repository đã có: `UserRepository`, `RoleRepository`, `StatusUserRepository`, `StatusEventRepository`, `CategoryRepository`, `EventRepository`, `EventFeeRepository`.
- Service đã có: `UserService`, `RoleService`, `StatusUserService`, `StatusEventService`, `CategoryService`, `EventService`, `EventFeeService`.
- Admin controller đã có:
  - `/admin`: dashboard.
  - `/admin/login`: login admin.
  - `/admin/users`: quản lý user, duyệt/khóa tài khoản.
  - `/admin/categories`: quản lý danh mục.
  - `/admin/events`: quản lý/duyệt/hủy/xóa sự kiện.
- API đã có:
  - `POST /api/users/login`.
  - `POST /api/users/register/attendee`.
  - `POST /api/users/register/organizer`.
  - `GET /api/users/secure/profile`.
  - `GET /api/categories`.

### 1.2. Cần bổ sung hoặc chỉnh lại

- Cần thêm `ApiEventController` vì hiện chưa thấy controller REST riêng cho sự kiện public/organizer.
- Cần thêm repository/service/controller cho `Booking`, `Payment`, `TicketDetail`, thống kê.
- Cần chuẩn hóa security cho các API secure ngoài `/api/users/secure/**`, ví dụ `/api/secure/**` hoặc `/api/organizer/**`.
- Cần kiểm tra lại mapping DB của `TicketDetail`: SQL đang tạo cột `qrCode`, entity dùng `@Column(name = "qr_code")`. Nên thống nhất thành `qr_code` trong SQL hoặc đổi annotation.
- Cần thống nhất status event trong code với seed DB:
  - DB hiện có: `1=PENDING_REVIEW`, `2=PUBLISHED`, `3=DRAFT`, `4=COMPLETED`, `5=CANCELLED`.
  - `EventServiceImpl` comment đang hiểu khác ở vài chỗ. Khi tạo/sửa event phải dùng đúng ID.

## 2. Quy ước vai trò và trạng thái

### 2.1. Role

- `ROLE_ADMIN` (`id=1`): quản trị viên.
- `ROLE_ORGANIZER` (`id=2`): nhà tổ chức.
- `ROLE_ATTENDEE` (`id=3`): khách tham dự.

### 2.2. User status

- `PENDING` (`id=1`): chờ admin duyệt, dùng cho organizer mới đăng ký.
- `ACTIVE` (`id=2`): được phép đăng nhập/sử dụng chức năng.
- `BANNED` (`id=3`): bị khóa.

### 2.3. Event status

- `PENDING_REVIEW` (`id=1`): chờ admin duyệt.
- `PUBLISHED` (`id=2`): đã duyệt, hiển thị công khai, được bán vé.
- `DRAFT` (`id=3`): bản nháp/chưa hoàn tất phí đăng sự kiện.
- `COMPLETED` (`id=4`): sự kiện đã kết thúc.
- `CANCELLED` (`id=5`): đã hủy.

### 2.4. Booking, payment, ticket status

- Booking:
  - `PENDING_PAYMENT` (`id=1`): đã tạo đơn, chờ thanh toán.
  - `PAID` (`id=2`): đã thanh toán/đã xác nhận.
  - `CANCELLED` (`id=3`): đã hủy.
- Payment:
  - `PENDING` (`id=1`), `SUCCESS` (`id=2`), `FAILED` (`id=3`).
- Ticket:
  - `VALID` (`id=1`), `CHECKED_IN` (`id=2`).

## 3. Repository cần làm

Mỗi repository nên có interface trong `com.group3.repository` và implementation trong `com.group3.repository.impl`, theo đúng pattern hiện tại.

### 3.1. `BookingRepository`

Chức năng cần có:

- `Booking addBooking(Booking booking)`: tạo đơn đặt vé.
- `Booking getBookingById(Integer id)`: lấy chi tiết booking, fetch event/user/status/payment/ticket nếu cần.
- `List<Booking> getBookingsByUser(Integer userId, Map<String, String> params)`: lịch sử đặt vé của attendee.
- `List<Booking> getBookingsByEvent(Integer eventId, Map<String, String> params)`: danh sách khách mua vé của một sự kiện cho organizer/admin.
- `List<Booking> getBookingsByOrganizer(Integer organizerId, Map<String, String> params)`: toàn bộ đơn thuộc các sự kiện của organizer.
- `Booking updateBooking(Booking booking)`: cập nhật trạng thái.
- `long countBookingsByUser(Integer userId, Map<String, String> params)`.
- `long countBookingsByEvent(Integer eventId, Map<String, String> params)`.
- `boolean existsPaidBooking(Integer eventId, Integer userId)`: dùng cho quyền chat/đánh dấu quan tâm nếu cần.

Filter nên hỗ trợ:

- `statusId`.
- `eventId`.
- `fromDate`, `toDate`.
- `page`, `size`.

### 3.2. `PaymentRepository`

Chức năng cần có:

- `Payment addPayment(Payment payment)`: ghi nhận thanh toán.
- `Payment getPaymentById(Integer id)`.
- `Payment getPaymentByBookingId(Integer bookingId)`.
- `Payment getPaymentByTransactionId(String transactionId)`: xử lý callback/idempotency.
- `List<Payment> getPaymentsByUser(Integer userId, Map<String, String> params)`.
- `List<Payment> getPaymentsByOrganizer(Integer organizerId, Map<String, String> params)`.
- `List<Payment> getPayments(Map<String, String> params)`: cho admin đối soát.
- `Payment updatePayment(Payment payment)`.
- `BigDecimal sumRevenueByEvent(Integer eventId)`.
- `BigDecimal sumRevenueByOrganizer(Integer organizerId, Date from, Date to)`.
- `BigDecimal sumSystemRevenue(Date from, Date to)`.

Filter nên hỗ trợ:

- `method`: `CASH`, `PAYPAL`, `STRIPE`, `MOMO`, `ZALOPAY`, `VNPAY`, `BANK_TRANSFER`.
- `statusId`.
- `fromDate`, `toDate`.
- `page`, `size`.

### 3.3. `TicketDetailRepository`

Chức năng cần có:

- `TicketDetail addTicket(TicketDetail ticket)`.
- `List<TicketDetail> addTickets(List<TicketDetail> tickets)`: sinh vé sau khi payment success.
- `TicketDetail getTicketByQrCode(String qrCode)`.
- `List<TicketDetail> getTicketsByBooking(Integer bookingId)`.
- `List<TicketDetail> getTicketsByUser(Integer userId, Map<String, String> params)`.
- `TicketDetail updateTicket(TicketDetail ticket)`.
- `boolean checkIn(String qrCode, Integer organizerId)`: đổi `VALID` thành `CHECKED_IN`, chỉ organizer sở hữu event được check-in.

### 3.4. `StatisticRepository`

Có thể tách riêng để không làm phình `PaymentRepository` và `BookingRepository`.

Chức năng cho organizer:

- `long countSoldTicketsByEvent(Integer eventId)`.
- `BigDecimal getRevenueByEvent(Integer eventId)`.
- `List<Object[]> revenueByMonth(Integer organizerId, int year)`.
- `List<Object[]> revenueByQuarter(Integer organizerId, int year)`.
- `List<Object[]> revenueByYear(Integer organizerId)`.
- `List<Object[]> topEventsBySoldTickets(Integer organizerId, int limit)`.

Chức năng cho admin:

- `long countEvents(Map<String, String> params)`.
- `long countUsersByRole(Integer roleId)`.
- `long countBookings(Date from, Date to)`.
- `long countSoldTickets(Date from, Date to)`.
- `BigDecimal totalRevenue(Date from, Date to)`.
- `List<Object[]> systemRevenueByMonth(int year)`.
- `List<Object[]> eventFrequencyByCategory(Date from, Date to)`.

### 3.5. Bổ sung cho `EventRepository`

Hiện đã có `getEvents`, `findByCategory`, `findByParams`, `countEvents`. Nên mở rộng:

- `List<Event> getPublicEvents(Map<String, String> params)`: chỉ lấy `PUBLISHED`, chưa bị hủy.
- `long countPublicEvents(Map<String, String> params)`.
- `List<Event> getEventsByOrganizer(Integer organizerId, Map<String, String> params)`.
- `long countEventsByOrganizer(Integer organizerId, Map<String, String> params)`.
- `List<Event> compareEvents(List<Integer> eventIds)`.
- `boolean isOwner(Integer eventId, Integer organizerId)`.

Filter public cần hỗ trợ đúng đề:

- `kw` hoặc `title`.
- `categoryId`.
- `location`.
- `fromDate`, `toDate`.
- `minPrice`, `maxPrice`.
- `sort`: `date_asc`, `date_desc`, `price_asc`, `price_desc`, `popular_desc`.
- `page`, `size`, giới hạn `size <= 20`.

### 3.6. Bổ sung cho `UserRepository`

Nên thêm:

- `List<User> getPendingOrganizers(Map<String, String> params)`.
- `boolean changeUserStatus(Integer userId, Integer statusId)`: nếu chưa muốn dùng riêng `StatusUserRepository`.
- `boolean isActiveUser(String email)`: chặn login user `PENDING` hoặc `BANNED`.

## 4. Service cần làm

Service nằm trong `com.group3.service`, implementation trong `com.group3.service.impl`.

### 4.1. `BookingService`

Chức năng nghiệp vụ:

- Tạo booking:
  - Kiểm tra user là `ROLE_ATTENDEE` và `ACTIVE`.
  - Kiểm tra event tồn tại, status là `PUBLISHED`.
  - Kiểm tra event chưa kết thúc/chưa hủy.
  - Kiểm tra số lượng vé > 0 và không vượt số vé còn lại.
  - Tính `unitPrice = event.price`.
  - Tính `totalPrice = unitPrice * quantity`.
  - Nếu event miễn phí (`price=0`): tạo booking `PAID`, tạo ticket ngay.
  - Nếu event có phí: tạo booking `PENDING_PAYMENT`, tạo payment `PENDING`.
- Lấy lịch sử booking của attendee.
- Lấy chi tiết booking, chỉ owner/admin/organizer sở hữu event được xem.
- Hủy booking pending:
  - Chỉ cho hủy khi `PENDING_PAYMENT`.
  - Không trừ tồn kho nếu chỉ tăng `soldTickets` sau payment success.
- Lấy danh sách khách đã mua vé theo event cho organizer.

Method đề xuất:

- `BookingResponse createBooking(BookingRequest request, User attendee)`.
- `List<BookingResponse> getMyBookings(User attendee, Map<String, String> params)`.
- `BookingResponse getBookingDetail(Integer bookingId, User currentUser)`.
- `boolean cancelBooking(Integer bookingId, User attendee)`.
- `List<BookingResponse> getEventBookings(Integer eventId, User organizer, Map<String, String> params)`.

### 4.2. `PaymentService`

Chức năng nghiệp vụ:

- Tạo payment record cho booking.
- Xử lý thanh toán tiền mặt tại quầy:
  - Method `CASH`.
  - Có thể để `PENDING` cho admin/organizer xác nhận, hoặc `SUCCESS` nếu xác nhận trực tiếp.
- Tạo URL thanh toán online:
  - `PayPal`, `Stripe`, `MoMo`, `ZaloPay` là phần mở rộng.
  - Trong giai đoạn đầu có thể mock bằng transaction code.
- Xử lý callback:
  - Tìm payment theo transaction id/booking id.
  - Chống xử lý trùng callback.
  - Nếu success: cập nhật `payment=SUCCESS`, `booking=PAID`, tăng `soldTickets`, sinh `TicketDetail`.
  - Nếu failed: cập nhật `payment=FAILED`, booking có thể giữ pending hoặc chuyển cancelled tùy quy định.
- Lưu mọi giao dịch để phục vụ minh bạch tài chính.

Method đề xuất:

- `PaymentResponse createPayment(Integer bookingId, String method, User user)`.
- `PaymentInitResponse initOnlinePayment(Integer bookingId, PaymentMethod method, User user)`.
- `PaymentResponse handlePaymentCallback(PaymentCallbackRequest request)`.
- `PaymentResponse confirmCashPayment(Integer bookingId, User currentUser)`.
- `List<PaymentResponse> getMyPayments(User user, Map<String, String> params)`.
- `List<PaymentResponse> getPaymentsForAdmin(Map<String, String> params)`.

### 4.3. `TicketService`

Chức năng nghiệp vụ:

- Sinh QR code theo format duy nhất, ví dụ `E-TKT-{bookingId}-{UUID}`.
- Tạo đủ số lượng vé theo `booking.quantity` khi booking đã paid.
- Lấy danh sách vé của attendee.
- Check-in vé:
  - QR tồn tại.
  - Vé còn `VALID`.
  - Organizer phải là chủ event.
  - Chuyển status sang `CHECKED_IN`.

Method đề xuất:

- `List<TicketResponse> issueTicketsForBooking(Booking booking)`.
- `List<TicketResponse> getMyTickets(User attendee, Map<String, String> params)`.
- `TicketResponse getTicketByQr(String qrCode, User currentUser)`.
- `TicketResponse checkIn(String qrCode, User organizer)`.

### 4.4. `StatisticService`

Chức năng cho organizer:

- Số vé đã bán theo event.
- Doanh thu theo event.
- Doanh thu theo tháng/quý/năm.
- Tỷ lệ lấp đầy: `soldTickets / totalTickets`.
- Danh sách khách đã mua vé để theo dõi mức độ quan tâm.

Chức năng cho admin:

- Tổng số user theo vai trò.
- Tổng số event theo trạng thái.
- Tổng số booking/payment.
- Tổng doanh thu toàn hệ thống.
- Tần suất bán vé theo ngày/tháng.
- Báo cáo theo category, location, organizer.

Method đề xuất:

- `OrganizerDashboardResponse getOrganizerDashboard(User organizer, Map<String, String> params)`.
- `EventStatisticResponse getEventStats(Integer eventId, User organizer)`.
- `List<RevenuePointResponse> getOrganizerRevenue(User organizer, String groupBy, Integer year)`.
- `AdminDashboardResponse getAdminDashboard(Map<String, String> params)`.
- `List<RevenuePointResponse> getSystemRevenue(String groupBy, Integer year)`.

### 4.5. `EventService` cần hoàn thiện

Hiện đã có tạo/sửa/xóa event nhưng cần thêm nghiệp vụ:

- Chỉ organizer `ACTIVE` mới được tạo event.
- Organizer chỉ được sửa/xóa event của chính mình.
- Không cho sửa event đã `PUBLISHED` nếu đã có booking paid.
- Khi tạo event có phí:
  - Event nên là `DRAFT` đến khi trả listing fee.
  - Sau khi trả listing fee thành công thì chuyển `PENDING_REVIEW`.
- Khi tạo event miễn phí:
  - Chuyển thẳng `PENDING_REVIEW`.
- Admin duyệt event:
  - `PENDING_REVIEW` -> `PUBLISHED`.
- Admin từ chối/hủy event:
  - `PENDING_REVIEW` hoặc `PUBLISHED` -> `CANCELLED`.
- Public API chỉ hiển thị event `PUBLISHED`.

### 4.6. `UserService` cần hoàn thiện

- Login phải chặn user `PENDING` và `BANNED`.
- Organizer đăng ký phải có đủ thông tin xác minh:
  - `identityCard`.
  - `businessLicense` hoặc thông tin tổ chức phù hợp.
  - `organizationName`.
  - `taxCode` nếu có.
- Profile update:
  - Cho user đổi thông tin cá nhân/avatar.
  - Không cho tự đổi role/status.
  - Organizer đổi giấy tờ quan trọng có thể chuyển lại `PENDING`.

### 4.7. `FirebaseChatService` hoặc tài liệu tích hợp chat

Vì Firebase Realtime Database thường do frontend dùng trực tiếp, backend có thể chỉ cần:

- API cấp quyền/lấy chat room:
  - Attendee đã mua vé hoặc quan tâm event mới được chat với organizer.
  - Room id đề xuất: `event_{eventId}_user_{attendeeId}`.
- API trả Firebase config nếu cần.
- Không nhất thiết lưu message vào MySQL nếu Firebase đã lưu.

Method đề xuất:

- `ChatRoomResponse getOrCreateRoom(Integer eventId, User attendee)`.
- `List<ChatRoomResponse> getOrganizerRooms(User organizer)`.

## 5. DTO cần tạo thêm

### 5.1. Request DTO

- `BookingRequest`
  - `eventId`.
  - `quantity`.
  - `paymentMethod`.
- `PaymentRequest`
  - `bookingId`.
  - `method`.
- `PaymentCallbackRequest`
  - `bookingId`.
  - `transactionId`.
  - `provider`.
  - `status`.
  - `amount`.
  - `signature` nếu có tích hợp thật.
- `CheckInRequest`
  - `qrCode`.
- `CompareEventRequest`
  - `eventIds`.
- `StatisticFilterRequest` hoặc dùng `Map<String, String>` giống project hiện tại.

### 5.2. Response DTO

- `TicketResponse`
  - `id`, `bookingId`, `eventId`, `eventTitle`, `qrCode`, `statusId`, `statusName`, `createdDate`.
- `PaymentInitResponse`
  - `paymentId`, `bookingId`, `method`, `amount`, `paymentUrl`, `transactionId`, `status`.
- `PageResponse<T>`
  - `items`, `page`, `size`, `totalItems`, `totalPages`.
- `OrganizerDashboardResponse`.
- `AdminDashboardResponse`.
- `EventStatisticResponse`.
- `RevenuePointResponse`.
- `ChatRoomResponse`.

## 6. API cần làm

Nên dùng format response thống nhất:

```json
{
  "status": 200,
  "message": "Thành công",
  "data": {}
}
```

### 6.1. Auth và user API

Đã có:

| Method | Endpoint | Role | Ghi chú |
| --- | --- | --- | --- |
| `POST` | `/api/users/login` | Public | Đăng nhập, trả JWT |
| `POST` | `/api/users/register/attendee` | Public | Đăng ký khách tham dự |
| `POST` | `/api/users/register/organizer` | Public | Đăng ký organizer, status pending |
| `GET` | `/api/users/secure/profile` | Authenticated | Xem profile |

Cần làm thêm:

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| `PUT` | `/api/users/secure/profile` | Authenticated | Cập nhật profile/avatar |
| `POST` | `/api/users/logout` | Authenticated | Optional, frontend xóa token là đủ |

Lưu ý security:

- Nếu giữ prefix hiện tại thì secure user là `/api/users/secure/**`.
- Với API khác nên thống nhất dùng `/api/secure/**` và mở security matcher tương ứng.

### 6.2. Public event API

Cần tạo `ApiEventController`.

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| `GET` | `/api/events` | Public | Danh sách event published, search/filter/sort/page |
| `GET` | `/api/events/{id}` | Public | Chi tiết event |
| `GET` | `/api/events/{id}/available` | Public | Số vé còn lại |
| `GET` | `/api/events/compare?ids=1,2,3` | Public/Auth | So sánh nhiều sự kiện |

Query cho `/api/events`:

- `kw`: tìm theo tên/mô tả.
- `categoryId`: lĩnh vực.
- `location`: địa điểm.
- `fromDate`, `toDate`: khoảng thời gian.
- `minPrice`, `maxPrice`: khoảng giá.
- `sort`: `date_asc`, `date_desc`, `price_asc`, `price_desc`, `popular_desc`.
- `page`: bắt đầu từ 1.
- `size`: tối đa 20.

### 6.3. Organizer event API

Nên dùng prefix `/api/secure/organizer`.

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| `POST` | `/api/secure/organizer/events` | Organizer | Tạo event, upload image/video |
| `GET` | `/api/secure/organizer/events` | Organizer | Danh sách event của mình |
| `GET` | `/api/secure/organizer/events/{id}` | Organizer | Chi tiết event của mình |
| `PUT` | `/api/secure/organizer/events/{id}` | Organizer | Cập nhật event |
| `DELETE` | `/api/secure/organizer/events/{id}` | Organizer | Xóa/hủy event nếu hợp lệ |
| `GET` | `/api/secure/organizer/events/{id}/bookings` | Organizer | Danh sách khách đã mua/đăng ký |
| `GET` | `/api/secure/organizer/events/{id}/stats` | Organizer | Thống kê event |
| `POST` | `/api/secure/organizer/checkin` | Organizer | Check-in QR vé |

### 6.4. Booking API

Cần tạo `ApiBookingController`.

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| `POST` | `/api/secure/bookings` | Attendee | Tạo đơn đặt vé |
| `GET` | `/api/secure/bookings` | Attendee | Lịch sử booking của bản thân |
| `GET` | `/api/secure/bookings/{id}` | Attendee/Admin/Owner organizer | Chi tiết booking |
| `POST` | `/api/secure/bookings/{id}/cancel` | Attendee | Hủy booking pending |

Luồng tạo booking:

1. Frontend gọi `POST /api/secure/bookings`.
2. Backend kiểm tra vé còn.
3. Backend tạo booking.
4. Nếu free event: trả booking `PAID` và ticket.
5. Nếu paid event: trả booking `PENDING_PAYMENT`, frontend gọi payment API.

### 6.5. Payment API

Cần tạo `ApiPaymentController`.

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| `POST` | `/api/secure/payments` | Attendee | Tạo payment cho booking |
| `POST` | `/api/secure/payments/{bookingId}/cash` | Attendee | Chọn thanh toán tiền mặt |
| `POST` | `/api/secure/payments/{bookingId}/paypal` | Attendee | Tạo giao dịch PayPal |
| `POST` | `/api/secure/payments/{bookingId}/stripe` | Attendee | Tạo giao dịch Stripe |
| `POST` | `/api/secure/payments/{bookingId}/momo` | Attendee | Tạo giao dịch MoMo |
| `POST` | `/api/secure/payments/{bookingId}/zalopay` | Attendee | Tạo giao dịch ZaloPay |
| `GET` | `/api/payment/callback/{provider}` | Public | Provider callback |
| `POST` | `/api/payment/webhook/{provider}` | Public | Provider webhook nếu cần |
| `GET` | `/api/secure/payments` | Attendee | Lịch sử payment của bản thân |

Giai đoạn làm đồ án có thể chia:

- Bản tối thiểu: `CASH` + mock online payment success/fail.
- Bản mở rộng: tích hợp thật PayPal/Stripe/MoMo/ZaloPay.

### 6.6. Ticket API

Cần tạo `ApiTicketController`.

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| `GET` | `/api/secure/tickets` | Attendee | Danh sách vé QR đã mua |
| `GET` | `/api/secure/tickets/{id}` | Attendee | Chi tiết vé |
| `GET` | `/api/secure/tickets/qr/{qrCode}` | Organizer/Admin | Tra cứu vé bằng QR |
| `POST` | `/api/secure/tickets/checkin` | Organizer | Check-in vé |

### 6.7. Statistic API

Cần tạo `ApiStatisticController`.

Organizer:

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| `GET` | `/api/secure/organizer/stats/overview` | Organizer | Tổng quan của organizer |
| `GET` | `/api/secure/organizer/stats/events/{eventId}` | Organizer | Thống kê một event |
| `GET` | `/api/secure/organizer/stats/revenue?groupBy=month&year=2026` | Organizer | Doanh thu tháng/quý/năm |

Admin:

| Method | Endpoint | Role | Chức năng |
| --- | --- | --- | --- |
| `GET` | `/api/secure/admin/stats/overview` | Admin | Tổng quan toàn hệ thống |
| `GET` | `/api/secure/admin/stats/revenue?groupBy=month&year=2026` | Admin | Doanh thu hệ thống |
| `GET` | `/api/secure/admin/stats/categories` | Admin | Event/booking/revenue theo danh mục |
| `GET` | `/api/secure/admin/stats/organizers` | Admin | Hiệu quả theo organizer |

### 6.8. Admin web routes cần hoàn thiện

Hiện admin web đã có user/category/event cơ bản. Cần bổ sung:

| Method | Route | Chức năng |
| --- | --- | --- |
| `GET` | `/admin` | Dashboard có số liệu thật |
| `POST` | `/admin/events/approve` | Chuyển event sang `PUBLISHED` |
| `POST` | `/admin/events/reject` | Chuyển event sang `CANCELLED` hoặc thêm lý do từ chối |
| `GET` | `/admin/payments` | Đối soát giao dịch |
| `GET` | `/admin/bookings` | Tra cứu đơn vé |
| `GET` | `/admin/reports` | Báo cáo hệ thống |
| `GET/POST` | `/admin/settlements` | Đối soát/chuyển tiền cho organizer |

## 7. Các bước triển khai đề xuất

### Giai đoạn 1: Sửa nền tảng và public event

- Sửa mismatch `ticket_detail.qrCode` vs `qr_code`.
- Thống nhất status event trong `EventServiceImpl` và `EventController`.
- Thêm `ApiEventController`.
- Bổ sung filter/sort/pagination public events, giới hạn 20 event/trang.
- Chỉnh security cho public endpoint `/api/events/**`, `/api/categories`.

Kết quả cần đạt:

- Frontend xem/search/filter/sort event công khai được.
- Admin duyệt event đúng trạng thái.

### Giai đoạn 2: Organizer CRUD

- Thêm organizer API tạo/sửa/xóa/list event của chính mình.
- Kiểm tra organizer phải `ACTIVE`.
- Kiểm tra owner khi sửa/xóa.
- Upload image/video qua Cloudinary.
- Tạo listing fee cho event có phí.

Kết quả cần đạt:

- Organizer được admin duyệt mới tạo event.
- Event miễn phí vào `PENDING_REVIEW`.
- Event có phí vào `DRAFT` đến khi thanh toán phí đăng.

### Giai đoạn 3: Booking và ticket

- Tạo `BookingRepository`, `BookingService`, `ApiBookingController`.
- Tạo `TicketDetailRepository`, `TicketService`, `ApiTicketController`.
- Free event: booking xong sinh ticket ngay.
- Paid event: booking pending, chờ payment success.
- Organizer xem danh sách khách đã đăng ký/mua vé.

Kết quả cần đạt:

- Attendee đặt vé được.
- Không bán vượt `totalTickets`.
- Vé QR được sinh đúng số lượng.

### Giai đoạn 4: Payment

- Tạo `PaymentRepository`, `PaymentService`, `ApiPaymentController`.
- Bản tối thiểu: cash/mock online.
- Bản mở rộng: PayPal/Stripe/MoMo/ZaloPay.
- Callback success cập nhật booking/payment/ticket/soldTickets trong cùng transaction.

Kết quả cần đạt:

- Mọi giao dịch được lưu.
- Thanh toán thành công mới sinh vé cho event có phí.
- Có thể xem lịch sử payment.

### Giai đoạn 5: Thống kê và báo cáo

- Tạo `StatisticRepository`, `StatisticService`.
- Organizer dashboard: vé bán, doanh thu theo event/tháng/quý/năm.
- Admin dashboard: tổng event, booking, doanh thu, user, tần suất bán vé.
- Bổ sung admin web dashboard hiển thị dữ liệu thật.

Kết quả cần đạt:

- Đáp ứng phần thống kê trong đề.
- Có API cho React và view cho admin.

### Giai đoạn 6: Chat Firebase

- Tạo API lấy/tạo chat room.
- Frontend dùng Firebase Realtime Database để gửi/nhận message.
- Backend kiểm tra quyền chat dựa trên event/booking/user.

Kết quả cần đạt:

- Attendee và organizer trao đổi theo từng event.
- Organizer có danh sách room cần phản hồi.

## 8. Checklist kiểm thử nghiệp vụ

### Auth/user

- Attendee đăng ký có avatar.
- Organizer đăng ký có avatar và thông tin xác minh.
- Organizer mới đăng ký không tạo được event khi còn `PENDING`.
- User `BANNED` không đăng nhập hoặc không dùng API secure được.
- JWT sai/hết hạn trả 401.

### Event

- Public chỉ thấy event `PUBLISHED`.
- Search theo tên hoạt động.
- Filter theo category/location/date/price hoạt động.
- Sort theo ngày/giá/phổ biến hoạt động.
- Pagination không vượt 20 event/trang.
- Organizer không sửa/xóa event của người khác.
- Không cho sửa event đã có vé bán nếu rule yêu cầu.

### Booking/payment/ticket

- Không đặt được event chưa publish.
- Không đặt được số lượng <= 0.
- Không đặt vượt số vé còn lại.
- Free event tạo booking paid và ticket ngay.
- Paid event tạo booking pending.
- Payment success cập nhật booking paid, payment success, tăng sold tickets, sinh ticket.
- Callback chạy lại không sinh trùng vé.
- Ticket check-in lần 1 thành công, lần 2 báo đã check-in.

### Statistic

- Revenue chỉ tính booking/payment success.
- Doanh thu theo event đúng với tổng payment success.
- Doanh thu theo tháng/quý/năm đúng khoảng thời gian.
- Admin thấy dữ liệu toàn hệ thống, organizer chỉ thấy dữ liệu của mình.

## 9. Thứ tự file nên tạo

Ưu tiên tạo theo thứ tự này để ít bị lỗi dây chuyền:

1. DTO request/response còn thiếu:
   - `BookingRequest`, `PaymentRequest`, `PaymentCallbackRequest`, `CheckInRequest`.
   - `TicketResponse`, `PaymentInitResponse`, `PageResponse`, statistic responses.
2. Repository:
   - `BookingRepository` + impl.
   - `PaymentRepository` + impl.
   - `TicketDetailRepository` + impl.
   - `StatisticRepository` + impl.
3. Service:
   - `TicketService` trước một phần để payment success gọi sinh vé.
   - `BookingService`.
   - `PaymentService`.
   - `StatisticService`.
4. Controller API:
   - `ApiEventController`.
   - `ApiBookingController`.
   - `ApiPaymentController`.
   - `ApiTicketController`.
   - `ApiStatisticController`.
5. Security config:
   - Public: `/api/events/**`, `/api/categories`, payment callback/webhook.
   - Authenticated: `/api/secure/**`.
   - Role-based: organizer/admin endpoints.
6. Admin web:
   - Dashboard data thật.
   - Payment/booking/report/settlement pages nếu còn thời gian.

## 10. Gợi ý phân chia công việc nhóm

### Thành viên 1: Auth, user, admin, category

- Hoàn thiện đăng ký/login/profile.
- Duyệt/khóa user.
- Admin category.
- Security role.

### Thành viên 2: Event và organizer

- Public event API.
- Organizer event CRUD.
- Upload image/video.
- Admin duyệt/hủy event.
- Compare events.

### Thành viên 3: Booking, payment, ticket

- Booking flow.
- Payment mock/online.
- Ticket QR.
- Check-in.

### Thành viên 4: Statistic, report, Firebase chat

- Organizer/admin statistic.
- Dashboard admin.
- Chat room API.
- Firebase frontend integration.

Nếu nhóm chỉ có 2 người, nên chia:

- Người A: Auth/user/admin/category/event.
- Người B: Booking/payment/ticket/statistic/chat.

## 11. Rủi ro cần xử lý sớm

- Mismatch tên cột `qrCode` và `qr_code` có thể làm lỗi Hibernate khi chạy ticket.
- CORS hiện set origin `http://localhost:3000/` có dấu `/` cuối, nên kiểm tra nếu React bị lỗi CORS thì đổi thành `http://localhost:3000`.
- Cloudinary key đang hard-code trong `SpringSecurityConfigs`; nên chuyển sang `configs.properties` hoặc biến môi trường.
- JWT hiện generate token theo email, cần đảm bảo filter set `Principal` đúng và load role để phân quyền.
- Trạng thái event trong code cần thống nhất với DB trước khi làm booking/payment.
- Khi payment success phải dùng transaction để tránh tình trạng payment success nhưng chưa sinh ticket hoặc chưa tăng `soldTickets`.

# **API DOCUMENTATION**

## 1. TÀI KHOẢN & XÁC THỰC (API USERS)
| API Endpoint | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/api/users/login` | **POST** | Public | Đăng nhập bằng Email & Password -> Nhận JWT Token | `Done` |
| `/api/users/register/attendee` | **POST** | Public | Đăng ký người mua vé (Tự gán Role=3, Status=ACTIVE) | `Done` |
| `/api/users/register/organizer` | **POST** | Public | Đăng ký nhà tổ chức (Tự gán Role=2, Status=PENDING) | `Done` |
| `/api/secure/profile` | **GET** | All | Lấy thông tin cá nhân qua `Principal` (Token) | `` |
| `/api/secure/profile` | **PUT** | All | Cập nhật thông tin (Organizer có thêm field CCCD, GPKD...) | `` |

## 2. DỮ LIỆU CÔNG KHAI (API PUBLIC EVENTS & CATEGORIES)
| API Endpoint | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/api/categories` | **GET** | Public | Lấy danh sách danh mục sự kiện | `Done` |
| `/api/events` | **GET** | Public | Lấy danh sách sự kiện (Dùng `@RequestParam` để search) | `Done` |
| `/api/events/{eventId}` | **GET** | Public | Xem chi tiết 1 sự kiện | `Done` |
| `/api/events/{eventId}/available`| **GET** | Public | Check số vé khả dụng (Tính bằng: totalTickets - soldTickets) | `Done` |

## 3. LUỒNG ĐẶT VÉ & THANH TOÁN (API BOOKING & PAYMENT)
| API Endpoint | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/api/secure/bookings` | **POST** | Attendee | Khởi tạo đơn đặt vé mới | `` |
| `/api/secure/bookings` | **GET** | Attendee | Xem lịch sử đơn hàng của bản thân | `` |
| `/api/secure/bookings/{bookingId}`| **GET** | Attendee | Xem chi tiết 1 đơn hàng cụ thể | `` |
| `/api/secure/tickets` | **GET** | Attendee | Lấy danh sách mã QR vé đã thanh toán thành công | `` |
| `/api/secure/payment/vnpay` | **POST** | Attendee | Lấy URL cổng thanh toán VNPay | `` |
| `/api/payment/callback` | **GET** | Public | VNPay gọi lại (IPN) để hệ thống cập nhật trạng thái đơn | `` |

## 4. LUỒNG NHÀ TỔ CHỨC (API ORGANIZER)
| API Endpoint | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/api/secure/organizer/events` | **POST** | Organizer| Đăng sự kiện (Nhận Multipart Ảnh/Video, Tự tính ListingFee) | `Done` |
| `/api/secure/organizer/events` | **GET** | Organizer| Danh sách sự kiện do chính mình tạo | `` |
| `/api/secure/organizer/events/{id}`| **PUT** | Organizer| Sửa thông tin sự kiện (Chỉ khi chưa mở bán/chưa duyệt) | `` |
| `/api/secure/organizer/stats` | **GET** | Organizer| Thống kê doanh thu, số vé đã bán của mình | `` |
| `/api/secure/organizer/checkin`| **POST** | Organizer| Quét mã QR để soát vé khách hàng tham gia | `` |

## 5. QUẢN TRỊ VIÊN (ADMIN WEB - THYMELEAF)
*Lưu ý: Phần này gọi trên Web Browser, trả về View HTML, không trả về JSON.*

| Web Route | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/admin/login` | **GET** | Admin | Giao diện đăng nhập Admin | `` |
| `/admin/` | **GET** | Admin | Giao diện Dashboard (Tổng doanh thu sàn, đăng ký mới...) | `` |
| `/admin/users` | **GET/POST**| Admin | Quản lý & Khóa/Mở tài khoản, Duyệt giấy tờ Organizer | `` |
| `/admin/events` | **GET/POST**| Admin | Xét duyệt sự kiện của Organizer (Publish hoặc Reject) | `` |
| `/admin/categories` | **GET/POST**| Admin | Quản lý danh mục (Thêm/Sửa/Xóa) | `` |
| `/admin/settlements`| **GET/POST**| Admin | Đối soát & Chuyển tiền vé cho Organizer (Sinh settlementCode)| `` |
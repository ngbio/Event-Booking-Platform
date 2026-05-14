# **API DOCUMENTATION - EVENT BOOKING SYSTEM (FULL SCOPE)**

## 1. TÀI KHOẢN & XÁC THỰC (API USERS)
| API Endpoint | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/api/login` | **POST** | Public | Đăng nhập -> Nhận JWT Token | `` |
| `/api/users` | **POST** | Public | Đăng ký (Nhận `MultipartFile avatar` và `Map params`) | `` |
| `/api/secure/profile` | **GET** | All | Lấy thông tin cá nhân qua `Principal` | `` |
| `/api/secure/profile` | **PUT** | All | Cập nhật thông tin cá nhân | `` |

## 2. DỮ LIỆU CÔNG KHAI (API PUBLIC EVENTS & CATEGORIES)
| API Endpoint | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/api/categories` | **GET** | Public | Lấy danh sách danh mục sự kiện | `` |
| `/api/events` | **GET** | Public | Danh sách sự kiện (Dùng `@RequestParam Map params` để search/lọc) | `` |
| `/api/events/{eventId}` | **GET** | Public | Xem chi tiết 1 sự kiện và các loại vé | `` |

## 3. LUỒNG ĐẶT VÉ & THANH TOÁN (API BOOKING & PAYMENT)
| API Endpoint | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/api/secure/pay` | **POST** | Attendee | Khởi tạo đơn đặt vé (Giống `addReceipt` của thầy) | `` |
| `/api/secure/bookings` | **GET** | Attendee | Xem lịch sử đơn hàng của bản thân | `` |
| `/api/secure/bookings/{bookingId}`| **GET** | Attendee | Xem chi tiết 1 đơn hàng cụ thể | `` |
| `/api/secure/tickets` | **GET** | Attendee | Lấy danh sách mã QR vé đã thanh toán thành công | `` |
| `/api/secure/payment/vnpay` | **POST** | Attendee | Lấy URL cổng thanh toán VNPay | `` |
| `/api/payment/callback` | **GET** | Public | VNPay gọi lại (IPN) để hệ thống cập nhật trạng thái | `` |

## 4. LUỒNG NHÀ TỔ CHỨC (API ORGANIZER)
| API Endpoint | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/api/secure/organizer/events` | **POST** | Organizer| Đăng ký sự kiện mới (Kèm file ảnh Banner) | `` |
| `/api/secure/organizer/events` | **GET** | Organizer| Danh sách sự kiện do chính mình tạo | `` |
| `/api/secure/organizer/events/{eventId}`| **PUT** | Organizer| Sửa thông tin sự kiện (khi chưa mở bán) | `` |
| `/api/secure/organizer/stats` | **GET** | Organizer| Thống kê doanh thu, số vé đã bán của mình | `` |
| `/api/secure/organizer/checkin`| **POST** | Organizer| Quét mã QR để soát vé khách hàng | `` |

## 5. QUẢN TRỊ VIÊN (ADMIN WEB - THYMELEAF)
*Lưu ý: Phần này trả về View (`.html`), không trả về JSON.*

| Web Route | Method | Role | Mô tả nghiệp vụ | Trạng thái |
| :--- | :--- | :--- | :--- | :--- |
| `/admin/login` | **GET** | Admin | Giao diện đăng nhập Admin | `` |
| `/admin/` | **GET** | Admin | Giao diện Dashboard (Tổng doanh thu sàn, user mới) | `` |
| `/admin/users` | **GET/POST**| Admin | Giao diện quản lý & Khóa/Mở tài khoản người dùng | `` |
| `/admin/events` | **GET/POST**| Admin | Giao diện xét duyệt sự kiện (Publish hoặc Reject) | `` |
| `/admin/categories` | **GET/POST**| Admin | Giao diện quản lý danh mục (Thêm/Sửa/Xóa) | `` | 
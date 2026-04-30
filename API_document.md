
# **API document demo**

## 1. LUỒNG XÁC THỰC NGƯỜI DÙNG
| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/auth/login` | **POST** | Public | Gửi `{username, password}` -> Nhận về JWT Token. |
| `/auth/register` | **POST** | Public | Đăng ký tài khoản (Khách hàng hoặc Nhà tổ chức). |
| `/users/me` | **GET** | All | Lấy thông tin profile của user đang đăng nhập. |
| `/users/me` | **PUT** | All | Cập nhật thông tin cá nhân (Tên, SĐT, Avatar). |
| `/users/me/password` | **PATCH**| All | Đổi mật khẩu. |

---

## 2. LUỒNG KHÁCH HÀNG

| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/categories` | **GET** | Public | Lấy danh sách danh mục sự kiện |
| `/events` | **GET** | Public | Lấy danh sách sự kiện (`status = PUBLISHED`). Hỗ trợ param: `?cateId=1&kw=abc&page=1` |
| `/events/{id}` | **GET** | Public | Xem chi tiết sự kiện (Mô tả, giá vé, số lượng vé còn). |
| `/bookings` | **POST** | Attendee | Đặt vé. Body: `{eventId, quantity}`. Trả về `bookingId`. |
| `/bookings` | **GET** | Attendee | Xem lịch sử tất cả đơn hàng của chính mình. |
| `/bookings/{id}` | **GET** | Attendee | Xem chi tiết 1 đơn hàng (Tổng tiền, trạng thái thanh toán). |
| `/bookings/{id}/tickets` | **GET** | Attendee | Lấy danh sách vé chi tiết (chứa `qrCode`) của đơn hàng đã thanh toán. |

---

## 3. LUỒNG NHÀ TỔ CHỨC

| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/organizer/events` | **POST** | Organizer| Tạo sự kiện mới (Sẽ có `status = PENDING_REVIEW`). |
| `/organizer/events` | **GET** | Organizer| Lấy danh sách các sự kiện do chính mình tổ chức. |
| `/organizer/events/{id}`| **PUT** | Organizer| Cập nhật thông tin sự kiện (Chỉ khi chưa mở bán). |
| `/organizer/events/{id}`| **DELETE**| Organizer| Hủy sự kiện (Cập nhật `status = CANCELLED`). |
| `/organizer/events/{id}/stats` | **GET** | Organizer| Thống kê: Số vé bán ra, Doanh thu, Tỷ lệ check-in. |
| `/organizer/events/{id}/bookings`| **GET** | Organizer| Danh sách khách hàng đã mua vé của sự kiện này. |
| `/organizer/tickets/checkin` | **POST** | Organizer| Soát vé tại cổng. Body: `{qrCode}`. Đổi `statusticket` thành `CHECKED_IN`. |

---

## 4. LUỒNG QUẢN TRỊ VIÊN

| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/admin/users` | **GET** | Admin | Quản lý người dùng. (Lọc theo `role_id`, `status_id`). |
| `/admin/users/{id}/status` | **PATCH**| Admin | Khóa (BANNED) hoặc Duyệt (ACTIVE) tài khoản. |
| `/admin/events` | **GET** | Admin | Quản lý toàn bộ sự kiện trên hệ thống. |
| `/admin/events/{id}/status`| **PATCH**| Admin | Duyệt sự kiện (Đổi `PENDING_REVIEW` -> `PUBLISHED` hoặc `REJECTED`). |
| `/admin/categories` | **POST** | Admin | Thêm danh mục sự kiện mới. |
| `/admin/categories/{id}` | **PUT** | Admin | Sửa tên/mô tả danh mục. |
| `/admin/stats` | **GET** | Admin | Thống kê tổng quan hệ thống: Tổng doanh thu, Tổng User mới... |

---

## 7. LUỒNG THANH TOÁN
| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/payment/vnpay/create-url` | **POST** | Attendee | Gửi `{bookingId, amount}` -> Nhận về URL chuyển hướng sang VNPay. |
| `/payment/vnpay/callback` | **GET** | Public | VNPay gọi lại API này. Backend cập nhật `payment` thành `SUCCESS` và sinh ra `ticketdetail` (mã QR). |
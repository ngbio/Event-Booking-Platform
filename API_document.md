
## 1. LUỒNG XÁC THỰC & TÀI KHOẢN (PUBLIC)
| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/api/login` | **POST** | Public | Gửi `{username, password}` -> Nhận về JWT Token. |
| `/api/register` | **POST** | Public | Đăng ký tài khoản (Khách hàng hoặc Nhà tổ chức). |

## 2. LUỒNG CÁ NHÂN & BẢO MẬT (SECURE)
| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/api/secure/profile` | **GET** | All | Lấy thông tin profile của user đang đăng nhập. |
| `/api/secure/profile` | **PUT** | All | Cập nhật thông tin cá nhân (Tên, SĐT). |
| `/api/secure/upload-avatar` | **POST** | All | Upload ảnh đại diện cá nhân. |
| `/api/secure/change-password` | **PATCH**| All | Đổi mật khẩu cho user đang đăng nhập. |

## 3. LUỒNG KHÁCH HÀNG (ATTENDEE)
| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/api/categories` | **GET** | Public | Lấy danh sách danh mục sự kiện đang có. |
| `/api/events` | **GET** | Public | Lấy danh sách sự kiện đang mở bán (`status = PUBLISHED`). |
| `/api/events/{id}` | **GET** | Public | Xem chi tiết 1 sự kiện (Giá vé, số lượng còn). |
| `/api/secure/book-ticket` | **POST** | Attendee | Đặt mua vé cho một sự kiện cụ thể. |
| `/api/secure/my-bookings` | **GET** | Attendee | Xem lịch sử toàn bộ đơn hàng của chính mình. |
| `/api/secure/my-bookings/{id}`| **GET** | Attendee | Xem chi tiết 1 đơn hàng (Thông tin vé và QR Code). |

## 4. LUỒNG NHÀ TỔ CHỨC (ORGANIZER)
| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/api/secure/create-event` | **POST** | Organizer| Tạo sự kiện mới (Mặc định ở trạng thái `PENDING_REVIEW`). |
| `/api/secure/my-events` | **GET** | Organizer| Xem danh sách sự kiện do chính mình quản lý. |
| `/api/secure/update-event/{id}` | **PUT** | Organizer| Chỉnh sửa thông tin sự kiện (Chỉ khi chưa mở bán). |
| `/api/secure/delete-event/{id}` | **DELETE**| Organizer| Hủy hoặc xóa sự kiện. |
| `/api/secure/upload-banner` | **POST** | Organizer| Upload ảnh banner minh họa cho sự kiện. |
| `/api/secure/event-stats/{id}` | **GET** | Organizer| Xem thống kê doanh thu và lượng vé bán ra của sự kiện. |
| `/api/secure/checkin` | **POST** | Organizer| Soát vé tại cổng bằng cách quét mã QR. |

## 5. LUỒNG ADMIN
| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/api/secure/admin/users` | **GET** | Admin | Xem danh sách và quản lý toàn bộ người dùng. |
| `/api/secure/admin/lock-user/{id}`| **PATCH**| Admin | Khóa hoặc mở khóa tài khoản người dùng. |
| `/api/secure/admin/events` | **GET** | Admin | Quản lý và duyệt toàn bộ sự kiện trên hệ thống. |
| `/api/secure/admin/approve-event/{id}`| **PATCH**| Admin | Duyệt sự kiện để đưa lên sàn bán vé. |
| `/api/secure/admin/create-category`| **POST** | Admin | Thêm mới danh mục sự kiện (Music, Workshop...). |
| `/api/secure/admin/stats` | **GET** | Admin | Báo cáo tổng doanh thu sàn và lượng truy cập. |

## 6. LUỒNG THANH TOÁN (PAYMENT)
| API Endpoint | Method | Role | Mô tả nghiệp vụ |
| :--- | :--- | :--- | :--- |
| `/api/secure/create-payment-url`| **POST** | Attendee | Gửi đơn hàng -> Nhận link chuyển hướng sang VNPay. |
| `/api/payment-callback` | **GET** | Public | Cổng thanh toán gọi về để cập nhật trạng thái đơn hàng. |
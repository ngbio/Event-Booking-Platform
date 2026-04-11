# BÀI TẬP LỚN: HỆ THỐNG BÁN VÉ SỰ KIỆN TRỰC TUYẾN

Đồ án môn học **Phát triển hệ thống Web** - Giải pháp toàn diện cho việc đăng ký, quản lý và mua vé tham dự các sự kiện trực tuyến, tích hợp thanh toán đa phương thức và tương tác thời gian thực.

## 👥 THÀNH VIÊN THỰC HIỆN
| STT | Họ và Tên | MSSV |
|:---:|:---|:---:|
| 1 | Mai Thanh Hải | 2351010054 |
| 2 | Nguyễn Thanh Thuận | [MSSV] |

## 🚀 CÔNG NGHỆ SỬ DỤNG

### Backend
* **Ngôn ngữ:** Java
* **Framework:** Spring MVC (Spring Framework)
* **Cấu hình:** applicationContext.xml
* **ORM:** Hibernate
* **Security:** Spring Security
* **IDE:** NetBeans

### Frontend
* **Phân hệ Admin:** HTML, CSS, Bootstrap, Thymeleaf
* **Phân hệ Người dùng (Nhà tổ chức & Khách):** ReactJS

### Database & Tools
* **Cơ sở dữ liệu:** MySQL
* **Realtime:** Firebase Realtime Database
* **Lưu trữ API:** Cấu hình chuẩn RESTful
* **Thanh toán:** PayPal, Stripe, MoMo, ZaloPay

## ✨ CHỨC NĂNG CHÍNH

### 1. Phân hệ Admin (Quản trị viên)
* **Kiểm duyệt:** Phê duyệt và xác minh tài khoản của Nhà tổ chức sự kiện.
* **Quản lý tài khoản:** Phân quyền hệ thống, bảo mật thông tin người dùng.
* **Báo cáo & Thống kê:** Xem báo cáo tổng quan về số lượng sự kiện, tần suất bán vé, doanh thu toàn hệ thống. Mở rộng tùy biến báo cáo quản lý.

### 2. Phân hệ Nhà tổ chức (Organizer)
* **Quản lý sự kiện:** Tạo mới sự kiện (tên, mô tả, hình ảnh, video, thời gian, địa điểm, số lượng vé, giá vé). Cập nhật, chỉnh sửa, xóa sự kiện.
* **Quản lý khách hàng:** Theo dõi danh sách khách đã đăng ký mua vé và tình trạng bán vé.
* **Thống kê:** Đánh giá hiệu quả qua số lượng vé bán, doanh thu theo sự kiện/tháng/quý/năm.
* **Tương tác:** Chat thời gian thực với khách tham dự (Firebase).

### 3. Phân hệ Khách tham dự (Attendee)
* **Tìm kiếm & Phân trang:** Tìm sự kiện theo tên, lĩnh vực (âm nhạc, thể thao...), địa điểm, thời gian, mức giá. Hiển thị tối đa 20 sự kiện/trang.
* **Sắp xếp & So sánh:** Sắp xếp theo ngày diễn ra, chi phí, độ phổ biến. So sánh các sự kiện cùng lĩnh vực (nội dung, thời gian, địa điểm, giá vé, nhà tổ chức).
* **Thanh toán:** Đa dạng phương thức (tiền mặt tại quầy, PayPal, Stripe, MoMo, ZaloPay).
* **Tương tác:** Chat trực tiếp với nhà tổ chức (Firebase).

## 🛠 HƯỚNG DẪN CÀI ĐẶT

### Bước 1: Clone dự án
```bash
https://github.com/ngbio/Event-Booking-Platform.git
```

### Bước 2: Cấu hình Cơ sở dữ liệu
* Mở MySQL Workbench, tạo database tên `event_ticket_db`.
* Cấu hình thông số kết nối (username/password) trong `database.properties`.

### Bước 3: Chạy Backend (Spring MVC)
* Mở dự án bằng NetBeans IDE.
* Clean & Build dự án.
* Triển khai (Deploy) lên server Apache Tomcat và Run.

### Bước 4: Chạy Frontend (ReactJS)
```bash
cd frontend
npm install
npm start
```
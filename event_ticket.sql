-- ======================================================
-- PROJECT: EVENT TICKET MANAGEMENT SYSTEM (E-TICKET HUB)
-- DATABASE SCRIPT: EXACT POJO MAPPING VERSION (UPDATED REFUND FLOW)
-- ======================================================

DROP DATABASE IF EXISTS `event_ticket_db`;
CREATE DATABASE `event_ticket_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `event_ticket_db`;

-- ------------------------------------------------------
-- 1. DICTIONARY LOOKUP TABLES (STATIC STATUS & ROLES)
-- ------------------------------------------------------
CREATE TABLE `role` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `status_user` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `status_event` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `status_booking` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `status_pay` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `status_ticket` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

-- ------------------------------------------------------
-- 2. CORE SYSTEM TABLES
-- ------------------------------------------------------
CREATE TABLE `category` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1, 
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `user` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `role_id` int NOT NULL,
  `status_id` int NOT NULL,
  `email` varchar(100) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `phone` varchar(20),
  `avatar` varchar(255),
  `identity_card` varchar(20) DEFAULT NULL, -- CCCD Của cá nhân/người đại diện (Dạng chuỗi số)
  `organization_name` varchar(100) DEFAULT NULL, -- Tên tổ chức/doanh nghiệp
  `tax_code` varchar(20) DEFAULT NULL, -- Mã số thuế
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  FOREIGN KEY (`status_id`) REFERENCES `status_user` (`id`)
);

CREATE TABLE `event` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `organizer_id` int NOT NULL,
  `status_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `image_url` varchar(255),
  `video_url` varchar(255),
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `location` varchar(255) NOT NULL,
  `total_tickets` int NOT NULL DEFAULT 0,
  `price` decimal(10,2) DEFAULT 0.00,
  `sold_tickets` int NOT NULL DEFAULT 0,
  `listing_fee` decimal(10,2) DEFAULT 0.00,
  `is_paid_fee` tinyint(1) DEFAULT 0,
  `settlement_code` varchar(100) DEFAULT NULL, 
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`organizer_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`status_id`) REFERENCES `status_event` (`id`)
);

CREATE TABLE `event_category` (
  `event_id` int NOT NULL,
  `category_id` int NOT NULL,
  PRIMARY KEY (`event_id`, `category_id`),
  FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE
);

CREATE TABLE `booking` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `event_id` int NOT NULL,
  `user_id` int NOT NULL,
  `status_id` int NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `unit_price` decimal(10,2) NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`status_id`) REFERENCES `status_booking` (`id`)
);

CREATE TABLE `payment` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `user_id` int NOT NULL,
  `status_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `method` varchar(50) NOT NULL,
  `transaction_id` varchar(255),
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`status_id`) REFERENCES `status_pay` (`id`)
);

CREATE TABLE `ticket_detail` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `qrCode` varchar(255) NOT NULL UNIQUE,
  `status_id` int NOT NULL,
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_date` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`status_id`) REFERENCES `status_ticket` (`id`)
);

CREATE TABLE `event_fee` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `event_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status_id` int NOT NULL,
  `payment_method` varchar(50),
  `transaction_id` varchar(255),
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`status_id`) REFERENCES `status_pay` (`id`)
);

-- ------------------------------------------------------
-- 3. SEEDING MOCK DATA
-- ------------------------------------------------------

INSERT INTO `role` VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_ORGANIZER'), (3, 'ROLE_ATTENDEE');
INSERT INTO `status_user` VALUES (1, 'PENDING'), (2, 'ACTIVE'), (3, 'BANNED');

INSERT INTO `status_event` VALUES (1, 'PENDING'), (2, 'PUBLISHED'), (3, 'DRAFT'), (4, 'COMPLETED'), (5, 'CANCELLED');

-- ĐÃ CẬP NHẬT: Thêm REFUNDING và REFUNDED, đổi PENDING_PAYMENT thành PENDING
INSERT INTO `status_booking` VALUES (1, 'PENDING'), (2, 'PAID'), (3, 'REFUNDING'), (4, 'REFUNDED'), (5, 'CANCELLED');

INSERT INTO `status_pay` VALUES (1, 'PENDING'), (2, 'SUCCESS'), (3, 'FAILED');
INSERT INTO `status_ticket` VALUES (1, 'VALID'), (2, 'CHECKED_IN');

-- 20 Categories
INSERT INTO `category` (`id`, `name`, `active`) VALUES 
(1, 'Âm nhạc', 1), (2, 'Hội thảo', 1), (3, 'Thể thao', 1), (4, 'Nghệ thuật', 1), (5, 'Du lịch', 1), 
(6, 'Cộng đồng', 1), (7, 'Giáo dục', 1), (8, 'Công nghệ', 1), (9, 'Kinh doanh', 1), (10, 'Giải trí', 1),
(11, 'Ẩm thực', 1), (12, 'Triển lãm', 1), (13, 'Sân khấu', 1), (14, 'Thời trang', 1), (15, 'Điện ảnh', 1),
(16, 'Khoa học', 1), (17, 'Sức khỏe', 1), (18, 'Gia đình', 1), (19, 'Phong cách sống', 1), (20, 'Từ thiện', 1);

-- 20 Users
INSERT INTO `user` (`id`, `role_id`, `status_id`, `email`, `password`, `full_name`, `phone`, `avatar`, `identity_card`, `organization_name`, `tax_code`) VALUES 
(1, 1, 2, 'admin@eventhub.vn', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Quản Trị Viên Hệ Thống', '0901112223', 'admin_avatar.png', NULL, NULL, NULL),
(2, 2, 2, 'contact@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Vibe Entertainment', '0912333444', 'vibe_logo.png', '079090123456', 'Công ty TNHH Giải Trí Vibe', '0314556677'),
(3, 2, 2, 'info@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Saigon Music Group', '0915666777', 'sgm_logo.png', '079091987654', 'Công ty Cổ phần Âm nhạc Sài Gòn', '0319888999'),
(4, 2, 1, 'ceo.techtech@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Phạm Minh Trí', '0982777111', 'tri_avatar.png', '001092334455', 'Công ty Công Nghệ Trẻ TechTech', '0315551234'),
(5, 2, 1, 'marketing@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Glory Event Agency', '0938444555', 'glory_logo.png', '030095667788', 'Hộ Kinh Doanh Sự Kiện Glory', '8811223344'),
(6, 3, 2, 'nguyenvana@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Nguyễn Văn A', '0988000111', 'user1.png', NULL, NULL, NULL),
(7, 3, 2, 'tranventh@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Trần Thị B', '0977222333', 'user2.png', NULL, NULL, NULL),
(8, 3, 2, 'lehoangc@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Lê Hoàng C', '0966333444', 'user3.png', NULL, NULL, NULL),
(9, 3, 2, 'phamvand@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Phạm Văn D', '0955444555', 'user4.png', NULL, NULL, NULL),
(10, 3, 2, 'vuhoange@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Vũ Hoàng E', '0944555666', 'user5.png', NULL, NULL, NULL),
(11, 3, 2, 'doanthif@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Đoàn Thị F', '0933666777', 'user6.png', NULL, NULL, NULL),
(12, 3, 2, 'hoangvangg@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Hoàng Văn G', '0922777888', 'user7.png', NULL, NULL, NULL),
(13, 3, 2, 'buitih@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Bùi Thị H', '0911888999', 'user8.png', NULL, NULL, NULL),
(14, 3, 2, 'nguyenvani@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Nguyễn Văn I', '0912000111', 'user9.png', NULL, NULL, NULL),
(15, 3, 2, 'tranvanj@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Trần Văn J', '0913000222', 'user10.png', NULL, NULL, NULL),
(16, 3, 2, 'lethik@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Lê Thị K', '0914000333', 'user11.png', NULL, NULL, NULL),
(17, 3, 3, 'banneduser@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Tài Khoản Vi Phạm', '0915000444', 'user12.png', NULL, NULL, NULL),
(18, 2, 1, 'organizer.pending1@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Sài Gòn Đêm Lạnh Studio', '0916000555', 'studio_logo.png', '079199223344', 'Công ty Giải trí Đêm Lạnh', '0315559999'),
(19, 2, 1, 'organizer.pending2@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Mekong Event Group', '0917000666', 'mekong_logo.png', '044088112233', 'Công ty TNHH Truyền thông Mekong', '0316668888'),
(20, 2, 2, 'active.org3@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Hà Nội Show và Giải Trí', '0918000777', 'hnshow_logo.png', '001099556677', 'Công ty Cổ phần HN Show', '0317775555');

-- 20 Events
INSERT INTO `event` (`id`, `organizer_id`, `status_id`, `title`, `description`, `start_time`, `end_time`, `location`, `total_tickets`, `price`, `sold_tickets`, `listing_fee`, `is_paid_fee`, `settlement_code`) VALUES 
(1, 2, 1, 'Triển Lãm Tranh Sơn Dầu Việt', 'Trưng bày tác phẩm nghệ thuật.', '2026-10-05 09:00:00', '2026-10-12 18:00:00', 'Bảo tàng Mỹ thuật TP.HCM', 300, 80000.00, 0, 100000.00, 1, NULL),
(2, 2, 1, 'Đại Hội Kpop Dance Cover 2026', 'Sân chơi âm nhạc vũ đạo trẻ.', '2026-07-20 18:00:00', '2026-07-20 22:00:00', 'Nhà thi đấu Phú Thọ', 800, 120000.00, 0, 150000.00, 1, NULL),
(3, 3, 1, 'Workshop Thiết Kế UI/UX Nâng Cao', 'Học thiết kế cùng chuyên gia.', '2026-06-25 08:30:00', '2026-06-25 12:00:00', 'Toà nhà văn phòng TechHub', 50, 450000.00, 0, 50000.00, 1, NULL),
(4, 2, 2, 'Đêm Nhạc Hội Indie 2026', 'Sự kiện âm nhạc quy mô lớn.', '2026-07-15 19:00:00', '2026-07-15 23:00:00', 'Sân vận động Hoa Lư', 1000, 500000.00, 5, 200000.00, 1, NULL),
(5, 2, 2, 'Hội Thảo Khởi Nghiệp Công Nghệ', 'Xu hướng phát triển công nghệ.', '2026-08-10 08:00:00', '2026-08-10 12:00:00', 'Trung tâm Hội nghị SSE', 200, 150000.00, 4, 100000.00, 1, NULL),
(6, 3, 2, 'Saigon Rock Fest 2026', 'Đại nhạc hội Rock bùng nổ sức trẻ.', '2026-09-01 17:00:00', '2026-09-01 22:30:00', 'Nhà thi đấu Nguyễn Du', 1500, 350000.00, 3, 200000.00, 1, NULL),
(7, 3, 2, 'Giải Chạy Marathon Vì Cộng Đồng', 'Chạy bộ 5km và 10km bảo vệ môi trường.', '2026-06-20 05:00:00', '2026-06-20 10:00:00', 'Công viên Vinhomes Central Park', 500, 0.00, 0, 0.00, 1, NULL),
(8, 2, 2, 'Giải Đấu Liên Minh Huyền Tao', 'Tranh cúp vô địch.', '2026-07-22 13:00:00', '2026-07-24 20:00:00', 'Vikings Esports Arena', 100, 50000.00, 0, 50000.00, 1, NULL),
(9, 2, 2, 'Hội Thảo Xu Hướng Học Tiếng Anh', 'Bí quyết đạt TOEIC 625 nhanh chóng.', '2026-06-05 09:00:00', '2026-06-05 11:30:00', 'Đại Học Mở TP.HCM', 120, 0.00, 0, 0.00, 1, NULL),
(10, 3, 2, 'Đêm Hài Độc Thoại Sài Gòn tếu', 'Mang lại tiếng cười cuối tuần.', '2026-07-02 20:00:00', '2026-07-02 22:30:00', 'Sân khấu kịch Q1', 80, 200000.00, 0, 50000.00, 1, NULL),
(11, 2, 2, 'Lễ Hội Ẩm Thực Đường Phố Á Châu', 'Khám phá các món ăn đặc sắc Châu Á.', '2026-07-10 10:00:00', '2026-07-12 22:00:00', 'Phố đi bộ Nguyễn Huệ', 5000, 0.00, 0, 300000.00, 1, NULL),
(12, 3, 2, 'Hội Chợ Sách Quốc Tế Sài Gòn', 'Triển lãm và giao lưu tác giả sách.', '2026-08-01 08:00:00', '2026-08-05 21:00:00', 'SECC Quận 7', 3000, 20000.00, 0, 200000.00, 1, NULL),
(13, 2, 3, 'Workshop Làm Bánh Gối Cổ Truyền', 'Làm bánh gối chuẩn vị.', '2026-06-15 14:00:00', '2026-06-15 17:00:00', 'Bếp Bánh Ngon Q3', 30, 250000.00, 0, 50000.00, 0, NULL),
(14, 2, 3, 'Đêm Kịch Nói: Lan Và Điệp 2026', 'Vở kịch cải lương cổ truyền tái hiện.', '2026-08-15 19:30:00', '2026-08-15 22:00:00', 'Nhà hát Thành Phố', 400, 300000.00, 0, 100000.00, 0, NULL),
(15, 3, 3, 'Triển Lãm High-Tech & Giới Thiệu AI', 'Giới thiệu robot và AI thế hệ mới.', '2026-09-10 09:00:00', '2026-09-12 17:00:00', 'Trung tâm Triển lãm Q. Tân Bình', 1000, 50000.00, 0, 150000.00, 0, NULL),
(16, 3, 4, 'Live Concert Thanh Âm Hoàng Hôn', 'Đêm nhạc acoustic đã kết thúc.', '2026-04-01 18:00:00', '2026-04-01 21:00:00', 'Đà Lạt Mộng Mơ', 150, 450000.00, 150, 150000.00, 1, 'FT260401888'),
(17, 2, 4, 'Hội Thảo Digital Marketing Quốc Tế', 'Cập nhật xu hướng SEO và Ads 2026.', '2026-05-02 08:00:00', '2026-05-02 17:00:00', 'Khách sạn Caravelle Sài Gòn', 200, 1200000.00, 200, 500000.00, 1, 'ST260502111'),
(18, 3, 5, 'Hội Chợ Sách Cũ Sài Gòn', 'Sự kiện bị hủy bỏ.', '2026-05-10 08:00:00', '2026-05-12 21:00:00', 'Nhà văn hóa Thanh Niên', 2000, 0.00, 0, 0.00, 1, NULL),
(19, 2, 5, 'Giải Marathon Quốc Tế Đà Nẵng', 'Sự kiện bị hủy do thời tiết bất lợi.', '2026-05-14 04:00:00', '2026-05-14 11:00:00', 'Công viên Biển Đông Đà Nẵng', 3000, 600000.00, 0, 400000.00, 1, NULL),
(20, 2, 2, 'Đêm Nhạc Jazz Độc Bản Tháng 7', 'Thưởng thức nhạc Jazz nguyên bản tinh tế.', '2026-07-05 20:00:00', '2026-07-05 23:00:00', 'Nhà hát lớn Hà Nội', 250, 600000.00, 0, 250000.00, 1, NULL);

-- 20 Event Categories
INSERT INTO `event_category` (`event_id`, `category_id`) VALUES 
(1, 4), (1, 12), (2, 1), (2, 10), (3, 7), (3, 8), (4, 1), (4, 10), (5, 2), (5, 8), 
(5, 9), (6, 1), (7, 3), (7, 6), (8, 3), (8, 10), (9, 7), (10, 10), (10, 13), (11, 11),
(12, 7), (13, 11), (14, 13), (15, 8), (15, 16), (16, 1), (17, 2), (17, 9), (18, 6), (20, 1);

-- 20 Bookings 
-- ĐÃ CẬP NHẬT MOCK DATA: Chuyển các booking có ID trạng thái cũ là 3 (CANCELLED) sang 5 (CANCELLED) để khớp với bảng status_booking mới.
INSERT INTO `booking` (`id`, `event_id`, `user_id`, `status_id`, `quantity`, `unit_price`, `total_price`) VALUES 
(1, 4, 6, 2, 2, 500000.00, 1000000.00),
(2, 4, 7, 2, 2, 500000.00, 1000000.00),
(3, 4, 8, 1, 1, 500000.00, 500000.00),
(4, 5, 9, 2, 2, 150000.00, 300000.00),
(5, 5, 10, 2, 2, 150000.00, 300000.00),
(6, 6, 11, 2, 3, 350000.00, 1050000.00),
(7, 4, 9, 5, 2, 500000.00, 1000000.00), -- CANCELLED
(8, 5, 6, 1, 1, 150000.00, 150000.00),
(9, 6, 7, 5, 1, 350000.00, 350000.00), -- CANCELLED
(10, 12, 8, 1, 2, 20000.00, 40000.00),
(11, 8, 12, 1, 1, 50000.00, 50000.00),
(12, 10, 13, 1, 2, 200000.00, 400000.00),
(13, 4, 14, 2, 1, 500000.00, 500000.00),
(14, 5, 15, 2, 2, 150000.00, 300000.00),
(15, 6, 16, 2, 1, 350000.00, 350000.00),
(16, 12, 6, 2, 5, 20000.00, 100000.00),
(17, 14, 7, 1, 2, 300000.00, 600000.00),
(18, 15, 8, 1, 1, 50000.00, 50000.00),
(19, 16, 9, 2, 2, 450000.00, 900000.00),
(20, 17, 10, 2, 1, 1200000.00, 1200000.00);

-- 20 Payments
INSERT INTO `payment` (`id`, `booking_id`, `user_id`, `status_id`, `amount`, `method`, `transaction_id`) VALUES 
(1, 1, 6, 2, 1000000.00, 'VNPAY', 'VNP20260701999'),
(2, 2, 7, 2, 1000000.00, 'MOMO', 'MOMO_MM112233'),
(3, 3, 8, 1, 500000.00, 'VNPAY', NULL),
(4, 4, 9, 2, 300000.00, 'BANK_TRANSFER', 'FT260810AAA'),
(5, 5, 10, 2, 300000.00, 'MOMO', 'MOMO_MM445566'),
(6, 6, 11, 2, 1050000.00, 'VNPAY', 'VNP20260901888'),
(7, 7, 3, 1, 1000000.00, 'VNPAY', NULL),
(8, 8, 6, 1, 150000.00, 'MOMO', NULL),
(9, 10, 8, 1, 40000.00, 'VNPAY', NULL),
(10, 12, 13, 1, 400000.00, 'MOMO', NULL),
(11, 13, 14, 2, 500000.00, 'VNPAY', 'VNP20260715111'),
(12, 14, 15, 2, 300000.00, 'MOMO', 'MOMO_MM778899'),
(13, 15, 16, 2, 350000.00, 'BANK_TRANSFER', 'FT260901BBB'),
(14, 16, 6, 2, 100000.00, 'VNPAY', 'VNP20260801222'),
(15, 17, 7, 1, 600000.00, 'MOMO', NULL),
(16, 18, 8, 3, 50000.00, 'VNPAY', NULL),
(17, 19, 9, 2, 900000.00, 'BANK_TRANSFER', 'FT260401CCC'),
(18, 20, 10, 2, 1200000.00, 'VNPAY', 'VNP20260502333'),
(19, 11, 12, 1, 50000.00, 'MOMO', NULL),
(20, 12, 13, 3, 400000.00, 'VNPAY', NULL);

-- 20 Ticket Details
INSERT INTO `ticket_detail` (`id`, `booking_id`, `qrCode`, `status_id`) VALUES 
(1, 1, 'E-TKT-UUID-A1B2-C3D4-0001', 1),
(2, 1, 'E-TKT-UUID-A1B2-C3D4-0002', 2),
(3, 2, 'E-TKT-UUID-E5F6-G7H8-0003', 1),
(4, 2, 'E-TKT-UUID-E5F6-G7H8-0004', 1),
(5, 4, 'E-TKT-UUID-I9J0-K1L2-0005', 1),
(6, 4, 'E-TKT-UUID-I9J0-K1L2-0006', 1),
(7, 5, 'E-TKT-UUID-M3N4-O5P6-0007', 2),
(8, 5, 'E-TKT-UUID-M3N4-O5P6-0008', 1),
(9, 6, 'E-TKT-UUID-Q7R8-S9T0-0009', 1),
(10, 6, 'E-TKT-UUID-Q7R8-S9T0-0010', 1),
(11, 6, 'E-TKT-UUID-Q7R8-S9T0-0011', 1),
(12, 13, 'E-TKT-UUID-U1V2-W3X4-0012', 1),
(13, 14, 'E-TKT-UUID-Y5Z6-A7B8-0013', 1),
(14, 14, 'E-TKT-UUID-Y5Z6-A7B8-0014', 2),
(15, 15, 'E-TKT-UUID-C9D0-E1F2-0015', 1),
(16, 16, 'E-TKT-UUID-G3H4-I5J6-0016', 1),
(17, 16, 'E-TKT-UUID-G3H4-I5J6-0017', 1),
(18, 19, 'E-TKT-UUID-K7L8-M9N0-0018', 1),
(19, 19, 'E-TKT-UUID-K7L8-M9N0-0019', 2),
(20, 20, 'E-TKT-UUID-O1P2-Q3R4-0020', 1);

-- 20 Event Fees
INSERT INTO `event_fee` (`id`, `event_id`, `amount`, `status_id`, `payment_method`, `transaction_id`) VALUES 
(1, 4, 200000.00, 2, 'VNPAY', 'FEE_VNP111'),
(2, 5, 100000.00, 2, 'MOMO', 'FEE_MOMO222'),
(3, 6, 200000.00, 2, 'BANK_TRANSFER', 'FEE_BANK333'),
(4, 1, 100000.00, 2, 'VNPAY', 'FEE_VNP444'),
(5, 13, 50000.00, 1, NULL, NULL),
(6, 16, 150000.00, 2, 'MOMO', 'FEE_MOMO555'),
(7, 8, 50000.00, 2, 'VNPAY', 'FEE_VNP666'),
(8, 10, 50000.00, 2, 'MOMO', 'FEE_MOMO755'),
(9, 2, 150000.00, 2, 'BANK_TRANSFER', 'FEE_BANK444'),
(10, 3, 50000.00, 2, 'VNPAY', 'FEE_VNP888'),
(11, 11, 300000.00, 2, 'MOMO', 'FEE_MOMO999'),
(12, 12, 200000.00, 2, 'VNPAY', 'FEE_VNP123'),
(13, 14, 100000.00, 1, NULL, NULL),
(14, 15, 150000.00, 1, NULL, NULL),
(15, 17, 500000.00, 2, 'BANK_TRANSFER', 'FEE_BANK555'),
(16, 20, 250000.00, 2, 'VNPAY', 'FEE_VNP777'),
(17, 7, 0.00, 2, 'SYSTEM', 'FREE_FEE_777'),
(18, 9, 0.00, 2, 'SYSTEM', 'FREE_FEE_999'),
(19, 18, 0.00, 2, 'SYSTEM', 'FREE_FEE_188'),
(20, 19, 400000.00, 3, 'VNPAY', NULL);
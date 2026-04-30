-- ======================================================
-- PROJECT: EVENT TICKET MANAGEMENT SYSTEM
-- DATA: MASSIVE DUMMY DATA FOR TESTING
-- ======================================================

DROP DATABASE IF EXISTS `event_ticket_db`;
CREATE DATABASE `event_ticket_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `event_ticket_db`;

-- ------------------------------------------------------
-- 1. TẠO BẢNG CẤU TRÚC
-- ------------------------------------------------------
CREATE TABLE `role` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `statususer` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `statusevent` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `statusbooking` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `statuspay` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `statusticket` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );

CREATE TABLE `category` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `active` tinyint(1) DEFAULT 1,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `user` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `role_id` int NOT NULL,
  `status_id` int NOT NULL,
  `username` varchar(50) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL UNIQUE,
  `fullName` varchar(100) NOT NULL,
  `phone` varchar(20),
  `avatar` varchar(255),
  `active` tinyint(1) DEFAULT 1,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
  FOREIGN KEY (`status_id`) REFERENCES `statususer` (`id`)
);

CREATE TABLE `event` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `organizer_id` int NOT NULL,
  `status_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `imageUrl` varchar(255),
  `videoUrl` varchar(255),
  `startTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `location` varchar(255) NOT NULL,
  `totalTickets` int NOT NULL DEFAULT 0,
  `price` decimal(10,2) DEFAULT 0.00,
  `active` tinyint(1) DEFAULT 1,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`organizer_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`status_id`) REFERENCES `statusevent` (`id`)
);

CREATE TABLE `eventcategory` (
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
  `unitPrice` decimal(10,2) NOT NULL,
  `totalPrice` decimal(10,2) NOT NULL,
  `active` tinyint(1) DEFAULT 1,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`status_id`) REFERENCES `statusbooking` (`id`)
);

CREATE TABLE `payment` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `user_id` int NOT NULL,
  `status_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `method` varchar(50) NOT NULL,
  `transaction_id` varchar(255),
  `active` tinyint(1) DEFAULT 1,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`status_id`) REFERENCES `statuspay` (`id`)
);

CREATE TABLE `ticketdetail` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `qrCode` varchar(255) NOT NULL UNIQUE,
  `status_id` int NOT NULL,
  `active` tinyint(1) DEFAULT 1,
  `createdAt` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedAt` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`status_id`) REFERENCES `statusticket` (`id`)
);

-- ------------------------------------------------------
-- 2. DỮ LIỆU TỪ ĐIỂN (DICTIONARY DATA)
-- ------------------------------------------------------
INSERT INTO `role` VALUES (1, 'ADMIN'), (2, 'ORGANIZER'), (3, 'ATTENDEE');
INSERT INTO `statususer` VALUES (1, 'PENDING'), (2, 'ACTIVE'), (3, 'BANNED');
INSERT INTO `statusevent` VALUES (1, 'DRAFT'), (2, 'PENDING_REVIEW'), (3, 'PUBLISHED'), (4, 'CANCELLED'), (5, 'COMPLETED');
INSERT INTO `statusbooking` VALUES (1, 'PENDING_PAYMENT'), (2, 'PAID'), (3, 'CANCELLED');
INSERT INTO `statuspay` VALUES (1, 'PENDING'), (2, 'SUCCESS'), (3, 'FAILED');
INSERT INTO `statusticket` VALUES (1, 'VALID'), (2, 'CHECKED_IN');

INSERT INTO `category` (`name`) VALUES 
('Âm nhạc'), ('Hội thảo'), ('Thể thao'), ('Nghệ thuật'), ('Du lịch'), 
('Cộng đồng'), ('Giáo dục'), ('Công nghệ'), ('Kinh doanh'), ('Giải trí');

-- ------------------------------------------------------
-- 3. DỮ LIỆU NGƯỜI DÙNG (20 Users: 1 Admin, 4 Orgs, 15 Attendees)
-- ------------------------------------------------------
INSERT INTO `user` (`role_id`, `status_id`, `username`, `password`, `email`, `fullName`, `phone`) VALUES 
(1, 2, 'admin', '123456', 'admin@event.vn', 'Hệ thống Quản trị', '0900000000'),
(2, 2, 'org_vibe', '123456', 'contact@vibemusic.vn', 'Vibe Music Entertainment', '0911000111'),
(2, 2, 'org_tech', '123456', 'hello@techhub.vn', 'TechHub Vietnam', '0922000222'),
(2, 2, 'org_run', '123456', 'info@vnmarathon.com', 'Vietnam Marathon Club', '0933000333'),
(2, 1, 'org_art', '123456', 'art@gallery.vn', 'SaiGon Art Gallery', '0944000444'), -- Đang chờ duyệt
(3, 2, 'user01', '123456', 'nguyenvana@gmail.com', 'Nguyễn Văn An', '0955111222'),
(3, 2, 'user02', '123456', 'tranthib@gmail.com', 'Trần Thị Bình', '0955222333'),
(3, 2, 'user03', '123456', 'leminhc@gmail.com', 'Lê Minh Cường', '0955333444'),
(3, 2, 'user04', '123456', 'phamthid@gmail.com', 'Phạm Thị Dung', '0955444555'),
(3, 2, 'user05', '123456', 'hoangvane@gmail.com', 'Hoàng Văn Em', '0955555666'),
(3, 2, 'user06', '123456', 'vothif@gmail.com', 'Võ Thị Phương', '0955666777'),
(3, 2, 'user07', '123456', 'dangvang@gmail.com', 'Đặng Văn Giang', '0955777888'),
(3, 2, 'user08', '123456', 'buithih@gmail.com', 'Bùi Thị Hoa', '0955888999'),
(3, 3, 'user09', '123456', 'spammer@gmail.com', 'Kẻ Gian Lận', '0955999000'), -- Banned User
(3, 2, 'user10', '123456', 'lyvank@gmail.com', 'Lý Văn Khoa', '0966111222'),
(3, 2, 'user11', '123456', 'daothil@gmail.com', 'Đào Thị Lan', '0966222333'),
(3, 2, 'user12', '123456', 'mai_thanh_hai', 'Mai Thanh Hải', '0966333444'),
(3, 2, 'user13', '123456', 'ngo_van_n@gmail.com', 'Ngô Văn Nam', '0966444555'),
(3, 2, 'user14', '123456', 'chu_thi_o@gmail.com', 'Chu Thị Oanh', '0966555666'),
(3, 2, 'user15', '123456', 'phan_van_p@gmail.com', 'Phan Văn Phú', '0966666777');

-- ------------------------------------------------------
-- 4. DỮ LIỆU SỰ KIỆN (15 Sự kiện đa dạng)
-- ------------------------------------------------------
INSERT INTO `event` (`organizer_id`, `status_id`, `title`, `description`, `startTime`, `endTime`, `location`, `totalTickets`, `price`) VALUES 
(2, 3, 'The Eras Tour Vietnam - Đêm Nhạc Indie', 'Đêm nhạc hội tụ các band Indie hot nhất.', '2026-06-15 19:00:00', '2026-06-15 23:00:00', 'Nhà thi đấu Quân Khu 7, TP.HCM', 500, 350000.00),
(2, 3, 'Acoustic Chill Lắng Nghe Mùa Thu', 'Thư giãn với acoustic và cà phê.', '2026-05-10 18:30:00', '2026-05-10 21:00:00', 'Lululola Coffee, Đà Lạt', 100, 150000.00),
(2, 5, 'EDM Festival: Cháy Cùng Mùa Hè', 'Lễ hội nhạc điện tử bãi biển.', '2025-07-20 16:00:00', '2025-07-20 23:59:00', 'Công viên Biển Đông, Đà Nẵng', 2000, 500000.00), -- Đã hoàn thành
(3, 3, 'React Native vs Flutter 2026', 'Cuộc chiến framework di động.', '2026-05-20 08:30:00', '2026-05-20 12:00:00', 'Đại học Mở TP.HCM', 300, 0.00), -- Miễn phí
(3, 3, 'Spring Boot Masterclass', 'Làm chủ Spring Boot từ Zero đến Hero.', '2026-06-05 09:00:00', '2026-06-05 17:00:00', 'Tòa nhà Bitexco, TP.HCM', 50, 1200000.00),
(3, 2, 'AI & Machine Learning Expo', 'Triển lãm công nghệ AI.', '2026-08-15 08:00:00', '2026-08-16 18:00:00', 'SECC Quận 7, TP.HCM', 1000, 200000.00), -- Chờ duyệt
(4, 3, 'HCMC Midnight Run 2026', 'Giải chạy đêm khám phá Sài Gòn.', '2026-07-12 00:00:00', '2026-07-12 05:00:00', 'Đường Lê Duẩn, Quận 1', 5000, 650000.00),
(4, 3, 'Trail Marathon Tà Xùa', 'Chạy bộ địa hình trên những tầng mây.', '2026-09-20 04:00:00', '2026-09-21 12:00:00', 'Tà Xùa, Sơn La', 500, 1500000.00),
(4, 4, 'Đạp Xe Vì Môi Trường', 'Hủy do thời tiết xấu.', '2026-05-01 06:00:00', '2026-05-01 10:00:00', 'Hồ Bán Nguyệt, TP.HCM', 200, 0.00), -- Bị hủy
(2, 3, 'Đêm Nhạc Trịnh Công Sơn', 'Tưởng nhớ người nhạc sĩ tài hoa.', '2026-05-25 19:30:00', '2026-05-25 22:00:00', 'Nhà hát Hòa Bình, TP.HCM', 800, 800000.00),
(3, 3, 'Cyber Security Workshop', 'Bảo mật dữ liệu cá nhân trong kỷ nguyên số.', '2026-06-12 14:00:00', '2026-06-12 17:00:00', 'Khách sạn Rex, TP.HCM', 150, 250000.00),
(4, 3, 'Giải Cầu Lông Sinh Viên Mở Rộng', 'Tranh tài các trường Đại học khu vực Miền Nam.', '2026-05-28 08:00:00', '2026-05-30 18:00:00', 'Nhà thi đấu Phú Thọ, TP.HCM', 1000, 50000.00),
(2, 3, 'Triển Lãm Nghệ Thuật Ánh Sáng', 'Khám phá không gian nghệ thuật thị giác.', '2026-07-01 09:00:00', '2026-07-15 21:00:00', 'Bảo tàng Mỹ thuật TP.HCM', 2000, 100000.00),
(3, 1, 'Hội thảo Lập Trình Blockchain', 'Tương lai của Web3.', '2026-08-01 08:30:00', '2026-08-01 16:30:00', 'Tòa nhà Landmark 81', 200, 500000.00), -- Bản nháp (Draft)
(2, 3, 'Giao Lưu Fan Meeting G-Dragon', 'Sự kiện fan meeting lớn nhất năm.', '2026-11-20 18:00:00', '2026-11-20 21:30:00', 'Sân vận động Mỹ Đình, Hà Nội', 15000, 2500000.00);

-- Gắn Category cho Event
INSERT INTO `eventcategory` (`event_id`, `category_id`) VALUES 
(1, 1), (1, 10), (2, 1), (3, 1), (3, 10), (4, 8), (4, 7), (5, 8), (5, 7),
(6, 8), (6, 9), (7, 3), (7, 6), (8, 3), (8, 5), (9, 3), (9, 6), (10, 1),
(11, 8), (12, 3), (13, 4), (13, 10), (14, 8), (15, 1), (15, 10);

-- ------------------------------------------------------
-- 5. DỮ LIỆU ĐẶT VÉ (BOOKING & PAYMENT)
-- ------------------------------------------------------
-- Giao dịch thành công (PAID)
INSERT INTO `booking` (`id`, `event_id`, `user_id`, `status_id`, `quantity`, `unitPrice`, `totalPrice`) VALUES 
(1, 1, 6, 2, 2, 350000, 700000),
(2, 1, 7, 2, 1, 350000, 350000),
(3, 2, 8, 2, 4, 150000, 600000),
(4, 5, 12, 2, 1, 1200000, 1200000), -- Hải mua vé Spring Boot
(5, 7, 12, 2, 2, 650000, 1300000), -- Hải mua vé Marathon
(6, 4, 10, 2, 1, 0, 0),             -- Vé miễn phí
(7, 4, 11, 2, 3, 0, 0),
(8, 10, 15, 2, 2, 800000, 1600000);

INSERT INTO `payment` (`booking_id`, `user_id`, `status_id`, `amount`, `method`, `transaction_id`) VALUES 
(1, 6, 2, 700000, 'VNPAY', 'VNP123456789'),
(2, 7, 2, 350000, 'MOMO', 'MOMO987654321'),
(3, 8, 2, 600000, 'VNPAY', 'VNP111222333'),
(4, 12, 2, 1200000, 'VNPAY', 'VNP444555666'),
(5, 12, 2, 1300000, 'MOMO', 'MOMO777888999'),
(8, 15, 2, 1600000, 'VNPAY', 'VNP000999888');

-- Giao dịch đang chờ thanh toán (PENDING)
INSERT INTO `booking` (`id`, `event_id`, `user_id`, `status_id`, `quantity`, `unitPrice`, `totalPrice`) VALUES 
(9, 15, 6, 1, 1, 2500000, 2500000),
(10, 8, 13, 1, 2, 1500000, 3000000),
(11, 13, 14, 1, 5, 100000, 500000);

-- Giao dịch bị hủy (CANCELLED)
INSERT INTO `booking` (`id`, `event_id`, `user_id`, `status_id`, `quantity`, `unitPrice`, `totalPrice`) VALUES 
(12, 1, 9, 3, 10, 350000, 3500000), -- User spammer bị hủy
(13, 10, 6, 3, 2, 800000, 1600000);

-- ------------------------------------------------------
-- 6. CHI TIẾT VÉ (TICKET DETAIL - Sinh ra từ Booking đã PAID)
-- ------------------------------------------------------
INSERT INTO `ticketdetail` (`booking_id`, `qrCode`, `status_id`) VALUES 
-- Booking 1 (2 vé, EV1)
(1, 'QR_EV1_B1_T1_X8A9B', 1), (1, 'QR_EV1_B1_T2_K9L2M', 2), -- 1 vé chưa quét, 1 vé đã check-in
-- Booking 2 (1 vé, EV1)
(2, 'QR_EV1_B2_T1_P3Q4R', 1),
-- Booking 3 (4 vé, EV2)
(3, 'QR_EV2_B3_T1_H7J8K', 2), (3, 'QR_EV2_B3_T2_W2E3R', 2), (3, 'QR_EV2_B3_T3_T5Y6U', 2), (3, 'QR_EV2_B3_T4_I8O9P', 1),
-- Booking 4 (1 vé, EV5 - Hải)
(4, 'QR_EV5_B4_T1_HAI_MASTERCLASS', 1),
-- Booking 5 (2 vé, EV7 - Hải)
(5, 'QR_EV7_B5_T1_HAI_RUN1', 1), (5, 'QR_EV7_B5_T2_HAI_RUN2', 1),
-- Booking 6 & 7 (Vé miễn phí EV4)
(6, 'QR_EV4_B6_T1_FREE_1', 1),
(7, 'QR_EV4_B7_T1_FREE_2', 1), (7, 'QR_EV4_B7_T2_FREE_3', 1), (7, 'QR_EV4_B7_T3_FREE_4', 1),
-- Booking 8 (2 vé, EV10)
(8, 'QR_EV10_B8_T1_TRINH1', 1), (8, 'QR_EV10_B8_T2_TRINH2', 1);
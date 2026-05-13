-- ======================================================
-- PROJECT: EVENT TICKET MANAGEMENT SYSTEM
-- FULL SCRIPT: ALL TABLES + HASHED PASSWORDS + DUMMY DATA
-- ======================================================

DROP DATABASE IF EXISTS `event_ticket_db`;
CREATE DATABASE `event_ticket_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `event_ticket_db`;

-- ------------------------------------------------------
-- 1. CẤU TRÚC BẢNG (SNAKE_CASE)
-- ------------------------------------------------------
CREATE TABLE `role` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `status_user` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `status_event` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `status_booking` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `status_pay` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );
CREATE TABLE `status_ticket` ( `id` int PRIMARY KEY, `name` varchar(50) NOT NULL );

CREATE TABLE `category` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `user` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `role_id` int NOT NULL,
  `status_id` int NOT NULL,
  `username` varchar(50) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL UNIQUE,
  `full_name` varchar(100) NOT NULL,
  `phone` varchar(20),
  `avatar` varchar(255),
  `active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
  `active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`organizer_id`) REFERENCES `user` (`id`),
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
  `active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
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
  `active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`),
  FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  FOREIGN KEY (`status_id`) REFERENCES `status_pay` (`id`)
);

CREATE TABLE `ticket_detail` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `booking_id` int NOT NULL,
  `qr_code` varchar(255) NOT NULL UNIQUE,
  `status_id` int NOT NULL,
  `active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`status_id`) REFERENCES `status_ticket` (`id`)
);

-- ------------------------------------------------------
-- 2. DỮ LIỆU TỪ ĐIỂN & NGƯỜI DÙNG (BĂM MK: 123456)
-- ------------------------------------------------------
INSERT INTO `role` VALUES (1, 'ADMIN'), (2, 'ORGANIZER'), (3, 'ATTENDEE');
INSERT INTO `status_user` VALUES (1, 'PENDING'), (2, 'ACTIVE'), (3, 'BANNED');
INSERT INTO `status_event` VALUES (1, 'DRAFT'), (2, 'PENDING_REVIEW'), (3, 'PUBLISHED'), (4, 'CANCELLED'), (5, 'COMPLETED');
INSERT INTO `status_booking` VALUES (1, 'PENDING_PAYMENT'), (2, 'PAID'), (3, 'CANCELLED');
INSERT INTO `status_pay` VALUES (1, 'PENDING'), (2, 'SUCCESS'), (3, 'FAILED');
INSERT INTO `status_ticket` VALUES (1, 'VALID'), (2, 'CHECKED_IN');

INSERT INTO `category` (`name`) VALUES 
('Âm nhạc'), ('Hội thảo'), ('Thể thao'), ('Nghệ thuật'), ('Du lịch'), 
('Cộng đồng'), ('Giáo dục'), ('Công nghệ'), ('Kinh doanh'), ('Giải trí');

INSERT INTO `user` (`role_id`, `status_id`, `username`, `password`, `email`, `full_name`, `phone`) VALUES 
(1, 2, 'super_admin', '$2a$10$7zBCl5.S7W.mK9K1yTqOue9BvYqI6yM1.8g9GfVzE6B6zXzXzXzXz', 'admin@eventhub.vn', 'Quản Trị Viên Hệ Thống', '0901112223'),
(2, 2, 'vibe_ent', '$2a$10$7zBCl5.S7W.mK9K1yTqOue9BvYqI6yM1.8g9GfVzE6B6zXzXzXzXz', 'contact@vibe.vn', 'Vibe Entertainment', '0912333444'),
(3, 2, 'user_tester', '$2a$10$7zBCl5.S7W.mK9K1yTqOue9BvYqI6yM1.8g9GfVzE6B6zXzXzXzXz', 'tester@gmail.com', 'Người Dùng Thử Nghiệm', '0988000111');

-- ------------------------------------------------------
-- 3. DỮ LIỆU SỰ KIỆN & ĐẶT VÉ MẪU
-- ------------------------------------------------------
INSERT INTO `event` (`organizer_id`, `status_id`, `title`, `description`, `start_time`, `end_time`, `location`, `total_tickets`, `price`) VALUES 
(2, 3, 'Đêm Nhạc Hội Indie 2026', 'Sự kiện âm nhạc quy mô lớn cho sinh viên.', '2026-07-15 19:00:00', '2026-07-15 23:00:00', 'Sân vận động Hoa Lư', 1000, 500000.00);

INSERT INTO `event_category` (`event_id`, `category_id`) VALUES (1, 1);

INSERT INTO `booking` (`event_id`, `user_id`, `status_id`, `quantity`, `unit_price`, `total_price`) VALUES 
(1, 3, 2, 1, 500000, 500000);

INSERT INTO `payment` (`booking_id`, `user_id`, `status_id`, `amount`, `method`, `transaction_id`) VALUES 
(1, 3, 2, 500000, 'VNPAY', 'PAY_TEST_OK_2026');

INSERT INTO `ticket_detail` (`booking_id`, `qr_code`, `status_id`) VALUES 
(1, 'QR_INDIE_2026_TEST_01', 1);
-- ======================================================
-- PROJECT: EVENT TICKET MANAGEMENT SYSTEM (E-TICKET HUB)
-- DATABASE SCRIPT: JAVA MAPPING OPTIMIZED VERSION
-- - Table names: PascalCase (e.g., EventFee, TicketDetail)
-- - Column names: camelCase (e.g., roleId, createdDate)
-- ======================================================

DROP DATABASE IF EXISTS `event_ticket_db`;
CREATE DATABASE `event_ticket_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `event_ticket_db`;

-- ------------------------------------------------------
-- 1. DICTIONARY LOOKUP TABLES (STATIC STATUS & ROLES)
-- ------------------------------------------------------
CREATE TABLE `Role` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `StatusUser` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `StatusEvent` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `StatusBooking` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `StatusPay` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

CREATE TABLE `StatusTicket` (
  `id` int PRIMARY KEY,
  `name` varchar(50) NOT NULL
);

-- ------------------------------------------------------
-- 2. CORE SYSTEM TABLES
-- ------------------------------------------------------
CREATE TABLE `Category` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT 1, 
  `createdDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedDate` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE `User` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `roleId` int NOT NULL,
  `statusId` int NOT NULL,
  `email` varchar(100) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `fullName` varchar(100) NOT NULL,
  `phone` varchar(20),
  `avatar` varchar(255),
  `identityCard` varchar(255) DEFAULT NULL,
  `businessLicense` varchar(255) DEFAULT NULL,
  `organizationName` varchar(255) DEFAULT NULL,
  `taxCode` varchar(20) DEFAULT NULL,
  `createdDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedDate` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`roleId`) REFERENCES `Role` (`id`),
  FOREIGN KEY (`statusId`) REFERENCES `StatusUser` (`id`)
);

CREATE TABLE `Event` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `organizerId` int NOT NULL,
  `statusId` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text,
  `imageUrl` varchar(255),
  `videoUrl` varchar(255),
  `startTime` datetime NOT NULL,
  `endTime` datetime NOT NULL,
  `location` varchar(255) NOT NULL,
  `totalTickets` int NOT NULL DEFAULT 0,
  `price` decimal(10,2) DEFAULT 0.00,
  `soldTickets` int NOT NULL DEFAULT 0,
  `listingFee` decimal(10,2) DEFAULT 0.00,
  `isPaidFee` tinyint(1) DEFAULT 0,
  `settlementCode` varchar(100) DEFAULT NULL, 
  `createdDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedDate` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`organizerId`) REFERENCES `User` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`statusId`) REFERENCES `StatusEvent` (`id`)
);

CREATE TABLE `EventCategory` (
  `eventId` int NOT NULL,
  `categoryId` int NOT NULL,
  PRIMARY KEY (`eventId`, `categoryId`),
  FOREIGN KEY (`eventId`) REFERENCES `Event` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`categoryId`) REFERENCES `Category` (`id`) ON DELETE CASCADE
);

CREATE TABLE `Booking` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `eventId` int NOT NULL,
  `userId` int NOT NULL,
  `statusId` int NOT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `unitPrice` decimal(10,2) NOT NULL,
  `totalPrice` decimal(10,2) NOT NULL,
  `createdDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedDate` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`eventId`) REFERENCES `Event` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`userId`) REFERENCES `User` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`statusId`) REFERENCES `StatusBooking` (`id`)
);

CREATE TABLE `Payment` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `bookingId` int NOT NULL,
  `userId` int NOT NULL,
  `statusId` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `method` varchar(50) NOT NULL,
  `transactionId` varchar(255),
  `createdDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedDate` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`bookingId`) REFERENCES `Booking` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`userId`) REFERENCES `User` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`statusId`) REFERENCES `StatusPay` (`id`)
);

CREATE TABLE `TicketDetail` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `bookingId` int NOT NULL,
  `qrCode` varchar(255) NOT NULL UNIQUE,
  `statusId` int NOT NULL,
  `createdDate` datetime DEFAULT CURRENT_TIMESTAMP,
  `updatedDate` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`bookingId`) REFERENCES `Booking` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`statusId`) REFERENCES `StatusTicket` (`id`)
);

CREATE TABLE `EventFee` (
  `id` int PRIMARY KEY AUTO_INCREMENT,
  `eventId` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `statusId` int NOT NULL,
  `paymentMethod` varchar(50),
  `transactionId` varchar(255),
  `createdDate` datetime DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`eventId`) REFERENCES `Event` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`statusId`) REFERENCES `StatusPay` (`id`)
);

-- ------------------------------------------------------
-- 3. SEEDING MOCK DATA
-- ------------------------------------------------------

INSERT INTO `Role` VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_ORGANIZER'), (3, 'ROLE_ATTENDEE');
INSERT INTO `StatusUser` VALUES (1, 'PENDING'), (2, 'ACTIVE'), (3, 'BANNED');
INSERT INTO `StatusEvent` VALUES (1, 'DRAFT'), (2, 'PENDING_REVIEW'), (3, 'PUBLISHED'), (4, 'CANCELLED'), (5, 'COMPLETED');
INSERT INTO `StatusBooking` VALUES (1, 'PENDING_PAYMENT'), (2, 'PAID'), (3, 'CANCELLED');
INSERT INTO `StatusPay` VALUES (1, 'PENDING'), (2, 'SUCCESS'), (3, 'FAILED');
INSERT INTO `StatusTicket` VALUES (1, 'VALID'), (2, 'CHECKED_IN');

INSERT INTO `Category` (`name`, `active`) VALUES 
('Âm nhạc', 1), ('Hội thảo', 1), ('Thể thao', 1), ('Nghệ thuật', 1), ('Du lịch', 1), 
('Cộng đồng', 1), ('Giáo dục', 1), ('Công nghệ', 1), ('Kinh doanh', 1), ('Giải trí', 0);

INSERT INTO `User` (`roleId`, `statusId`, `email`, `password`, `fullName`, `phone`, `avatar`, `identityCard`, `businessLicense`, `organizationName`, `taxCode`) VALUES 
(1, 2, 'admin@eventhub.vn', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Quản Trị Viên Hệ Thống', '0901112223', 'admin_avatar.png', NULL, NULL, NULL, NULL),
(2, 2, 'contact@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Vibe Entertainment', '0912333444', 'vibe_logo.png', 'https://storage/cccd_vibe.jpg', 'https://storage/gpkd_vibe.pdf', 'Công ty TNHH Giải Trí Vibe', '0314556677'),
(2, 2, 'info@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Saigon Music Group', '0915666777', 'sgm_logo.png', 'https://storage/cccd_sgm.jpg', 'https://storage/gpkd_sgm.pdf', 'Công ty Cổ phần Âm nhạc Sài Gòn', '0319888999'),
(2, 1, 'ceo.techtech@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Phạm Minh Trí', '0982777111', 'tri_avatar.png', 'https://storage/cccd_tri.jpg', 'https://storage/gpkd_tech.pdf', 'Công ty Công Nghệ Trẻ TechTech', '0315551234'),
(2, 1, 'marketing@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Glory Event Agency', '0938444555', 'glory_logo.png', 'https://storage/cccd_glory.jpg', NULL, 'Hộ Kinh Doanh Sự Kiện Glory', NULL),
(3, 2, 'nguyenvana@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Nguyễn Văn A', '0988000111', 'user1.png', NULL, NULL, NULL, NULL),
(3, 2, 'tranventh@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Trần Thị B', '0977222333', 'user2.png', NULL, NULL, NULL, NULL),
(3, 2, 'lehoangc@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Lê Hoàng C', '0966333444', 'user3.png', NULL, NULL, NULL, NULL),
(3, 2, 'phamvand@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Phạm Văn D', '0955444555', 'user4.png', NULL, NULL, NULL, NULL),
(3, 2, 'vuhoange@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Vũ Hoàng E', '0944555666', 'user5.png', NULL, NULL, NULL, NULL),
(3, 2, 'doanthif@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Đoàn Thị F', '0933666777', 'user6.png', NULL, NULL, NULL, NULL),
(3, 2, 'hoangvangg@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Hoàng Văn G', '0922777888', 'user7.png', NULL, NULL, NULL, NULL),
(3, 2, 'buitih@gmail.com', '$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO', 'Bùi Thị H', '0911888999', 'user8.png', NULL, NULL, NULL, NULL);

INSERT INTO `Event` (`organizerId`, `statusId`, `title`, `description`, `startTime`, `endTime`, `location`, `totalTickets`, `price`, `soldTickets`, `listingFee`, `isPaidFee`, `settlementCode`) VALUES 
(2, 3, 'Đêm Nhạc Hội Indie 2026', 'Sự kiện âm nhạc quy mô lớn.', '2026-07-15 19:00:00', '2026-07-15 23:00:00', 'Sân vận động Hoa Lư', 1000, 500000.00, 5, 200000.00, 1, NULL),
(2, 3, 'Hội Thảo Khởi Nghiệp Công Nghệ', 'Xu hướng phát triển công nghệ.', '2026-08-10 08:00:00', '2026-08-10 12:00:00', 'Trung tâm Hội nghị SSE', 200, 150000.00, 4, 100000.00, 1, NULL),
(3, 3, 'Saigon Rock Fest 2026', 'Đại nhạc hội Rock bùng nổ sức trẻ.', '2026-09-01 17:00:00', '2026-09-01 22:30:00', 'Nhà thi đấu Nguyễn Du', 1500, 350000.00, 3, 200000.00, 1, NULL),
(3, 3, 'Giải Chạy Marathon Vì Cộng Đồng', 'Chạy bộ 5km và 10km bảo vệ môi trường.', '2026-06-20 05:00:00', '2026-06-20 10:00:00', 'Công viên Vinhomes Central Park', 500, 0.00, 0, 0.00, 1, NULL),
(2, 2, 'Triển Lãm Tranh Sơn Dầu Việt', 'Trưng bày tác phẩm nghệ thuật.', '2026-10-05 09:00:00', '2026-10-12 18:00:00', 'Bảo tàng Mỹ thuật TP.HCM', 300, 80000.00, 0, 100000.00, 1, NULL),
(2, 1, 'Workshop Làm Bánh Gối Cổ Truyền', 'Làm bánh gối chuẩn vị.', '2026-06-15 14:00:00', '2026-06-15 17:00:00', 'Bếp Bánh Ngon Q3', 30, 250000.00, 0, 50000.00, 0, NULL),
(3, 5, 'Live Concert Thanh Âm Hoàng Hôn', 'Đêm nhạc acoustic đã kết thúc.', '2026-04-01 18:00:00', '2026-04-01 21:00:00', 'Đà Lạt Mộng Mơ', 150, 450000.00, 150, 150000.00, 1, 'FT260401888'),
(3, 4, 'Hội Chợ Sách Cũ Sài Gòn', 'Sự kiện bị hủy bỏ.', '2026-05-10 08:00:00', '2026-05-12 21:00:00', 'Nhà văn hóa Thanh Niên', 2000, 0.00, 0, 0.00, 1, NULL),
(2, 3, 'Giải Đấu Liên Minh Huyền Thoại', 'Tranh cúp vô địch.', '2026-07-22 13:00:00', '2026-07-24 20:00:00', 'Vikings Esports Arena', 100, 50000.00, 0, 50000.00, 1, NULL),
(2, 3, 'Hội Thảo Xu Hướng Học Tiếng Anh', 'Bí quyết đạt TOEIC 625 nhanh chóng.', '2026-06-05 09:00:00', '2026-06-05 11:30:00', 'Đại Học Mở TP.HCM', 120, 0.00, 0, 0.00, 1, NULL),
(3, 3, 'Đêm Hài Độc Thoại Sài Gòn tếu', 'Mang lại tiếng cười cuối tuần.', '2026-07-02 20:00:00', '2026-07-02 22:30:00', 'Sân khấu kịch Q1', 80, 200000.00, 0, 50000.00, 1, NULL);

INSERT INTO `EventCategory` (`eventId`, `categoryId`) VALUES 
(1, 1), (1, 10), (2, 2), (2, 8), (2, 9), (3, 1), (4, 3), (4, 6), (5, 4), (6, 10), (7, 1), (8, 6), (8, 7), (9, 3), (9, 10), (10, 7), (11, 10);

INSERT INTO `Booking` (`id`, `eventId`, `userId`, `statusId`, `quantity`, `unitPrice`, `totalPrice`) VALUES 
(1, 1, 6, 2, 2, 500000.00, 1000000.00),
(2, 1, 7, 2, 2, 500000.00, 1000000.00),
(3, 1, 8, 1, 1, 500000.00, 500000.00),
(4, 2, 9, 2, 2, 150000.00, 300000.00),
(5, 2, 10, 2, 2, 150000.00, 300000.00),
(6, 3, 11, 2, 3, 350000.00, 1050000.00),
(7, 1, 9, 3, 2, 500000.00, 1000000.00),
(8, 2, 6, 1, 1, 150000.00, 150000.00),
(9, 3, 7, 3, 1, 350000.00, 350000.00),
(10, 5, 8, 1, 2, 80000.00, 160000.00),
(11, 9, 12, 1, 1, 50000.00, 50000.00),
(12, 11, 13, 1, 2, 200000.00, 400000.00);

INSERT INTO `Payment` (`bookingId`, `userId`, `statusId`, `amount`, `method`, `transactionId`) VALUES 
(1, 6, 2, 1000000.00, 'VNPAY', 'VNP20260701999'),
(2, 7, 2, 1000000.00, 'MOMO', 'MOMO_MM112233'),
(3, 8, 1, 500000.00, 'VNPAY', NULL),
(4, 9, 2, 300000.00, 'BANK_TRANSFER', 'FT260810AAA'),
(5, 10, 2, 300000.00, 'MOMO', 'MOMO_MM445566'),
(6, 11, 2, 1050000.00, 'VNPAY', 'VNP20260901888'),
(7, 9, 3, 1000000.00, 'VNPAY', NULL),
(8, 6, 1, 150000.00, 'MOMO', NULL),
(10, 8, 1, 160000.00, 'VNPAY', NULL),
(12, 13, 1, 400000.00, 'MOMO', NULL);

INSERT INTO `TicketDetail` (`bookingId`, `qrCode`, `statusId`) VALUES 
(1, 'E-TKT-UUID-A1B2-C3D4-0001', 1),
(1, 'E-TKT-UUID-A1B2-C3D4-0002', 2),
(2, 'E-TKT-UUID-E5F6-G7H8-0003', 1),
(2, 'E-TKT-UUID-E5F6-G7H8-0004', 1),
(4, 'E-TKT-UUID-I9J0-K1L2-0005', 1),
(4, 'E-TKT-UUID-I9J0-K1L2-0006', 1),
(5, 'E-TKT-UUID-M3N4-O5P6-0007', 2),
(5, 'E-TKT-UUID-M3N4-O5P6-0008', 1),
(6, 'E-TKT-UUID-Q7R8-S9T0-0009', 1),
(6, 'E-TKT-UUID-Q7R8-S9T0-0010', 1),
(6, 'E-TKT-UUID-Q7R8-S9T0-0011', 1);

INSERT INTO `EventFee` (`eventId`, `amount`, `statusId`, `paymentMethod`, `transactionId`) VALUES 
(1, 200000.00, 2, 'VNPAY', 'FEE_VNP111'),
(2, 100000.00, 2, 'MOMO', 'FEE_MOMO222'),
(3, 200000.00, 2, 'BANK_TRANSFER', 'FEE_BANK333'),
(5, 100000.00, 2, 'VNPAY', 'FEE_VNP444'),
(6, 50000.00, 1, NULL, NULL),
(7, 150000.00, 2, 'MOMO', 'FEE_MOMO555'),
(9, 50000.00, 2, 'VNPAY', 'FEE_VNP666'),
(11, 50000.00, 2, 'MOMO', 'FEE_MOMO755');
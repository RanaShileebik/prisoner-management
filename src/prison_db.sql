-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 29, 2025 at 06:37 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `prison_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `prisoners`
--

CREATE TABLE `prisoners` (
  `id` int(11) NOT NULL,
  `name` text NOT NULL,
  `age` int(11) DEFAULT NULL,
  `crime` text DEFAULT NULL,
  `nationality` text DEFAULT NULL,
  `sentence` text DEFAULT NULL,
  `entry_date` date DEFAULT NULL,
  `release_date` date DEFAULT NULL,
  `legal_status` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `prisoners`
--

INSERT INTO `prisoners` (`id`, `name`, `age`, `crime`, `nationality`, `sentence`, `entry_date`, `release_date`, `legal_status`) VALUES
(2, 'Ahmed Saleh	', 34, 'Theft', 'Libyan', '2 years', '2023-01-10', '2025-01-10', 'Convicted'),
(3, 'Omar Al-Kabir	', 28, 'Fraud', 'Egyptian', '3 years', '2022-06-22', '2025-06-22', 'Pre-trial'),
(4, 'Sara Jibril', 38, 'Embezzlement', 'Tunisian', '5 years', '2021-11-01', '2026-11-01', 'Convicted'),
(5, 'Mohamed Faraj	', 22, 'Assault', 'Libyan', '1 year', '2024-03-15', '2025-03-15', 'Convicted'),
(6, 'Layla Bashir', 37, 'Drug Possession', 'Algerian', '4 years', '2022-08-05', '2026-08-05', 'Convicted'),
(7, 'Youssef Karroumi', 31, 'Arson', 'Moroccan', '3 years', '2023-09-12', '2026-09-12', 'Pre-trial'),
(8, 'Fatima Al-Zahra	', 29, 'Smuggling', 'Sudanese', '2.5 years', '2023-04-10', '2025-10-10', 'Convicted'),
(9, 'Khaled Ramadan', 40, 'Homicide', 'Libyan', '20 years', '2020-02-14', '2040-02-14', 'Convicted'),
(10, 'Noura Saadi', 33, 'Cybercrime', 'Syrian', '3 years', '2023-07-18', '2026-07-18', 'Pre-trial'),
(11, 'Tarek Al-Hamdi	', 36, 'Bribery', 'Libyan', '2 years', '2023-12-01', '2025-12-01', 'Pre-trial');

-- --------------------------------------------------------

--
-- Table structure for table `visitations`
--

CREATE TABLE `visitations` (
  `id` int(11) NOT NULL,
  `prisoner_id` int(11) DEFAULT NULL,
  `visitor_name` varchar(100) DEFAULT NULL,
  `relation` varchar(100) DEFAULT NULL,
  `visit_date` date DEFAULT NULL,
  `time_in` varchar(10) DEFAULT NULL,
  `time_out` varchar(10) DEFAULT NULL,
  `security_approval` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `visitations`
--

INSERT INTO `visitations` (`id`, `prisoner_id`, `visitor_name`, `relation`, `visit_date`, `time_in`, `time_out`, `security_approval`) VALUES
(1, 1, 'Taha Salem', 'brother', '2025-06-25', '10:00', '10:15', 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `prisoners`
--
ALTER TABLE `prisoners`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `visitations`
--
ALTER TABLE `visitations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `prisoner_id` (`prisoner_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `prisoners`
--
ALTER TABLE `prisoners`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `visitations`
--
ALTER TABLE `visitations`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

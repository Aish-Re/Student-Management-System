-- Create Database
CREATE DATABASE IF NOT EXISTS student_management_system;
USE student_management_system;

-- Create Admin Table
CREATE TABLE IF NOT EXISTS admin (
    admin_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Students Table
CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    date_of_birth DATE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    address TEXT,
    course VARCHAR(50) NOT NULL,
    department VARCHAR(50) NOT NULL,
    semester INT NOT NULL,
    enrollment_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert Default Admin (password: admin123)
INSERT INTO admin (username, password, email) VALUES 
('admin', 'admin123', 'admin@system.com');

-- Insert Sample Students
INSERT INTO students (student_id, full_name, gender, date_of_birth, email, phone_number, address, course, department, semester, enrollment_date) VALUES
('STU001', 'John Doe', 'Male', '2000-05-15', 'john.doe@email.com', '9876543210', '123 Main St, New York', 'Computer Science', 'Software Engineering', 5, '2022-08-15'),
('STU002', 'Jane Smith', 'Female', '2001-08-22', 'jane.smith@email.com', '9876543211', '456 Oak Ave, Los Angeles', 'Information Technology', 'Web Development', 3, '2023-01-10'),
('STU003', 'Mike Johnson', 'Male', '2000-11-30', 'mike.johnson@email.com', '9876543212', '789 Pine Rd, Chicago', 'Computer Science', 'Data Science', 6, '2022-08-15');

-- Create View for Student Statistics
CREATE VIEW student_statistics AS
SELECT 
    course,
    department,
    COUNT(*) as student_count,
    AVG(CASE WHEN semester <= 4 THEN 1 ELSE 0 END) * 100 as underclassmen_percentage
FROM students
GROUP BY course, department;

-- Stored Procedure for Student Search
DELIMITER //
CREATE PROCEDURE SearchStudents(IN search_term VARCHAR(100))
BEGIN
    SELECT * FROM students 
    WHERE student_id LIKE CONCAT('%', search_term, '%')
       OR full_name LIKE CONCAT('%', search_term, '%')
       OR email LIKE CONCAT('%', search_term, '%')
    ORDER BY full_name;
END //
DELIMITER ;
<<<<<<< HEAD
CREATE TABLE IF NOT EXISTS system_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    details TEXT,
    INDEX idx_timestamp (timestamp),
    INDEX idx_username (username),
    INDEX idx_action (action)
=======
CREATE TABLE IF NOT EXISTS system_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    details TEXT,
    INDEX idx_timestamp (timestamp),
    INDEX idx_username (username),
    INDEX idx_action (action)
>>>>>>> f2657b5aed8478585daa9d54677536c97f57fb7c
); 
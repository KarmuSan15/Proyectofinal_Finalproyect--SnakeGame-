DROP DATABASE  IF EXISTS snake_game;
CREATE DATABASE IF NOT EXISTS snake_game;
USE snake_game;

CREATE TABLE IF NOT EXISTS scores (
 id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  score INT NOT NULL 
);

INSERT INTO scores (name, score)
VALUES 
('Pro Gamer', 100),
('Semi-Pro', 80),
('Amateur', 60),
('Bueno', 50),
('Oro', 40),
('Decente', 30),
('Plata', 20),
('Bronce', 10),
('Principiante', 5),
('Noob', 3);

ALTER TABLE scores ADD UNIQUE (name);
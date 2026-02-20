CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    user_name VARCHAR(150) NOT NULL,
    password VARCHAR(150) NOT NULL,
    role TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO users (user_name, password, role)
VALUES
('admin', 'admin123', "ADMIN");
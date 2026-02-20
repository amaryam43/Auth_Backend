CREATE TABLE IF NOT EXISTS user_sessions (
    token VARCHAR(255) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(150) NOT NULL,
    role VARCHAR(100) NOT NULL,
    expires_at BIGINT NOT NULL,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS users (
            id SERIAL PRIMARY KEY,
            username VARCHAR(50) NOT NULL UNIQUE,
            password_hash VARCHAR(255) NOT NULL,
            role VARCHAR(50) NOT NULL DEFAULT 'user'
        );
CREATE TABLE IF NOT EXISTS loans (
            id SERIAL PRIMARY KEY,
            user_id INTEGER REFERENCES users(id),
            amount DECIMAL(15, 2) NOT NULL,
            type VARCHAR(50) NOT NULL,
            status VARCHAR(10) DEFAULT 'pending',
            CONSTRAINT status_check CHECK (status IN ('pending', 'approved', 'rejected'))
        );
CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid (),
  display_name VARCHAR(100) NOT NULL,
  login_id VARCHAR(100) NOT NULL UNIQUE,
  hashed_password TEXT NOT NULL,
  mail_address TEXT NOT NULL UNIQUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
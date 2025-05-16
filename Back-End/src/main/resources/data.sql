-- Insert admin user
INSERT INTO users (
    full_name,
    email,
    password,
    phone_number,
    creation_date,
    last_login,
    role,
    active,
    locked,
    expired,
    credentials_expired
) VALUES (
    'Admin User',
    'admin@restaurant.com',
    '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', -- This is a BCrypt encoded password for 'admin123'
    '+1234567890',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    'ADMIN',
    true,
    false,
    false,
    false
); 
CREATE TABLE IF NOT EXISTS member(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50),
    name VARCHAR(50),
    password VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS post(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200),
    content TEXT,
    member_id BIGINT
);

CREATE TABLE IF NOT EXISTS comment(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT,
    post_id BIGINT,
    member_id BIGINT
);
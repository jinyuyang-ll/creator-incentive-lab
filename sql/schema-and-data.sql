DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS creators;

CREATE TABLE creators (
    creator_id BIGINT PRIMARY KEY,
    join_date DATE NOT NULL
);

CREATE TABLE posts (
    post_id BIGINT PRIMARY KEY,
    creator_id BIGINT NOT NULL REFERENCES creators(creator_id),
    post_date DATE NOT NULL,
    is_valid BOOLEAN NOT NULL
);

INSERT INTO creators (creator_id, join_date) VALUES
    (1, CURRENT_DATE - 100),
    (2, CURRENT_DATE - 30),
    (3, CURRENT_DATE - 10),
    (4, CURRENT_DATE - 2);

INSERT INTO posts (post_id, creator_id, post_date, is_valid) VALUES
    (101, 1, CURRENT_DATE, TRUE),
    (102, 1, CURRENT_DATE - 1, TRUE),
    (103, 1, CURRENT_DATE - 1, FALSE),
    (104, 1, CURRENT_DATE - 4, TRUE),
    (105, 1, CURRENT_DATE - 7, TRUE),
    (106, 2, CURRENT_DATE - 2, FALSE),
    (107, 2, CURRENT_DATE - 8, TRUE),
    (108, 2, CURRENT_DATE - 20, TRUE),
    (109, 3, CURRENT_DATE - 1, TRUE),
    (110, 3, CURRENT_DATE - 2, TRUE),
    (111, 3, CURRENT_DATE - 3, TRUE),
    (112, 3, CURRENT_DATE - 4, TRUE),
    (113, 3, CURRENT_DATE - 5, TRUE);


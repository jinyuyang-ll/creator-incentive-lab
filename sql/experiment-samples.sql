-- MySQL 8.x
-- 一行 = 一位创作者在一个决策日、一个实验中的状态。
CREATE TABLE IF NOT EXISTS creator_experiment_samples (
    sample_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    creator_id BIGINT NOT NULL,
    decision_date DATE NOT NULL,

    -- 决策前7天已知的特征
    posts_last_7_days INT NOT NULL,
    active_days_last_7_days INT NOT NULL,
    valid_posts_last_7_days INT NOT NULL,
    creator_age_days INT NOT NULL,

    -- 决策当天确定的实验处理
    experiment_name VARCHAR(100) NOT NULL,
    experiment_group VARCHAR(16) NOT NULL,
    incentive_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    accepted_incentive BOOLEAN NULL,

    -- 决策后7天才能填写的结果；观察窗口结束前允许为NULL
    future_posts_7_days INT NULL,
    future_valid_posts_7_days INT NULL,
    active_next_7_days BOOLEAN NULL,
    violation_count INT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_experiment_sample_creator
        FOREIGN KEY (creator_id) REFERENCES creators(creator_id),
    CONSTRAINT chk_experiment_group
        CHECK (experiment_group IN ('CONTROL', 'TREATMENT')),
    CONSTRAINT uq_creator_experiment_day
        UNIQUE (creator_id, decision_date, experiment_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 小样本只用于练习SQL，不能据此作真实业务结论。
INSERT INTO creator_experiment_samples (
    creator_id, decision_date,
    posts_last_7_days, active_days_last_7_days,
    valid_posts_last_7_days, creator_age_days,
    experiment_name, experiment_group,
    incentive_amount, accepted_incentive,
    future_posts_7_days, future_valid_posts_7_days,
    active_next_7_days, violation_count
) VALUES
    (1, DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), 4, 3, 3, 86,
     'creator_incentive_v1', 'CONTROL', 0.00, NULL, 4, 3, TRUE, 0),
    (2, DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), 1, 1, 0, 16,
     'creator_incentive_v1', 'TREATMENT', 20.00, TRUE, 3, 2, TRUE, 0),
    (3, DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), 5, 5, 5, 1,
     'creator_incentive_v1', 'CONTROL', 0.00, NULL, 4, 4, TRUE, 0),
    (4, DATE_SUB(CURRENT_DATE, INTERVAL 14 DAY), 0, 0, 0, 0,
     'creator_incentive_v1', 'TREATMENT', 20.00, FALSE, 1, 1, TRUE, 0)
ON DUPLICATE KEY UPDATE
    posts_last_7_days = VALUES(posts_last_7_days),
    active_days_last_7_days = VALUES(active_days_last_7_days),
    valid_posts_last_7_days = VALUES(valid_posts_last_7_days),
    creator_age_days = VALUES(creator_age_days),
    experiment_group = VALUES(experiment_group),
    incentive_amount = VALUES(incentive_amount),
    accepted_incentive = VALUES(accepted_incentive),
    future_posts_7_days = VALUES(future_posts_7_days),
    future_valid_posts_7_days = VALUES(future_valid_posts_7_days),
    active_next_7_days = VALUES(active_next_7_days),
    violation_count = VALUES(violation_count);

SELECT *
FROM creator_experiment_samples
ORDER BY decision_date, creator_id;
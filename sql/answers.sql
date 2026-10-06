-- MySQL 8.x
-- 统一口径：包含今天在内的7个自然日，即 [CURRENT_DATE - 6天, CURRENT_DATE]。

-- 1. 过去7天发布数（这里只返回有发布的创作者）
SELECT creator_id, COUNT(*) AS posts_last_7_days
FROM posts
WHERE post_date BETWEEN DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY) AND CURRENT_DATE
GROUP BY creator_id
ORDER BY creator_id;

-- 2. 过去7天不同活跃日期数
SELECT creator_id, COUNT(DISTINCT post_date) AS active_days_last_7_days
FROM posts
WHERE post_date BETWEEN DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY) AND CURRENT_DATE
GROUP BY creator_id
ORDER BY creator_id;

-- 3. 完整 CreatorStats；无发布的创作者也保留为0。
-- 日期条件必须放在 ON 中，否则 WHERE 会过滤 LEFT JOIN 产生的空行。
-- COUNT(p.post_id) 不统计 NULL；COUNT(*) 会把无发布作者对应的连接行统计成1。
SELECT
    c.creator_id,
    COUNT(p.post_id) AS posts_last_7_days,
    COUNT(DISTINCT p.post_date) AS active_days_last_7_days,
    COUNT(CASE WHEN p.is_valid = TRUE THEN 1 END) AS valid_posts_last_7_days
FROM creators c
LEFT JOIN posts p
    ON p.creator_id = c.creator_id
   AND p.post_date BETWEEN DATE_SUB(CURRENT_DATE, INTERVAL 6 DAY) AND CURRENT_DATE
GROUP BY c.creator_id
ORDER BY c.creator_id;

-- 4. 窗口函数：每位有发文记录的创作者最近一次发布的内容。
WITH ranked_posts AS (
    SELECT
        p.*,
        ROW_NUMBER() OVER (
            PARTITION BY creator_id
            ORDER BY post_date DESC, post_id DESC
        ) AS row_number
    FROM posts p
)
SELECT post_id, creator_id, post_date, is_valid
FROM ranked_posts
WHERE row_number = 1
ORDER BY creator_id;
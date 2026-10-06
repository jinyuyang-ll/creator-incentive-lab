-- MySQL 8.x：creator_incentive_v1 实验分析
-- 所有主要分析均采用 ITT：按最初随机分组分析，不按是否接受激励筛选。

-- 1. 分组人数：最基础的分流检查
SELECT
    experiment_group,
    COUNT(*) AS creators
FROM creator_experiment_samples
WHERE experiment_name = 'creator_incentive_v1'
GROUP BY experiment_group;

-- 2. 主指标、辅助指标与护栏指标
SELECT
    experiment_group,
    COUNT(*) AS creators,
    AVG(future_valid_posts_7_days) AS avg_future_valid_posts_7_days,
    AVG(active_next_7_days) AS active_next_7_days_rate,
    SUM(future_valid_posts_7_days) / NULLIF(SUM(future_posts_7_days), 0)
        AS valid_post_rate,
    AVG(violation_count > 0) AS creator_violation_rate,
    SUM(incentive_amount) AS total_incentive_cost
FROM creator_experiment_samples
WHERE experiment_name = 'creator_incentive_v1'
  AND future_valid_posts_7_days IS NOT NULL
GROUP BY experiment_group;

-- 3. 绝对提升与相对提升
WITH group_metrics AS (
    SELECT
        experiment_group,
        COUNT(*) AS creators,
        AVG(future_valid_posts_7_days) AS mean_valid_posts
    FROM creator_experiment_samples
    WHERE experiment_name = 'creator_incentive_v1'
      AND future_valid_posts_7_days IS NOT NULL
    GROUP BY experiment_group
), pivoted AS (
    SELECT
        MAX(CASE WHEN experiment_group = 'CONTROL' THEN creators END) AS control_creators,
        MAX(CASE WHEN experiment_group = 'TREATMENT' THEN creators END) AS treatment_creators,
        MAX(CASE WHEN experiment_group = 'CONTROL' THEN mean_valid_posts END) AS control_mean,
        MAX(CASE WHEN experiment_group = 'TREATMENT' THEN mean_valid_posts END) AS treatment_mean
    FROM group_metrics
)
SELECT
    control_mean,
    treatment_mean,
    treatment_mean - control_mean AS absolute_lift,
    (treatment_mean - control_mean) / NULLIF(control_mean, 0) AS relative_lift
FROM pivoted;

-- 4. 增量内容与单位增量成本
WITH group_metrics AS (
    SELECT
        experiment_group,
        COUNT(*) AS creators,
        AVG(future_valid_posts_7_days) AS mean_valid_posts,
        SUM(incentive_amount) AS total_cost
    FROM creator_experiment_samples
    WHERE experiment_name = 'creator_incentive_v1'
      AND future_valid_posts_7_days IS NOT NULL
    GROUP BY experiment_group
), pivoted AS (
    SELECT
        MAX(CASE WHEN experiment_group = 'CONTROL' THEN mean_valid_posts END) AS control_mean,
        MAX(CASE WHEN experiment_group = 'TREATMENT' THEN mean_valid_posts END) AS treatment_mean,
        MAX(CASE WHEN experiment_group = 'TREATMENT' THEN creators END) AS treatment_creators,
        MAX(CASE WHEN experiment_group = 'TREATMENT' THEN total_cost END) AS treatment_cost
    FROM group_metrics
)
SELECT
    treatment_creators * (treatment_mean - control_mean)
        AS estimated_incremental_valid_posts,
    treatment_cost,
    CASE
        WHEN treatment_mean > control_mean THEN
            treatment_cost /
            (treatment_creators * (treatment_mean - control_mean))
        ELSE NULL
    END AS cost_per_incremental_valid_post
FROM pivoted;

-- 5. 实验前平衡性：随机分组后，两组决策前特征应大致接近
SELECT
    experiment_group,
    COUNT(*) AS creators,
    AVG(posts_last_7_days) AS avg_posts_before,
    AVG(active_days_last_7_days) AS avg_active_days_before,
    AVG(valid_posts_last_7_days) AS avg_valid_posts_before,
    AVG(creator_age_days) AS avg_creator_age_days
FROM creator_experiment_samples
WHERE experiment_name = 'creator_incentive_v1'
GROUP BY experiment_group;

-- 6. SRM基础检查：50:50分流时，期望每组人数为总人数的一半。
-- df=1时卡方统计量 > 3.841，可作为5%显著性水平的异常警报。
WITH group_counts AS (
    SELECT
        experiment_group,
        COUNT(*) AS observed_count
    FROM creator_experiment_samples
    WHERE experiment_name = 'creator_incentive_v1'
    GROUP BY experiment_group
), totals AS (
    SELECT SUM(observed_count) AS total_count
    FROM group_counts
)
SELECT
    SUM(
        POWER(observed_count - total_count / 2.0, 2)
        / (total_count / 2.0)
    ) AS chi_square_statistic,
    CASE
        WHEN SUM(
            POWER(observed_count - total_count / 2.0, 2)
            / (total_count / 2.0)
        ) > 3.841 THEN 'WARNING: possible SRM'
        ELSE 'PASS: no SRM signal'
    END AS srm_check
FROM group_counts
CROSS JOIN totals;

-- 7. 均值差95%置信区间所需统计量（大样本正态近似）
-- 标准误 SE = SQRT(var_t/n_t + var_c/n_c)，区间 = 差值 +/- 1.96*SE。
WITH stats AS (
    SELECT
        experiment_group,
        COUNT(*) AS n,
        AVG(future_valid_posts_7_days) AS mean_value,
        VAR_SAMP(future_valid_posts_7_days) AS sample_variance
    FROM creator_experiment_samples
    WHERE experiment_name = 'creator_incentive_v1'
      AND future_valid_posts_7_days IS NOT NULL
    GROUP BY experiment_group
), pivoted AS (
    SELECT
        MAX(CASE WHEN experiment_group = 'CONTROL' THEN n END) AS n_control,
        MAX(CASE WHEN experiment_group = 'TREATMENT' THEN n END) AS n_treatment,
        MAX(CASE WHEN experiment_group = 'CONTROL' THEN mean_value END) AS mean_control,
        MAX(CASE WHEN experiment_group = 'TREATMENT' THEN mean_value END) AS mean_treatment,
        MAX(CASE WHEN experiment_group = 'CONTROL' THEN sample_variance END) AS var_control,
        MAX(CASE WHEN experiment_group = 'TREATMENT' THEN sample_variance END) AS var_treatment
    FROM stats
), calculated AS (
    SELECT
        mean_treatment - mean_control AS mean_difference,
        SQRT(var_treatment / n_treatment + var_control / n_control) AS standard_error
    FROM pivoted
)
SELECT
    mean_difference,
    standard_error,
    mean_difference - 1.96 * standard_error AS ci_95_lower,
    mean_difference + 1.96 * standard_error AS ci_95_upper,
    mean_difference / NULLIF(standard_error, 0) AS test_statistic
FROM calculated;
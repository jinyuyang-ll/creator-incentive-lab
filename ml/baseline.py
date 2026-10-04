"""时间切分的极小分类基线：运行 `python ml/baseline.py`。"""

from datetime import date, timedelta

try:
    import pandas as pd
    from sklearn.linear_model import LogisticRegression
    from sklearn.metrics import accuracy_score, roc_auc_score
except ImportError as exc:
    raise SystemExit("缺少依赖，请运行: python -m pip install -r ml/requirements.txt") from exc


def build_samples() -> pd.DataFrame:
    rows = []
    first_day = date(2026, 1, 1)
    # 每一行代表“某位作者在某个决策日”的快照；特征只使用决策日前7天。
    for index in range(60):
        decision_date = first_day + timedelta(days=index)
        posts = index % 8
        active_days = min(posts, 1 + index % 5) if posts else 0
        valid_posts = max(0, posts - (1 if index % 4 == 0 else 0))
        # 教学用合成标签：真实项目中应来自决策后的事实表。
        active_next_7_days = int(valid_posts >= 2 or (index % 11 == 0))
        rows.append({
            "creator_id": 1000 + index % 12,
            "decision_date": decision_date,
            "posts_last_7_days": posts,
            "active_days_last_7_days": active_days,
            "valid_posts_last_7_days": valid_posts,
            "active_next_7_days": active_next_7_days,
        })
    return pd.DataFrame(rows)


def main() -> None:
    samples = build_samples().sort_values("decision_date")
    feature_columns = [
        "posts_last_7_days",
        "active_days_last_7_days",
        "valid_posts_last_7_days",
    ]
    # 前45天训练、后15天验证，不能随机打散未来样本到训练集。
    train = samples.iloc[:45]
    validation = samples.iloc[45:]

    model = LogisticRegression(random_state=42)
    model.fit(train[feature_columns], train["active_next_7_days"])
    probabilities = model.predict_proba(validation[feature_columns])[:, 1]
    predictions = (probabilities >= 0.5).astype(int)

    result = validation[["creator_id", "decision_date", "active_next_7_days"]].copy()
    result["predicted_probability"] = probabilities.round(3)
    result["prediction"] = predictions
    print(result.to_string(index=False))
    print("\naccuracy:", round(accuracy_score(validation["active_next_7_days"], predictions), 3))
    print("roc_auc:", round(roc_auc_score(validation["active_next_7_days"], probabilities), 3))
    print("\n样本=作者在决策日的快照；预测=未来7天是否活跃；验证=按时间留出未来15天。")


if __name__ == "__main__":
    main()


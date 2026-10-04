# 创作者激励系统：7天入职前练习

这是一个能跑通的最小 Spring Boot 项目。它用内存数据完成“读取作者近7天统计 → 生成并记录激励建议”，并配套 SQL、实验评估和机器学习基线。

> 练习口径：过去7天是包含今天的7个自然日，即 `[今天-6天, 今天]`。

## 先跑起来（第1天）

环境要求：Java 8+、Maven 3.6+、Python 3.9+。当前电脑的 Java 8 可直接运行本项目。

```powershell
mvn test
mvn spring-boot:run
```

另开一个 PowerShell：

```powershell
Invoke-RestMethod http://localhost:8080/creators/1/stats

$body = @{ creatorId = 1 } | ConvertTo-Json
Invoke-RestMethod -Method Post `
  -Uri http://localhost:8080/incentive-decisions `
  -ContentType 'application/json' `
  -Body $body
```

也可以在 IntelliJ IDEA 中运行 `IncentiveApplication.main()`，再使用 [requests.http](requests.http) 逐个发送请求。

## 请求怎样流动

```text
HTTP 请求
  → Controller：解析路径/JSON，校验参数
  → Service：计算7天统计，执行激励规则
  → Repository：读取作者与帖子，保存决策
  → Controller：把 Java 对象序列化成 JSON
```

建议先按断点顺序阅读：

1. `CreatorController#getStats` 或 `IncentiveDecisionController#create`
2. `CreatorService#getSevenDayStats`
3. `IncentiveDecisionService#createDecision`
4. 两个 `InMemory...Repository`
5. `GlobalExceptionHandler` 和 `RequestLoggingFilter`

内存数据每次重启都会重置，这是本练习有意为之。作者 1 有多条近7天内容，作者 2 只有一条无效内容，作者 3 没有内容。

## 7天安排（每天2–3小时）

### 第1天：认识工程

- 运行测试和服务，调用 `GET /creators/1/stats`。
- 从 Controller 开始逐层加断点。
- 画出上面的调用链，并用自己的话解释每一层为何存在。

验收：不看答案也能找到统计逻辑和内存数据的位置。

### 第2天：决策与记录

- 调用 `POST /incentive-decisions`，理解 `activeDays >= 2 && validPosts < 5`。
- 用返回的 `decisionId` 调用 `GET /incentive-decisions/{id}`，确认建议已记录。
- 修改一条规则并补一个测试，再运行 `mvn test`。

验收：Controller 中没有业务判断；重启后记录消失的原因能解释清楚。

### 第3天：错误、日志和 Git

先故意制造再修复三个问题，每次记录“现象 → 日志 → 代码位置 → 原因 → 修复 → 验证”：

1. POST `{}`，观察 400 和 `creatorId is required`。
2. 请求作者 999，观察 404 和日志中的 `requestId`。
3. 暂时把 `CreatorStats.validPostsLast7Days` 改成 `String`，观察测试为何失败，然后恢复成 `long`。

推荐 Git 节奏：

```powershell
git status
git diff
git add .
git diff --staged
git commit -m "Add creator incentive decision endpoint"
```

验收：能用三句话说明改了什么、为什么、如何验证。

### 第4天：SQL

- 在 PostgreSQL（或兼容环境）执行 `sql/schema-and-data.sql`。
- 先自己写4题，再对照 `sql/answers.sql`。
- 手算作者 1、2、4；重点比较 `COUNT(*)` 和 `COUNT(p.post_id)`。
- 思考为何 LEFT JOIN 的日期过滤要写在 `ON` 中。

验收：查询列能一一映射到 `CreatorStats`。

### 第5天：A/B 实验

先独立回答目标、成本、对照、风险四题，再阅读 `docs/experiment.md`。试着解释“收到激励后发5条”为什么不等于“激励带来5条”。

### 第6天：机器学习

```powershell
python -m pip install -r ml/requirements.txt
python ml/baseline.py
```

阅读代码时只抓住四件事：一行样本是谁、预测目标是什么、特征在哪一刻可用、为何按时间切分。然后尝试把 `active_next_7_days` 偷偷加进特征，观察这种数据泄漏为何会产生虚假的好成绩，再撤销修改。

### 第7天：10分钟演示

1. 2分钟：展示目录结构和 Controller → Service → Repository。
2. 2分钟：发正常、400、404 三类请求并看日志。
3. 2分钟：讲 SQL 的 LEFT JOIN、COUNT 和窗口函数。
4. 2分钟：讲实验目标、对照组和质量护栏。
5. 1分钟：运行 Python 基线，说明时间切分。
6. 1分钟：`git diff --staged` 总结改动和验证方式。

## 可以自己继续加的功能

- 初级：给 `CreatorStats` 新增“无效内容数”，补测试。
- 中级：把 `LocalDate.now()`/`Instant.now()` 替换成注入的 `Clock`，让时间测试完全稳定。
- 中级：增加“接受激励”和“决策后7天结果”的数据结构。
- 进阶：用 H2/PostgreSQL 和 Spring JDBC 替换内存 Repository，Service 与 Controller 不变。

真实团队的字段口径、分层规范、数据库和实验平台应以入职后的项目为准。


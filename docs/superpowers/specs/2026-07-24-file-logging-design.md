# 文件日志配置设计

## 目标

为 Spring Boot 应用增加可供后续故障分析的本地文件日志，同时保留 IDEA 控制台输出，并控制日志占用空间和敏感信息风险。

## 输出位置与格式

- 当前日志文件：`logs/spring-ai-pg.log`
- 归档日志文件：`logs/archive/spring-ai-pg.YYYY-MM-DD.N.log.gz`
- 日志字段：时间、级别、进程号、线程名、Logger 名称和消息
- 控制台和文件使用一致、便于检索的纯文本格式

## 滚动与清理策略

- 每天创建新的归档文件
- 单个文件达到 10MB 时在当天继续按序号滚动
- 最多保留 14 天
- 所有归档日志总量不超过 200MB
- 归档文件使用 gzip 压缩

## 日志级别与安全

- 根日志级别为 `INFO`
- 项目包 `com.hx.springaipg` 的日志级别为 `DEBUG`
- 不启用 HTTP 客户端、Spring AI 或请求正文的 `TRACE` 日志
- 不在日志配置中输出环境变量、请求头或 DeepSeek API Key

## 工程集成

- 使用 Spring Boot 原生支持的 `logback-spring.xml`，不增加第三方依赖
- 将 `/logs/` 加入 `.gitignore`，防止运行日志进入版本控制
- 保留现有 `application.yaml` 中的端口和 Spring AI 配置

## 验证

- 运行现有测试，确认应用上下文仍可正常加载
- 启动应用并确认 `logs/spring-ai-pg.log` 自动生成
- 确认文件包含应用启动日志且日志格式符合设计
- 确认构建产物仍可正常生成

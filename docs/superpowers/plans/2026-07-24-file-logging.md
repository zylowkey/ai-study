# File Logging Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add readable, rolling local file logs that Codex can inspect during later troubleshooting.

**Architecture:** Configure Spring Boot's built-in Logback integration with a console appender and a size-and-time rolling file appender. Verify the live Logback context in the existing Spring Boot test, then verify a real application start writes the expected file.

**Tech Stack:** Java 21, Spring Boot 3.5.11, Logback, JUnit 5, AssertJ

## Global Constraints

- Write the active log to `logs/spring-ai-pg.log`.
- Archive to `logs/archive/spring-ai-pg.YYYY-MM-DD.N.log.gz`.
- Roll daily and at 10MB, retain 14 days, and cap archives at 200MB.
- Keep root logging at `INFO` and `com.hx.springaipg` at `DEBUG`.
- Do not enable HTTP, Spring AI, request-body, header, environment-variable, or API-key TRACE logging.
- Add no third-party dependency and preserve existing application configuration.

---

### Task 1: Configure and verify rolling file logging

**Files:**
- Create: `src/main/resources/logback-spring.xml`
- Modify: `.gitignore`
- Modify: `src/test/java/com/hx/springaipg/SpringAiPgApplicationTests.java`

**Interfaces:**
- Consumes: Spring Boot's Logback initialization and the existing `@SpringBootTest` application context.
- Produces: the named Logback appender `ROLLING_FILE` and the active file `logs/spring-ai-pg.log`.

- [ ] **Step 1: Add a failing live-configuration test**

Add a test that obtains Logback's root logger and asserts that `ROLLING_FILE` is a `RollingFileAppender` whose active file ends with `logs/spring-ai-pg.log`:

```java
@Test
void writesApplicationLogsToTheProjectLogDirectory() {
    Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    Appender<ILoggingEvent> appender = rootLogger.getAppender("ROLLING_FILE");

    assertThat(appender).isInstanceOf(RollingFileAppender.class);
    RollingFileAppender<?> rollingFileAppender = (RollingFileAppender<?>) appender;
    assertThat(rollingFileAppender.getFile().replace('\\', '/'))
            .endsWith("logs/spring-ai-pg.log");
}
```

- [ ] **Step 2: Run the test and verify the missing appender causes failure**

Run with Java 21:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn -Dtest=SpringAiPgApplicationTests#writesApplicationLogsToTheProjectLogDirectory test
```

Expected: FAIL because `rootLogger.getAppender("ROLLING_FILE")` returns `null`.

- [ ] **Step 3: Add the minimal Logback configuration**

Create `logback-spring.xml` with `CONSOLE` and `ROLLING_FILE` appenders. Use `SizeAndTimeBasedRollingPolicy`, `10MB` maximum file size, `14` maximum history, `200MB` total-size cap, gzip archives, root `INFO`, and project package `DEBUG`.

- [ ] **Step 4: Ignore generated logs**

Append this repository-root rule to `.gitignore`:

```gitignore
### Application logs ###
/logs/
```

- [ ] **Step 5: Run all tests and package the application**

Run:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn clean package
```

Expected: BUILD SUCCESS with all tests passing.

- [ ] **Step 6: Verify a real application start writes readable logs**

Start the packaged application with a non-secret placeholder key and a temporary port, wait for the startup message, stop it, then inspect `logs/spring-ai-pg.log`.

Expected: the file exists and contains `Started SpringAiPgApplication`; no API key value appears in the file.

- [ ] **Step 7: Record completion**

Do not commit because the current directory is not a Git repository. Report the changed files, build result, log path, and how future troubleshooting will read the log.

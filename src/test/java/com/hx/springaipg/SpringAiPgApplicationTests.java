package com.hx.springaipg;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.ai.openai.api-key=test-key")
class SpringAiPgApplicationTests {

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    @Test
    void contextLoads() {
    }

    @Test
    void configuresDeepSeekAsTheChatProvider() {
        assertThat(baseUrl).isEqualTo("https://api.deepseek.com");
        assertThat(model).isEqualTo("deepseek-v4-pro");
    }

    @Test
    void writesApplicationLogsToTheProjectLogDirectory() {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Appender<ILoggingEvent> appender = rootLogger.getAppender("ROLLING_FILE");

        assertThat(appender).isInstanceOf(RollingFileAppender.class);
        RollingFileAppender<?> rollingFileAppender = (RollingFileAppender<?>) appender;
        assertThat(rollingFileAppender.getFile().replace('\\', '/'))
                .endsWith("logs/spring-ai-pg.log");
    }

}

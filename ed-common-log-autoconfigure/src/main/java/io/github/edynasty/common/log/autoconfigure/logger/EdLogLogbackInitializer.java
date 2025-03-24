package io.github.edynasty.common.log.autoconfigure.logger;

import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.charset.StandardCharsets;

import static io.github.edynasty.common.log.core.constants.LogConfigConstants.LOG_TYPE_CONFIG;
import static io.github.edynasty.common.log.core.constants.LogConfigConstants.LOG_TYPE_LOGGER;

/**
 * @author tangxingpeng
 * @since 2022/9/2 09:16
 */
@ConditionalOnProperty(name = LOG_TYPE_CONFIG, havingValue = LOG_TYPE_LOGGER, matchIfMissing = true)
public class EdLogLogbackInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String LOG_AUDIT_FILE = "./logs/audit";
    private static final int LOG_FILEMAXDAY = 7;
    private static final FileSize LOG_MAXFILESIZE = FileSize.valueOf("50MB");
    private static final String LOG_NAME = "io.github.edynasty.common.log.logger.service.impl.LoggerLogServiceImpl";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        String applicationName = applicationContext.getApplicationName();
        if (StringUtils.isEmpty(applicationName)) {
            applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        }
        StaticLoggerBinder singleton = StaticLoggerBinder.getSingleton();
        LoggerContext loggerContext = (LoggerContext) singleton.getLoggerFactory();

        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(loggerContext);
        appender.setName("audit_log");
        appender.setFile(LOG_AUDIT_FILE + "/" + applicationName + ".log");

        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(loggerContext);
        encoder.setCharset(StandardCharsets.UTF_8);

        PatternLayout layout = new PatternLayout();
        layout.setPattern("%msg%n");
        layout.setContext(loggerContext);
        layout.start();
        encoder.setLayout(layout);

        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setFileNamePattern(LOG_AUDIT_FILE + "/" + applicationName + ".%d{yyyy-MM-dd}.%i.log");
        rollingPolicy.setMaxHistory(LOG_FILEMAXDAY);
        rollingPolicy.setMaxFileSize(LOG_MAXFILESIZE);
        rollingPolicy.setParent(appender);
        rollingPolicy.start();
        appender.setRollingPolicy(rollingPolicy);

        appender.setEncoder(encoder);
        appender.start();

        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(loggerContext);
        asyncAppender.setName("audit_log_async");
        asyncAppender.setDiscardingThreshold(0);
        asyncAppender.addAppender(appender);
        asyncAppender.start();

        Logger logger = loggerContext.getLogger(LOG_NAME);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);
        logger.addAppender(asyncAppender);

    }

}

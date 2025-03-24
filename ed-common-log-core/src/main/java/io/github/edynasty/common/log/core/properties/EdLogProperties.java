package io.github.edynasty.common.log.core.properties;

import io.github.edynasty.common.log.core.enums.EdLogType;
import io.github.edynasty.common.log.core.enums.LoggerFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import static io.github.edynasty.common.log.core.constants.LogConfigConstants.LOG_PREFIX;

/**
 * 审计日志配置
 *
 * @author tangxingpeng
 * @since 2020/12/26 22:34
 */
@Setter
@Getter
@ConfigurationProperties(prefix = LOG_PREFIX)
@RefreshScope
public class EdLogProperties {

    /**
     * 是否开启审计日志
     */
    private Boolean enabled = false;

    /**
     * 日志记录类型(logger/redis/db/es/kafka)
     */
    private EdLogType logType = EdLogType.LOGGER;

    /**
     * 日志记录格式
     */
    private LoggerFormat loggerFormat = LoggerFormat.JSON;

    public void setLogType(String logType) {
        this.logType = EdLogType.fromString(logType);
    }

    public void setLoggerFormat(String loggerFormat) {
        this.loggerFormat = LoggerFormat.fromString(loggerFormat);
    }

}

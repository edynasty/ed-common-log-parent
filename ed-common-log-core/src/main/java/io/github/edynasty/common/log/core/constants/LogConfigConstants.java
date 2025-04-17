package io.github.edynasty.common.log.core.constants;

/**
 * 配置项常量
 *
 * @author tangxingpeng
 * @since 2021/2/9 15:22
 */
public interface LogConfigConstants {

    /**
     * Log 配置前缀
     */
    String LOG_PREFIX = "ed.log";

    /**
     * 日志记录类型(logger/redis/db/es/kafka)
     */
    String LOG_TYPE = "log-type";

    /**
     * 日志记录类型配置
     */
    String LOG_TYPE_CONFIG = LOG_PREFIX + "." + LOG_TYPE;

    /**
     * 日志记录类型-logger
     */
    String LOG_TYPE_LOGGER = "logger";

    /**
     * 日志记录类型-kafka
     */
    String LOG_TYPE_KAFKA = "kafka";

    /**
     * 日志记录类型-db
     */
    String LOG_TYPE_DB = "db";

    /**
     * kafka topic
     */
    String LOG_KAFKA_TOPIC = "ed-log-topic";

}

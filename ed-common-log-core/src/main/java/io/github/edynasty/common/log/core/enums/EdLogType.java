package io.github.edynasty.common.log.core.enums;

/**
 * @author tangxingpeng
 * @since 2025/1/22 09:27
 */
public enum EdLogType {

    /**
     * LOGGER
     */
    LOGGER,
    /**
     * REDIS
     */
    REDIS,
    /**
     * DB
     */
    DB,
    /**
     * ES
     */
    ES,
    /**
     * KAFKA
     */
    KAFKA;

    public static EdLogType fromString(String value) {
        try {
            return EdLogType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 默认返回 LOGGER 或者抛出异常
            return LOGGER;
        }
    }

}

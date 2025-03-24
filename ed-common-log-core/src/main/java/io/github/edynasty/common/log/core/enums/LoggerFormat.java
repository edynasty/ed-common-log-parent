package io.github.edynasty.common.log.core.enums;

/**
 * @author tangxingpeng
 * @since 2025/1/22 09:29
 */
public enum LoggerFormat {

    /**
     * JSON
     */
    JSON,
    /**
     * TEXT
     */
    TEXT;

    public static LoggerFormat fromString(String value) {
        try {
            return LoggerFormat.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 默认返回 JSON 或者抛出异常
            return JSON;
        }
    }

}

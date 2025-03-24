package io.github.edynasty.common.log.core.enums;

import lombok.Getter;

/**
 * 请求状态
 *
 * @author tangxingpeng
 * @since 2022/8/30 11:50
 */
@Getter
public enum LogRequestStatus {

    /**
     * 警告
     */
    WARN(2),
    /**
     * 成功
     */
    SUCCESS(1),
    /**
     * 失败
     */
    FAIL(0);

    LogRequestStatus(Integer value) {
        this.value = value;
    }

    private final Integer value;

}

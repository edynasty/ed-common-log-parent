package io.github.edynasty.common.log.core.model;

import io.github.edynasty.common.log.core.enums.LogTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 审计日志
 *
 * @author tangxingpeng
 * @since 2022/8/22 16:46
 */
@Setter
@Getter
public class SysLogger {

    /**
     * id
     */
    private Long id;
    /**
     * 父id
     */
    private Long parentId;
    /**
     * 名称
     */
    private String name;
    /**
     * 接口操作类型{@link LogTypeEnum}
     */
    private String logType;
    /**
     * 操作信息
     */
    private String operation;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 链路ID
     */
    private Long traceId;
    /**
     * 执行时间（s）
     */
    private Double runTime;
    /**
     * 操作时间
     */
    private LocalDateTime createTime;
    /**
     * 应用名
     */
    private String applicationName;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 调用者ip
     */
    private String requestIp;
    /**
     * 调用结果：
     * 0: 失败
     * 1：成功
     */
    private Integer requestStatus;
    /**
     * 请求方式（GET POST PUT DELETE...）
     */
    private String requestMethod;
    /**
     * 操作系统
     */
    private String requestOs;
    /**
     * 浏览器
     */
    private String requestBrowser;
    /**
     * 调用参数
     */
    private String requestArgs;
    /**
     * 调用返回结果
     */
    private String requestResult = "-";
    /**
     * 错误信息
     */
    private String errorMsg = "-";

}

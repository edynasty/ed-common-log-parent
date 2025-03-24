package io.github.edynasty.common.log.db.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.edynasty.common.log.core.enums.LogTypeEnum;
import io.github.edynasty.common.log.core.model.SysLogger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.Date;

/**
 * 系统日志
 *
 * @author tangxingpeng
 * @since 2025/3/21 15:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdSysLog implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    private Date createTime;
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

    public EdSysLog(SysLogger sysLogger) {
        this.id = sysLogger.getId();
        this.parentId = sysLogger.getParentId();
        this.name = sysLogger.getName();
        this.logType = sysLogger.getLogType();
        this.userId = sysLogger.getUserId();
        this.userName = sysLogger.getUserName();
        this.tenantId = sysLogger.getTenantId();
        this.traceId = sysLogger.getTraceId();
        this.runTime = sysLogger.getRunTime();
        this.createTime = Date.from(
                sysLogger.getCreateTime().atZone(ZoneId.systemDefault())
                        .toInstant()
        );
        this.applicationName = sysLogger.getApplicationName();
        this.className = sysLogger.getClassName();
        this.methodName = sysLogger.getMethodName();
        this.requestIp = sysLogger.getRequestIp();
        this.requestStatus = sysLogger.getRequestStatus();
        this.requestMethod = sysLogger.getRequestMethod();
        this.requestOs = sysLogger.getRequestOs();
        this.requestBrowser = sysLogger.getRequestBrowser();
    }

}

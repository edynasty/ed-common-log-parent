package io.github.edynasty.common.log.logger.service.impl;

import cn.hutool.json.JSONUtil;
import io.github.edynasty.common.log.core.enums.LoggerFormat;
import io.github.edynasty.common.log.core.model.SysLogger;
import io.github.edynasty.common.log.core.properties.EdLogProperties;
import io.github.edynasty.common.log.core.service.BaseLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.time.format.DateTimeFormatter;

/**
 * 日志服务实现
 *
 * @author tangxingpeng
 * @since 2022/8/31 10:14
 */
@Slf4j
@RequiredArgsConstructor
public class LoggerLogServiceImpl implements BaseLogService {

    private static final String MSG_PATTERN = "{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}|{}";

    private final EdLogProperties edLogProperties;

    @Override
    @Async("taskExecutor")
    public void save(SysLogger logger) {
        if (edLogProperties.getEnabled() && edLogProperties.getLoggerFormat() == LoggerFormat.TEXT) {
            log.debug(MSG_PATTERN
                    , logger.getId(), logger.getParentId()
                    , logger.getName(), logger.getLogType(), logger.getOperation()
                    , logger.getUserId(), logger.getUserName(), logger.getTenantId(), logger.getTraceId()
                    , logger.getRunTime()
                    , logger.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))
                    , logger.getApplicationName(), logger.getClassName(), logger.getMethodName()
                    , logger.getRequestIp(), logger.getRequestStatus(), logger.getRequestMethod()
                    , logger.getRequestOs(), logger.getRequestBrowser()
                    , logger.getRequestArgs(), logger.getRequestResult(), logger.getErrorMsg()
            );
        } else {
            log.debug("{}", JSONUtil.toJsonStr(logger));
        }
    }

}

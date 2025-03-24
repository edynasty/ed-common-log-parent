package io.github.edynasty.common.log.db.service.impl;

import io.github.edynasty.common.log.core.model.SysLogger;
import io.github.edynasty.common.log.core.service.BaseLogService;
import io.github.edynasty.common.log.db.model.EdSysLog;
import io.github.edynasty.common.log.db.model.EdSysLogDetail;
import io.github.edynasty.common.log.db.service.BaseEdSysLogDetailService;
import io.github.edynasty.common.log.db.service.BaseEdSysLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

/**
 * 日志服务实现
 *
 * @author tangxingpeng
 * @since 2022/8/31 10:14
 */
@Slf4j
@RequiredArgsConstructor
public class DbLogServiceImpl implements BaseLogService {

    private final BaseEdSysLogService edSysLogService;
    private final BaseEdSysLogDetailService baseEdSysLogDetailService;

    @Override
    @Async("taskExecutor")
    public void save(SysLogger logger) {
        edSysLogService.save(new EdSysLog(logger));
        baseEdSysLogDetailService.save(new EdSysLogDetail(logger));
    }

}

package io.github.edynasty.common.log.kafka.service.impl;

import io.github.edynasty.common.log.core.model.SysLogger;
import io.github.edynasty.common.log.core.service.BaseLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;

/**
 * 日志服务实现
 *
 * @author tangxingpeng
 * @since 2022/8/31 10:14
 */
@Slf4j
@RequiredArgsConstructor
public class KafkaLogServiceImpl implements BaseLogService {

    private final MessageChannel output;

    @Override
    @Async("taskExecutor")
    public void save(SysLogger logger) {
        output.send(MessageBuilder.withPayload(logger).build());
    }

}

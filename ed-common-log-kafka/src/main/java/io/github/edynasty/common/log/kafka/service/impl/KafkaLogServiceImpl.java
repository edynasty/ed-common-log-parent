package io.github.edynasty.common.log.kafka.service.impl;

import cn.hutool.json.JSONUtil;
import io.github.edynasty.common.log.core.constants.LogConfigConstants;
import io.github.edynasty.common.log.core.model.SysLogger;
import io.github.edynasty.common.log.core.service.BaseLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
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

    private final StreamBridge streamBridge;

    @Override
    @Async("taskExecutor")
    public void save(SysLogger logger) {
        streamBridge.send(LogConfigConstants.LOG_KAFKA_TOPIC, JSONUtil.toJsonStr(logger));
    }

}

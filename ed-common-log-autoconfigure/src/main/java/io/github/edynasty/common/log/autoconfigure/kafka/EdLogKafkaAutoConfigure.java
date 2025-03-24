package io.github.edynasty.common.log.autoconfigure.kafka;

import io.github.edynasty.common.log.core.service.BaseLogService;
import io.github.edynasty.common.log.kafka.service.impl.KafkaLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;

import static io.github.edynasty.common.log.core.constants.LogConfigConstants.LOG_TYPE_CONFIG;
import static io.github.edynasty.common.log.core.constants.LogConfigConstants.LOG_TYPE_KAFKA;

/**
 * @author tangxingpeng
 * @since 2025/3/24 10:14
 */
@AutoConfiguration
@ConditionalOnClass(Source.class)
@ConditionalOnProperty(name = LOG_TYPE_CONFIG, havingValue = LOG_TYPE_KAFKA, matchIfMissing = true)
public class EdLogKafkaAutoConfigure {

    @Bean
    @ConditionalOnMissingBean(BaseLogService.class)
    public BaseLogService kafkaLogServiceImpl(@Autowired Source source) {
        return new KafkaLogServiceImpl(source.output());
    }

}

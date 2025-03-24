package io.github.edynasty.common.log.autoconfigure;

import io.github.edynasty.common.log.core.aspect.EdLogAspect;
import io.github.edynasty.common.log.core.context.EdLogListenerContext;
import io.github.edynasty.common.log.core.handler.BaseLogUserHandler;
import io.github.edynasty.common.log.core.listener.EdLogEventListener;
import io.github.edynasty.common.log.core.properties.EdLogProperties;
import io.github.edynasty.common.log.core.service.BaseLogService;
import io.github.edynasty.common.log.logger.service.impl.LoggerLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

import static io.github.edynasty.common.log.core.constants.LogConfigConstants.LOG_TYPE_CONFIG;
import static io.github.edynasty.common.log.core.constants.LogConfigConstants.LOG_TYPE_LOGGER;

/**
 * 日志自动配置
 *
 * @author tangxingpeng
 * @since 2022/8/31 09:41
 */
@EnableAsync
@AutoConfiguration
@EnableConfigurationProperties({EdLogProperties.class})
public class EdLogAutoConfigure {

    @Bean
    public EdLogListenerContext edLogListenerContext(@Autowired(required = false) List<EdLogEventListener> listeners) {
        return new EdLogListenerContext(listeners);
    }

    @Bean
    public EdLogAspect edLogAspect(@Autowired EdLogProperties edLogProperties,
                                   @Autowired(required = false) BaseLogService baseLogService,
                                   @Autowired(required = false) BaseLogUserHandler baseLogUserService,
                                   @Autowired EdLogListenerContext edLogListenerContext) {
        return new EdLogAspect(edLogProperties, baseLogService, baseLogUserService, edLogListenerContext);
    }

    @Bean
    @ConditionalOnMissingBean(BaseLogService.class)
    @ConditionalOnProperty(name = LOG_TYPE_CONFIG, havingValue = LOG_TYPE_LOGGER, matchIfMissing = true)
    public BaseLogService loggerLogServiceImpl(@Autowired EdLogProperties edLogProperties) {
        return new LoggerLogServiceImpl(edLogProperties);
    }

}

package io.github.edynasty.common.log.autoconfigure.db;

import io.github.edynasty.common.log.core.service.BaseLogService;
import io.github.edynasty.common.log.db.service.BaseEdSysLogDetailService;
import io.github.edynasty.common.log.db.service.BaseEdSysLogService;
import io.github.edynasty.common.log.db.service.impl.BaseEdSysLogDetailServiceImpl;
import io.github.edynasty.common.log.db.service.impl.BaseEdSysLogServiceImpl;
import io.github.edynasty.common.log.db.service.impl.DbLogServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static io.github.edynasty.common.log.core.constants.LogConfigConstants.LOG_TYPE_CONFIG;
import static io.github.edynasty.common.log.core.constants.LogConfigConstants.LOG_TYPE_DB;

/**
 * @author tangxingpeng
 * @since 2025/3/24 10:18
 */
@AutoConfiguration
@ConditionalOnClass({JdbcTemplate.class, DataSource.class})
@MapperScan("io.github.edynasty.common.log.db.mapper")
@ConditionalOnProperty(name = LOG_TYPE_CONFIG, havingValue = LOG_TYPE_DB)
public class EdLogDbAutoConfigure {

    @Bean
    @ConditionalOnMissingBean(BaseEdSysLogService.class)
    public BaseEdSysLogService baseEdSysLogService() {
        return new BaseEdSysLogServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(BaseEdSysLogDetailService.class)
    public BaseEdSysLogDetailService baseEdSysLogDetailService() {
        return new BaseEdSysLogDetailServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(BaseLogService.class)
    public BaseLogService dbLogServiceImpl(@Autowired BaseEdSysLogService baseEdSysLogService,
                                           @Autowired BaseEdSysLogDetailService baseEdSysLogDetailService) {
        return new DbLogServiceImpl(baseEdSysLogService, baseEdSysLogDetailService);
    }

}

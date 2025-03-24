CREATE TABLE ed_sys_log (
                            id BIGINT PRIMARY KEY,
                            parent_id BIGINT,
                            name NVARCHAR(255),
                            log_type NVARCHAR(50),
                            user_id BIGINT,
                            user_name NVARCHAR(64),
                            tenant_id BIGINT,
                            trace_id BIGINT,
                            run_time FLOAT,
                            create_time DATETIME,
                            application_name NVARCHAR(64),
                            class_name NVARCHAR(255),
                            method_name NVARCHAR(255),
                            request_ip NVARCHAR(64),
                            request_status INT,
                            request_method NVARCHAR(10),
                            request_os NVARCHAR(64),
                            request_browser NVARCHAR(64)
);
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = '系统日志主表',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE', @level1name = 'ed_sys_log';
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = '主键ID',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE', @level1name = 'ed_sys_log',
    @level2type = N'COLUMN', @level2name = 'id';
-- 其他字段注释同理，需逐个添加...

CREATE TABLE ed_sys_log_detail (
                                   id BIGINT PRIMARY KEY,
                                   log_id BIGINT,
                                   operation NVARCHAR(255),
                                   request_args NVARCHAR(MAX),
    request_result NVARCHAR(MAX),
    error_msg NVARCHAR(MAX)
);
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = '系统日志详情表',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE', @level1name = 'ed_sys_log_detail';
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = '主键ID',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE', @level1name = 'ed_sys_log_detail',
    @level2type = N'COLUMN', @level2name = 'id';
-- 其他字段注释同理...
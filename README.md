# ed-common-log-parent

EdCommonLog 是一个基于 Spring Boot 的统一日志框架，旨在简化日志处理的集成与管理。它由多个模块组成，支持多种日志记录方式，包括基于
Logback、Kafka 等的日志服务。

## 项目模块

此项目包含以下几个模块：

- **ed-common-log-parent**: 父级模块，定义了全局的依赖和插件配置。
- **ed-common-log-core**: 核心模块，包含基础日志服务、日志类型、事件模型及其他日志管理功能。
- **ed-common-log-logger**: 提供基础日志记录功能，主要实现日志的记录和管理。
- **ed-common-log-logger-starter**: 为 Spring Boot 提供自动化配置，用于快速集成日志记录功能。
- **ed-common-log-kafka**: 集成 Kafka 作为日志的传输工具，支持将日志发送到 Kafka。
- **ed-common-log-kafka-starter**: 为 Spring Boot 提供 Kafka 日志传输功能的自动化配置。
- **ed-common-log-autoconfigure**: 自动配置模块，简化日志相关服务的配置。

## 快速开始

### 1. 添加依赖

你可以通过以下依赖来集成 EdCommonLog 模块到你的 Spring Boot 项目中。

#### 在 `pom.xml` 中添加父级依赖

```xml

<parent>
    <groupId>io.github.edynasty</groupId>
    <artifactId>ed-cloud-parent</artifactId>
    <version>0.0.8</version>
</parent>
```

#### 在子模块中引入需要的日志模块

例如，引入核心日志模块和 logger 支持模块：

```xml

<dependencies>
    <dependency>
        <groupId>io.github.edynasty</groupId>
        <artifactId>ed-common-logger-start</artifactId>
        <version>0.0.8</version>
    </dependency>
</dependencies>
```

### 2. 配置日志

在 application.properties 或 application.yml 中添加日志配置。

例如，配置 logger 日志：

```yaml
ed:
  log:
    enabled: true
    log-type: LOGGER
    logger-format: JSON
```

### 3. 使用日志

在代码中使用 @EdLog 注解来记录日志。

- `title`: 日志标题。无值时默认取`@ApiOperation`或`@Operation`注解值。
- `type`: 日志类型。默认为`OTHER`,用于区分不同类型的日志。
- `key`: 日志关键字。默认为空,使用`spel`表达式获取参数中的关键信息。
- `needArgs`: 是否需要记录参数。默认为`false`。
- `needResult`: 是否需要记录返回结果。默认为`false`。
- `needErrorMsg`: 是否需要记录异常信息。默认为`false`。
- `serializableHandler`: 序列化处理器。默认为`DefaultLogArgsSerializableHandler`。用于序列化参数、返回结果和异常信息。

```java
import io.github.edynasty.common.log.core.annotation.EdLog;

@Service
public class MyService {

    @ApiOperation(title = "自定义日志")
    @EdLog(title = "自定义日志", type = "INFO", key = "#id", needArgs = true, needResult = true, needErrorMsg = true, serializableHandler = DefaultLogArgsSerializableHandler.class)
    public void performAction(Long id, String name) {
        // your logic here
    }
}
```

## 模块说明

### `ed-common-log-core`

核心模块，包含基础日志功能、事件模型和日志配置。

- `EdLog`: 日志注解，用于标记需要记录日志的方法。
- `EdLogAspect`: 切面，用于拦截日志记录的方法。
- `EdLogListenerContext`: 日志监听器上下文。储存全部的日志监听器。
- `SysLoggerContext`: 系统日志记录器上下文。栈式存储系统日志记录器。
- `BaseLogUserHandler`: 用于获取当前用户信息。
- `BaseLogArgsSerializableHandler`: 序列化日志参数接口。
- `DefaultLogArgsSerializableHandler`: 默认的序列化处理器。
- `EdLogEventListener`: 日志事件监听器。如异常后发送消息通知。
- `EdLogEvent`: 定义日志事件。
- `SysLogger`: 系统日志记录器。
- `BaseLogService`: 提供基础日志服务接口。

### `ed-common-log-db`

`Mybatis-plus`集成模块，将日志保存到数据库中。

`DbLogServiceImpl`: 实现基于数据库的日志服务。

`EdSysLog`: 系统日志实体类。

`EdSysLogDetail`: 系统日志详情实体类。

`EdSysLogService`: 系统日志服务接口。

`EdSysLogServiceImpl`: 系统日志服务实现类。

`EdSysLogDetailService`: 系统日志详情服务接口。

`EdSysLogDetailServiceImpl`: 系统日志详情服务实现类。

可以通过实现`EdSysLogService`和`EdSysLogDetailService`接口来自定义日志`service`覆盖默认的`Service Bean`。

请先执行`docs/db`模块下的`sql`文件，初始化数据库表。

### `ed-common-log-logger`

日志记录模块，提供了基于 Logback 的日志服务。

`LoggerLogServiceImpl`: 实现日志记录服务。

具体实现见: [ELK详解](docs/ELK详解.md)

### `ed-common-log-kafka`

`Kafka` 集成模块，将日志通过 `Kafka` 发送到指定的日志中心。

`KafkaLogServiceImpl`: 实现基于 `Kafka` 的日志服务。

### `ed-common-log-autoconfigure`

自动配置模块，简化日志服务的集成。该模块会自动配置必要的日志服务并进行初始化。

`EdLogAutoConfigure`: 日志自动配置类。
`EdLogLogbackInitializer`: 初始化 Logback 配置。

## 许可证信息

该项目采用 Apache Software License 2.0 许可证，您可以自由使用、修改和分发本项目。有关详细信息，请参阅 Apache License 2.0。

## 贡献

欢迎提交 Pull Request 来修复问题或增强功能。
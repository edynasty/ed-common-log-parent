# 一、开启审计日志功能

确保功能开启，并生成出审计日志，具体参考：[README.md](../README.md)

# 二、部署 `ELK`

## 1. 安装`elasticsearch`

### 1.1 拷贝es配置

```shell
docker run -d --name es -e "discovery.type=single-node" elasticsearch:7.17.16
```

```shell
mkdir -p /home/docker/compose/ed/es

docker cp es:/usr/share/elasticsearch/config /home/docker/compose/ed/es/config;

docker cp es:/usr/share/elasticsearch/data /home/docker/compose/ed/es/data;

docker cp es:/usr/share/elasticsearch/plugins /home/docker/compose/ed/es/plugins;

docker cp es:/usr/share/elasticsearch/logs /home/docker/compose/ed/es/logs;
```

```shell
docker stop es;

docker rm es;
```

`docker-compose`文件

```yaml
services:
  es:
    image: elasticsearch:7.17.16
    container_name: es
    restart: always
    environment:
      # 限制内存
      - ES_JAVA_OPTS=-Xms64m -Xmx1024m
      - discovery.type=single-node
    ports:
      - 9200:9200
      - 9300:9300
    volumes:
      - ./es/config:/usr/share/elasticsearch/config
      - ./es/data:/usr/share/elasticsearch/data
      - ./es/plugins:/usr/share/elasticsearch/plugins
      - ./es/logs:/usr/share/elasticsearch/logs
```

### 1.2 修改es配置

```yaml
cluster.name: "docker-cluster"
network.host: 0.0.0.0

http.cors.enabled: true
http.cors.allow-origin: "*"
http.cors.allow-headers: Authorization,X-Requested-With,Content-Length,Content-Type

# Enable security features
xpack.security.enabled: true
xpack.security.http.ssl.enabled: false
xpack.security.transport.ssl.enabled: false
```

### 1.3 安装分词插件

#### 1.3.1 下载

下载地址: [elasticsearch-analysis-ik](https://github.com/medcl/elasticsearch-analysis-ik/releases)

解压-->将文件复制到 es的安装目录/plugin/ik下面即可

#### 1.3.2 重启es并检查插件是否安装成功

[http://localhost:9200/_cat/plugins](http://localhost:9200/_cat/plugins)

### 1.4 初始化密码

#### 1.4.1 进入容器

```shell
docker compose up -d es
docker exec -it es bash
```

#### 1.4.2 初始化密码

```shell
bin/elasticsearch-setup-passwords interactive
 ```

#### 1.4.3 创建ELK索引模板

`kibana` `curl`

```
PUT /_index_template/template_audit_log
{
	"index_patterns": ["audit-log-*"],
	"priority": 0,
	"template": {
		"settings": {
			"number_of_replicas": 0,
			"analysis.analyzer.default.type": "ik_max_word"
		},
		"mappings": {
			"_doc": {
				"properties": {
					"operation": {
						"type": "text",
						"fields": {
							"keyword": {
								"type": "keyword",
								"ignore_above": 256
							}
						}
					}
				}
			}
		}
	}
}
```

```shell
curl -XPUT http://es:9200/_template/template_audit_log -H 'Content-Type: application/json' -d '
{
	"index_patterns": ["audit-log-*"],
	"priority": 0,
	"template": {
		"settings": {
			"number_of_replicas": 0,
			"analysis.analyzer.default.type": "ik_max_word"
		},
		"mappings": {
			"_doc": {
				"properties": {
					"operation": {
						"type": "text",
						"fields": {
							"keyword": {
								"type": "keyword",
								"ignore_above": 256
							}
						}
					}
				}
			}
		}
	}
}'
```

## 2. 安装`kibana`

### 2.1 复制配置

```shell
docker run -d --name kb kibana:7.17.16
```

```shell
mkdir -p /home/docker/compose/ed/kibana
docker cp kb:/usr/share/kibana/config /home/docker/compose/ed/kibana/config;
```

```shell
docker stop kb;

docker rm kb;
```

### 2.2 修改配置

```yaml
### >>>>>>> BACKUP START: Kibana interactive setup (2022-08-03T02:58:52.275Z)

#
# ** THIS IS AN AUTO-GENERATED FILE **
#

# Default Kibana configuration for docker target
#server.host: "0.0.0.0"
#server.shutdownTimeout: "5s"
#elasticsearch.hosts: [ "http://elasticsearch:9200" ]
#monitoring.ui.container.elasticsearch.enabled: true
### >>>>>>> BACKUP END: Kibana interactive setup (2022-08-03T02:58:52.275Z)

# This section was automatically generated during setup.
server.host: 0.0.0.0
server.shutdownTimeout: 5s
elasticsearch.hosts: [ 'http://es:9200' ]
monitoring.ui.container.elasticsearch.enabled: true
elasticsearch.username: kibana_system
elasticsearch.password: ed-cloud
#使用中文
i18n.locale: "zh-CN"
```

### 2.3 `docker-compose`文件

```yaml
services:
  kibana:
    image: kibana:7.17.16
    container_name: kibana
    restart: always
    environment:
      ELASTICSEARCH_URL: http://es:9080
    ports:
      - 5601:5601
    volumes:
      - ./kibana/config:/usr/share/kibana/config
```

## 3. 安装`logstash`

### 3.1 复制配置

```shell
docker run -d --name lg logstash:7.17.16
```

```shell
mkdir -p /home/docker/compose/ed/logstash;

docker cp lg:/usr/share/logstash/config/ /home/docker/compose/ed/logstash/config;

docker cp lg:/usr/share/logstash/pipeline /home/docker/compose/ed/logstash/pipeline;
```

```shell
docker stop lg;

docker rm lg;
```

### 3.2 添加自定义`patterns`

```shell
mkdir -p /home/docker/compose/ed/logstash/config/patterns/

vi /home/docker/compose/ed/logstash/config/patterns/my_patterns
```

```yaml
  # RMI TCP Connection(2)-127.0.0.1
MYTHREADNAME ([0-9a-zA-Z._-]|\(|\)|\s)*
  # log-word
MYWORD ([\u4e00-\u9fa5_0-9a-zA-Z.-]|\(|\)|\s|\"|\{|\}|\[|\]|\,)*
MYLOGWORD ([\u4e00-\u9fa5_0-9a-zA-Z.-]|\(|\)|\s)*
```

具体参考：[Grok常用表达式](./Grok常用表达式.md)

### 3.3 修改pipeline配置

```shell
vi /home/docker/compose/ed/logstash/pipeline/logstash.conf
```

具体参考：[logstash.conf](./logstash.conf)

### 3.4 `docker-compose`文件

```yaml
services:
  logstash:
      image: logstash:7.17.16
      container_name: logstash
      restart: always
      ports:
        - 5044:5044
      volumes:
        - ./logstash/config:/usr/share/logstash/config
        - ./logstash/pipeline:/usr/share/logstash/pipeline
        - ./logstash/logs:/usr/share/logstash/logs
```

## 4. 安装`Filebeat`

### 4.1 复制配置

```shell
docker run -d --name ft elastic/filebeat:7.17.16

mkdir -p /home/docker/compose/ed/filebeat/config;

docker cp ft:/usr/share/filebeat/filebeat.yml /home/docker/compose/ed/filebeat/config/;

docker stop ft;

docker rm ft;

```

### 4.2 修改配置

```shell
vi /home/docker/compose/ed/filebeat/config/filebeat.yml
```

具体参考：[filebeat.yml](./filebeat.yml)

### 4.3 `docker-compose`文件

```yaml
services:
  logstash:
      image: elastic/filebeat:7.17.16
      container_name: filebeat
      restart: always
      # 启动用户
      user: root
      volumes:
        # 映射到容器中[作为数据源]
        - /var/lib/docker/containers:/var/lib/docker/containers
        - /var/run/docker.sock:/var/run/docker.sock
        - ./filebeat/config/filebeat.yml:/usr/share/filebeat/filebeat.yml
        - ./filebeat/logs:/usr/share/filebeat/logs
```

## 5. 安装`Kafka`

### 5.1 创建文件夹

```shell
mkdir -p /home/docker/compose/ed/kafka/data;
```

### 5.2 `docker-compose`文件

```yaml
services:
  kafka:
    image: 'bitnami/kafka:3.9'
    container_name: kafka-server
    hostname: kafka-server
    environment:
      # 启用 KRaft 模式
      KAFKA_CFG_NODE_ID: 0
      KAFKA_CFG_PROCESS_ROLES: "controller,broker"
      KAFKA_CFG_LISTENERS: "PLAINTEXT://:9092,CONTROLLER://:9093"
      KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP: "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT"
      KAFKA_CFG_CONTROLLER_QUORUM_VOTERS: "0@kafka-server:9093"
      KAFKA_CFG_CONTROLLER_LISTENER_NAMES: "CONTROLLER"
    ports:
      - '9092:9092'
    volumes:
      - './kafka/data:/bitnami/kafka'
    networks:
      - kafka-network
    
  kafka-ui:
    image: 'provectuslabs/kafka-ui:v0.7.2'
    container_name: kafka-ui
    environment:
      KAFKA_CLUSTERS_0_NAME: "kafka-server"
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: "kafka-server:9092"
      DYNAMIC_CONFIG_ENABLED: "true"
      AUTH_TYPE: "LOGIN_FORM"
      SPRING_SECURITY_USER_NAME: "admin"
      SPRING_SECURITY_USER_PASSWORD: "123456"
    ports:
      - 8080:8080
    networks:
      - kafka-network

networks:
  kafka-network:
    driver: bridge
```

# 二、记录日志

`LoggerLogServiceImpl` 类中的 `save` 方法，将审计日志写入到文件中。

以 `loggerFormat` `text` 为例：

```text
id|parentId|名称|接口操作类型|操作信息|用户id|用户名|租户id|链路ID|执行时间|操作时间|应用名|类名|方法名|调用者ip|调用结果|请求方式|操作系统|浏览器|调用参数|调用返回结果|错误信息

1900372265887354881|0|人员查询|SELECT|{}|-|-|-|1697076390616154112|0|2023-08-31T10:38:36.508|ed-module-user|io.github.edynasty.module.user.controller.UserController|human|192.168.88.66|1|GET|-|Apifox/1.0.0 (https://apifox.com)|[]|{"id":130879473,"isSuccess":false,"retDate":"2023-08-31 10:38:36","retMsg":"查询人员表失败"}|-
```

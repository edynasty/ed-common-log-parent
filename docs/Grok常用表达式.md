

## 一、前言

Logstash 内置了120种默认表达式，可以查看patterns，里面对表达式做了分组，每个文件为一组，文件内部有对应的表达式模式。下面只是部分常用的。

## 二、常用表达式


| 表达式标识          | 名称                 | 详情                                                                                                                              | 匹配例子                                                                                       |
| ------------------- | -------------------- | --------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| USERNAME 或 USER    | 用户名               | 由数字、大小写及特殊字符(.\_-)组成的字符串                                                                                        | 1234、Bob、Alex.Wong                                                                           |
| EMAILLOCALPART      | 用户名               | 首位由大小写字母组成，其他位由数字、大小写及特殊字符(\_.+-=:)组成的字符串。注意，国内的QQ纯数字邮箱账号是无法匹配的，需要修改正则 | windcoder、windcoder\_com、abc-123                                                             |
| EMAILADDRESS        | 电子邮件             |                                                                                                                                   | [windcoder@abc.com](mailto:windcoder@abc.com)、windcoder\_com@gmail.com、abc-123@163.com       |
| HTTPDUSER           | Apache服务器的用户   | 可以是EMAILADDRESS或USERNAME                                                                                                      |                                                                                                |
| INT                 | 整数                 | 包括0和正负整数                                                                                                                   | 0、-123、43987                                                                                 |
| BASE10NUM 或 NUMBER | 十进制数字           | 包括整数和小数                                                                                                                    | 0、18、5.23                                                                                    |
| BASE16NUM           | 十六进制数字         | 整数                                                                                                                              | 0x0045fa2d、-0x3F8709                                                                          |
| WORD                | 字符串               | 包括数字和大小写字母                                                                                                              | String、3529345、ILoveYou                                                                      |
| NOTSPACE            | 不带任何空格的字符串 |                                                                                                                                   |                                                                                                |
| SPACE               | 空格字符串           |                                                                                                                                   |                                                                                                |
| QUOTEDSTRING 或 QS  | 带引号的字符串       |                                                                                                                                   | “This is an apple”、’What is your name?’                                                   |
| UUID                | 标准UUID             |                                                                                                                                   | 550E8400-E29B-11D4-A716-446655440000                                                           |
| MAC                 | MAC地址              | 可以是Cisco设备里的MAC地址，也可以是通用或者Windows系统的MAC地址                                                                  |                                                                                                |
| IP                  | IP地址               | IPv4或IPv6地址                                                                                                                    | 127.0.0.1、FE80:0000:0000:0000:AAAA:0000:00C2:0002                                             |
| HOSTNAME            | IP或者主机名称       |                                                                                                                                   |                                                                                                |
| HOSTPORT            | 主机名(IP)+端口      |                                                                                                                                   | 127.0.0.1:3306、[api.windcoder.com:8000](http://api.windcoder.com:8000/)                       |
| PATH                | 路径                 | Unix系统或者Windows系统里的路径格式                                                                                               | /usr/local/nginx/sbin/nginx、c:\\windows\\system32\\clr.exe                                    |
| URIPROTO            | URI协议              |                                                                                                                                   | http、ftp                                                                                      |
| URIHOST             | URI主机              |                                                                                                                                   | [windcoder.com](http://windcoder.com/)、10.0.0.1:22                                            |
| URIPATH             | URI路径              |                                                                                                                                   | [//windcoder.com/abc/、/api.php](https://windcoder.com/abc/%E3%80%81/api.php)                  |
| URIPARAM            | URI里的GET参数       |                                                                                                                                   | ?a=1&b=2&c=3                                                                                   |
| URIPATHPARAM        | URI路径+GET参数      | /windcoder.com/abc/api.php?a=1&b=2&c=3                                                                                            |                                                                                                |
| URI                 | 完整的URI            |                                                                                                                                   | [https://windcoder.com/abc/api.php?a=1&b=2&c=3](https://windcoder.com/abc/api.php?a=1&b=2&c=3) |
| LOGLEVEL            | Log表达式            | Log表达式                                                                                                                         | Alert、alert、ALERT、Error                                                                     |

## 三、日期时间表达式


| 表达式标识         | 名称              | 匹配例子                                   |
| ------------------ | ----------------- | ------------------------------------------ |
| MONTH              | 月份名称          | Jan、January                               |
| MONTHNUM           | 月份数字          | 03、9、12                                  |
| MONTHDAY           | 日期数字          | 03、9、31                                  |
| DAY                | 星期几名称        | Mon、Monday                                |
| YEAR               | 年份数字          |                                            |
| HOUR               | 小时数字          |                                            |
| MINUTE             | 分钟数字          |                                            |
| SECOND             | 秒数字            |                                            |
| TIME               | 时间              | 00:01:23                                   |
| DATE\_US           | 美国时间          | 10-01-1892、10/01/1892/                    |
| DATE\_EU           | 欧洲日期格式      | 01-10-1892、01/10/1882、01.10.1892         |
| ISO8601\_TIMEZONE  | ISO8601时间格式   | +10:23、-1023                              |
| TIMESTAMP\_ISO8601 | ISO8601时间戳格式 | 2016-07-03T00:34:06+08:00                  |
| DATE               | 日期              | 美国日期%{DATE\_US}或者欧洲日期%{DATE\_EU} |
| DATESTAMP          | 完整日期+时间     | 07-03-2016 00:34:06                        |
| HTTPDATE           | http默认日期格式  | 03/Jul/2016:00:36:53 +0800                 |

## 四、使用例子

### 4.1. 例1

```
[2020-08-22 12:25:51.441] [TSC_IHU] [ERROR] [c.e.c.t.i.t.s.IhuTsaUplinkServiceImpl] Activation/Bind uplink, query UserSession by Token failure!
```

grok配置：

```
\[%{TIMESTAMP_ISO8601:time}\]\s*%{DATA:thread}\s*\[%{LOGLEVEL:level}\]\s*%{GREEDYDATA:data}
```

输出结果如下：

```
{
  "data": "[c.e.c.t.i.t.s.IhuTsaUplinkServiceImpl] Activation/Bind uplink, query UserSession by Token failure!",
  "level": "ERROR",
  "time": "2020-08-22 12:25:51.441",
  "thread": "[TSC_IHU]"
}
```

### 4.2. 例2

```
2020-09-12 14:16:36.320+08:00 INFO 930856f7-c78f-4f12-a0f1-83a2610b2dfc DispatcherConnector ip-192-168-114-244 [Mqtt-Device-1883-worker-18-1] com.ericsson.sep.dispatcher.api.transformer.v1.MessageTransformer {"TraceID":"930856f7-c78f-4f12-a0f1-83a2610b2dfc","clientId":"5120916600003466K4GA1059","username":"LB37622Z3KX609880"}
```

grok配置：

```
%{TIMESTAMP_ISO8601:access_time}\s*%{LOGLEVEL:level}\s*%{UUID:uuid}\s*%{WORD:word}\s*%{HOSTNAME:hostname}\s*\[%{DATA:work}\]\s*(?<api>([\S+]*))\s*(?<TraceID>([\S+]*))\s*%{GREEDYDATA:message_data}
```

输出结果如下：

```
{
  "level": "INFO",
  "work": "Mqtt-Device-1883-worker-18-1",
  "uuid": "930856f7-c78f-4f12-a0f1-83a2610b2dfc",
  "hostname": "ip-192-168-114-244",
  "message_data": "",
  "TraceID": "{\"TraceID\":\"930856f7-c78f-4f12-a0f1-83a2610b2dfc\",\"clientId\":\"5120916600003466K4GA1059\",\"username\":\"LB37622Z3KX609880\"}",
  "api": "com.ericsson.sep.dispatcher.api.transformer.v1.MessageTransformer",
  "word": "DispatcherConnector",
  "access_time": "2020-09-12 14:16:36.320+08:00"
}
```

### 4.3. 例3

```
192.168.125.138 - - [12/Sep/2020:14:10:58 +0800] "GET /backend/services/ticketRemind/query?cid=&msgType=1&pageSize=100&pageIndex=1&langCode=zh HTTP/1.1" 200 91
```

grok配置：

```
 %{IP:ip}\s*%{DATA:a}\s*\[%{HTTPDATE:access_time}\]\s*%{DATA:b}%{WORD:method}\s*%{URIPATH:url}%{URIPARAM:param}\s*%{URIPROTO:uri}%{DATA:c}%{NUMBER:treaty}%{DATA:d}\s*%{NUMBER:status}\s*%{NUMBER:latency_millis}
```

输出结果如下：

```
{
  "a": "- -",
  "b": "\"",
  "c": "/",
  "method": "GET",
  "d": "\"",
  "ip": "192.168.125.138",
  "latency_millis": "91",
  "uri": "HTTP",
  "url": "/backend/services/ticketRemind/query",
  "param": "?cid=&msgType=1&pageSize=100&pageIndex=1&langCode=zh",
  "treaty": "1.1",
  "access_time": "12/Sep/2020:14:10:58 +0800",
  "status": "200"
}
```

### 4.4. 例4

```
[08/Nov/2020:11:40:24 +0800] tc-com.g-netlink.net - - 192.168.122.58 192.168.122.58 192.168.125.135 80 GET 200 /geelyTCAccess/tcservices/capability/L6T7944Z0JN427155 ?pageIndex=1&pageSize=2000&vehicleType=0 21067 17
```

grok配置：

```
\[%{HTTPDATE:access_time}\] %{DATA:hostname} %{DATA:username} %{DATA:fwd_for} %{DATA:remote_hostname} %{IP:remote_ip} %{IP:local_ip} %{NUMBER:local_port} %{DATA:method} %{DATA:status} %{DATA:uri} %{DATA:query} %{NUMBER:bytes} %{NUMBER:latency_ms}
```

输出结果如下：

```
{
  "method": "GET",
  "local_port": "80",
  "fwd_for": "-",
  "query": "?pageIndex=1&pageSize=2000&vehicleType=0",
  "remote_hostname": "192.168.122.58",
  "uri": "/geelyTCAccess/tcservices/capability/L6T7944Z0JN427155",
  "latency_ms": "17",
  "local_ip": "192.168.125.135",
  "hostname": "tc-com.g-netlink.net",
  "remote_ip": "192.168.122.58",
  "bytes": "21067",
  "access_time": "08/Nov/2020:11:40:24 +0800",
  "username": "-",
  "status": "200"
}
```

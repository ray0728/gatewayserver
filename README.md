![travis_ci](https://www.travis-ci.org/ray0728/gatewayserver.svg?branch=master)
# gatewayserver
## 说明
提供路由以及所有HTML5功能。
* 需配合以下Server同时使用
  * [AuthServer][1]
  * [AccountServer][2]
  * [ResourceServer][3]
* **尚未基线**


## ToDo
* 页面资源文件未整理
* js文件未压缩
* 未移除冗余文件

## 运行方式：  
application.properties中并**不包含**完整配置信息，所以**不支持**直接运行  
* java 方式

```java
java
-Djava.security.egd=file:/dev/./urandom                  \
-Dspring.cloud.config.uri=$CONFIGSERVER_URI              \
-Deureka.client.serviceUrl.defaultZone=$EUREKASERVER_URI \
-Dspring.kafka.bootstrap-servers=$KAFKA_URI              \
-Dspring.zipkin.base-url=$ZIPKIN_URI                     \
-jar target.jar
```
* docker 方式

```docker
gatewayserver:
    image: ray0728/gatewayserv:1.0
    ports:
      - "80:10006"
    environment:
      EUREKASERVER_PORT: "10001"
      CONFIGSERVER_PORT: "10002"
      ZIPKIN_PORT:  "9411"
      ACCOUNT_PORT: "10003"
      RESOURCE_PORT: "10005"
      CONFIGSERVER_URI:
      EUREKASERVER_URI:
      KAFKA_URI:
```  
关于Docker  
编译完成的Docker位于[Dockerhub][4]请结合Release中的[Tag标签][5]获取对应的Docker

[1]:https://github.com/ray0728/authserver
[2]:https://github.com/ray0728/accountserver
[3]:https://github.com/ray0728/resourceserver
[4]:https://hub.docker.com/r/ray0728/gatwayserv/tags
[5]:https://github.com/ray0728/gatewayserver/tags

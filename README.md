# 烽火云短信平台beacon-cloud

## 端口配置

|服务|端口|
|--|--|
|beacon-api|8081|
|beacon-cache|8082|
|beacon-strategy|8083|
|beacon-push|8084|
|beacon-search|8085|
|beacon-smsgateway|8086|




|环境|端口|
|--|--|
|mysql|3306|
|redis|6379|
|nginx|80|
|rabbitmq|5672/15672|
|nacos|8848|
|kibana|5601|
|elasticsearch|9200|
|jenkins|8080|

## 接口模块 beacon-api

主要流程：

1. 接收客户发送来的请求，如发送单条信息、发送多条信息等。
2. 使用SingleForm对象接收请求信息。根据请求的apikey，从缓存模块中读取客户的相关信息，封装StandardSubmit对象。
3. 将submit对象送至定义好的策略模式责任链中，依次进行校验，某一项校验失败便抛出对应异常(责任链中过滤器有自己的Bean名称，将需要用到的过滤器名称存放至配置中心，实现动态可插拔)。

4. 接口模块校验成功，将submit对象作为消息，发送至MQ队列中，等待策略模块消费。
5. 校验失败，直接响应失败信息给客户端。

### 参数校验

​	对于客户端发来的HTTP请求参数，接口模块需要校验其是否合法。

​	由于都是一些硬性要求，如不为空，数值范围约束等，可以使用Spring自带的JSR303去完成，但是Spring2.3xx已经不再内置，需要先引入其依赖。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

​	然后，在接收请求参数的类成员属性上，通过@NotBlank, @NotNull, @Range注解来自动校验其是否为空，以及是否符合范围。
并在controller方法接收参数对象时，通过@Validated注解来启用校验。校验结果会存放在BindingResult对象中，因此，方法
入参还要添加一个BindingResult对象。

如果参数校验中出现错误，可以通过`BindingResult#hasErrors`方法来判断，通过`getFieldError`来查看出错参数。

### 过滤器校验

​	采用策略模式，定义一个过滤器接口CheckFilter，并给出针对不同内容的过滤器实现类。将每个实现类都通过@Service注解注入到IoC容器中，并通过`value="apikey"`的方式，给SpringBean命名。

​	然后，将需要用到的过滤器实现类的名称，以filters属性，记录到application.yml文件中。在需要用到的场景，通过@Value注解从配置文件中拿取，即可获得需要执行的过滤器的名称字符串。

​	接着，从SpringIoC容器中获取到所有CheckFilter实现类Bean，存放至Map集合中。根据从配置文件中读取出来的名称，来一一拿到对应的CheckFilter实现类对象，并执行`check()`方法，完成过滤逻辑。

​	最后，结合@RefreshScope注解，实现filters属性动态刷新。就可以实现过滤器的动态可插拔。需要添加新的过滤器，只需要编写实现类并将SpringBean的名称添加到配置文件的filters中即可。去除过滤器或交换顺序，亦然。

定义的的过滤器有：

- apikey校验
- IP地址校验
- 手机号校验
- 签名校验
- 模板校验
- 费用余额校验

#### apikey校验

​	客户端发送的HTTP请求，请求体结构为：apikey + 短信内容 + 目标手机号 + 短信类型

```java
{
    "apikey": "887559db54d911edba520242ac120002",
    "text": "【惠普/HP】 您的验证码是 1234",
    "mobile": "15212312312",
    "state": 2 
}
```

​	每个客户都有自己唯一的apikey用来请求短信服务。当前过滤器根据HTTP请求中的apikey查询数据库(事先建立索引调高效率)，判断结果是否为空，如果为空证明apikey不合法；否则给submit对象中传入客户对应的clientId。

#### IP地址校验

​	不能只用apikey来识别客户，否则恶意第三方获取HTTP请求中的apikey后就可以使用短信平台服务了。于是，在客户信息中记录了IP地址白名单，只有通过这些IP地址来请求服务才被视为有效。

​	但是，客户请求先发送至Nginx服务器，然后转发给网关，接着才到达接口模块。如果不经设置，HTTP请求的IP地址就可能是网关或Nginx的IP地址。于是，通过Nginx的配置文件，给转发的请求头中添加客户端发送请求的IP地址字段。

​	当前过滤器先获取请求真实IP地址，然后查询数据库(当前clientId对应记录的ipAddress字段)，判断请求IP是否在白名单中，如果不是抛出异常；否则通过校验。

#### 手机号校验

​	通过正则表达式判断目标手机号是否为中国合法的移动手机号。如果手机号为空或不合法，抛出异常。

#### 签名校验

​	短信内容必须以`【签名】`开始。当前过滤器根据括号提取出签名内容，然后查询缓存模块，获取当前客户注册的签名集合，并判断短信签名是否在签名集合中。如果签名为空、不以固定格式开头、签名不合法都抛出异常。

#### 模板校验

​	客户注册时需要给定短信模板，可以有多个。当前模块先去掉短信内容的签名部分，获取内容部分。然后查询数据库，获取客户注册的模板集合，并判断短信内容是否符合模板。如果不符合抛出异常。

### 获取客户端IP

客户端的请求到达接口模块之前会经过大量的反向代理操作，至少会经过Nginx和网关模块，才能得到达接口模块。

为了让服务端能够获取都客户端的真实IP地址，需要进行如下的两步操作：

1. Nginx中通过配置，向给服务端转发的请求的请求头中，添加两个字段，记录客户端的真实IP地址，并且可以传递。
2. 服务端，通过字段名称获取IP地址，并考虑多种反向代理服务器。

> 注：有了Nginx后，所有的请求都要发送至http://{Nginx的IP}:80，然后，再由Nginx转发给配置好的Web服务器。

由于在获取客户端IP地址时，使用到了大量的魔法值，为了方便后期维护，将其全部记录在Nacos配置中心中。



## 缓存模块 beacon-cache

​	需要校验的各种内容，都需要先通过查询数据库表来获取信息，才能够进行校验。但是查询数据库的效率很低，因此引入缓存。

​	但是，多个模块都需要用到缓存，因此单独抽取出来一个缓存微服务。另外，如果直接在各个模块的业务逻辑中直接使用RedisTemplate来和缓存交互，一是代码重复，二是缓存发生变化，涉及到的所有模块都要修改。

### 编写配置类
RedisTemplate针对key和value的序列化方式都是字节数组`byte[]`，在图形化界面上不太友好。
希望通过配置将key做String序列化，value做JSON序列化。

RedisTemplate对象支持修改序列化器。对于key，我们直接使用redis提供的String序列化即可；对于value，由于其中包含Date
日期格式的属性，我们需要自己定义一个序列化类，并注入Spring，然后将其指定为value的序列化器。

### 存在的问题

1. 直接使用RedisTemplate对象，基于其api来操作redis数据库时，方法的实用性比较差。方法的命名方式和Redis中的命令差别很大。
2. 基于Java客户端与redis交互时，很多时候会采用@Cacheable注解。如果缓存模块需要基于@Cacheable注解来与Redis交互，需要
再次配置CacheManager，以及keyGenerator等配置信息。
3. 如果需要使用redis实现分布式锁，一般分布式客户端大多是采用Redisson，Redisson内部的watchDog以及一些锁重入操作封装的
比较完善。

我们直接利用飞马框架来解决这些问题，它针对上述问题都做了封装。

### 飞马框架安装

​	飞马框架对直接使用Redis中的各种问题都给出了相关配置，并对查询方法做了封装，更符合业务逻辑。

​	下载源码，通过maven install到本地maven仓库即可。



## 策略模块 beacon-strategy

主要流程：

1. 监听MQ中SMS_PRE_SEND队列，如果有消息就开始消费。
2. 获取消息中的submit对象，并送到基于策略模式的责任链中，依次进行校验(每个客户需要校验什么方面的信息，可以通过管理端来管理，因此将过滤器名称存储在客户信息的client_filters字段中)。
3. 如果责任链中任意一个过滤器校验失败，就分别发消息至SMS_PUSH_REPORT和SMS_WRITE_LOG消息队列，等待消费者给客户发状态报告和写日志。
4. 责任链校验成功，在路由过滤器中，根据客户绑定的通道信息，发送消息至对应通道的消息队列中，等待消费。

### 过滤器校验

定义的过滤器有：

- 客户级黑名单过滤器
- 全局级黑名单过滤器
- 敏感词过滤器
- 号段补全过滤器
- 携号转网过滤器
- 分钟限制过滤器
- 小时限制过滤器
- 余额过滤器
- 路由过滤器

#### 客户级黑名单过滤器

​	客户自己可以指定那些手机号为自己的黑名单，通过`black:clientId+phoneNumber`的格式存储在缓存中。当前过滤器从缓存中读取出当前客户的客户级黑名单集合，并判断目标手机号是否在黑名单中。

#### 全局级黑名单过滤器

​	全局黑名单中的手机号，对于所有客户来说都是黑名单手机号，不能发送。

#### 敏感词校验

​	将敏感词记录在数据库中，借助Hutool工具来实现DFA算法(底层原理是将所有的敏感词构建为一个敏感词树，类似于前缀树)。然后，查询短信内容中的敏感词集合。如果短信内容中有敏感词，则校验失败，先给SMS_WRITE_LOG队列发送消息，等待消费写日志，再抛出异常；如果不包含敏感词则校验通过。

#### 分钟限制过滤器

#### 小时限制过滤器

#### 路由过滤器

​	系统中，给每个客户绑定了一定数量的短信通道，存储在数据库中。当前过滤器首先查询缓存模块，获取客户绑定的通过通道信息。然后，根据权重选择当前可用的通道，并给网关模块监听的MQ的、通道ID对应的消息队列中发送消息，等待网关消费。



## 推送模块 beacon-push

​	策略模块中的过滤器如果无法正常校验通过过，就不需要继续校验了，而是直接抛出异常，并发送消息到MQ，让推送模块处理后续。

主要功能：

1. 监听SMS_PUSH_REPORT队列，有消息就消费。
2. 封装HTTP报文，给用户推送状态报告。

## 搜索模块 beacon-search

​	在短信发送失败后，需要将短信内容以及一些标识存储到ElasticSearch中。搜索模块监听消息队列，消费发送失败的短信消息，负责和ElasticSearch交互，并提供对外的搜索功能。

> ​	短信平台的短信量是非常大的，因为它就是这种走量的模式，整体设计是基于一年12亿条的。传统的MySQL、Oracle等关系型数据无法满足需求。ElasticSearch存储功能十分强大。
>
> ​	且ElasticSearch内部提供了非常丰富的聚合函数，方便统计海量数据，前端可以轻松生成各种报表。

​	因为数据量比较大，不能将所有的数据都存储在一个索引中。这里采用一年构建一个索引的方式。

​	索引确定后，主从分片也要考虑。ElasticSearch在本业务场景下，是写多读少的情况。需要设置多一些的主分片，用于写操作，设置存储100G左右数据(一年12亿条，一条假设为1KB，一年大概需要存储1.12TB数据，12个分片就可以存储所有的数据)。设置15个主分片，一个从分片，支持动态扩展。

### 索引准备

​	需要构建Elasticsearch中的索引信息，索引中存储的内容就是StandardSubmit对象中的全部信息。

​	首先，给ElasticSearch安装ik分词器。

```
docker exec -it elasticsearch bash
cd bin
./elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.6.2/elasticsearch-analysis-ik-7.6.2.zip
```



​	通过Kibana界面构建索引，并指定短信内容的分词器：

```json
PUT /sms_submit_log_2024
{
    "settings" : {
      "number_of_shards" : 15, 
      "number_of_replicas" : 1
    },
    "mappings": {
      "properties": {
        "clientId": {
          "type": "long"
        },
        "ip": {
          "type": "ip"
        },
        "uid": {
          "type": "keyword"
        },
        "mobile": {
          "type": "text"
        },
        "sign": {
          "type": "keyword"
        },
        "text": {
          "type": "text",
          "analyzer": "ik_max_word"
        },
        "sendTime": {
          "type":   "date",
          "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
        },
        "fee": {
          "type": "long"
        },
        "operatorId": {
          "type": "integer"
        },
        "areaCode": {
          "type": "integer"
        },
        "area": {
          "type": "text"
        },
        "srcNumber": {
          "type": "text"
        },
        "channelId": {
          "type": "long"
        },
        "reportState": {
          "type": "integer"
        },
        "errorMsg": {
          "type": "text"
        },
        "realIP": {
          "type": "ip"
        },
        "apikey": {
          "type": "text"
        },
        "state": {
          "type": "integer"
        },
        "signId": {
          "type": "long"
        },
        "isTransfer": {
          "type": "boolean"
        },
        "oneHourLimitMilli": {
          "type": "date",
          "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
        }
      }
    }
}
```



## 短信网关模块 beacon-smsgateway

​	短信网关模块的功能就是将经过策略模块校验完成的短信发送出去。

​	发送短信可以和三大运营商对接，也可以和一些第三方平台对接，如阿里大鱼、亿美短信平台等。无论和谁对接，只需要根据平台给出的API和协议，做出对应的对接工作即可。

主要流程：

1. 监听MQ，如果收到策略模块发送的消息，就开始消费。
2. 封装Netty客户端，通过Netty将短信发送至运营商。运营商不会立即响应当前短信发送成功还是发送失败，而是先返回一个响应告知其已经收到了发送请求。此时，将短信的状态修改为`发送中`，并调用搜索模块将当前短信持久化到ES中。
3. 当运营商发送完短信后，会通知短信模块，告知发送成功还是发送失败。此时，短信模块需要调用搜索模块将短信对应的信息作出修改，更改短信的状态。并判断用户是否需要状态报告，决定是否做出推送。

> ​	由于会两次对ES中的同一条数据进行写操作，因此，要保证修改操作在写入操作之后进行。通过两方面来保证：
>
> - 修改操作的消息，可以基于死信队列做延迟处理。
> - 修改操作执行前，先查询数据是否存在，存在才能修改，否则不修改。


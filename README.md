# beacon-cloud

## 接口模块 beacon-api

### 过滤器校验

采用策略模式，定义一个过滤器接口CheckFilter，并给出针对不同内容的过滤器实现类。将每个实现类都通过@Service注解注入到IoC容器中，
并通过`value="apikey"`的方式，给SpringBean命名。

然后，将需要用到的过滤器实现类的名称，以filters属性，记录到application.yml文件中。在需要用到的场景，通过@Value注解从配置文件中拿取，
即可获得需要执行的过滤器的名称字符串。

接着，从SpringIoC容器中获取到所有CheckFilter实现类Bean，存放至Map集合中。根据从配置文件中读取出来的名称，来一一拿到
对应的CheckFilter实现类对象，并执行`check()`方法，完成过滤逻辑。

最后，结合@RefreshScope注解，实现filters属性动态刷新。就可以实现过滤器的动态可插拔。需要添加新的过滤器，只需要编写实现类
并将SpringBean的名称添加到配置文件的filters中即可。去除过滤器或交换顺序，亦然。

### 参数校验

对于客户端发来的请求参数，接口模块需要校验其是否合法。

可以使用Spring自带的JSR303去完成，但是Spring2.3xx已经不再内置，需要先引入其依赖。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

然后，在接收请求参数的类成员属性上，通过@NotBlank, @NotNull, @Range注解来自动校验其是否为空，以及是否符合范围。
并在controller方法接收参数对象时，通过@Validated注解来启用校验。校验结果会存放在BindingResult对象中，因此，方法
入参还要添加一个BindingResult对象。

如果参数校验中出现错误，可以通过`BindingResult#hasErrors`方法来判断，通过`getFieldError`来查看出错参数。

### 查询缓存

需要校验的各种内容，都需要先通过查询数据库表来获取信息，才能够进行校验。但是查询数据库的效率很低，因此引入缓存。

但是，多个模块都需要用到缓存，因此单独抽取出来一个缓存微服务。另外，如果直接在各个模块的业务逻辑中直接使用RedisTemplate
来和缓存交互，一是代码重复，二是缓存发生变化，涉及到的所有模块都要修改。

## 缓存模块

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

### 获取客户端IP

客户端的请求到达接口模块之前会经过大量的反向代理操作，至少会经过Nginx和网关模块，才能得到达接口模块。

为了让服务端能够获取都客户端的真实IP地址，需要进行如下的两步操作：
1. Nginx中通过配置，向给服务端转发的请求的请求头中，添加两个字段，记录客户端的真实IP地址，并且可以传递。
2. 服务端，通过字段名称获取IP地址，并考虑多种反向代理服务器。

> 注：有了Nginx后，所有的请求都要发送至http://{Nginx的IP}:80，然后，再由Nginx转发给配置好的Web服务器。

由于在获取客户端IP地址时，使用到了大量的魔法值，为了方便后期维护，将其全部记录在Nacos配置中心中。



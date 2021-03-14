 

# 微服务架构：Spring-Cloud

## 什么是微服务？

​	微服务就是把原本臃肿的一个项目的所有模块拆分开来并做到互相没有关联，甚至可以不使用同一个数据库。  比如：项目里面有User模块和Power模块，但是User模块和Power模块并没有直接关系，仅仅只是一些数据需要交互，那么就可以吧这2个模块单独分开来，当user需要调用power的时候，power是一个服务方，但是power需要调用user的时候，user又是服务方了， 所以，他们并不在乎谁是服务方谁是调用方，他们都是2个独立的服务，这时候，微服务的概念就出来了。



## 经典问题:微服务和分布式的区别

​	谈到区别，我们先简单说一下分布式是什么，

1. **什么是分布式？**

   所谓分布式，就是将偌大的系统划分为多个模块(这一点和微服务很像)部署到不同机器上(因为一台机器可能承受不了这么大的压力或者说一台非常好的服务器的成本可能够好几台普通的了)，各个模块通过接口进行数据交互，其实分布式也是一种微服务。

2. 因为都是把模块拆分开来变为独立的单元，提供接口来调用，那么**他们本质的区别在哪呢？** 

   他们的区别主要体现在“目标”上， 何为目标，就是你这样架构项目要做到的事情。

3. **分布式的目标是什么？**

   我们刚刚也看见了， 就是一台机器承受不了的，或者是成本问题 ， 不得不使用多台机器来完成服务的部署， 而微服务的目标 只是让各个模块拆分开来，不会被互相影响，比如模块的升级亦或是出现BUG等等... 

讲了这么多，可以用一句话来理解：分布式也是微服务的一种，而微服务他可以是在一台机器上。

 

## 微服务与Spring-Cloud的关系（区别）

​	微服务只是一种项目的架构方式，或者说是一种概念，就如同我们的`MVC`架构一样， 那么Spring-Cloud便是对这种技术的实现。



## 微服务一定要使用Spring-Cloud吗？

​	我们刚刚说过，微服务只是一种项目的架构方式，如果你足够了解微服务是什么概念你就会知道，其实微服务就算不借助任何技术也能实现，只是有很多问题需要我们解决罢了例如：负载均衡，服务的注册与发现，服务调用，路由。。。。等等等等一系列问题，所以,Spring-Cloud 就出来了，Spring-Cloud将处理这些问题的的技术全部打包好了，就类似那种开袋即食的感觉。。。 

- **微服务要面临的问题：**

1. 监听服务有没有宕机
2. 负载均衡
3. **熔断：**请求进入微服务，然后微服务调用另外的微服务，接着往下调用，如果其中一个微服务宕机了，那么请求就会卡在那里，若果还有其他客户端发送请求，就会接连不断的卡在那里，最终导致CPU没有办法分配给其他请求处理，导致整个系统宕机
4. 限流
5. 降级
6. 网关，路由，过滤
7. 服务注册与发现

- spring Cloud就是能够解决上述微服务可能面对的问题解决方法的技术总结包装

1. eureka，`zk` :  解决 服务注册与发现监听，服务有没有宕机
2. `zuul`     ：解决 网关，路由，过滤
3. `hystrix` ：解决 熔断，限流，降级
4. `nginx` ，`ribbon` ：解决 负载均衡 



# Spring-Cloud项目的搭建

### 1. 知识点：

因为spring-cloud是基于spring-boot项目来的，所以我们项目得是一个spring-boot项目，至于spring-boot项目，这节我们先不讨论，这里要注意的一个点是spring-cloud的版本与spring-boot的版本要对应下图：



1. spring cloud的版本使用英文字母做区分的，spring framework版本使用 数字.数字.数字作为版本号

   因为 spring cloud 的组件非常的多

2. spring cloud 与 spring boot的版本不匹配会报以下错误：



### 2. 此处构建的spring cloud项目版本如下所示：

spring-boot：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.2.RELEASE</version>
</parent>
```

spring-cloud:

```xml
<dependencyManagement>        
    <dependencies>            
        <dependency>                
            <groupId>org.springframework.cloud</groupId>                 
            <artifactId>spring-cloud-dependencies</artifactId>                 
            <version>Finchley.SR2</version>                
            <type>pom</type>                
            <scope>import</scope>         
        </dependency>        
    </dependencies>     
</dependencyManagement>
```

当你项目里面有这些依赖之后，你的spring cloud项目已经搭建好了(初次下载spring-cloud可能需要一点时间)

### 3. 项目源码：

#### 3.1 user微服务

1. 依赖：

   ```xml
   <dependencies>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
   </dependencies>
   ```

2. 启动类

   ```java
   package com.study;
   
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   
   @SpringBootApplication
   public class AppUserClient {
   
       public static void main(String[] args) {
           SpringApplication.run(AppUserClient.class);
       }
   
   }
   ```

3. 配置类

   ```java
   package com.study.config;
   
   import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.client.RestTemplate;
   
   @Configuration
   public class AppConfig {
   
       @Bean
       public RestTemplate createRestTemplate() {
           return new RestTemplate();
       }
   
       /**
        * 通过 TomcatServletWebServerFactory 配置tomcat容器的端口号
        * @return
        */
       @Bean
       public TomcatServletWebServerFactory createTomcatServletWebServerFactory() {
           TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
           tomcat.setPort(5000);
           return tomcat;
       }
   
   }
   ```

4. 配置controller类

   ```java
   package com.study.controller;
   
   import com.study.util.R;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;
   import org.springframework.web.client.RestTemplate;
   
   import java.util.HashMap;
   import java.util.Map;
   
   @RestController
   public class UserController {
   
       /**
        * spring开发的 org.springframework.web.client.RestTemplate 类，专门又来调用其他微服务使用的
        */
       @Autowired
       RestTemplate restTemplate;
   
       @RequestMapping("/getUser.do")
       public R getUser(){
           Map<String, Object> map = new HashMap<>();
           map.put("key", "user");
           return R.success("返回成功");
       }
   
       @RequestMapping("/getPower.do")
       public R getPower(){
   //        Map<String, Object> map = new HashMap<>();
   //        map.put("key1", "value1");
   //        map.put("key2", "value1");
   //        R.success().set("key1", "value1").set("key2", "value2");
   
           // 通过 http协议 完成服务之间的调用
   //        return R.success("操作成功",restTemplate.getForObject("http://localhost:6000/getPower.do", Object.class));
           return R.success("操作成功",restTemplate.getForObject("http://localhost:80/getPower.do", Object.class));
   
       }
   
   }
   ```

#### 3.2 power微服务

1. 启动类

   ```java
   package com.study;
   
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   
   @SpringBootApplication
   public class AppPowerServer {
       public static void main(String[] args) {
           SpringApplication.run(AppPowerServer.class);
       }
   }
   ```

2. 配置类

   ```java
   package com.study.config;
   
   import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   
   @Configuration
   public class AppConfig {
   
       @Bean
       public TomcatServletWebServerFactory CreateTomcatServletWebServerFactory() {
           TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
           tomcat.setPort(6000);
           return tomcat;
       }
   
   }
   ```

3. controller类

   ```java
   package com.study.controller;
   
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;
   
   import java.util.HashMap;
   import java.util.Map;
   
   @RestController
   public class PowerController {
   
       @RequestMapping("/getPower.do")
       public Object getPower() {
           Map<String, Object> map = new HashMap<>();
           map.put("key1", "power");
           return map;
       }
   
   }
   ```

#### 3.3 父类配置依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.study</groupId>
    <artifactId>spring-cloud</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.2.RELEASE</version>
    </parent>

    <modules>
        <module>user</module>
        <module>order</module>
        <module>power</module>
    </modules>

</project>
```

### 4. 集群

- 情景：

  power微服务 能够承载的并发量是20 W，然后做活动，结果有了30 W的并发量，此时power微服务吃不消

- 解决办法：

  申请一台服务器，或者搭一个集群

- 集群概念：

  把相同的服务（代码相同）部署在不同的机器上面

- **使用`nginx`完成服务代理，实现负载均衡**

  1. 一开始，只有一个power微服务，所以直接调用

  2. 现在为了解决并发量过高一个power微服务吃不消问题，使用了 `nginx` ; 现在 请求先经过 `nginx` 服务进行请求代理

     ![](images\nginx\nginx服务反向代理.png)

  3. `nginx.conf`的配置

     ```
     worker_processes  1;
     
     events {
         worker_connections  1024;
     }
     
     http {
         include       mime.types;
         default_type  application/octet-stream;
         
         sendfile        on;
     
         keepalive_timeout  65;
     
     	upstream mServer {
     		server localhost:6000;
     		server localhost:6001;
     	}
     	
         server {
             listen       80;
             server_name  localhost;
     
             location / {
                 root   html;
                 index  index.html index.htm;
     			proxy_pass http://mServer;
             }
     
             error_page   500 502 503 504  /50x.html;
             location = /50x.html {
                 root   html;
             }
     
         }
     }
     ```

  4. 启动 `nginx` ，进入根目录，执行 `start nginx.exe`

     会出现一闪而过的画面，那就是启动了没有问题



# Spring-Cloud组件：

## eureka：

### 1. eureka是什么？

eureka是 `Netflix` 的子模块之一（`Netflix`公司开发的），也是一个核心的模块，eureka里有2个组件。

- `EurekaServer`：(一个独立的项目) 用于定位服务以实现中间层服务器的负载平衡和故障转移

- `EurekaClient`：(我们的微服务) 用于与Server交互的，可以使得交互变得非常简单，只需要通过服务标识符即可拿到服务

### 2. eureka与spring-cloud的关系：

Spring Cloud 封装了 `Netflix` 公司开发的 Eureka 模块来**实现服务注册和发现** (可以对比`Zookeeper`)

- Eureka 采用了 C-S 的设计架构。Eureka Server 作为服务注册功能的服务器，它是服务注册中心

- 而系统中的其他微服务，使用 Eureka 的客户端连接到 Eureka Server并维持心跳连接

这样一来，系统的维护人员就可以通过 Eureka Server 来监控系统中各个微服务是否正常运行；

Spring Cloud 的一些其他模块（比如`Zuul`）就可以通过 Eureka Server 来发现系统中的其他微服务，并执行相关的逻辑。

### 3. eureka的角色关系图：

![](images\spring cloud\eureka.png)

 

### 4. eureka如何使用？

1. 在spring-cloud项目里面加入依赖：

-  eureka 客户端：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

-  eureka 服务端：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

2.  eureka服务端项目里面加入以下配置：

```yml
server:
  port: 3000
eureka:
  server:
    # enable-self-preservation: false 表示 关闭自我保护机制
    enable-self-preservation: false
    # eviction-interval-timer-in-ms: 4000 表示 设置清理时间间隔（单位：毫秒，默认是60*1000）
    eviction-interval-timer-in-ms: 4000
  instance:
    hostname: localhost

  client:
    # 表示不把自己作为一个客户端注册到自己身上
    registerWithEureka: false
    # 表示 不需要从服务端获取注册信息（因为在这里自己就是服务端，而且已经禁用了注册自己了）
    fetchRegistry: false
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka
```

3.  然后在spring-boot启动项目上，加入注解: **<font color=red>@EnableEurekaServer</font>** 就可以启动项目了

```java
 @EnableEurekaServer
 @SpringBootApplication
 public class AppEureka {
   public static void main(String[] args) {
     SpringApplication.run(AppEureka.class);
   }     
 }
```

 如果看见这个图片，那么说明你就搭建好了:



这个警告只是说你把他的自我保护机制关闭了

4. eureka客户端配置

```yml
server:
  port: 6000
 eureka:
  client:
   serviceUrl:
     defaultZone: http://localhost:3000/eureka/ #eureka服务端提供的注册地址 参考服务端配置的这个路径
  instance:

   instance-id: power-1 #此实例注册到eureka服务端的唯一的实例ID 
   prefer-ip-address: true #是否显示IP地址
   leaseRenewalIntervalInSeconds: 10 #eureka客户需要多长时间发送心跳给eureka服务器，表明它仍然活着,默认为30 秒 (与下面配置的单位都是秒)
   leaseExpirationDurationInSeconds: 30 #Eureka服务器在接收到实例的最后一次发出的心跳后，需要等待多久才可以将此实例删除，默认为90秒

 spring:
  application:
   name: server-power #此实例注册到eureka服务端的name 
```

4. 



然后在客户端的spring-boot启动项目上 加入注解:@EnableEurekaClient 就可以启动项目了 这里就不截图了我们直接来看效果图：

![img](file:///C:/Users/Allen/AppData/Local/Temp/msohtmlclip1/01/clip_image008.png)

 

这里我们能看见 名字叫server-power的（图中将其大写了） id为 power-1的服务 注册到我们的Eureka上面来了 至此，一个简单的eureka已经搭建好了。

###  5. eureka的自我保护机制

- 情景：

  Eureka客户端微服务还在正常工作，但是由于网络波动，导致发送给Eureka服务端的心跳信息出现了问题，此时Eureka服务端不会将Eureka客户端微服务删除掉

- 原因：

  Eureka的自我保护机制，当服务端未接受到客户端的心跳信息时，Eureka服务端微服务会进行判断。如果在服务端微服务上注册的所有客户端微服务中，15分钟内有85%的心跳信息没能接受到，会先查看自己的网络是否有问题
  
- 自我保护机制内容：


  1. Eureka 不再从注册列表中移除因为长时间没有收到心跳而过期的服务

  2. Eureka 仍然能够接收新服务的注册和查询请求，但是不会被同步到其它节点上（即保证当前节点可用）

  3. 当网络稳定后，当前实例新的注册信息会被同步到其它节点中



### 6. eureka集群

#### 6.1 eureka集群原理

服务启动后向Eureka注册，Eureka Server会将注册信息向其他Eureka Server进行同步，当服务消费者要调用服务提供者，则向服务注册中心获取服务提供者地址，然后会将服务提供者地址缓存在本地，下次再调用时，则直接从本地缓存中取，完成一次调用。

#### 6.2 eureka集群配置

 刚刚我们了解到 Eureka Server会将注册信息向其他Eureka Server进行同步 那么我们得声明有哪些server呢？

这里 假设我们有3个Eureka Server 如图：



现在怎么声明集群环境的server呢？ 我们看一张图：

![](images\spring cloud\集群环境设置.png)

可能看着有点抽象，我们来看看具体配置

```yml
server:
  port: 3000

eureka:
  server:
    # 表示 关闭自我保护机制
    enable-self-preservation: false
    # 表示 设置清理时间间隔（单位：毫秒，默认是60*1000）
    eviction-interval-timer-in-ms: 4000
  instance:
    hostname: eureka3000.com
    instance-id: eureka3000

  client:
    # 表示不把自己作为一个客户端注册到自己身上
    registerWithEureka: false
    # 表示 不需要从服务端获取注册信息（因为在这里自己就是服务端，而且已经禁用了注册自己了）
    fetchRegistry: false
    serviceUrl:
      # 其他微服务客户端(EurekaClient) 注册到 微服务的服务端(EurekaServer)时，微服务客户端的地址就是下面这个defaultZone
      # 服务端配置集群
      defaultZone: http://eureka3001.com:3001/eureka,http://eureka3002.com:3002/eureka

spring:
  application:
    name: eureka3
```

这里为了 方便理解集群，我们做了一个域名的映射(条件不是特别支持我使用三台笔记本来测试。。。)，至于域名怎么映射的话，这里简单提一下吧。修改 `C:\Windows\System32\drivers\etc` 目录下的 hosts文件：

- <img src="images\spring cloud\修改主机映射.png" style="zoom: 50%;" />

- <img src="images\spring cloud\hosts内容.png" style="zoom:50%;" />

我们回到主题， 我们发现 **集群配置与单体不同的点在于 原来是把服务注册到自己身上，而现在是注册到其它服务身上。**

至于为什么不注册自己了呢？回到最上面我们说过，eureka的server会把自己的注册信息与其他的server同步，所以这里我们不需要注册到自己身上，因为另外两台服务器会配置本台服务器。(这里可能有点绕，可以参考一下刚刚那张集群环境的图，或者自己动手配置一下，另外两台eureka的配置与这个是差不多的，就不发出来了，只要注意是注册到其他的服务上面就好了)

当三台eureka配置好之后，全部启动一下就可以看见效果了:



当然，我们这里仅仅是把服务端配置好了， 那客户端怎么配置呢？ 话不多说，上代码：

```yml
 client:
   serviceUrl:
     defaultZone: http://localhost:3000/eureka/,http://eureka3001.com:3001/eureka,http://eureka3002.com:3002/eureka
```

我们这里只截取了要改动的那一部分。 就是 原来是注册到那一个地址上面，现在是要写三个eureka注册地址，但是不是代表他会注册三次，因为我们eureka server的注册信息是同步的，这里只需要注册一次就可以了，但是为什么要写三个地址呢。因为这样就可以做到高可用的配置：打个比方有3台服务器。但是突然宕机了一台， 但是其他2台还健在，依然可以注册我们的服务，换句话来讲， 只要有一台服务还建在，那么就可以注册服务，这里 需要理解一下。

这里效果图就不发了， 和之前单机的没什么两样，只是你服务随便注册到哪个eureka server上其他的eureka server上都有该服务的注册信息。

 

## CAP定理的含义：

![](images\spring cloud\CAP.png)

1998年，加州大学的计算机科学家 Eric Brewer 提出，分布式系统有三个指标：

- **Consistency                       ---                 一致性**
-  **Availability                        ---                 可用性**
-  **Partition tolerance          ---              分区容错性**

他们第一个字母分别是C,A,P

Eric Brewer 说，这三个指标不可能同时做到，这个结论就叫做 CAP 定理。

### 1. Partition tolerance

- 中文叫做 "**分区容错**"
- 大多数分布式系统都分布在多个子网络。每个子网络就叫做一个区（partition）。分区容错的意思是，区间通信可能失败。比如，一台服务器放在本地，另一台服务器放在外地（可能是外省，甚至是外国），这就是两个区，它们之间可能无法通信。
- ![](images\spring cloud\分区容错性.png)

 

- 上图中，`S1 `和 `S2` 是两台跨区的服务器。`S1` 向 `S2` 发送一条消息，`S2` 可能无法收到。系统设计的时候，必须考虑到这种情况。

- **一般来说，分区容错无法避免，因此可以认为 CAP 的 P 总是成立。**CAP 定理告诉我们，剩下的 C 和 A 无法同时做到。

###  2. Consistency

- Consistency 中文叫做"一致性"。
- 意思是，写操作之后的读操作，必须返回该值。举例来说，某条记录是 v1，用户向 `S1` 发起一个写操作，将其改为 v0
- ![](images\spring cloud\一致性.png)

- 接下来用户读操作就会得到 v0，这就是一致性。

### 3. Availability

- Availability 中文叫做"可用性"
- 意思是，只要收到用户的请求，服务器就必须给出回应

- 用户可以选择向 `S1` 或 `S2` 发起读操作。不管是哪台服务器，只要收到请求，就必须告诉用户，到底是 v0 还是 v1，否则就不满足可用性

### 4. Consistency 和 Availability 的矛盾

- 一致性和可用性，为什么不可能同时成立？答案很简单，因为可能通信失败（即出现分区容错）；

- 如果保证 `S2` 的一致性，那么 `S1` 必须在写操作时，锁定 `S2` 的读操作和写操作。只有数据同步后，才能重新开放读写。锁定期间，`S2` 不能读写，没有可用性；

- 如果保证 `S2` 的可用性，那么势必不能锁定 `S2`，所以一致性不成立。

综上所述，`S2` 无法同时做到一致性和可用性。系统设计时只能选择一个目标。如果追求一致性，那么无法保证所有节点的可用性；如果追求所有节点的可用性，那就没法做到一致性。

 

## eureka 对比 `Zookeeper`

`Zookeeper` 在设计的时候遵循的是 `CP` 原则，即一致性，`Zookeeper` 会出现这样一种情况，当master节点因为网络故障与其他节点失去联系时剩余节点会重新进行leader选举，问题在于，选举leader的时间太长：30~120s，且选举期间整个 `Zookeeper` 集群是不可用的，这就导致在选举期间注册服务处于瘫痪状态，在云部署的环境下，因网络环境使 `Zookeeper` 集群失去master节点是较大概率发生的事情，虽然服务能够最终恢复，但是漫长的选举时间导致长期的服务注册不可用是不能容忍的。

Eureka 在设计的时候遵循的是 AP 原则，即可用性。Eureka各个节点（服务)是平等的， 没有主从之分，几个节点down掉不会影响正常工作，剩余的节点（服务） 依然可以提供注册与查询服务，而Eureka的客户端在向某个Eureka 注册或发现连接失败，则会自动切换到其他节点，也就是说，只要有一台Eureka还在，就能注册可用（保证可用性）， 只不过查询到的信息不是最新的（不保证强一致），除此之外，Eureka还有自我保护机制，如果在15分钟内超过85%节点都没有正常心跳，那么eureka就认为客户端与注册中心出现了网络故障，此时会出现以下情况:

1. Eureka 不再从注册列表中移除因为长时间没有收到心跳而过期的服务。

2. Eureka 仍然能够接收新服务的注册和查询请求，但是不会被同步到其它节点上（即保证当前节点可用）

3. 当网络稳定后，当前实例新的注册信息会被同步到其它节点中
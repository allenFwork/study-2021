# Spring-Cloud组件：

## ribbon:

### 1. ribbon是什么?

Spring Cloud Ribbon是基于Netflix Ribbon实现的一套客户端负载均衡的工具。

简单的说，Ribbon是Netflix发布的开源项目，主要功能是提供客户端的软件负载均衡算法，将Netflix的中间层服务连接在一起。Ribbon客户端组件提供一系列完善的配置项如连接超时，重试等。简单的说，就是在配置文件中列出Load Balancer（简称LB）后面所有的机器，Ribbon会自动的帮助你基于某种规则（如简单轮询，随机连接等）去连接这些机器。我们也很容易使用Ribbon实现自定义的负载均衡算法。

### 2. 客户端负载均衡？？ 服务端负载均衡?? 

 我们用一张图来描述一下这两者的区别

![](images\spring cloud\负载均衡原理.png)

这篇文章里面不会去解释nginx，如果不知道是什么的话，可以先忽略， 先看看这张图

- 服务端的负载均衡是一个 url 先经过一个代理服务器（这里是nginx），然后通过这个代理服务器通过算法（轮询，随机，权重等等 ..）反向代理你的服务，来完成负载均衡

- 客户端的负载均衡则是一个请求在客户端的时候已经声明了要调用哪个服务，然后通过具体的负载均衡算法来完成负载均衡

### 3. 如何使用:

1. ribbon 的依赖

- 首先，要引入依赖，但是，eureka已经把 ribbon 集成到他的依赖里面去了，所以这里不需要再引用ribbon的依赖，如图：



2. **添加 @LoadBalanced 注释**

- ```java
  package com.study.config;
  
  @Configuration
  public class AppConfig {
  
      @Bean
      @LoadBalanced // 使用ribbon组件实现客户端负载均衡
      public RestTemplate createRestTemplate() {
          return new RestTemplate();
      }
  
  }
  ```

- 在 RestTemplate 上面加入 @LoadBalanced 注解，这样子就已经有了负载均衡

3. 测试


​	3.1 先启动了eureka集群(3个eureka) 和 Power集群(2个power)  和一个服务调用者(User)



​	3.2 

但是 User 仅仅只需要调用服务，不需要注册服务信息，所以需要改一下配置文件：

```yml
server:
  port: 5000
 eureka:

  client:
   registerWithEureka: false
   serviceUrl:
     defaultZone: http://localhost:3000/eureka/,http://eureka3001.com:3001/eureka,http://eureka3002.com:3002/eureka
```

​	3.3 然后启动

我们能看见 微服务名:SERVER-POWER 下面有2个微服务（power-1,power2），现在我们来通过微服务名调用这个服务

这是我们的user项目的调用代码 ：

```java
private static final String URL="http://SERVER-POWER";

 @Autowired
 private RestTemplate restTemplate;

 @RequestMapping("/power.do")
 public Object power(){
   return restTemplate.getForObject(URL+"/power.do",Object.class);
 }
```



### 4. 核心组件：IRule

- IRule是什么? 

  它是Ribbon对于负载均衡策略实现的接口，实现这个接口，就能自定义负载均衡策略。


- IRule默认的实现

  ![](images\spring cloud\IRule的默认实现.png)

  默认使用 轮询 策略

- 如何使用

  在客户端微服务的配置类中配置对应的策略：

  ```java
  package com.study.config;
  
  @Configuration
  public class AppConfig {
  
      @Bean
      @LoadBalanced // 使用ribbon组件实现客户端负载均衡
      public RestTemplate createRestTemplate() {
          return new RestTemplate();
      }
  
      /**
       * 修改负载均衡策略
       * 默认使用轮询策略
       * @return
       */
      @Bean
      public IRule createIRule() {
          // 使用随机策略
  //        return new RandomRule();
          // 使用 RetryRule 策略，默认轮询，连续调用某个微服务3-4次出现问题，就将其清除掉，不再调用
          return new RetryRule();
      }
  
  }
  ```

  

 在Spring 的配置类里面把对应的实现作为一个Bean返回出去就行了。

### 5. 自定义负载均衡策略：

我们刚刚讲过，只要实现了IRule就可以完成自定义负载均衡，至于具体怎么来，我们先看看他默认的实现

```java
package com.study.util;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.Random;

public class CustomerRule extends AbstractLoadBalancerRule {

    Random random;

    public CustomerRule() {
        random = new Random();
    }

    /**
     * 伪随机，当一个下标(微服务) 连续被调用两次，第三次如果还是它，那么就再随即一次
     */
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;
        while (server == null) {
            /**
             * 判断当前线程是否处于中断状态
             */
            if (Thread.interrupted()) {
                return null;
            }

            // 返回所有 没出问题的 server组成的List
            List<Server> upList = lb.getReachableServers();

            // 返回所有的 server组成的List（包括出问题的）
            List<Server> allList = lb.getAllServers();

            int serverCount = allList.size();
            if (serverCount == 0) {
                /**
                 * 一个微服务也没有，直接返回 null
                 */
                return null;
            }

            int index = random.nextInt(serverCount);
            /**
             * 从 allList 中获取 server,可能获取有问题的server
             * 不能从upList中拿server,因为某些有问题的server可能又能够使用了，不能直接就再也不用它了
             */
            server = upList.get(index);

            if (server == null) {
                /**
                 * 当前线程让出 CPU
                 */
                Thread.yield();
                continue;
            }

            /**
             * 如果server 是个正常的 server
             */
            if (server.isAlive()) {
                return (server);
            }

            /**
             * 不应该执行下面的代码
             */
            server = null;
            Thread.yield();
        }

        return server;
    }

    @Override
    public Server choose(Object o) {
        return null;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

}
```

这里我们能发现，还是我们上面所说过的 实现了IRule就能够自定义负载均衡即使是他默认的策略也实现了IRule

我们可以直接把代码copy过来改动一点:

```java
package com.study.util;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.Random;

public class CustomerRule extends AbstractLoadBalancerRule {

    Random random;

    // 当前下标,设置默认值为-1,否则为0
    private int nowIndex = -1;
    // 上一次下标
    private int lastIndex = -1;
    // 跳过的下标
    private int skipIndex = -1;

    public CustomerRule() {
        random = new Random();
    }

    /**
     * 第一种选择策略：
     * 伪随机，当一个下标(微服务) 连续被调用两次，第三次如果还是它，那么就再随即一次
     */
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;
        while (server == null) {
            /**
             * 判断当前线程是否处于中断状态
             */
            if (Thread.interrupted()) {
                return null;
            }

            // 返回所有 没出问题的 server组成的List
            List<Server> upList = lb.getReachableServers();

            // 返回所有的 server组成的List（包括出问题的）
            List<Server> allList = lb.getAllServers();

            int serverCount = allList.size();
            if (serverCount == 0) {
                /**
                 * 一个微服务也没有，直接返回 null
                 */
                return null;
            }

            int index = random.nextInt(serverCount);
            /*-----------------------------------逻辑（开始）------------------------------------*/
            System.out.println("当前下标：nowIndex=" + index);
            // 随机选择完服务后，当前下表就变为了上一次的下标
            lastIndex = nowIndex;
            if (index == skipIndex) {
                System.out.println("跳过");
                System.out.println("跳过的下标：" + index);
                // 重新选择下标
                index = random.nextInt(serverCount);
            }
            // 1 1 0 是成立的
            skipIndex = -1;
            nowIndex = index;
            if (nowIndex == lastIndex) {
                skipIndex = nowIndex;
            }
            /*-----------------------------------逻辑（结束）------------------------------------*/
            /**
             * 从 allList 中获取 server,可能获取有问题的server
             * 不能从upList中拿server,因为某些有问题的server可能又能够使用了，不能直接就再也不用它了
             */
            server = upList.get(index);

            if (server == null) {
                /**
                 * 当前线程让出 CPU
                 */
                Thread.yield();
                continue;
            }

            /**
             * 如果server 是个正常的 server
             */
            if (server.isAlive()) {
                return (server);
            }

            /**
             * 不应该执行下面的代码
             */
            server = null;
            Thread.yield();
        }

        return server;
    }

    @Override
    public Server choose(Object o) {
        return null;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

}
```

具体测试的话就不测试了， 那个效果放在笔记上不太明显，可以自己把代码copy过去测试一下 

### 6. 不同微服务使用不同的均衡策略

1. 配置均衡策略的类再 @ComponentScan 下，默认该项目所有的微服务都是该配置类中的均衡策略

   ```java
   package com.study.config;
   
   import com.netflix.loadbalancer.IRule;
   import com.netflix.loadbalancer.RetryRule;
   import com.study.util.CustomerRule;
   import org.springframework.cloud.client.loadbalancer.LoadBalanced;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.client.RestTemplate;
   
   @Configuration
   public class AppConfig {
   
       @Bean
       @LoadBalanced // 使用ribbon组件实现客户端负载均衡
       public RestTemplate createRestTemplate() {
           return new RestTemplate();
       }
   
       /**
        * 修改负载均衡策略
        * 默认使用轮询策略
        *
        * 这里的均衡策略放在了 @ComponentScan之下，调用所有微服务斗勇这个策略
        * @return
        */
       @Bean
       public IRule createIRule() {
           // 使用随机策略
   //        return new RandomRule();
           // 使用 RetryRule 策略
           return new RetryRule();
           // 使用自定义的随机策略
   //        return  new CustomerRule();
       }
   
   }
   ```

2. 配置多个均衡策略类，通过 @RibbonClients 为微服务选择策略

   ```java
   package com.config;
   
   import com.netflix.loadbalancer.IRule;
   import com.netflix.loadbalancer.RandomRule;
   import org.springframework.context.annotation.Configuration;
   
   /**
    * 此配置类不能再 @ComponentScan 下面，否则将会变成所有调用的均衡策略
    */
   @Configuration
   public class OrderIRuleConfig {
   
       public IRule createIRule() {
           return new RandomRule();
       }
   
   }
   ```

   ```java
   package com.config;
   
   import com.netflix.loadbalancer.IRule;
   import com.netflix.loadbalancer.RandomRule;
   import org.springframework.context.annotation.Configuration;
   
   /**
    * 此配置类不能再 @ComponentScan 下面，否则将会变成所有调用的均衡策略
    */
   @Configuration
   public class PowerIRuleConfig {
   
       public IRule createIRule() {
           return new RandomRule();
       }
   
   }
   ```

   ```java
   package com.study;
   
   import com.config.OrderIRuleConfig;
   import com.config.PowerIRuleConfig;
   import org.springframework.boot.SpringApplication;
   import org.springframework.boot.autoconfigure.SpringBootApplication;
   import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
   import org.springframework.cloud.netflix.ribbon.RibbonClient;
   import org.springframework.cloud.netflix.ribbon.RibbonClients;
   
   @SpringBootApplication
   @EnableEurekaClient // 表示此微服务为 Eureka客户端
   @RibbonClients({
           @RibbonClient(name = "SERVER-ORDER", configuration = OrderIRuleConfig.class),
           @RibbonClient(name = "SERVER-POWER", configuration = PowerIRuleConfig.class)
   })
   public class AppUserClient {
   
       public static void main(String[] args) {
           SpringApplication.run(AppUserClient.class);
       }
   
   }
   ```

   

## feign负载均衡：

### feign是什么 :

 Feign是一个声明式WebService客户端。使用Feign能让编写Web Service客户端更加简单, 它的使用方法是定义一个接口，然后在上面添加注解，同时也支持JAX-RS标准的注解。Feign也支持可拔插式的编码器和解码器。Spring Cloud对Feign进行了封装，使其支持了Spring MVC标准注解和HttpMessageConverters。Feign可以与Eureka和Ribbon组合使用以支持负载均衡。

### feign 能干什么：

Feign旨在使编写Java Http客户端变得更容易。 前面在使用Ribbon+RestTemplate时，利用RestTemplate对http请求的封装处理，形成了一套模版化的调用方法。但是在实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。在Feign的实现下，我们只需创建一个接口并使用注解的方式来配置它(以前是Dao接口上面标注Mapper注解,现在是一个微服务接口上面标注一个Feign注解即可)，即可完成对服务提供方的接口绑定，简化了使用Spring cloud Ribbon时，自动封装服务调用客户端的开发量。

 

### 如何使用？

在客户端(User)引入依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

在启动类上面加上注解:@EnableFeignClients

然后编写一个service类加上@FeignClient()注解 参数就是你的微服务名字

```java
package com.study.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "SERVER-POWER")
public interface PowerFeignClient {

    @RequestMapping("/getPower.do")
    public Object getPower();

}
```

下面是调用代码：

```java
package com.study.controller;

import com.study.service.PowerFeignClient;
import com.study.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {

    /**
     * spring开发的 org.springframework.web.client.RestTemplate 类，专门又来调用其他微服务使用的
     */
    @Autowired
    RestTemplate restTemplate;

    private static final String POWER_URL = "http://SERVER-POWER";

    @Autowired
    private PowerFeignClient powerFeignClient;

    @RequestMapping("/getPower.do")
    public R getPower(){
        return R.success("操作成功", restTemplate.getForObject(POWER_URL + "/getPower.do", Object.class));
    }
    @RequestMapping("/getPowerByFeign.do")
    public R getPowerByFeign() {
        return R.success("操作成功", powerFeignClient.getPower());
    }

}
```

这里拿了RestTemplate做对比 可以看看2者区别

Feign集成了Ribbon

利用Ribbon维护了服务列表信息，并且融合了Ribbon的负载均衡配置，也就是说之前自定义的负载均衡也有效，这里需要你们自己跑一遍理解一下。而与Ribbon不同的是，通过feign只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用
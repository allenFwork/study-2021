# spring IOC

##  **what is IOC** 

```
控制反转（Inversion of Control，缩写为IoC），是面向对象编程中的一种设计原则，可以用来减低计算机代码之间的耦合度。其中最常见的方式叫做依赖注入（Dependency Injection，简称DI），还有一种方式叫“依赖查找”（Dependency Lookup） 
```



###  **Dependency Injection** 

​		依赖注入

​			关于什么是依赖

​			关于注入和查找以及拖拽



##  **为什么要使用spring IOC** 

 		spring体系结构----IOC的位置  自己看官网 

```
在日常程序开发过程当中，我们推荐面向抽象编程，面向抽象编程会产生类的依赖，当然如果你够强大可以自己写一个管理的容器，但是既然spring以及实现了，并且spring如此优秀，我们仅仅需要学习spring框架便可。
当我们有了一个管理对象的容器之后，类的产生过程也交给了容器，至于我们自己的app则可以不需要去关系这些对象的产生了。
```

1. 应用程序中提供类，提供依赖关系（属性或者构造方法）

2. 把需要交给容器管理的对象通过配置信息告诉容器（xml、annotation，javaconfig）

3. 把各个类之间的依赖关系通过配置信息告诉容器

```
配置这些信息的方法有三种分别是xml，annotation和javaconfig
维护的过程称为自动注入，自动注入的方法有两种构造方法和setter
自动注入的值可以是对象，数组，map，list和常量比如字符串整形等
```



###  **spring编程的风格** 

```
schemal-based-------xml
annotation-based-----annotation
java-based----java Configuration
```

###  **注入的两种方法** 

```
spring注入详细配置（字符串、数组等）参考文档：
https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-factory-properties-detailed
```

 **Constructor-based Dependency Injection** 

```
构造方法注入参考文档：
https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-constructor-injection
```

![](D:\myData\notes\架构师\java\03 spring源码解析\images\构造器注入.png)









###  **自动装配**

​		上面说过，IOC的注入有两个地方需要提供依赖关系，一是类的定义中，二是在spring的配置中需要去描述。自动装配则把第二个取消了，即我们仅仅需要在类中提供依赖，继而把对象交给容器管理即可完成注入。

​		在实际开发中，描述类之间的依赖关系通常是大篇幅的，如果使用自动装配则省去了很多配置，并且如果对象的依赖发生更新我们可以不需要去更新配置，但是也带来了一定的缺点

​		自动装配的优点参考文档：

​		https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-factory-autowire

​		缺点参考文档：

​		https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-autowired-exceptions

​		作为我来讲，我觉得以上缺点都不是缺点

 		**自动装配的方法** 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd"
        default-autowire="byType">

    <!-- spring默认是不开启注解,节省性能 开启注解 -->
    <context:annotation-config></context:annotation-config>
    <!-- 告诉spring注解所在的包 -->
    <context:component-scan base-package="com" />
    <!-- spring 3.X译后可以直接使用component-scan,这代表了两个注解：开启注解 和 扫描 -->


    <bean id="dao" class="com.luban.dao.IndexDaoImpl"></bean>
    <bean id="indexService" class="com.luban.dao.IndexService" autowire="byType"></bean>

</beans>
```

- 自动装配值：

  - no : 不是用自动装配
  - byType ：通过对象类型（对象的成员变量的类型）来自动装配 
  - byName：通过 set方法，去掉set后，小写第一个字母形成name，在容器中找该id=name的bean
  - constructor：通过构造器中传入的参数的类型来实现自动装配，类似于byName

- beans中的``default-autowire="byType"``是给所有的bean配置自动装配方法，bean中的``autowire="byType"``是给此bean配置自动装配方法

  自动装配的方式参考文档：

  https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-factory-autowire

![](D:\myData\notes\架构师\java\03 spring源码解析\images\image.png)



```java
@Autowired // 默认根据 byType 方式注入，但是如果在容器中找不到，就是用byName方式注入，name 等于属性名
@Resource  // 默认根据 byName方式注入，name 等于属性名
@Resource(type=XXX.class) // 根据 byType 方式注入
@Resource(name="XXX")     // 根据 byName 方式注入
```



### spring懒加载

官网已经解释的非常清楚了：

```
https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-factory-lazy-init
```

```
值得提醒的是，如果你想为所有的对都实现懒加载可以使用官网的配置
```

![](D:\myData\notes\架构师\java\03 spring源码解析\images\懒加载.png)



###  **springbean的作用域** 

```
文档参考：
https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-factory-scopes
```

- singleton ：单例，每次获取的是同一个实例对象
- prototype：原型，每次获取的都是不同的实例对象

![](D:\myData\notes\架构师\java\03 spring源码解析\images\作用域值.png)

- xml 定义方式

  ```xml
  <bean id="accountService" class="com.something.DefaultAccountService" scope="singleton"/> annotation的定义方式 
  ```

-  annotation的定义方式 



 **Singleton Beans with Prototype-bean Dependencies** 

```
意思是在 Singleton 当中引用了一个 Prototype 的 bean 的时候引发的问题
官网引导我们参考https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-factory-method-injection
```



###  **spring生命周期的<font color=red>回调</font>** 

- `Lifecycle Callbacks`
- **生命周期回调**：spring容器实例化对象执行初始化方法后会调用的方法，和spring容器销毁时调用的方法
- 三种方法：
  1. 实现 `InitializingBean` 接口
  2. 实现 `DisposableBean` 接口
  3. 使用默认的回调方法 ,`xml`配置文件中使用 `init-method` 属性
  4. 使用 `@PostConstruct` 和 `@PreDestroy` 注解，更好，**非侵入性**

```
参考文档：
https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#beans-factory-lifecycle

1、Methods annotated with @PostConstruct
2、afterPropertiesSet() as defined by the InitializingBean callback interface
3、A custom configured init() method
```

- 实例：

```java
package com.luban.dao;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class IndexDaoImpl implements InitializingBean, DisposableBean {

    public IndexDaoImpl(){
        System.out.println("Constructor ...");
    }

    /**
     * spring的 lifecycle callbacks
     * 初始化回调：实现 org.springframework.beans.factory.InitializingBean 接口，
     *            重写 afterPropertiesSet() 方法
     *            该方法会在spring容器实例化该对象时，执行完初始化方法后，调用
     * @throws Exception
     */
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean : afterPropertiesSet...");
    }

    /**
     * spring的 lifecycle callbacks
     * 销毁回调：实现 org.springframework.beans.factory.DisposableBean 接口，
     *            重写 destroy() 方法
     *            该方法会在spring容器销毁时，调用
     * @throws Exception
     */
    public void destroy() throws Exception {
        System.out.println("DisposableBean destroy ... ");
    }
}
```

```java
package com.luban.dao;

import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Repository
public class IndexDaoImpl2 {

    public IndexDaoImpl2(){
        System.out.println("Constructor ...");
    }

    // 该方法会在spring容器实例化该对象时，执行完初始化方法后，调用
    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        System.out.println("@PostConstruct : afterPropertiesSet...");
    }

    // 该方法会在spring容器销毁时，调用
    @PreDestroy
    public void destroy() throws Exception {
        System.out.println("@PreDestroy destroy ... ");
    }
}
```



### Depends-on

```xml
    <bean id="one" class="ExceptionBean" depends-on="manager"></bean>
    <bean id="manager" class="ManagerBean"></bean>
```

- spring容器会先实例化 manager 的 bean对象，然后再实例化 one 的bean 对象，**depends-on强调顺序性**
- one里虽然没有manager对象属性，并不需要manager的实例对象，但是它的初始化需要manager先初始化后的信息，所以使用 depends-on



**@Lazy**：懒加载

- 使用这个注解的类，都不会直接被spring容器创建实例，只有在使用时，才创建对应的实例



**@lookup**：解决单例对象中依赖原型对象属性问题

- spring 容器管理的对象中，如果一个对象是单例的，而它的成员变量为原型的，那么默认情况下，会将这个属性也变为单例，通过在get方法上使用 `@Lookup` 能够解决这个问题



**`@ComponentScan`**：扫描包

- ```java
  @ComponentScan(value="com.luban",
                 includeFilters = @Filter(),
                 excludeFilters = {@ComponentScan.Filter(type= FilterType.REGEX, 	 
                                                         pattern="com.luban.service.*") 
                                  }
                )
  ```

- 扫描 `com.luban` 包下所有的注解的类
- 排除掉 `com.luban.service`下的所有类，`ComponentScan.Filter`中 `FilterType.REGEX`表示这种类型的表达式



**spring可以使用索引增加扫描的速度**

```xml
<dependency>
	<groupId>org.springframework</groupId>
    <artifactId>spring-context-indexer</artifactId>
    <version>5.1.1.RELEASE</version>
    <optional>true</optional>
</dependency>
```



### @Profile 的使用

- **使用该注解不仅可以使某些类在某些环境起作用，也可以使某些配置文件在某些环境起着作用**

- **添加依赖：**

```xml
<dependencies>
        <!-- ioc 90%的功能 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.0.9.RELEASE</version>
        </dependency>

        <!-- spring自带的数据源所需要的包 -->
        <!-- 数据源，实现了DataSource接口（规定），用于连接数据库的，SqlSessionFactory中需要的配置信息参数 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.0.9.RELEASE</version>
        </dependency>

        <!-- DataSource 配置mysql数据库需要的驱动类在此包里 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.39</version>
        </dependency>


        <!-- mybatis 使用需要导入的jar包,可以自己配置连接数据库，自己设置url等信息 -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.3.0</version>
        </dependency>
        <!-- mybatis为了使spring框架能够直接管理mybatis中的SqlSessionFactory类,
             所以创建了mybatis-spring包,添加了SqlSessionFactoryBean实现类，用于给spring容器管理
        -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>1.3.1</version>
        </dependency>

    </dependencies>
```

- **配置数据源和 `SqlSessionFactory`**

```java
package com.luban.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Profile("dev") // 开发环境
@Configuration
public class DataSourceConfig {

    /**
     * org.mybatis.spring.SqlSessionFactoryBean类实现了 org.apache.ibatis.session.SqlSessionFactory接口
     * @return
     */
    @Bean
    public SqlSessionFactoryBean createSqlSessionFactoryBean(DataSource dataSource){
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }

    /**
     * 配置数据源：
     *      *   目前的数据源有 c3p0、阿里巴巴的德鲁伊pop、dbcp，这戏都要添加新的依赖
     *      *   可以使用spring自带的数据源是 spring-jdbc 包里的 DriverManagerDataSource类
     * @return
     */
    @Bean
    public DataSource createDataSourece(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("allen");
        dataSource.setPassword("19941021");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return  dataSource;
    }
}
```

- **配置启动环境**

```java
package com.luban;

import com.luban.config.DataSourceConfig;
import com.luban.config.SpringConfiguration;
import com.luban.dao.IndexDaoImpl2;
import com.luban.service.IndexService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

    public static void main(String[] args) {
        // ClassPathXmlApplicationContext 这个没有开启注解的功能,所以需要配置文件来开启注解
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-dao.xml");

        // 使用javaCOnfig方法，使用AnnotationConfigApplicationContext 这个类,这个类本身就有开启注解的功能
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);

        // 使用 @Profile来配置不同环境的数据连接
        AnnotationConfigApplicationContext context =
            			new AnnotationConfigApplicationContext();
        // 设置环境：使用dev环境的配置文件/bean类生效
        context.getEnvironment().setActiveProfiles("dev");
        context.register(DataSourceConfig.class);
        // 重新扫描
        context.refresh();
    }
}
```

- **如果想要配置测试环境的数据库，就改为`context.getEnvironment().setActiveProfiles("test");`**



### spring中的循环引用问题

- 在``java``类中两个类相互引用，`gc`使用可达性算法，解决这个问题
- 在spring中，如果两个类相互引用，是可以成功的，原因是：
  1. spring能实现这个，添加了类似缓存的机制
  2. **步骤：**spring扫描类 --> 扫描完将所扫描到的类（new到）放到spring的缓存机制中， 此时即使有相互依赖也不管 --> 将实例对象放到放到容器（`BeanFactory`）中，此时发现有引用关系，就将其进行相应的引用处理  --> 销毁缓存空间和其中包含的类
  3. 但是如果相互引用的类中有原型的(Singleton)，那么就会报错，因为无法使用这种先new的缓存极致了




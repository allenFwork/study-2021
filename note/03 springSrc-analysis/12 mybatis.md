# mybatis

## 1. mybatis的使用

### 1.1 添加依赖

- ```xml
  <!-- mybatis 使用需要导入的jar包,可以自己配置连接数据库，自己设置url等信息 -->
  <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <version>3.4.6</version>
  </dependency>
  ```

### 1.2 配置 spring-config.xml（数据源）

- ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <!DOCTYPE configuration
          PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
          "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
      <environments default="development">
          <environment id="development">
              <transactionManager type="JDBC"/>
              <dataSource type="POOLED">
                  <property name="driver" value="com.mysql.jdbc.Driver"/>
                  <property name="url" value="jdbc:mysql://localhost:3306/mybatis"/>
                  <property name="username" value="*****"/>
                  <property name="password" value="*****"/>
              </dataSource>
          </environment>
      </environments>
  </configuration>
  ```

### 1.3 mybatis 配置 Mapper

#### 1.3.1 在 configuration标签中配置(4种)

- resource      url      class      package     

- 实例：

  ```xml
  <configuration>
      <mappers>
          <!--  <mapper resource="com/study/mybatis/mapper/CountryMapper.xml"/>-->
          <!--  <mapper url="com/study/mybatis/mapper/CountryMapper.xml"/>-->
          <!--  <mapper class="com.study.mybatis.mapper.CountryMapper"/>-->
          <package name="com.study.mybatis.mapper"/>
      </mappers>
  </configuration>
  ```

#### 1.3.2 通过java代码配置

- 获取SqlSession对象，在获取其configuration属性，给它添加mapper的值

- 实例：

  ```java
  session.getConfiguration().addMapper(CountryMapper.class);
  ```

### 1.4 实例：

- mapper 文件

  ```java
  package com.study.mybatis.mapper;
  
  import org.apache.ibatis.annotations.Select;
  
  import java.util.List;
  import java.util.Map;
  
  public interface CountryMapper {
  
      @Select("select * from country")
      List<Map<String, Object>> query();
  
  }
  ```

- service 层调用 dao层

  ```java
  package com.study.mybatis.service;
  
  import com.study.mybatis.config.CustomerLog;
  import com.study.mybatis.mapper.CountryMapper;
  import org.apache.ibatis.io.Resources;
  import org.apache.ibatis.session.SqlSession;
  import org.apache.ibatis.session.SqlSessionFactory;
  import org.apache.ibatis.session.SqlSessionFactoryBuilder;
  
  import java.io.IOException;
  import java.io.InputStream;
  import java.util.List;
  import java.util.Map;
  
  public class CountryServiceImpl {
  
      public List<Map<String, Object>> list() {
  
          // 读取配置文件
          String resource = "mybatis-config.xml";
          InputStream inputStream = null;
          try {
              inputStream = Resources.getResourceAsStream(resource);
          } catch (IOException e) {
              e.printStackTrace();
          }
  
          // 构建SqlSessionFactory
          SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
  
          List<Map<String, Object>> list = null;
          try (SqlSession session = sqlSessionFactory.openSession()) {
              session.getConfiguration().addMapper(CountryMapper.class);
              // 设置日志
              session.getConfiguration().setLogImpl(CustomerLog.class);
              CountryMapper countryMapper = session.getMapper(CountryMapper.class);
              list = countryMapper.query();
          }
          return list;
      }
  
  }
  ```

- 测试类

  ```java
  package com.study.mybatis.test;
  
  import com.study.mybatis.service.CountryServiceImpl;
  
  public class MybatisTest {
  
      public static void main(String[] args) {
  
          CountryServiceImpl countryService = new CountryServiceImpl();
          System.out.println(countryService.list());
  
      }
  
  }
  ```



## 2. mybatis 缓存

### 2.1 mybatis的一级缓存

#### 2.1.1 概念

- 执行相同查询语句，不会多次执行查询数据库，只执行一遍

#### 2.1.2 原理

- sqlSession起作用
- ![](images\mybatis\mybatis一级缓存.png)

#### 2.1.3 框架整合：

- 单独使用mybaits时，mybatis能够正常使用一级缓存
- mybatis与spring整合后，一级缓存失效

#### 2.1.4 常见面试问题

1. 为什么 mybatis 与 spring 整合后，一级缓存会失效？

   因为mybatis和spring的集成包中扩展了一个类，SqlSessionTemplate，这个类在spring容器启动的时候，被注入给了mapper，这个类替代了原来的DefaultSqlSession，SqlSessionTemplate当中所有的查询放方法不是直接查询，而是经过一个代理对象，



2. mybatis



### 2.2 mybatis的二级缓存

- 概念：二级缓存，是多线程共享的

- 使用二级缓存，通过mybatis的注解 `@CacheNamespace` 开启

- 原理：（以前面试常问）

  - mybatis的二级缓存是 **基于mapper的命名空间的**

  - 例如，

    1. ```java
       package com.study.dao;
       
       import org.apache.ibatis.annotations.CacheNamespace;
       import org.apache.ibatis.annotations.Select;
       import org.apache.ibatis.annotations.Update;
       
       import java.util.List;
       import java.util.Map;
       
       @CacheNamespace
       public interface CountryMapper {
       
           @Select("select * from country")
           List<Map<String, Object>> list();
       
           @Update("update country set countryname = '中国2' where id = 1")
           void update();
       
       }
       ```

    2. ```java
       package com.study.dao;
       
       import org.apache.ibatis.annotations.CacheNamespace;
       import org.apache.ibatis.annotations.Select;
       import org.apache.ibatis.annotations.Update;
       
       import java.util.List;
       import java.util.Map;
       
       @CacheNamespace
       public interface CountryMapperCacheDemo {
       
           @Update("update country set countryname = '中国' where id = 1")
           void update();
       
       }
       ```

    上面个两个mapper会会生成两个二级缓存，一个是 `com.study.dao.CountryMapper` ，另一个是  `com.study.dao.CountryMapperCacheDemo`，执行第一个mapper中的查询语句，再执行第二个mapper中更新语句，对第一个CountryMapper的二级缓存而言，不会更新缓存，再执行CountryMapper查询会直接返回CountryMapper二级缓存中的数据。

- 总结：

  - 因为 mybatis的二级缓存是**基于mapper的命名空间的**，因此会导致一些坑，比较鸡肋，所以现在都是用第三方工具作为缓存，比如 redis

- 实例：















## 3. mybatis源码解析

### 3.1 mybatis源码需要精通的3个点(重点)：

1. mybatis 单独使用时，通过 mapperProxy 实现接口代理；

   mybatis + spring 使用时，通过 mapperProxy 和 SqlSessionTemplate 双重代理

2. mybatis 将mapper接口实例化交由spring管理是通过MapperFactoryBean实现的

3. mybatis 的 MappedStates 是一个map，用来存储执行的sql语句

### 3.1 mapper接口处理

#### 3.1.1 mybatis 底层流程

```java
// 获取 SqlSession,单独使用mabatis框架，获取了org.apache.ibatis.session.defaults.DefaultSqlSession类型的Sqlsession
SqlSession session = sqlSessionFactory.openSession();
// 向 Configuration 中添加 mapper 接口
session.getConfiguration().addMapper(CountryMapper.class);
```

1. 向Configuration的mapperRegistry属性添加该接口的信息

2. 向mapperRegistry的knownMappers属性(集合) 添加该mapper接口信息

   ![](images\mybatis\mybatis源码解析1.png)

3. 开始解析接口，解析接口中的方法

   ![](images\mybatis\mybatis源码解析2.png)

4. 解析方法中的sql

   ![](images\mybatis\mybatis源码解析3.png)

5. 在 parseStatement方法中接着向下执行，最终会执行到向Configuration的**mappedStatements**属性添加Statement 语句信息

   ![](images\mybatis\mybatis源码解析4.png)

   ![](images\mybatis\mybatis源码解析5.png)

   ```java
   protected final Map<String, MappedStatement> mappedStatements = new StrictMap<MappedStatement>("Mapped Statements collection");
   ```

   - **ms.getId** 就是`com.study.mybatis.mapper.CountryMapper.query`

6. 总结：此时configurationde的mappedStatements Map属性已经将mapper中的所有sql添加了，只是目前没有创建 mapper的实体类 和 执行sql

```java
CountryMapper countryMapper = session.getMapper(CountryMapper.class);
```

1. 调用DefaultSqlSessio的getMapper方法，进入configuration.getMapper方法

   ![](images\mybatis\mybatis源码解析6.png)

2. 调用configuration的mapperRegistry的getmapper方法

   ![](images\mybatis\mybatis源码解析7.png)

3. <font color=red>**通过 MapperProxyFactory 调用代理方法，获取代理类的实例化对象**</font>

   ![](images\mybatis\mybatis源码解析8.png)

   ![](images\mybatis\mybatis源码解析9.png)

   - 最底层是通过 jdk 的动态代理实现的：mapper接口的实例化

```java
list = countryMapper.query();
```

1. 这个countryMapper是代理类，不存在源码，所以直接调用到了 MapperProxy 的 invoke方法

   ![](images\mybatis\mybatis源码解析10.png)

2. 执行MapperMethod的execute方法，在方法中通过switch选择SELECT分支，执行executeForMany方法

3. 接着executeForMany中在执行sqlSession的selectList方法

   ![](images\mybatis\mybatis源码解析11.png)

4. **在执行 DefaultSqlSession中的selectList方法**

   ![](images\mybatis\mybatis源码解析12.png)

5. 在经过一系列操作最终执行通过java包中的Statement语句对象执行sql获取数据

### 3.2 mybatis + spring 底层流程

1. 通过 org.mybatis.spring.mapper.ClassPathMapperScanner的processBeanDefinitions方法将mapper对应的beanDefinition的beanClass变为MapperFactorybean

2. 接着实例化mapper时，会产生两个代理对象：

   1）mapper接口对应的代理对象

   2）向mapper实例对象注入SqlSession对象，实质时SqlSessionTemplate类型的，在SqlSessionTemplate中执行selectList方法时，又会产生一个代理对象

   ![](images\mybatis\mybatis+spring源码解析1.png)

3. 执行mapper接口中的方法，最终都会进入 MapperProxy 的 invoke方法



MapperScannerRegistrar



### 3.3 总结：

#### 3.3.1 sqlSession

- 单独使用mybatis框架时，SqlSession接口对应的实现默认是 DefaultSqlSession
- mybatis集成spring框架时，SqlSession接口对应的实现默认是 SqlSessionTemplate

#### 3.3.2 底层执行sql

- 单独使用mybatis框架时：

  DefaultSqlSession中执行selectList方法，进入该方法，底层用jdk包下api进行查询

- mybatis集成spring框架时：

  SqlSessionTemplate执行selectList方法，在方法中再通过 sqlSessionProxy 执行selectList方法，得到

- 

#### 3.3.3 

1. **执行 doCreateBean方法时，创建countryMapper接口的bean对象时，会实例化SqlSessionTemplate**

   原因：

   创建countryMapper的实例化对象，实际上就是实例化 MapperFactroyBean ；

   创建 MapperFactoryBean 实例化对象时，spring容器会对其属性进行自动装配，**其中有一个属性就是SqlSession**

   ![](images\mybatis\MapperFactroyBean1.png)

   ![MapperFactroyBean2](images\mybatis\MapperFactroyBean2.png)

   ![MapperFactroyBean3](images\mybatis\MapperFactroyBean3.png)

   

#### 3.3.4 spring 自动装配 mybatis 中的属性 

1. spring的自动装配规律：

   1）spring 自动装配默认是使用  NO 的，所以不会自动装配，只有给属性添加了 @Autowired 才表示该属性自动装配

   2）spring 需要通过类中是否有set方法，来判断该属性是否需要自动装配

   3)  spring 需要通过类中是否有get方法，来判断改属性是否需要自动装配

2. mybatis 自己在底层设置 mapper对应的 beanDefinition 属性：

   在 org.mybatis.spring.mapper.ClassPathMapperScanner的processBeanDefinitions方法中，做了2件事：

   1）遍历所有mapper对应的beanDefinition，将其对应的beanClass属性修改为：

   ​			`class org.mybatis.spring.mapper.MapperFactoryBean`

   2）遍历所有mapper对应的beanDefinition，将其对应的autowiredMode属性赋值为2，就是表明mapper生成的实例中所有属性都是通过spring容器的byType自动装配

3. 





###  自动装配属性需要满足以下要求：

1. 有set方法

2. 
3. 






## 4. mybait整合spring实现@MapperScan

### 4.1 @MapperScan功能

- 扫描所有的 mapper 接口，将其变为对象注入到spring管理的对象中，在对象中通过这些mapper类型的对象直接调用方法，执行sql

### 4.2 原理

1. 扫描包，获得所有的mapper接口
2. 将接口转化为实例对象 
3. 这些实例对象必须实现对应的接口mapper
4. 将这些对象交给spring容器去管理

### 4.3 实现

- 接口 mapper

  ```java
  package com.study.dao;
  
  import org.apache.ibatis.annotations.Select;
  import org.apache.ibatis.annotations.Update;
  
  import java.util.List;
  import java.util.Map;
  
  public interface CountryMapper {
  
      @Select("select * from country")
      List<Map<String, Object>> list();
  
      @Update("update country set countryname = '中国2' where id = 1")
      void update();
  
  }
  ```

- 因为接口是不可能存在实例化对象的，所以通过动态代理创建该接口的实例对象：

  ```java
  package com.study.util;
  
  import com.study.dao.CountryMapper;
  import com.study.proxy.MyInvocationHandler;
  import com.study.test.MapperScanTest;
  import org.springframework.beans.factory.support.BeanDefinitionBuilder;
  import org.springframework.beans.factory.support.BeanDefinitionRegistry;
  import org.springframework.beans.factory.support.GenericBeanDefinition;
  import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
  import org.springframework.core.type.AnnotationMetadata;
  
  import java.lang.reflect.Proxy;
  
  public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
      @Override
      public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
  
  //        /*-----------------------------------------------------方法一:----------------------------------------------*/
  //        // 1. 扫描报下所有的接口Mapper(myBatis中的接口)
  //
  //        // 2. 获取这些接口对应的每一个对象，并且这个对象实现了该mapper接口
  //        CountryMapper mapper = (CountryMapper) Proxy.newProxyInstance(MapperScanTest.class.getClassLoader(), new Class[]{CountryMapper.class}, new MyInvocationHandler());
  //        mapper.list();// 测试
  //        // 3. 将对象放到spring容器中
  //        // 3.1 得到beanDefinition
  //        // 获取代理对象的类名，然后通过类名获取beanDefinition
  //        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(mapper.getClass());
  //        // GenericBeanDefinition基本的BeanDefinition
  //        GenericBeanDefinition beanDefinition = (GenericBeanDefinition) builder.getBeanDefinition();
  //        // 3.2 注册到spring容器中
  //        beanDefinitionRegistry.registerBeanDefinition("countryMapper", beanDefinition);
  
  
          /*---------------------------------------------方法二（MyFactoryBean版本1）:-----------------------------------*/
  //        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((CountryMapper.class));
  //        GenericBeanDefinition beanDefinition = (GenericBeanDefinition) builder.getBeanDefinition();
  //        /**
  //         * 因为现在这个beanDefinition对应的class是CountryMapper.class，这是一个接口，是无法实例化的，
  //         * 所以设置这个beanDefinition对应的类为MyFactoryBean2.class，从而获取这个beanDefinition的bean对象时，
  //         * 就是获取MyFactoryBean.class中getObject()方法返回的对象，也就是CountryMapper接口对应的代理对象，这才是可以实例化的类型
  //         */
  //        beanDefinition.setBeanClass(MyFactoryBean.class);
  //        beanDefinitionRegistry.registerBeanDefinition("countryMapper", beanDefinition);
  
  
          /*---------------------------------------------方法二（MyFactoryBean2版本2）:-----------------------------------*/
          BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((CountryMapper.class));
          GenericBeanDefinition beanDefinition = (GenericBeanDefinition) builder.getBeanDefinition();
          /**
           * 因为 MyFactoryBean2.class 中有了构造方法，所以spring容器无法通过默认的无参构造器获取该对象的实例，
           * 所有需要告诉 spring 容器 使用那个构造方法获取实例对象：
           * 通过构造方法参数来确定交由spring管理的对象
           * 设置这个beanDefinition对相应的类 实例化时所使用的构造方法，其构造参数是 com.study.dao.CountryMapper
           *
           * beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.study.dao.CountryMapper");
           * 这句表达的意思就是：MyFactoryBean2有一个构造方法，这个构造方法是 MyFactoryBean2(Class com.study.dao.CountryMapper)，
           * 通过 名字为 com.study.dao.CountryMapper 执行构造方法进行实例化
           */
          beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.study.dao.CountryMapper");
          /**
           * 因为现在这个beanDefinition对应的class是CountryMapper.class，这是一个接口，是无法实例化的，
           * 所以设置这个beanDefinition对应的类为MyFactoryBean2.class，从而获取这个beanDefinition的bean对象时，
           * 就是获取MyFactoryBean2.class中getObject()方法返回的对象，也就是CountryMapper接口对应的代理对象，这才是可以实例化的类型
           */
          beanDefinition.setBeanClass(MyFactoryBean2.class);
          beanDefinitionRegistry.registerBeanDefinition("countryMapper", beanDefinition);
      }
  }
  ```

  ```java
  package com.study.util;
  
  import com.study.dao.CountryMapper;
  import com.study.proxy.MyInvocationHandler;
  import org.springframework.beans.factory.FactoryBean;
  import org.springframework.stereotype.Component;
  
  import java.lang.reflect.Proxy;
  
  /**
   * 这个类会产生两个bean对象交spring容器管理：
   * 1. MyFactoryBean 本身，对应的beanName-beanDefinition 为 &myFactoryBean-beanDefinition
   * 2. getObject()返回的对象，对应的beanName-beanDefinition 为 myFactoryBean-beanDefinition
   *
   * 版本1
   */
  @Component
  public class MyFactoryBean implements FactoryBean {
  
      Class[] classes = new Class[]{CountryMapper.class};
  
      @Override
      public Object getObject() throws Exception {
          Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), classes, new MyInvocationHandler());
          return proxy;
      }
  
      @Override
      public Class<?> getObjectType() {
          return CountryMapper.class;
      }
  
  }
  ```

  ```java
  package com.study.util;
  
  import org.springframework.beans.factory.FactoryBean;
  import org.springframework.stereotype.Component;
  
  import java.lang.reflect.InvocationHandler;
  import java.lang.reflect.Method;
  import java.lang.reflect.Proxy;
  
  /**
   * 这个类会产生两个bean对象交spring容器管理：
   * 1. MyFactoryBean 本身，对应的beanName-beanDefinition 为 &myFactoryBean-beanDefinition
   * 2. getObject()返回的对象，对应的beanName-beanDefinition 为 myFactoryBean-beanDefinition
   *
   * 版本2
   */
  @Component
  public class MyFactoryBean2 implements FactoryBean, InvocationHandler {
  
      private Class clazz;
  
      public MyFactoryBean2(Class clazz){
          this.clazz = clazz;
      }
  
      @Override
      public Object getObject() throws Exception {
          Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, this);
          return proxy;
      }
  
      @Override
      public Class<?> getObjectType() {
          return clazz;
      }
  
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          System.out.println("----------proxy-------");
          return null;
      }
  }
  ```

  ```java
  package com.study.proxy;
  
  import java.lang.reflect.InvocationHandler;
  import java.lang.reflect.Method;
  
  public class MyInvocationHandler implements InvocationHandler {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) {
          System.out.println("proxy ... ");
          return null;
      }
  }
  ```

- 测试：

  ```java
  package com.study.test;
  
  import com.study.config.MyBatisSpringConfig;
  import com.study.dao.CountryMapper;
  import org.springframework.context.annotation.AnnotationConfigApplicationContext;
  
  /**
   * 模拟 mybatis
   */
  public class MapperScanTest {
  
      public static void main(String[] args) {
          AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyBatisSpringConfig.class);
          context.getBean("countryMapper");
  //        ((CountryMapper) context.getBean("countryMapper")).list();
  
      }
  
  }
  ```


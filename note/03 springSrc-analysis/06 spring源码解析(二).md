# Spring 源码解析 二

## 1. BeanFactory 与 FactoryBean的区别

- BeanFactory 是spring的bean工厂，能够生产和获取bean对象，主要方法有`getBean("xxx")`。

- FactoryBean 本身是一个bean，还能产生一个bean，是为了解决某种问题而产生的。

  - FactoryBean 知识点：
    1. 如果类实现了 `org.springframework.beans.factory.FactoryBean` ，那么 spring容器当中存在两个对象：一个是 getObject()返回的对象，还有一个是当前对象
    2. 实现了 `FactoryBean` 接口后，需要重写三个方法，其中有一个 `getObject()`：
    3. **`getObject()` 方法得到的对象存储在spring容器中时，它指定的名字是在类上面使用 @Component("xxx") 命名的**
    4. **<font color=red>当前对象在spring容器中的名字是  “&” + 当前类的名字</font>**
  - 实例：

  ```java
  package com.study.knowledge;
  
  import com.study.dao.TempDaoFactoryBean;
  import org.springframework.beans.factory.FactoryBean;
  import org.springframework.stereotype.Component;
  
  /**
   * 如果类实现了 org.springframework.beans.factory.FactoryBean,
   * 那么 spring容器当中存在两个对象:
   *   一个是 getObject()返回的对象,还有一个是当前对象
   *
   * getObject()方法得到的对象存储在spring容器中时,它指定的名字是在类上面使用 @Component("xxx") 命名的
   * 例如，下面的 TempDaoFactoryBean类型在spring容器中的bean对象的id是factoryBeanDemo
   *
   * 当前对象在spring容器中的名字是  “&” + 当前类的名字
   * 例如，当前类FactoryBeanDemo 在spring容器中的bean对象的id是 &factoryBeanDemo
   */
  @Component("factoryBeanDemo")
  public class FactoryBeanDemo implements FactoryBean {
  
      @Override
      public Object getObject() throws Exception {
          return new TempDaoFactoryBean();
      }
      
      @Override
      public Class<?> getObjectType() {
          return null;
      }
      
      @Override
      public boolean isSingleton() {
          return false;
      }
  }
  ```

  ```java
  package com.study.dao;
  
  public class TempDaoFactoryBean {
      public void test(){
          System.out.println("TempDaoFactoryBean test()");
      }
  }
  ```

  - 测试

  ```java
  package com.study.test;
  
  import com.study.config.AppConfig;
  import com.study.knowledge.FactoryBeanDemo;
  import org.springframework.context.annotation.AnnotationConfigApplicationContext;
  
  public class SpringTest {
          AnnotationConfigApplicationContext context =
                  new AnnotationConfigApplicationContext(AppConfig.class);
      
  //        FactoryBeanDemo factoryBeanDemo =
  //                (FactoryBeanDemo) context.getBean("factoryBeanDemo");
      
          FactoryBeanDemo factoryBeanDemo =
                  (FactoryBeanDemo) context.getBean("&factoryBeanDemo");
      
          System.out.println(factoryBeanDemo);
      }
  }
  
  如果不注释掉第一部分，会报异常：类型转换异常
  Exception in thread "main" java.lang.ClassCastException: com.study.dao.TempDaoFactoryBean cannot be cast to com.study.knowledge.FactoryBeanDemo
  ```

- FactoryBean 的经典应用场景：
  - 如果一个类非常复杂，有几十个成员变量，当这个类是第三方的框架（例如mybatis），不能直接使用 `@Component`注解，此时如果使用 xml配置，那么添加几十个依赖太过复杂。
  - Mybatis 的`SqlSessionFactory`因为其中包含了太多依赖，使用spring进行管理时，开发人员无法进行处理，所以 MyBatis开发了 `SqlSessionfactoryBean` 来代替它，`SqlSessionfactoryBean` 实现了 `FactoryBean`接口，其中的 `getObject()`方法最终返回的其实还是`SqlSessinFactory`类型的实例对象， 是 `FactoryBean` 最典型的应用。
  - ![](images\mybatis的SqlSessionFactoryBean实现原理.png)





## 2. 模拟ioc - 注解扫描

### 2.1 初始化 spring 的环境有几种方法：

1. 使用xml配置：`ClassPathXmlApplicationContext`
2. 使用注解：
3. javaconfig：`AnnotationConfigApplicationContext`

### 2.2 为什么要初始化spring容器：

​	因为要将对象的实例化交给 spring 管理

### 2.3 模拟

- 代码：

  ```java
  package org.spring.util;
  
  import java.io.File;
  
  /**
   * 模拟 spring ioc 注解管理bean对象
   */
  public class AnnotationConfigApplicationContext {
  
      public void scan(String basePackage) {
          // 通过获取文件名来获取类
          // 获取class所在的根路径
          String rootPath = this.getClass().getResource("/").getPath();
          String basePackagePath = basePackage.replaceAll("\\.","\\\\");
          String filePath = rootPath + "//" + basePackagePath;
          File file = new File(filePath);
          String[] fileNames = file.list();
          for (String name : fileNames) {
              name = name.replace(".class", "");
              try {
                  Class clazz = Class.forName(basePackage + "." + name);
                  // 判断是否属于添加了注解的类，例如判断该类是否添加过@CustomerAnnotation
                  if(clazz.isAnnotationPresent(CustomerAnnotation.class)) {
                      CustomerAnnotation customerAnnotation = (CustomerAnnotation) clazz.getAnnotation(CustomerAnnotation.class);
                      System.out.println(customerAnnotation.value());
                      System.out.println(clazz.newInstance());
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }
      }
  }
  ```

# Spring MVC 

## 1. Spring MVC 的使用

### 1.1 添加依赖

- ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <parent>
          <artifactId>study-2021-01-06</artifactId>
          <groupId>com.study</groupId>
          <version>1.0-SNAPSHOT</version>
      </parent>
      <modelVersion>4.0.0</modelVersion>
      <artifactId>spring-mvc</artifactId>
  
      <dependencies>
          <!-- 使用spring mvc -->
          <!-- 第一步：添加spring 核心 依赖 -->
          <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-context</artifactId>
              <version>${spring5.version}</version>
          </dependency>
          <!-- 第二步：添加 spring 对 web 支持的依赖 -->
          <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-webmvc</artifactId>
              <version>${spring5.version}</version>
          </dependency>
  
          <!-- servlet的依赖：此处是servlet3版本，现在已经有servlet4版本了 -->
          <dependency>
              <groupId>javax.servlet</groupId>
              <artifactId>javax.servlet-api</artifactId>
              <version>3.1.0</version>
          </dependency>
  
      </dependencies>
  
      <build>
          <plugins>
              <!--添加tomcat的maven插件-->
              <plugin>
                  <groupId>org.apache.tomcat.maven</groupId>
                  <artifactId>tomcat7-maven-plugin</artifactId>
                  <version>2.2</version>
              </plugin>
  
              <!--添加jetty插件-->
              <plugin>
                  <groupId>org.eclipse.jetty</groupId>
                  <artifactId>jetty-maven-plugin</artifactId>
                  <version>9.4.14.v20181114</version>
              </plugin>
          </plugins>
      </build>
  
  </project>
  ```

### 1.2 不使用 web.xml 来启动服务

- Tomcat 的入口是 web.xml，但是我们也可以使用实现 org.springframework.web.WebApplicationInitializer接口的类作为Tomcat的入口

- ```java
  package com.study.app;
  
  import com.study.config.AppConfig;
  import org.springframework.web.WebApplicationInitializer;
  
  /**
   * 为什么实现了 WebApplicationInitializer 接口，Tomcat 就会执行 onStartup方法中的代码？
   * 因为 Tomcat一定会执行 WebApplicationInitializer 的 onStartup方法
   *
   * 添加 servlet 的方法：
   *  1. web.xml中配置
   *  2. 使用 @WebServlet 注解
   *  3. 实现 WebApplicationInitializer 接口，重写onStartup方法，在方法中注入servlet
   */
  //@WebServlet
  public class MyWebApplicationInitializer implements WebApplicationInitializer {
  
      @Override
      public void onStartup(ServletContext servletContext) {
      
      }
  
  }
  ```

#### 1.2.1 实例化 spring context

```java
package com.study.app;

import com.study.config.AppConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

/**
 * 为什么实现了 WebApplicationInitializer 接口，Tomcat 就会执行 onStartup方法中的代码？
 * 因为 Tomcat一定会执行 WebApplicationInitializer 的 onStartup方法
 * 
 */
//@WebServlet
public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) {
        /**
         * 实例化 spring context
         */
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        // 通过 AppConfig配置类，开启了扫描
        context.register(AppConfig.class);
        context.refresh(); // 到这里 spring容器就初始化结束了
    }

}
```



#### 1.2.2 添加 spring Servlet

1. 使用 @WebServlet 注解 作用在类上面，这个类就是 Servlet
2. 手动注册servlet

- ```java
  package com.study.app;
  
  import com.study.config.AppConfig;
  import org.springframework.web.WebApplicationInitializer;
  import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
  import org.springframework.web.servlet.DispatcherServlet;
  
  import javax.servlet.ServletContext;
  import javax.servlet.ServletRegistration;
  
  /**
   * 为什么实现了 WebApplicationInitializer 接口，Tomcat 就会执行 onStartup方法中的代码？
   * 因为 Tomcat一定会执行 WebApplicationInitializer 的 onStartup方法
   *
   * 想要知道为什么绘制想到 onStartup方法里的代码，可以通过在onStartup方法中设置断点，
   * 然后启动tomcat容器，再通过方法栈的调用，查看是从哪里进来的。
   *
   * 添加 servlet 的方法：
   *  1. web.xml中配置
   *  2. 使用 @WebServlet 注解
   *  3. 实现 WebApplicationInitializer 接口，重写onStartup方法，在方法中注入servlet
   */
  //@WebServlet
  public class MyWebApplicationInitializer implements WebApplicationInitializer {
  
      @Override
      public void onStartup(ServletContext servletContext) {
  
          /**
           * 实例化 spring context
           */
          AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
          // 通过 AppConfig配置类，开启了扫描
          context.register(AppConfig.class);
          context.refresh(); // 到这里 spring容器就初始化结束了
  
          /**
           * 手动添加 spring Servlet:
           * 创建一个 DispatcherServlet实例，并将其注入到spring容器中
           * Create and register the DispatcherServlet
           */
          DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
          // 获取注册器
          ServletRegistration.Dynamic registration = servletContext.addServlet("servletName", dispatcherServlet);
          /**
           * LoadOnStartup 设置为1的原因：
           * 因为需要在tomcat启动时就要立即加载DispatcherServlet类的，
           * 如果不设置为1，就变成了等你请求时tomcat才会加载DispatcherServlet
           */
          registration.setLoadOnStartup(1);
          // 此servlet，即DispatcherServlet,处理所有的请求
          registration.addMapping("/");
      }
  
  }
  ```

#### 1.2.3 将视图解析器注入到spring容器中

```java
package com.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@ComponentScan("com.study")
public class AppConfig implements WebMvcConfigurer {

    /**
     * 方法1：
     * 创建视图解析器,并将其交给spring管理
     */
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
        internalResourceViewResolver.setPrefix("/");
        internalResourceViewResolver.setSuffix(".jsp");
        return internalResourceViewResolver;
    }

    /**
     * 方法2：
     * 可以实现 WebMvcConfigurer 接口，然后重写 configureViewResolvers方法，
     * 在该方法中设置 视图解析器跳转的文件的前缀和后缀，
     * 此方法底层的源码就是利用 internalResourceViewResolver() 方法中的代码实现的。
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/",".jsp");
    }
}
```

### 1.3 使用 web.xml 来启动服务 (基于Servlet2.5,过时)

#### 1.3.1 配置 DispatcherServlet 

- ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
           version="4.0">
  
      <!-- 实例化spring环境的 init spring -->
      <listener>
          <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
      </listener>
  
      <!--
          DispatcherServlet是springMVC的核心控制器，通过该控制器想起与具体的控制器分配请求，
          它也是spring管理最开始的部分，必须将它交给tomcat管理
      -->
      <servlet>
          <servlet-name>center-Servlet</servlet-name>
          <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
          <!-- spring mvc 的配置文件位置 -->
          <init-param>
              <param-name>contextConfigLocation</param-name>
              <param-value>classpath*:springMVC.xml</param-value>
          </init-param>
          <!-- tomcat加载的顺序，数字越小，加载越靠前 -->
          <load-on-startup>1</load-on-startup>
      </servlet>
      <!-- Dispatcher处理所有的请求 -->
      <servlet-mapping>
          <servlet-name>center-Servlet</servlet-name>
          <url-pattern>/</url-pattern>
      </servlet-mapping>
  
  </web-app>
  ```

#### 1.3.2 添加spring Servlet

1. 通过**实现接口自定义控制器**，再通过 spring-mvc.xml 配置将该控制器交由spring管理

- 自定义控制器：

  ```java
  package com.study.controller;
  
  import org.springframework.web.servlet.ModelAndView;
  import org.springframework.web.servlet.mvc.Controller;
  
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  
  public class DefaultController implements Controller {
  
      @Override
      public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
          // “/index”是试图文件的名字，通过视图解析器会找到 /WEB-INF/index.jsp
          return new ModelAndView("/index", "message", "这是通过xml实现org.springframework.web.servlet.mvc.Controller接口完成的控制器");
      }
  }
  ```

- 将该控制器交由spring容器管理：

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
         xmlns:mvc="http://www.springframework.org/schema/mvc"
         xsi:schemaLocation="http://www.springframework.org/schema/beans
          http://www.springframework.org/schema/beans/spring-beans.xsd
          http://www.springframework.org/schema/context
          http://www.springframework.org/schema/context/spring-context-4.3.xsd
          http://www.springframework.org/schema/mvc
          http://www.springframework.org/schema/mvc/spring-mvc-4.3.xsd">
  
      <!-- 视图解析器 -->
      <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"
            id="internalResourceViewResolver">
          <!-- 前缀 -->
          <property name="prefix" value="/WEB-INF/views/" />
          <!-- 后缀 -->
          <property name="suffix" value=".jsp" />
      </bean>
  
      <!-- 在这里配置bean，将com.study.controller.DefaultController类纳入spring容器的管理 -->
      <bean name="/defaultByXml" class="com.study.controller.DefaultController"></bean>
  </beans>
  ```

2. 通过**注解方式自定义控制器**，再将其交由spring容器管理

- 注解方式自定义控制器

  ```java
  package com.study.controller;
  
  import org.springframework.stereotype.Controller;
  import org.springframework.ui.Model;
  import org.springframework.web.bind.annotation.RequestMapping;
  
  @Controller
  public class DefaultXMLController2 {
  
      @RequestMapping("/defaultByXml2")
      public String index(Model model){
          model.addAttribute("message","这里是通过注解的方式实现自定义控制器");
          return "index";
      }
  
  }
  ```

- 配置文件中让spring开启注解功能

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
         xmlns:mvc="http://www.springframework.org/schema/mvc"
         xsi:schemaLocation="http://www.springframework.org/schema/beans
          http://www.springframework.org/schema/beans/spring-beans.xsd
          http://www.springframework.org/schema/context
          http://www.springframework.org/schema/context/spring-context-4.3.xsd
          http://www.springframework.org/schema/mvc
          http://www.springframework.org/schema/mvc/spring-mvc-4.3.xsd">
  
      <!-- 自动扫描包，实现支持注解的IOC -->
      <context:component-scan base-package="com.study" />
  
      <!-- Spring MVC不处理静态资源 -->
      <mvc:default-servlet-handler />
  
      <!-- 支持mvc注解驱动 -->
      <mvc:annotation-driven />
  
      <!-- 视图解析器 -->
      <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"
            id="internalResourceViewResolver">
          <!-- 前缀 -->
          <property name="prefix" value="/WEB-INF/views/" />
          <!-- 后缀 -->
          <property name="suffix" value=".jsp" />
      </bean>
  
      <!-- 在这里配置bean，将com.study.controller.DefaultController类纳入spring容器的管理 -->
      <bean name="/defaultByXml" class="com.study.controller.DefaultXMLController"></bean>
  </beans>
  ```

## 2. 知识点

### 2.1 添加 servlet 的方法：

1. 在 web.xml  中配置
2. 在对应的 Servlet 的类上面添加 @WebServlet 注解

### 2.2 tomcat 会执行 WebApplicationInitializer的onStartup方法原因

#### 2.2.1 查看方法：

* 在WebApplicationInitializer实现类的onStartup方法中设置断点，然后启动tomcat容器，再通过查看方法栈的调用层次，查看是从哪里进来的

* ![](images\tomcat调用WebApplicationInitializer的onStartup方法.png)

* org.springframework.web.SpringServletContainerInitializer源码：

  ```java
  package org.springframework.web;
  
  import java.lang.reflect.Modifier;
  import java.util.Iterator;
  import java.util.LinkedList;
  import java.util.List;
  import java.util.Set;
  import javax.servlet.ServletContainerInitializer;
  import javax.servlet.ServletContext;
  import javax.servlet.ServletException;
  import javax.servlet.annotation.HandlesTypes;
  import org.springframework.core.annotation.AnnotationAwareOrderComparator;
  import org.springframework.lang.Nullable;
  import org.springframework.util.ReflectionUtils;
  
  /**
   * @HandlesTypes({WebApplicationInitializer.class}) 注解的作用:
   * 将实现了 WebApplicationInitializer 接口的实现类实例化，然后将该实例化后的对象作为
   * onStartup方法的 Set<Class<?>> webAppInitializerClasses 参数值传进该方法
   */
  @HandlesTypes({WebApplicationInitializer.class})
  public class SpringServletContainerInitializer implements ServletContainerInitializer {
      public SpringServletContainerInitializer() {
      }
  
      public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) throws ServletException {
          List<WebApplicationInitializer> initializers = new LinkedList();
          Iterator var4;
          if (webAppInitializerClasses != null) {
              var4 = webAppInitializerClasses.iterator();
  
              while(var4.hasNext()) {
                  Class<?> waiClass = (Class)var4.next();
                  if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) && WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
                      try {
                          initializers.add((WebApplicationInitializer)ReflectionUtils.accessibleConstructor(waiClass, new Class[0]).newInstance());
                      } catch (Throwable var7) {
                          throw new ServletException("Failed to instantiate WebApplicationInitializer class", var7);
                      }
                  }
              }
          }
  
          if (initializers.isEmpty()) {
              servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
          } else {
              servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
              AnnotationAwareOrderComparator.sort(initializers);
              var4 = initializers.iterator();
  
              while(var4.hasNext()) {
                  WebApplicationInitializer initializer = (WebApplicationInitializer)var4.next();
                  initializer.onStartup(servletContext);
              }
  
          }
      }
  }
  
  ```

* **Tomcat一定会调用 javax.servlet.ServletContainerInitializer 的 onStartup 方法**

  ```java
  package javax.servlet;
  
  import java.util.Set;
  
  public interface ServletContainerInitializer {
      void onStartup(Set<Class<?>> var1, ServletContext var2) throws ServletException;
  }
  ```

* **但是Tomcat是如何获取到SpringServletContainerInitializer类的呢？**

  1. 因为只有 Tomcat 获取到SpringServletContainerInitializer类，它去执行才会SpringServletContainerInitializer 的 onStartup方法

  2. Tomcat是通过配置文件知道要加载 SpringServletContainerInitializer 这个类的

     ![](images\tomcat通过配置文件加载类.png)

#### 2.2.2 使用示例：

1. 实现  ServletContainerInitializer 重写 onStartup方法

   ```java
   package com.study.util;
   
   import javax.servlet.ServletContainerInitializer;
   import javax.servlet.ServletContext;
   import javax.servlet.ServletException;
   import javax.servlet.annotation.HandlesTypes;
   import java.util.Set;
   
   /**
    * 实现了 javax.servlet.ServletContainerInitializer 接口，重写onStartup方法，
    * 如果tomcat启动时，加载到了 CustomerInitializer 这个类，那么一定会执行这个重写的onStartup方法。
    */
   @HandlesTypes(HandlesTypesDemo.class)
   public class CustomerInitializer implements ServletContainerInitializer {
   
       @Override
       public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
           System.out.println("------- CustomerInitializer onStartup() ... ---------");
           System.out.println(set);
       }
   
   }
   ```

2. 在maven项目的resouces下配置META-INF\services\javax.servlet.ServletContainerInitializer 文件

   ```txt
   com.study.util.CustomerInitializer
   ```

3. @HandlesTypes的作用：拿到该注解value的值所对应的接口的所有实现类，将其作为实参传入方法中

   ```java
   package com.study.util;
   
   public interface HandlesTypesDemo {
       
   }
   ```

   ```java
   package com.study.util;
   
   public class HandlesTypesDemoImpl implements HandlesTypesDemo {
   
   }
   ```

   ![](images\servlet3.0新特新-tomcat加载类.png)

   - **这是利用了 Servlet3.0 的新特性实现的**

#### 2.2.3 作用：

- 动态插拔的jar包（作业）

### 2.3 maven项目中启动web项目方法

1. 写一个tomcat项目，然后将web项目作为webapp放到tomcat项目中跑

2. 使用idea的tomcat插件跑项目

3. 使用maven的命令跑项目：

   ```
   mvn tomcat7:run
   ```

   ![](images\maven启动tomcat.png)

## 3. 模拟spring boot（tomcat 内置到项目中）

### 3.1 添加依赖

```xml
<dependencies>
    <!--spring boot模拟: tomcat的源码包,springboot内部是依赖tomcat的源码包的-->
    <dependency>
        <groupId>org.apache.tomcat.embed</groupId>
        <artifactId>tomcat-embed-core</artifactId>
        <version>8.5.5</version>
    </dependency>
    <dependency>
        <groupId>org.apache.tomcat.embed</groupId>
        <artifactId>tomcat-embed-el</artifactId>
        <version>8.5.5</version>
    </dependency>
    <dependency>
        <groupId>org.apache.tomcat.embed</groupId>
        <artifactId>tomcat-embed-jasper</artifactId>
        <version>8.5.5</version>
    </dependency>
</dependencies>
```

### 3.2 配置启动 tomcat 源码

```java
package com.study.tomcat;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import javax.servlet.ServletException;
import java.io.File;

/**
 * 模拟springboot内部启动tomcat
 * 通过该类来启动tomcat
 */
public class SpringBootApplicationDemo {

    public static void run() throws ServletException {

        // 第一步：简单的启动 tomcat
        // 创建 Tomcat 的实例
        Tomcat tomcat  = new Tomcat();
        // 设置 tomcat 的端口号
        tomcat.setPort(9090);

        /**
         * 第二步：告诉tomcat源码在哪里,设置webapp位置
         * springboot的是没有webapp，因为springboot是打包成jar包的
         */
        // 获取当前项目的路径
        String sourcePath = SpringBootApplicationDemo.class.getResource("/").getPath();
        System.out.println(sourcePath); // /G:/blue_world/Documents/study-project/study-2021-01-06/spring-boot/target/classes/
        // 告诉tomcat源码在哪里
//        Context ctx = tomcat.addWebapp("/", new File("src/main/webapp").getAbsolutePath()); 这样写，在idea中寻找到父项目路径下，找不到
//        System.out.println(new File("src/main/webapp").getAbsolutePath()); G:\blue_world\Documents\study-project\study-2021-01-06\src\main\webapp

        Context ctx = tomcat.addWebapp("/", sourcePath);
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", sourcePath,"/"));
        ctx.setResources(resources);

        // 启动 tomcat
        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }
}
```

### 3.3 模拟 DispatcherServlet

#### 3.3.1 配置Servlet

```java
package com.study.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 模拟 spring中的 DispatcherServlet处理所有请求
 */
public class SpringServlet extends HttpServlet {

    /**
     * 此方法理论上是处理请求分配，
     * 这里只是间的做下载处理
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // "index.html"
        // 获取classpath的路径, 项目发布后最终index.html是放在classpath路径下面的,classes文件夹下面
        String classPath = SpringServlet.class.getResource("/").getPath();
        // /G:/blue_world/Documents/study-project/study-2021-01-06/spring-boot/target/classes/
        System.out.println(classPath);
        String fileName = request.getRequestURI();
        String path = classPath + fileName;
        System.out.println(path);
        // /G:/blue_world/Documents/study-project/study-2021-01-06/spring-boot/target/classes//index.html

        File file = new File(path);
        InputStream inputStream = new FileInputStream(file);
        byte[] buffer = new byte[2048];
        inputStream.read(buffer);
        String str = new String(buffer);
        System.out.println(str);
        response.setContentType("text/html");
        response.getWriter().write(str);
//        response.getOutputStream().write(buffer);
//        FileUtils.writeFile(file, response.getOutputStream());
    }

}
```

#### 3.3.2 将配置Servlet纳交由tomcat管理

```java
package com.study.config;

import com.study.servlet.SpringServlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

public class MyServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        System.out.println("com.study.config.MyServletContainerInitializer onStartup() ... ");
        // 将 自己模拟的 DispatcherServlet，即 SpringServlet 交给tomcat
        ServletRegistration.Dynamic registration =  servletContext.addServlet("springServlet", new SpringServlet());
        // 拦截所有的请求
        registration.addMapping("/");
    }

}
```

#### 3.3.3 配置文件，将MyServletContainerInitializer交由tomcat加载

```txt
com.study.config.MyServletContainerInitializer
```

![](images\模拟springboot-tomcat加载类.png)

### 3.4 启动模拟的springboot

```java
package com.study.app;

import com.study.tomcat.SpringBootApplicationDemo;
import javax.servlet.ServletException;

public class AppTest {

    public static void main(String[] args) {

        try {
            SpringBootApplicationDemo.run();
        } catch (ServletException e) {
            e.printStackTrace();
        }

    }

}
```

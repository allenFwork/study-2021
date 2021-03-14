# spring boot模拟

## 1. 模拟spring boot（tomcat 内置到项目中）

### 1.1 添加依赖

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

### 1.2 配置启动 tomcat 源码

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

### 1.3 模拟 DispatcherServlet

#### 1.3.1 配置Servlet

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

#### 1.3.2 将配置Servlet纳交由tomcat管理

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

#### 1.3.3 配置文件，将MyServletContainerInitializer交由tomcat加载

```txt
com.study.config.MyServletContainerInitializer
```

![](D:/myData/documents/git-repositories/study-2021/note/micro-service/images/模拟springboot-tomcat加载类.png)

### 1.4 启动模拟的springboot

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

## 2.  模拟上传功能

### 2.1 spring5.0上传文件的两种技术:

1. Apache Commons FileUpload
2. Servlet 3.0新特性

### 2.2 Apache Commons FileUpload (过时)

1. 配置一个 命名为  **<font color=red>multipartResolver</font>** 的  CommonsMultipartResolver 类型的 bean 对象

   ```java
   package com.study.config;
   
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.multipart.commons.CommonsMultipartResolver;
   
   @Configuration
   public class UploadConfig {
   
       /**
        * 使用 Apache Commons FileUpload 技术实现文件上传，
        * 可以通过向spring容器注入CommonsMultipartResolver的实例对象，
        * 但是这个实例对象的对应的BeanDefinition的BeanName必须是 multipartResolver
        */
       @Bean("multipartResolver")
       public CommonsMultipartResolver createCommonsMultipartResolver(){
           return new CommonsMultipartResolver();
       }
   
   }
   ```

2. 上传表单，使用 enctype="multipart/form-data" 属性

   ```html
   <html>
       <body>
           <form method="post" action="upload.do" enctype="multipart/form-data">
               <input type="file" name="fileName">
               <input type="submit">
           </form>
       </body>
   </html>
   ```

3. 使用 @RequestPart("xxx") 注解来接受请求文件

   ```java
   package com.study.controller;
   
   import org.springframework.stereotype.Controller;
   import org.springframework.util.FileCopyUtils;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RequestMethod;
   import org.springframework.web.bind.annotation.RequestPart;
   import org.springframework.web.multipart.MultipartFile;
   
   import java.io.*;
   
   @Controller
   public class UpLoadController {
   
       /**
        * 这里的处理方法中必须添加 @RequestPart("fileName") 这个注解
        * @param multipartFile
        */
       @RequestMapping(value = "/upload", method = RequestMethod.POST)
       public void upload(@RequestPart("fileName") MultipartFile multipartFile) {
           System.out.println("UpLoadController upload(MultipartFile multipartFile) ... ");
           // 将上传的文件下载
           OutputStream fileOutputStream = null;
           try {
               fileOutputStream = new FileOutputStream(new File("d:/download.txt"));
               FileCopyUtils.copy(multipartFile.getInputStream(), fileOutputStream);
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   
   }
   ```

4. 添加 Apache Commons FileUpload 的依赖

   ```xml
       <!--使用 Apache Commons FileUpload 技术实现文件上传,添加commons-fileupload依赖-->
       <dependency>
         <groupId>commons-fileupload</groupId>
         <artifactId>commons-fileupload</artifactId>
         <version>1.3.3</version>
       </dependency>
   ```

### 2.3 Servlet 3.0 自己实现了上传文件功能

1. spring boot 中自定义了 StandardServletMultipartResolver 类型的bean对象
2. StandardServletMultipartResolver 类型的对象能够直接使用 Servlet 3.0 上传文件功能

## 3. 知识点

### 3.1 为什么tomcat可以直接响应请求html文件的请求

子路老师的博客：

### 3.2 tomcat maven插件配置

```xml
<!--添加tomcat的maven插件-->
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <port>8888</port>
        <!-- 配置了path,访问就不需要带项目名字 -->
        <path>/</path>
    </configuration>
</plugin>
```

### 3.3 spring boot所完成的事

1. 零 xml 配置，不需要配置 web.xml、springmvc-servlet.xml、applicationContext.xml等配置文件
2. 内嵌 tomcat 
3. 自动配置
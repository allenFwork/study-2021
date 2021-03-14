# SpringMVC源码解析

## 1. 创建项目

### 1.1 创建java项目

- 创建该项目并依赖springMVC，从而产看springMVC源码
- 创建步骤：
  1. 在spring5的源码项目中，创建一个新的模块，该模块用gradle进行管理，创建java项目，不要创建web项目，web项目过于复杂
  2. ![](images\springMVC\创建项目1.png)

### 1.2 代码：

- 如果是web项目，就通过实现WebApplicationInitializer类来初始化spring环境

  ```java
  package com.study.web;
  
  import javax.servlet.ServletContext;
  import javax.servlet.ServletException;
  
  public class WebApplicationInitializer implements org.springframework.web.WebApplicationInitializer {
  
  	@Override
  	public void onStartup(ServletContext servletContext) throws ServletException {
  		// 初始化spring的环境 和 springWeb的环境
  		System.out.println("=========== tomcat 容器执行的方法 =============");
  	}
  
  }
  ```

- springboot都是java项目，不使用web项目，打成jar包，不打成war包

  ```java
  package com.study.web;
  
  import com.study.config.AppConfig;
  import org.apache.catalina.Context;
  import org.apache.catalina.LifecycleException;
  import org.apache.catalina.startup.Tomcat;
  import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
  import org.springframework.web.servlet.DispatcherServlet;
  
  import java.io.File;
  
  public class SpringApplicationCustomer {
  	public static void run() throws LifecycleException {
  		/**
  		 * spring环境的初始化
  		 * 1. spring ioc 环境初始化完成
  		 * 2. AnnotationConfigWebApplicationContext 此处使用的是 AnnotationConfig “Web” ApplicationContext,
  		 *    不是AnnotationConfigApplicationContext,所以初始化了spring ioc 和 spring web 两个环境
  		 * 3. 此处还没有完成 spring web 的环境，spring mvc(也就是spring web)的环境是基于 DispatcherServlet的，
  		 *    所以在web容器（tomcat）中添加了DispatcherServlet，才完成了spring web环境的初始化。
  		 * 4. spring将 spring web归类于spring framework
  		 * 5. 因为传统的web项目是基于web.xml的，所以以前的spring web环境是通过web.xml配置的，现在的web项目已经没有web.xml，
  		 *    所以需要以下处理来完成 spring web 环境。
  		 */
  		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
  		context.register(AppConfig.class);
  		context.refresh();
  
  		// tomcat的工作目录
  		File baseDir = new File(System.getProperty("java.io.tmpdir"));
  		System.out.println(baseDir); // C:\Users\Allen\AppData\Local\Temp
  		// 启动 tomcat
  		Tomcat tomcat = new Tomcat();
  		// tomcat设置端口号
  		tomcat.setPort(9090);
  
  		/**
  		 * addWebapp，表示这是一个web项目
  		 * 第一个参数是 contextPath，项目的访问路径
  		 * 第二个参数是 项目有的web目录
  		 * 所以这里不能使用 addWebapp 方法
  		 *
  		 * 不使用addWebapp方法，导致tomcat容器启动初始化过程中无法调用 WebApplicationInitializer 的 onStartup 方法，
  		 * 所以spring的环境无法在 onStartup 方法中进行初始化。所以spring环境初始化只能在这个方法中进行了，如上述代码。
  		 */
  //		tomcat.addWebapp("/","d:/webapp/index.html");
  
  		Context rootContext = tomcat.addContext("/", baseDir.getAbsolutePath());
  		DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
  		// tomcat启动的过程当中，就会调用 DispatcherServlet的init方法，该方法用来初始化controller和请求映射
  		tomcat.addServlet(rootContext, "dispatcherServlet", dispatcherServlet).setLoadOnStartup(1);
  		/*----------------------------------- 到这里完成了spring web 环境的初始化 -----------------------------*/
  		tomcat.start();
  		tomcat.getServer().await();
  
  	}
  }
  ```

- controller类

  - spring中controller类分为三种：

    1. 实现 Controller 接口
    2. 添加 @Controller 注解

    3. 

  - 代码：

    ```java
    package com.study.controller;
    
    
    import org.springframework.stereotype.Component;
    import org.springframework.web.servlet.ModelAndView;
    import org.springframework.web.servlet.mvc.Controller;
    
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    
    @Component("/controller")
    public class IndexController implements Controller {
    
    	@Override
    	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    		return null;
    	}
    
    }
    ```

    ```java
    package com.study.controller;
    
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.RequestMapping;
    
    @Controller
    public class IndexController2 {
    
    	@RequestMapping("/index")
    	public void index() {
    
    	}
    
    	@RequestMapping("/index2")
    	public void index2() {
    
    	}
    
    }
    ```

  - DispatcherServlet init方法执行下去，最终会将controller类配置到map中：

    1. Bea
    2. d

  - ![](images\springMVC\controller配置对应的路径到Map中.png)

### 1.3 启动项目

- 启动项目时，会出现报错，找不到类

  ![](images\springMVC\启动项目报错.png)

- 出现原因：

  直接寻找，能够找到这个spring-oxm模块中石油这些类的源码的，但是为什么会在启动时找不到这些类呢？因为启动时，找的是编译打包后的二进制文件，spring-oxm之前没有编译过所以找不到。

- 解决办法：

  在spring-oxm中跑测试类，进行编译，大门时会发现编译不过去，因为有两个测试类中引用别的类时，有问题，直接将这两个测试类注释掉。

  ![](images\springMVC\spring-oxm测试类报错.png)

  测试类编译完成后，spring-oxm模块会生成对应的out文件夹







  



## 2. spring MVC分析

### 2.1 web项目的演变

**以前：request --> servlet --> 方法调用 -->  controller**

- 不可能是 request --> servlet --> 转发（forward） -->  controller，因为**转发只能到 jsp 或者其余的 servlet，因为转发还是请求**
- 方法调用的底层是通过反射实现的，controller是普通的 Class

**现在：request --> ! servlet（非servlet的普通类）**

### 2.2 spring mvc源码分析

#### 2.2.1 项目启动流程



#### 2.2.2 请求处理流程

- BeanNameMapping --> MethodMapping --> 拦截所有的请求，做静态处理  --> 404



#### 2.2.3 

HandlerMapping 





## 小技巧：

1. idea 中使用 ctrl + F12 可以查看本类中所有的方法
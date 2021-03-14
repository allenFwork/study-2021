# Tomcat

## 1. Tomcat总体框架

- tomcat历史

- Tomcat总体框架

- Tomcat源码搭建



### 1.1 Tomcat历史

- Tomcat最初有sun公司的架构师James Duncan Davidson开发，名称“JavaWebServer”

- 1999与Apache软件基金会旗下的JServ项目合并，也就是Tomcat。

- 2001 tomcat4.0 里程碑式的版本。完全重新设计了其架构，并实现了Servlet2.3 和 JSP 1.2规范。

到目前，Tomcat已经成为成熟的Servlet容器产品，并作为 JBoss等应用的服务器的内嵌Servlet容器

- 版本、规范支持：

| Tomcat    | 6.X   | 7.X   | 8.X   | 8.5.X | 9.X   |
| --------- | ----- | ----- | ----- | ----- | ----- |
| JDK       | >=5.0 | >=6.0 | >=7.0 | >=7.0 | >=8.0 |
| Servlet   | 2.5   | 3.0   | 3.1   | 3.1   | 4.0   |
| JSP       | 2.1   | 2.2   | 2.3   | 2.3   | 2.3   |
| EL        | 2.1   | 2.2   | 3.0   | 3.0   | 3.0   |
| WebSocket | N/A   | 1.1   | 1.1   | 1.1   | 1.1   |

- Tomcat许可：
  - 完全免费
  - 修改后不必公开源代码，但是项目开完，不能以Tomcat、catalina命名发布

### 1.2 总体结构

#### 1.2.1 Server：

- 接受请求并解析，完成相关任务，返回处理结果

- 通常情况下使用Socket监听服务器指定端口来实现该功能，一个最简单的服务设计如下：

  ![](images\tomcat\server框架.png)

- **Start()：**启动服务器，打开socket连接，监听服务端口，接受客户端请求、处理、返回响应

- **Stop()：**关闭服务器，释放资源

- 缺点：
  - 这样的设计 只能进行简单处理，访问量不能太大，才不会出现问题
  - 请求监听和请求处理放一起，扩展性很差（协议的切换 tomcat独立部署使用HTTP协议，与Apache集成时使用AJP协议）

- 改进：
  - 网络协议与请求处理分离

#### 1.2.2 改进1：网络协议与请求处理分离

![](images\tomcat\connector-container.png)

- 一个Server包含 <font color=red>**多个**</font> Connector（链接器）和 <font color=red>**多个**</font> Container（容器）

- Connector：开启Socket并监听客户端请求，返回响应数据；

- Container：负责具体的请求处理

- 缺点：
  - 因为有多个Connector和多个Container，所以对于Connector接受的请求交由哪个Container处理，需要建立映射规则
  - server中包含Connector和Container的映射

- 改进：
  - 添加service的组件概念

#### 1.2.3 改进2：添加service的组件概念

![](images\tomcat\service.png)

- 一个Server可以包含<font color=red>多个</font>Service，每一个Service都是独立的，他们共享一个JVM以及系统类库。

- **<font color=red>一个</font>Service负责维护<font color=red>多个</font>Connector和<font color=red>一个</font>Container**，这样来自Connector的请求只能由它所属的Service维护的Container处理。
- 这样最终实现了 多个 Connector 到 多个 Container的关系映射，实质是简化为了多对一的映射

#### 1.2.4 改进3

- 在这里Container是一个通用的概念，为了明确功能，并与Tomcat中的组件名称相同，可以将Container命名为Engineer

  ![](images\tomcat\engine.png)

- Engine 就是 Servlet引擎

- 一个 Service 包含多个 Connector 和 一个 Engine

#### 1.2.5 改进5：将web应用从engine中分离出来

![](images\tomcat\context.png)

- 在Engine容器中需要支持管理WEB应用，当接收到Connector的处理请求时，Engine容器能够找到一个合适的Web应用来处理，因此在上面设计的基础上增加Context来表示一个WEB应用，并且一个Engine可以包含**多个**Context。
- 缺点：
  - 应用服务器需要将每个域名抽象为一个虚拟主机

#### 1.2.6 改进6：将管理主机地址的功能分离出来

![](images\tomcat\host.png)

- Host：

  - 我们可以看成`虚拟主机`，一个tomcat可以支持多个虚拟主机 

  - 指一个虚拟主机，进行主机服务，可以看作是C盘下的host文件

- Engine根据不同的请求，可以先分到不同的主机上去，在不同的主机上再根据请求中的web应用名称，选择不同的应用服务来处理

- 在一个web应用中，可以包含多个Servlet实例来处理来自不同的链接请求，因此我们还需要一个组件概念来表示Servlet定义，即Wrapper。

  ![](images\tomcat\wrapper.png)

- 在Container容器中，有Engine、Host、Context、Wrapper等，可以理解为Container的子类  

  ![](images\tomcat\Container.png)

- 容器之间的组合关系是一种弱依赖，用虚线表示

- tomcat每隔30秒会扫描，查看host里的context是否全部注册，context是否全部注册wrapper

#### 1.2.7 Connector

- 链接器功能：

  1. 监听 Socket 服务，读取请求信息
  2. 按照协议解析请求
  3. 配置对应的容器
  4. 通过 Socket 返回响应信息

- tomcat支持多种协议：HTTP、AJP （默认这两种）

- tomcat支持多种IO：BIO(8.0以后移除了)、NIO、NIO2、APR

- 所以tomcat的响应方式有 6 种组合方法，处理方式都不一样，将功能全交给Connector处理太耦合，要添加组件，进行解耦

  ![](images\tomcat\Connector.png)

- EndPoint：监听器，监听端口，监听请求，接受请求

- Processor：处理器，处理请求数据

- Mapper：映射器，管理请求和容器之间的映射关系

- CoyoteAdapter：适配器， 根据 具体的处理器 处理的结果，再通过映射器的映射关系，适配到对应的容器 

### 1.3 整体流程

1. Server启动，Service也就启动了

2. Service主要分为 链接器(Connector) 和 容器(Container)

3. 链接器中 EndPoint开始监听请求，一接收到请求就将其交由对应的处理器处理请求数据

4. 处理器处理完请求数据，再将请求数据交由适配器，适配器通过 Service管理的映射器(Mapper)，通过映射器给的映射关系，将请求交给对应的容器处理

   - 这是**适配器模式**

   - Mapper中是map的映射关系，是由service维护的，当将 Context、Wrapper 发布到service里时，Mapper的 监听器 (MapperListener) 就会监听到，service就会更新Mapper管理的映射关系
   - 虽然每个service中只有一个Container，但是如果没有对应的映射关系，是无法将请求交给对应的容器的

5. 请求传到容器中，Engine开始处理，交给Host，查看请求中的主机，再选择相应的主机，接着再看请求中的web服务名称，选择相应的Context容器，最后再选择相应的Wrapper进行处理请求的逻辑处理

   - 这就是**责任链模式**

### 1.4 生命周期

![](images\tomcat\LifeCycle.png)

![](images\tomcat\tomcat框架和流程2.png)

- 每一个组件都有启动、停止等生命周期方法，拥有生命周期的特征。所以定义一个通用的LifeCycle接口  

### 1.5 解决并发问题 和 启动

![](images\tomcat\Executor.png)

- Executor：tomcat 默认的线程池

  - 接收多个请求时，由线程池去处理

  - tomcat启动时，就创建好线程池

  - server创建的线程池，是共享的，是所有组件都可以使用的

  - 每个组件都可以自己创建自己的线程池，这个线程池不是共享的，其他的组件无法共享

- BootStrap：tomcat的入口，启动Catalina

## 2. Tomcat 启动过程源码分析

### 2.1 配置环境

- 配置源码环境 和 运行环境

  ![](images\tomcat\源码.png)

- 在运行环境的 catalina-home 中添加 ：

  - conf 文件夹 ：从源码包中直接复制过来的
  - lib 文件夹
  - logs 文件夹 ：日志信息存放，Tomcat启动自动会生成
  - temp 文件夹
  - webapps 文件夹：用于默认的部署目录

  ![](images\tomcat\运行环境1.png)

- 配置 Maven 依赖

  1. 在项目根目录下创建 pom.xml，其内容如下：

     ```xml
     <?xml version="1.0" encoding="UTF-8"?>
     <project xmlns="http://maven.apache.org/POM/4.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
     
         <modelVersion>4.0.0</modelVersion>
         <groupId>com.study</groupId>    
         <artifactId>study-tomcat-8</artifactId>    
         <name>tomcat-source</name>    
         <version>1.0</version>    
         <packaging>pom</packaging> 
         <modules>    
             <module>apache-tomcat-8.5.61-src</module>    
         </modules>    
     </project>
     ```

     ![](images\tomcat\源码2-pom.png)

  2. 在源码模块中配置pom.xml文件，其内容如下：

     ```xml
     <?xml version="1.0" encoding="UTF-8"?>
     <project xmlns="http://maven.apache.org/POM/4.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
     
         <modelVersion>4.0.0</modelVersion>
         <groupId>org.apache.tomcat</groupId>
         <artifactId>Tomcat8.5.61</artifactId>
         <name>Tomcat8.5.61</name>
         <version>8.5</version>
     
     
     	<build>    
             <finalName>Tomcat8.0</finalName>    
             <sourceDirectory>java</sourceDirectory>    
             <testSourceDirectory>test</testSourceDirectory>    
             <resources>    
                 <resource>    
                     <directory>java</directory>    
                 </resource>    
             </resources>    
             <testResources>    
                 <testResource>    
                     <directory>test</directory>    
                 </testResource>    
             </testResources>    
             <plugins>    
                 <plugin>    
                     <groupId>org.apache.maven.plugins</groupId>    
                     <artifactId>maven-compiler-plugin</artifactId>    
                     <version>2.0.2</version>    
         
                     <configuration>    
                         <encoding>UTF-8</encoding>    
                         <source>1.8</source>    
                         <target>1.8</target>    
                     </configuration>    
                 </plugin>    
             </plugins>    
         </build>    
     
        <dependencies>  
             <dependency>  
                 <groupId>org.easymock</groupId>  
                 <artifactId>easymock</artifactId>  
                 <version>3.5</version>  
                 <scope>test</scope>  
             </dependency>  
       
             <dependency>    
                 <groupId>junit</groupId>    
                 <artifactId>junit</artifactId>    
                 <version>4.12</version>  
                 <scope>test</scope>    
             </dependency>    
             <dependency>    
                 <groupId>ant</groupId>    
                 <artifactId>ant</artifactId>    
                 <version>1.7.0</version>    
             </dependency>    
             <dependency>    
                 <groupId>wsdl4j</groupId>    
                 <artifactId>wsdl4j</artifactId>    
                 <version>1.6.2</version>    
             </dependency>    
             <dependency>    
                 <groupId>javax.xml</groupId>    
                 <artifactId>jaxrpc</artifactId>    
                 <version>1.1</version>    
             </dependency>    
             <dependency>    
                 <groupId>org.eclipse.jdt.core.compiler</groupId>    
                 <artifactId>ecj</artifactId>    
                 <version>4.6.1</version>  
             </dependency>    
         </dependencies>    
     </project>
     ```

- 配置tomcat启动文件：

  ![](images\tomcat\EditConfiguration.png)

  - 虚拟机参数配置：

    ```
    -Dcatalina.home=catalina-home
    -Dcatalina.base=catalina-home
    -Djava.endorsed.dirs=catalina-home/endorsed
    -Djava.io.tmpdir=catalina-home/temp
    -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager
    -Djava.util.logging.config.file=catalina-home/conf/logging.properties
    ```

- 启动程序测试

  - 测试包一直报错，将整个测试包删掉

- 启动完成后，浏览器跳转 localhost:8080 报500错误：

  - 原因是我们直接启动 org.apache.catalina.startup.Bootstrap 的时候没有加载org.apache.jasper.servlet.JasperInitializer，从而无法编译JSP。

  - 这在Tomcat6/7是没有这个问题的。解决办法是在tomcat的源码org.apache.catalina.startup.ContextConfig 中手动将JSP解析器初始化：

    ![](images\tomcat\初始化JSP解析器.png)

- 最是实现了 将源码与对应实例 分开的项目

- 默认情况下我们必须把应用程序部署到catalina-home/webapps下，如何直接部署访问外部的应用程序?我们只需要修改server.xml和web.xml配置即可。

  1) 编辑

  2) catalina-home/conf/server.xml

  ```xml
  <Context path="/tomcatsrc-web"
  
  ​         reloadable="true"
  
  ​         debug="0"         docBase="c:\code\work\tomcatsrc2018\tomcatsrc-web\target\tomcatsrc-web-1.0-SNAPSHOT"         workDir="c:\code\work\tomcatsrc2018\tomcatsrc-web\target\tomcatsrc-web-1.0-SNAPSHOT\work"
  
  ​         crossContext="true" />
  ```

### 2.2 



## 2. Tomcat Web请求过程源码分析



## 3. Tomcat相关协议



## 4. Tomcat相关协议



## 5. Tomcat详细配置



## 6. Tomcat与Apache、Nginx集成



## 7. Tomcat性能优化


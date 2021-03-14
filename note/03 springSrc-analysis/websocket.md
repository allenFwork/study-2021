# WebScoket

## 1. websocket原生使用

### 1.1 新建maven的web项目

- 配置pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.study</groupId>
  <artifactId>webSocket</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>war</packaging>

  <name>webSocket Maven Webapp</name>
  <!-- FIXME change it to the project's website -->
  <url>http://www.example.com</url>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
  </properties>

  <dependencies>

    <dependency>
      <!-- websocket的依赖 -->
      <groupId>javax</groupId>
      <artifactId>javaee-api</artifactId>
      <version>8.0</version>
      <scope>provided</scope>
    </dependency>

  </dependencies>

  <build>
    <finalName>webSocket</finalName>
    <plugins>
      <!--
        可以使用tomcat作为此web项目的容器，有两种方法：
          1. 使用idea配置 edit configuration 中添加tomcat容器
          2. 使用maven管理的插件，直接将tomcat容器的插件添加到该maven项目中
        使用方法一，麻烦。
        使用方法二，因为此web项目是用来测试websocket协议的，但是tomcat维护的maven里tomcat插件只到7的版本，
        这个版本不支持websocket协议，所以也没法使用tomcat插件完成此项目。
        所以使用 jetty 容器作为此web项目的容器。
      -->
      <!-- 添加jetty的插件,使其作为该web项目的容器-->
      <plugin>
        <groupId>org.eclipse.jetty</groupId>
        <artifactId>jetty-maven-plugin</artifactId>
        <version>9.4.14.v20181114</version>
      </plugin>
    </plugins>
  </build>
  
</project>
```

- 配置 Edit Configuration 运行

  ![](images\websocket\idea-jetty.png)

### 1.2 代码

- 代码：

  ```
  
  ```

  ```
  
  ```

- 测试：

  使用 谷歌 和 ie 开启两个客户端同时请求服务端

### 1.3 使用场景

- 作为观看视频时的弹幕



## 2. websocket在spring boot中使用

### 2.1 知识点

1. 通过浏览器可以**直接访问**到springboot项目中**resources资源路径(classpath)**下的哪些文件夹下的资源：

- resources
- META-INF/resources
- static
- public

2. 为什么可以直接访问？

- 因为源码org.springframework.boot.autoconfigure.web.ResourceProperties类中

- 设置了CLASSPATH_RESOURCE_LOCATIONS属性

  ```java
  private static final String[] CLASSPATH_RESOURCE_LOCATIONS = 
      new String[]{"classpath:/META-INF/resources/", 
                   "classpath:/resources/",
                   "classpath:/static/", 
                   "classpath:/public/"
                  };
  ```



### 2.2 




























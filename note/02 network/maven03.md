# maven 补充（图灵）

## 一. maven 安装与核心概念

### 1. 安装



### 2. maven编译

1. 创建 maven项目

- 使用 git 的命令窗口模拟创建项目

- 约定大于配置，项目的结构：

  pom.xml 文件

  src/main/java 目录

  src/main/resources 目录

- **编译 src/main/java 和 src/main/resources 目录下的文件到 target/classes 下**

2. 编译

![](\images\images-maven\maven编译.png)

- mvn compile; 编译当前项目

3. 创建静态文件，编译

![](\images\images-maven\maven编译2.png)

- **find -type f：显示当前目录及子目录下所有的文件**
- touch xxx ：创建xxx文件

### 3. maven打包

- 打包就是将 target/classes 下的文件 压缩到 target目录下，生成 xxx.jar 文件（xxx.war文件）
- ![](\images\images-maven\打包.png)
- 命令：mvn package;

### 4. maven 单元测试

#### 4.1 maven原生测试

- **类以 Test开头，方法也以 test开头的，这个默认作为maven的测试方法**

- ```java
  package com.study.test;
  
  public class TestHello {
  	public void testSayHello() {
  		System.out.println("run test ...");
  	}
  }
  ```

- ![](\images\images-maven\maven测试1.png)

- ![](\images\images-maven\maven测试2.png)

- ![](\images\images-maven\测试报告.png)

#### 4.2  junit测试

1. 使用 junit 进行测试，添加依赖
2. maven中添加了junit的依赖，那么maven默认的原生测试就会失效
3. 执行 mvn clean test; 测试

- ```java
  package com.study.test;
  import org.junit.Test;
  import org.junit.Assert;
  
  public class TestHello {
  	
  	public void testSayHello() {
  		System.out.println("run test ...");
  	}
  	
  	@Test
  	public void testSayHello2() {
  		System.out.print("run junit test ...");
  		Assert.assertEquals("1", "2");
  	}
  	
  }
  ```

- ![](\images\images-maven\maven通过junit测试结果.png)

### 5. maven依赖管理

1. maven默认配置的仓库路径： `C:\Users\用户名\.m2\repository `

2. maven默认配置文件的路径：`C:\Users\用户名\.m2\setting.xml` 

- 如果该 setting.xml 存在，那么的安装目录下 conf/setting.xml 中配置会被此文件中的配置覆盖



## 二. maven的核心配置

### 1. 项目依赖

项目依赖是指 maven 通过 依赖传播、依赖优先原则、可选原则、排除依赖、依赖范围等特性来管理项目ClassPath

#### 1.1 依赖传播

我们的项目通常依赖第三方组件，而第三方组件又会依赖其他组件，遇到这种情况 maven 会将依赖网络中的所有节点都加入到 ClassPath中，这就是依赖的传播特性。

- 在项目中使用 spring-webmvc 依赖时，会自动添加他所需要的依赖
- ![](\images\images-maven\依赖传播1.png)

#### 1.2 依赖优先原则

基于以来传播特性，导致整个依赖网络会很复杂，难免会出现相同组件不同版本的情况。maven此时会基于依赖优先原则选择其中一个版本。

> **第一原则：最短路径优先**

> **第二原则：相同路径下配置在前的优先**

##### 1.2.1 第一原则演示

在添加了 spring-webmvc依赖后，添加 commons-logging 的依赖

![](\images\images-maven\依赖优先原则1.png)

- 上述例子中，项目一开始通过 spring-webmvc 依赖了 1.1.3 版本的 commons-logging ，后来在项目的配置中直接添加了 1.2 版本的 commons-logging，**基于最短路径原则最终引入项目的是1.2版本**

##### 1.2.2 第二原则演示

1. study-maven-child    添加依赖 spring-web.4.3.8.RELEASE
2. study-maven-parent 添加依赖 study-maven-child 和 spring-webmvc.4.0.4.RELEASE

3. 配置完成后查看，项目的依赖管理

![](\images\images-maven\依赖优先原则2.png)

- 有两个版本的 spring-web ，且路径结点数相同，都是2，所以第一原则无法判断出选择哪个

- 通过第二原则，比较哪个配置在前面：

  study-maven-child 配置在 spring-webmvc 前面，所以选择 4.3.8 的 spring-web 

##### 1.2.3 特殊情况

同一个项目的一个pom文件中同时配置了两个版本的同一个依赖，那么谁配置在后面，谁生效

![](\images\images-maven\特殊情况.png)

#### 1.3 可选依赖

可选依赖表示这个以来不是必须的。通过在 `<dependency> ` 添加 `<optional>`true`</optional>` 表示，默认是不可选的。可选的依赖不会被传递。

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>4.3.8.RELEASE</version>
    <optional>true</optional>
</dependency>
```

![](\images\images-maven\可选依赖.png)

- study-maven-child 的 spring-web 没有传递给 study-maven-parent

#### 1.4 排除依赖

排除指定的间接依赖，通过 `<exclusions>` 配置排除指定组件。

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-webmvc</artifactId>
    <version>4.0.4.RELEASE</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

#### 1.5 依赖范围

像 junit 这个组件，我们只有运行测试用例的时候才用到，这就没有必要再打包的时候将 junit.jar 包也构建进去，可以通过 Maven 的依赖范围配置 `<scope>` 来进行配置。

maven支持以下四种依赖范围：

1. **<font color=red>compile(默认)：</font>**

- 编译范围，编译和打包都会依赖

2. **<font color=red>provided：</font>**

- 编译时会依赖，但是不会打包进去。如：servlet-api.jar

3. **<font color=red>runtime：</font>**

- 打包时依赖，编译不会。如：mysql-connector-java.jar（运行时才用到，编译时只用到DriverManager，并不需要使用到驱动包里的类对象）

4. **<font color=red>test：</font>**

- 编译运行测试用例依赖，但是不会打包进去。如：junit.jar

5. **<font color=red>system：</font>**

- 表示由系统中CLASSPATH指定，编译时依赖，不会打包进去。配合 `<systemPath>` 一起使用，如：java.home 下的 tool.jar

- 使用下面这个插件可以将system的依赖也打包进去：

  ```xml
  <build>
      <plugins>
          <plugin>
              <groupId>org.apache.maven.plugins</groupId>
              <artifactId>maven-dependency-plugin</artifactId>
              <version>2.10</version>
              <executions>
                  <execution>
                      <id>copy-dependency</id>
                      <phase>compile</phase>
                      <goals>
                          <goal>copy-dependencies</goal>
                      </goals>
                      <configuration>
                          <outputDirectory>${project.build.directory}/${project.build.finalName}/WEB-INF/lib</outputDirectory>
                          <includeScope>system</includeScope>
                          <!-- 用来排除掉tools.jar -->
                          <excludeGroupIds>som.sun</excludeGroupIds>
                      </configuration>
                  </execution>
              </executions>
          </plugin>
      </plugins>
  </build>
  ```

  

### 2. 项目聚合与继承

#### 2.1 聚合

指将多个模块整合在一起，统一构建，避免一个一个的构建，聚合需要一个父工程，然后使用 `<module>` 进行配置其中对应的子工程的相对路径。

```xml
<modules>
    <module>study-maven-child1</module>
    <module>study-maven-child2</module>
</modules>
```

#### 2.2 继承

继承指子工程继承父工程 当中的属性、依赖、插件等配置，避免重复配置。

1.属性继承

2.依赖继承

3.插件继承

上面三个配置，子工程都可以进行重写，重写之后以子工程的为准。

#### 2.3 依赖管理

通过继承的特性，子工程是可以间接继承父工程的依赖，但多个子工程依赖有时并不一致，这时就可以**在父工程中加入 `<dependencyManagement>` 声明该工程需要的 jar 包**，然后在子工程中引入。

```xml
<!-- 父工程中声明 junit 4.12 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
    </dependencies>
</dependencyManagement>
<!-- 子工程中引入 -->
<dependencies>
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
    </dependency>
</dependencies>
```

#### 2.4 项目属性

1. 通过 `<properties>` 配置参数（自定义），可以简化配置

```xml
<properties>
    <my.project.value>parameter value</my.project.value>
</properties>
```

2. maven 默认的属性

```
${basedir}                       项目根路径
${version}                       项目版本
${project.basedir}               同 ${basedir
${project.version}               同 ${version}    
${project.build.directory}       构建目录，缺省为 target
${project.build.sourceEncoding}  表示主源码的编码格式
${project.build.sourceDirectory} 表示主源码的路径
${project.build.finalName}       表示输出文件名称
${project.build.outputDirectory} 构建过程输出目录，缺省为 target/classes
```



### 3. 项目构建配置

#### 3.1 构建资源配置

基本配置示例：

```xml
<defaultGoal>package</defaultGoal>
<directory>${basedir}</directory>
<finalName>${artifactId}-${version}</finalName>
```

- defaultGoal，执行构建是默认的goal或phase，如 jar:jar 或 pacckage 等
- directory，构建的结果所在的路径，默认为 ${basedir}/target 目录
- finalName，构建的最终结果的名字，该名字可能在其他 plugin 中被改变



`<resources>` 配置示例

```xml
<build>
    <resources>
		<!-- maven打包时，默认只将 src/main/java 目录下的 .java 文件打包进去 -->
    	<!-- 添加了此配置后，打包时会将src/main/java目录下所有.MF和.XML文件也打包进去 -->
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.MF</include>
                <include>**/*.XML</include>
            </includes>
            <filtering>true</filtering>
        </resource>
        <!-- 添加了此配置后，打包时会将 src/main/resources 目录下所有文件打包进去 -->
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*</include>
                <include>*</include>
            </includes>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

- resources，build 过程中涉及的资料文件

  - **targetPath**，资料文件的目录路径
  - **directory**，资料文件的路径，默认位于 ${basedir}/src/main/resources/ 目录下
  - **includes**，一组文件名的匹配模式，被匹配的资料文件将被构建过程处理
  - **excludes**，一组文件名的匹配模式，被匹配的资料文件被构建过程忽略。同时呗 includes 和 excludes 匹配的资料文件，将被忽略。
  - **filtering**：默认false，true表示 通过参数 对 资源文件中的 ${key} 在**编译时进行动态变更**，替换源可以是 -Dkey 和 pom 中的 `<properties>` 值 或 `<filters>` 中指定的 Properties 文件。

- filtering设置为true，动态修改 变量值

  1. 通过 pom 中的 ` <properties>`

     ![](\images\images-maven\filtering动态变更1.png)

  2. 通过 -Dkey 

     ![](\images\images-maven\filtering动态变更2.png)

  - -Dkey 的优先级 高于 pom 中的 ` <properties>`，同时设置了同一个变量，-Dkey会覆盖掉
  - 通过这种动态设置的方式，能够实现 开发、测试、生产，**不同环境使用不同的配置文件打包**

#### 3.2编译插件





#### 3.3 profile 指定编译环境



## 四、maven生命周期

### 1. 生命周期的概念与意义

​	在项目构建时通常会包含清理、编译、测试、打包、验证、部署，文档生成等步骤，maven 统一对其进行了整理抽像成 **三个生命周期 (lifecycle)及各自对应的多个阶段(phase)**。这么做的意义是：

1. 每个阶段都成为了一个扩展点，可以采用不同的方式来实现，提高了扩展性与灵活性。

2. 规范统一了maven 的执行路径。

在执行项目构建阶段时可以采用jar方式构建，也可以采用war包方式构建提高了灵活性。我们可以通过命令 mvn ${phase name} 直接触发指定阶段的执行如：

- 演示 phase 的执行

1. **执行清理phase**

> mvn clean

2. **执行compile phase**

> mvn compile

3. **同时执行 清理 和 编译**

> mvn clean compile

 

<img src="images\images-maven\default生命周期.png" style="zoom:50%;" />

### 2. maven三大生命周期与其对应的phase(阶段)

#### 2.1 maven 三大生生命周期

- maven 总共包含三大生生命周期

1. <font color=red>**clean Lifecycle** </font>：清理生命周期，用于清理项目

2. <font color=red>**default Lifecycle**</font>：默认生命周期，用于编译、打包、测试、部署等

3. <font color=red>**site Lifecycle**</font> ：站点文档生成，用于构建站点文档

| **生命周期(lifecycle)** |     **阶段(phase)**     | **描述(describe)** |
| :---------------------: | :---------------------: | :----------------: |
|     clean Lifecycle     |        pre-clean        |       预清理       |
|                         |          clean          |        清理        |
|                         |       post-clean        |      清理之后      |
|    default Lifecycle    |        validate         |        验证        |
|                         |       initialize        |       初始化       |
|                         |    generate-sources     |                    |
|                         |     process-sources     |                    |
|                         |   generate-resources    |                    |
|                         |    process-resources    |                    |
|                         |         compile         |        编译        |
|                         |     process-classes     |                    |
|                         |  generate-test-sources  |                    |
|                         |  process-test-sources   |                    |
|                         | generate-test-resources |                    |
|                         | process-test-resources  |                    |
|                         |      test-compile       |     编译测试类     |
|                         |  process-test-classes   |                    |
|                         |          test           |      执行测试      |
|                         |     prepare-package     |     构建前准备     |
|                         |         package         |      打包构建      |
|                         |  pre-integration-test   |                    |
|                         |    integration-test     |                    |
|                         |  post-integration-test  |                    |
|                         |         verify          |        验证        |
|                         |         install         |   上传到本地仓库   |
|                         |         deploy          |   上传到远程仓库   |
|     site Lifecycle      |        pre-site         |    准备构建站点    |
|                         |          site           |      构建站点      |
|                         |        post-site        |    构建站点之后    |
|                         |       site-deploy       |      站点部署      |

三大生命周期其相互独立执行，也可以合在一起执行。但 lifecycle 中的 phase 是有严格执行的顺序的，比如必须先执行完 compile，才能执行 pakcage 动作，此外 phase 还有包含逻辑存在，即当你执行一个 phase 时，其前面的 phase 会自动执行。

- 演示 phase 执行

1. **执行编译**

> mvn compile

2. **执行打包就包含了编译指令的执行**

> mvn package

### 3. 生命周期与插件的关系

- **生命周期的 phase 组成了项目过建的完整过程**，但这些过程具体由谁来实现呢？这就是插件，maven 的核心部分代码量其实很少，**其大部分实现都是由插件来完成的**。

- 比如：test 阶段就是由 maven-surefire-plugin 实现。在 pom.xml 中我们可以设置指定插件目标 (gogal) 与 phase 绑定，当项目构建到达指定 phase 时，就会触发些插件gogal 的执行。

- 一个插件有时会实现多个 phase 比如：maven-compiler-plugin 插件分别实现了 compile 和 testCompile。

 **总结：**

- 生命周期的 阶段 可以绑定具体的插件及目标

- 不同配置下，同一个阶段可以对应多个插件和目标

- phase ==> plugin ==> goal(功能)

### 4. 生命周期与插件的默认绑定

​	在我们的项目当中并没有配置 maven-compiler-plugin 插件，但当我们执行 compile 阶段时，一样能够执行编译操作，原因是 maven 默认为指定阶段绑定了插件实现。列如下以下两个操作在一定程度上是等价的。

- 演示

1. **mvn compile**

2. 直接执行 compile 插件目标

> mvn org.apache.maven.plugins:maven-compiler-plugin:3.1:compile

 

**lifecycle phase 的默认绑定见下表：**

1. clean Lifecycle 默认绑定

```xml
<phases>
    <phase>pre-clean</phase>
    <phase>clean</phase>
    <phase>post-clean</phase>
</phases>
<default-phases>
    <clean>
        org.apache.maven.plugins:maven-clean-plugin:2.5:clean
    </clean>
</default-phases>
```

2. site Lifecycle 默认绑定

```xml
<phases>
    <phase>pre-site</phase>
    <phase>site</phase>
    <phase>post-site</phase>
    <phase>site-deploy</phase>
</phases>
<default-phases>
    <site>
        org.apache.maven.plugins:maven-site-plugin:3.3:site
    </site>
    <site-deploy>
        org.apache.maven.plugins:maven-site-plugin:3.3:deploy
    </site-deploy>
</default-phases>
```

3. Default Lifecycle JAR默认绑定

注：不同的项目类型 其默认绑定是不同的，这里只指列举了packaging 为 jar 的默认绑定，全部的默认绑定参见：[https://maven.apache.org/ref/3.5.4/maven-core/default-bindings.html#](https://maven.apache.org/ref/3.5.4/maven-core/default-bindings.html)。

```xml
<phases>
    <process-resources>
        org.apache.maven.plugins:maven-resources-plugin:2.6:resources
    </process-resources>
    <compile>
        org.apache.maven.plugins:maven-compiler-plugin:3.1:compile
    </compile>
    <process-test-resources>
        org.apache.maven.plugins:maven-resources-plugin:2.6:testResources
    </process-test-resources>
    <test-compile>
        org.apache.maven.plugins:maven-compiler-plugin:3.1:testCompile
    </test-compile>
    <test>
        org.apache.maven.plugins:maven-surefire-plugin:2.12.4:test
    </test>
    <package>
        org.apache.maven.plugins:maven-jar-plugin:2.4:jar
    </package>
    <install>
        org.apache.maven.plugins:maven-install-plugin:2.4:install
    </install>
    <deploy>
        org.apache.maven.plugins:maven-deploy-plugin:2.7:deploy
    </deploy>
</phases>
```



## 五、maven 自定义插件开发

### 1. maven 插件相关概念

#### 1.1 插件坐标定位

插件与普通 jar 包一样，包含一组件坐标定位属性，即：

groupId、artifactId、version，当使用该插件时会从本地仓库中搜索，如果没有即从远程仓库下载

```xml-dtd
<!-- 唯一定位到dependency 插件 -->
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-dependency-plugin</artifactId>
<version>2.10</version>
```

#### 1.2 插件执行 execution

execution 配置包含一组指示插件如何执行的属性：

**id** ： 执行器命名

**phase**：在什么阶段执行？

**goals**：执行一组什么目标或功能？

**configuration**：执行目标所需的配置文件？

- 演示一个插件的配置与使用

```xml
# 将插件依赖拷贝到指定目录
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-dependency-plugin</artifactId>
  <version>3.1.1</version>
  <executions>
    <execution>
      <id>copy-dependencies</id>
      <phase>package</phase>
      <goals>
        <goal>copy-dependencies</goal>
      </goals>
      <configuration>       <outputDirectory>${project.build.directory}/alternateLocation</outputDirectory>
        <overWriteReleases>false</overWriteReleases>
        <overWriteSnapshots>true</overWriteSnapshots>
        <excludeTransitive>true</excludeTransitive>
      </configuration>
    </execution>
  </executions>
</plugin>
```

### 2. 常用插件的使用

#### 2.1 使用

除了通过配置的方式使用插件以外，Maven也提供了通过命令直接调用插件目标其命令格式如下：

```bash
mvn groupId:artifactId:version:goal -D{参数名}
```

- 演示通过命令执行插件

```bash
# 展示 pom 的依赖关系树, 生成项目的依赖树
mvn org.apache.maven.plugins:maven-dependency-plugin:2.10:tree

# 也可以直接简化版的命令，但前提必须是 maven 官方插件
mvn dependency:tree
```



#### 2.2 其它常用插件

```bash
# 查看 pom 文件的最终配置 
mvn help:effective-pom

# 原型项目生成
archetype:generate

# 快速创建一个WEB程序
mvn archetype:generate -DgroupId=tuling -DartifactId=simple-webbapp -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false

# 快速创建一个java 项目
mvn archetype:generate -DgroupId=tuling -DartifactId=simple-java -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```



### 3. 开发一个自定义插件

#### 3.1 实现步骤

1. 创建 maven 插件项目

2. 设定 packaging 为 smaven-plugin

3. 添加插件依赖

4. 编写插件实现逻辑

6. 打包构建插件

#### 3.2 实例

1. 插件 pom 配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <!--<parent>
        <artifactId>study-maven</artifactId>
        <groupId>com.study</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>-->
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.study</groupId>
    <version>1.0-SNAPSHOT</version>
    <artifactId>study-maven-plugin</artifactId>
    <!-- 表明maven插件项目有 -->
    <packaging>maven-plugin</packaging>

    <dependencies>
        <dependency>
            <groupId>org.apache.maven</groupId>
            <artifactId>maven-plugin-api</artifactId>
            <version>3.0.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.maven.plugin-tools</groupId>
            <artifactId>maven-plugin-annotations</artifactId>
            <version>3.1</version>
        </dependency>
    </dependencies>

</project>
```

2. 插件实现类：

```java
package com.study.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "myGoal")
public class StudyPlugin extends AbstractMojo {

    @Parameter // 表示这是外面传入的参数
    private String sex;

    @Parameter // 表示这是外面传入的参数
    private String describe;

    // 执行器
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(String.format("myGoal sex=%s, describe=%s", sex, describe));
    }
}
```

#### 3.3 测试

1. **在 应用到插件的 maven项目中配置该插件：**

```xml
<build>
    <plugin>
        <groupId>com.study</groupId>
        <artifactId>study-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
            <execution>
                <!-- id是自己编写的 -->
                <id>my-plugin</id>
                <phase>compile</phase>
                <goals>
                    <goal>myGoal</goal>
                </goals>
                <!-- 配置参数 -->
                <configuration>
                    <sex>man</sex>
                    <describe>good man</describe>
                </configuration>
            </execution>
        </executions>
    </plugin>
    </plugins>
</build>
```

2. **编译该maven项目，会执行插件中的功能，也就是打印字符串**



## 六、nexus 私服搭建与核心功能

### 1. 私服使用场景

1. 公司不能连接公网，可以用一个私服务来统一连接
2. 公司内部 jar 组件的共享

### 2. nexus 下载安装

**nexus 下载地址：**

https://sonatype-download.global.ssl.fastly.net/nexus/oss/nexus-2.14.5-02-bundle.tar.gz

**解压并设置环境变量**

```bash
# 解压
shell>tar -zxvf nexus-2.14.5-02-bundle.tar.gz

# 在环境变量当中设置启动用户
shell> vim /etc/profile

# 添加profile文件。安全起见不建议使用root用户，如果使用其它用户需要加相应权限
export RUN_AS_USER=root

```

**配置启动参数：**

```bash
shell> vi ${nexusBase}/conf/nexus.properties

# 端口号
 application-port=9999

# 启动nexus
shell> ${nexusBase}/bin/nexus start

# 停止nexus
shell> ${nexusBase}/bin/nexus stop

登录 nexus 界面
地址：http://{ip}:9999/nexus/
用户名:admin
密码：admin123
```

![](images\images-maven\nexus配置1.png)

![](images\images-maven\nexus配置2.png)

**启动测试nexus：**

![](images\images-maven\nexus测试.png)



### 3. nexus 仓库介绍

![](images\images-maven\nexus仓库介绍.png)

- 3rd party：第三方仓库，不是公司的，也不是apache maven开发的
- Apache Snapshots：apache 快照仓库
- Central: maven 中央仓库
- Releases：私有发布版本仓库
- Snapshots：私有 快照版本仓库

 

### 4. 本地远程仓库配置

#### 4.1 配置私服

1.  在 pom 中配置远程仓库（公司的私有仓库，私服）

```xml
<!-- 配置自己搭建的私服仓库(nexus) -->
<repositories>
    <repository>
        <!-- id的名字随意取 -->
        <id>my-repository-nexus</id>
        <name>nexus repository</name>
        <url>http://192.168.33.10:8081/nexus/content/groups/public/</url>
    </repository>
</repositories>
```

2. 或者在settings.xml 文件中配置远程仓库镜像 效果一样，但作用范围广了

```xml
<mirror>    
    <id>nexus-aliyun</id>
    <mirrorOf>*</mirrorOf>
    <name>Nexus aliyun</name>
    <url>http://192.168.0.147:9999/nexus/content/groups/public/</url>
</mirror> 
```

#### 4.2 配置maven的仓库

- 项目会先从本地仓库中查找jar包，找不到就去私服上的仓库中找jar包，还是没有就会去自己配置的maven仓库中找
- ![](\images\images-maven\配置远程仓库.png)



### 5. 发布项目至nexus 远程仓库

1. **配置仓库地址**

```xml
<!-- 配置发布的仓库 -->
<distributionManagement>
    <!-- 配置release仓库地址 -->
    <repository>
        <id>nexus-releases</id>
        <name>release repository</name>
        <url>http://192.168.33.10:8081/nexus/content/repositories/releases/</url>
    </repository>
    <!-- 配置snapshot仓库地址 -->
    <snapshotRepository>
        <id>nexus-snapshots</id>
        <name>snapshot repository</name>
        <url>http://192.168.33.10:8081/nexus/content/repositories/snapshots//</url>
    </snapshotRepository>
</distributionManagement>
```

- 注意点：项目的每个模块都需要配置 `<distributionManagement>`

2. **设置 setting.xml 中设置server**

```xml
<servers>	
    <server>
        <id>nexus-releases</id>
        <username>deployment</username>
        <password>deployment123</password>
    </server>
    <server>
        <id>nexus-snapshots</id>
        <username>deployment</username>
        <password>deployment123</password>
    </server>
</servers>
```

3. 执行deploy 命令

- mvn deploy
- ![](\images\images-maven\远程仓库deploy结果.png)



## 七、maven问题

1、maven 引入外部jar包时， 版本号是否必须指定， 什么情况下可以不指定， 什么情况下必须指定？

答：必须指定版本号。你看到某些项目引入的时候 没有写版本号，原因是 父项目的 dependencyManagement 中已指定版本号。

 

2、自定义插件里面可以调用其他插件吗？

答：插件也是一个JAR 包可以别的项目直接依赖进去，但一般不会这么去做。

 

3、repository里存放的都是各种jar包和maven插件。当向仓库请求插件或依赖的时候，会先检查local repository，

如果local repository有则直接返回，否则会向remote repository请求，并缓存到local repository。

也可以把做的东西放到本地仓库，仅供本地使用；或上传到远程仓库，供大家使用。

答：是的



4、自定义Maven插件，设置运行的 phase 为 clean 或 post-clean 不执行？

答：phase 为clean 只有在执行clean 生命周期的时候才会被执行，compile 是不会被触发的



5、maven执行那个全部pom文件内容的时候，如果屏幕显示不全，那么在执行的时候这个能放入到指定文件里面吗？

可以 mvn help:effective-pom > effective.txt



6、公司里面普通开发人员会有deploy的权限吗？

答 ：一般公司都是用jenkins 去deploy



7、maven 先从本地下载，之后是私有库，再是远程仓库。（配置本地setting指向私库和在项目中的pom文件写私库地址区别）？

答 ：这里面有两个概念，一个叫仓库，一个叫镜像。仓库可以配置自己的地址，而镜像用于覆盖仓库的地址。一般我们会用<mirrorOf>*</mirrorOf> 覆盖所有仓库地址。



8、http://maven.apache.org 这个网站 怎么用? 上课讲的那些 在哪里找到？

答：相关链接已记录在文档中了



9、不同环境打包，老师没讲吧？就是通过打包参数来做不同的操作，比如版本号修改，上传私服快照还是发布版本

 

10、本地库里有依赖，怎么配置不用下载呢？

答：本地库里存在了，是不会在重新下载的

 

11、为何在引入一些外包jar的时候有些必须指定版本，有些又不可以指定呢？

答：必须指定版本号。你看到某些项目引入的时候 没有写版本号，原因是 父项目的 dependencyManagement 中已指定版本号

 

12、mvn上传jar包的时候，如何修改版本？SNAPSHOT如何变成release？

答：直接改父类的版本 改成 release即可

 

13、私服中的账号怎么设置？权限怎么设置？

答 ：用admin登陆 菜单 Security==>users 中可直接设置

 

14、本地maven的setting里面配置了阿里云镜像，项目的pom里配置公司私服的url，项目有依赖私服上其他的项目jar包，

这样下载依赖时候走本地配置的阿里云镜像还是私服。因为并没有出现课堂上老师演示时候被本地配置覆盖的现象，所以比较奇怪

答 ：这里面有两个概念，pom里面配的叫仓库，setting.xml中配的叫镜像，通过 <mirrorOf>{仓库ID}</mirrorOf> 指定覆盖仓库的地址 ,如果配置* 会覆盖所有仓库的地址。

默认会有一个叫central 的仓库。



15、今天讲到Maven的自定义插件，我在想Maven能否自定义项目模板？(实际场景：我们公司有很多项目，后台每个项目都要新启一个新的工程进行开发，每次都要在改包名配成一个新工程)

执行一下Maven命令通过修改一些参数就可以生成相应的新项目，类似于前端vue-cli这样的脚手架。

答：可以自定义项目模板

 

16、镜像如果不配置，有默认从哪个路径下载吗，？

答：会有一个默认的 central 仓库，地址是 http://repo.maven.apache.org/maven2。

```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>central</id>
        <name>Central Repository</name>
        <url>http://repo.maven.apache.org/maven2</url>
    </repository>
</repositories>
```

 

17、项目配置的私服地址和镜像中的是在吗转换的，都配置的话用哪个？

​    答 ：这里面有两个概念，pom里面配的叫仓库，setting.xml中配的叫镜像，通过 `<mirrorOf>{仓库ID}</mirrorOf> ` 指定覆盖仓库的地址 ,如果配置* 会覆盖所有仓库的地址。

默认会有一个叫central 的仓库。

 

​    

18、鲁班写的自定义插件那块，没有指定插件的phase，但是在引用这个插件的时候却可以定义phase为compile，插件的phase不是应该先指定好吗？

答：插件可以指定默认phase，但phase 不是固定的，后期设置插件的时候可以调整 

 

19、昨晚启动nexus把页面关闭之后，今天通过nexus.bat 文件启动nexus的时候为什么会提示已经启动？

![IMG_256](file:///C:/Users/Allen/AppData/Local/Temp/msohtmlclip1/01/clip_image001.gif)

答：检查下nexus 进程是否存在

 

 

 

20、为什么我执行mvn test没有出现test信息，好像跳过test了，我取git上的鲁班老师的代码跑了（鲁班老师代码的test文件夹没有test代码，我加了），也是没有出现TEST

![IMG_256](file:///C:/Users/Allen/AppData/Local/Temp/msohtmlclip1/01/clip_image003.jpg)

 

 

答：参照第一节课讲的 Test 的执行规则。1、Test类名和方法名开关 2、如果引入junit 依赖，第一条无效，必须方法上加@Test 注解 

 

21、我执行mvn compile 结果显示执行了两个插件：

maven-resources-plugin:3.0.2:resources (default-resources) 和maven-compiler-plugin:3.7.0:compile (default-compile) 

但是我直接执行插件 为什么只有 maven-compiler-plugin:3.7.0:compile (default-cli) 

不是说这两个命令是一样的吗？

![IMG_256](file:///C:/Users/Allen/AppData/Local/Temp/msohtmlclip1/01/clip_image004.gif)

 

![IMG_256](file:///C:/Users/Allen/AppData/Local/Temp/msohtmlclip1/01/clip_image005.gif)

 

 

 

答：这是maven 生命周期的概念，执行后面的 phase 前面的phase 也会被执行，phase 的执行就会触发与之绑定的插件goal。

 

 
Jekins

## 一、jenkins 概述与环境配置

**知识点：**

1. 关于可持续化集成 （CI）

2. jenkins 概述

3. 下载安装jenkins 

4. 基础环境配置与常用插件下载

### 1. 关于可持续化集成

相信大家都知道 jenkins 是用来做可持续集成的? 但部问题是很多人并不明白什么叫可持续化集成。讲概念之前我们先来举一个场景：

​	一个团队正着手开发一个项目，在需求评审之后由小组长把项目拆成了若干个模块，分给不同的小伙伴去实现。2个星期以后各自模块的功能都实现了，大家都很开心。这时该合在一起提测了吧。这一合小组长傻眼了，功能没一个能用的，要么公共类冲突了，要么配置冲突....

​	经过上次的教训，小组长变聪明了，在也不等功能开发之后在合并提测了，而是每天集成提测，如果发现冲突当天就要解决。

 

**持续集成**，即 Continuous integration（CI） 是一种软件开发实践，即团队开发成员经常集成他们的工作，每次集成都通过自动化的构建（包括编译，发布，自动化测试）来验证，从而尽快地发现集成错误，让团队能够更高效的开发软件。

 ![](images\images-jekins\jekins1.png)

 

#### 1.1 持续集成要点：

- 统一的代码库(git)

- 统一的依赖包管理(nexus)

- 测试自动化

- 构建全自动化(maven)

- 部署自动化

- 可追踪的集成记录

#### 1.2 jenkins 概述

jenkins 就是为了满足上述持续集成的要点而设计的一款工具，其主体框架采用JAVA开发，实质内部功能都是由各种插件实现，极大提高了系统的扩展性。其不仅可以满足JAVA系统的集成，也可以实现PHP等语言的集成发布。通过其 pipeline 插件，用户可以随自己需要定制集成流程。

#### 1.3 下载安装jenkins 

##### 1.3.1 下载：

jenkins 支持 Docker、yum、msi 等安装，在这里推荐大家直接选择下载他对应的WAR包进行安装。

https://jenkins.io/download/

##### 1.3.2 启动：

1. 下载完成之后直接可通过 jar -jar 命令启动

```bash
java -jar jenkins.war --httpPort=8888
```

![](images\images-jekins\启动jekins.png)

- 第一次启动中文乱码的原因是：git是UTF-8的编码格式，而jdk启动时编码格式是GBK

-   解决中文乱码问题

  ```bash
  java -jar -Dfile.encoding=UTF-8 jenkins.war --httpPort=8080
  ```

在Centos上启动 jenkins：

![](images\images-jekins\启动jenkins2.png)

2. 也可以将其放至到servlet容器（tomcat\jetty\jboss）中直接启动，无需过多的配置，一切插件化这是jenkins 比较优秀的设计。

##### 1.3.3 配置：

下载完成之后进入启动页(http://127.0.0.1:8080/) 会有一个 验证过程，验证码存储在 ${user_home}\.jenkins\secrets\initialAdminPassword 中，接着就是进入安装插件页，选择默认即可，这个过程稍长。

账号密码：

```
用户名 ：allen
密码   ：sw19941021
全名   ：allenwork
电子邮箱：allenwork2021@163.com
```

##### 1.3.4 卸载：

- windows下 删除掉 C:\Users\User 下的 .jenkins 文件夹

#### 1.4 基础环境配置与常用插件下载

在集成的时候，jenkins 用到了 Maven 、Git 所以服务器中必须提前安装好这些环境，具体参照前面的 git 与 maven 课程。

##### 1.4.1 插件下载

更换源 ->系统管理->管理插件->高级 ->升级站点

把：http://updates.jenkins-ci.org/update-center.json 

换成：http://mirror.esuni.jp/jenkins/updates/update-center.json 

镜像源查询：http://mirrors.jenkins-ci.org/status.html

**基本插件列表**

| **插件名称**         | **插件描述**   |
| -------------------- | -------------- |
| Maven  Integration   | maven 管理插件 |
| Deploy  to container | 容器部署插件   |
| Pipeline             | 管道集成插件   |
| Email Extension      | 邮件通知插件   |
| SSH                  | 用于ssh 通信   |

 

 Jenkins新版本 (2.289.1)可点击 [download](https://updates.jenkins.io/download/war/2.289.1/jenkins.war) ([变更说明](https://jenkins.io/changelog-stable))下载 



##### 1.4.2 jekins全局工具类配置

- 配置maven
- 配置jdk
- 配置git

 

## 二、基于jenkins 实现可持续化集成

### 1 持续化集成完成的目标

**需要到达的目标如下：**

1. 自动基于分支构建项目
2. 构建好的项目自动部署至Tomcat容器

3. 构建好的项目自动上传至Nexus 私服存档

4. 保存构建历史记录，并可以下载历史记录

### 2 持续化集成配置

#### 2.1 新建maven job

![](images\images-jekins\创建job.png)

#### 2.2  配置checkout 源码

![](images\images-jekins\jenkins域名配置.png)

![](images\images-jekins\jenkins域名配置2.png)

![](images\images-jekins\jenkins域名配置3.png)

![](images\images-jekins\jenkins域名配置4.png)



#### 2.3 编写 maven构建 命令

![](images\images-jekins\build.png)

![](images\images-jekins\build2.png)

![](images\images-jekins\build3.png)

#### 2.4 自动部署至Tomcat配置

**添加构建后操作：Deploy war/ear to container 项目**

![](images\images-jekins\jenkins打包存放位置2.png)



**自动部署的前提条件：**

1. 需要下载 Deploy to container 插件

2. 设置 Tomcat manager 用户和密码，以下配置加入至 Tomcat conf/tomcat-users.xml 中

3. tomcat webapp 中必须保留 manager 项目

```xml
<role rolename="admin-gui"/>
<role rolename="manager-gui"/>
<role rolename="manager-script"/>
<user username="manager" password="manager" roles="manager-gui,manager-script"/>
<user username="admin" password="admin" roles="admin-gui,manager-gui"/>
```

![](images\images-jekins\tomcat配置cof下tomcat-users.xml文件.png)

![](images\images-jekins\tomcat配置cof下tomcat-users.xml文件2.png)

- 路径：/opt/apps/tomcat8.5.31
- 文件：vi conf/tomcat-users.xml 

**问题：**

Tomcat 访问Manager APP报403 解决方案（虚拟机可以正常使用，外网访问报错），虚拟机中Tomcat启动后，可以访问项目（虚拟机里面和外面都可以）。虚拟机中能够正常进入manager app进行热部署工作，但是在外面能访问tomcat首页，点击manager app报403错误。
![](images\images-jekins\tomcat的manager app配置问题.png)

**解决办法：**

这是因为tomcat进行了ip限制，还需要将限制取消掉。将tomcat文件夹下的webapps/manager/META-INF/context.xml文件夹的以下内容注销掉

```xml
<Context antiResourceLocking="false" privileged="true" >
    <!-- <Valve className="org.apache.catalina.valves.RemoteAddrValve" allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" /> -->
</Context>
```

#### 2.5 存档配置

- 之前的操作只是将 项目添加到 jenkins 中，这样对于项目之间的依赖关系管理依旧十分复杂；所以需要自动化部署，将项目打包到maven仓库（私服）中，通过私服管理依赖

**构建后操作添加  Deploy artifacts to Maven repository  项目**

![](images\images-jekins\jenkins配置私服nexus1.png)

- 私服地址：http://192.168.33.10:8081/nexus/#welcome

![](images\images-jekins\jenkins配置私服nexus2.png)

- 复制私服 snapshots 的仓库地址

![](images\images-jekins\jenkins配置私服nexus3.png)

- 给 jenkins 配置相应的 仓库路径

**问题：部署项目用到仓库时，没有权限，报 401 错误**

![](images\images-jekins\jenkins配置私服nexus4.png)

**解决途径：**

- **配置 setting.xml 用于获取上传至nexus 的权限**

- ```bash
  vi ~/.m2/settings.xml
  ```

- 添加一个 server id 与 存档配置中的 repository id 相对应

  ```xml
  <server>
      <id>10-releases</id>
      <username>deployment</username>
      <password>deployment123</password>
  </server>
  <server>
      <id>10-snapshots</id>
      <username>deployment</username>
      <password>deployment123</password>
  </server>
  ```

  ![](images\images-jekins\jenkins配置私服nexus5.png)
  
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  
  <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  		  
    <localRepository>/usr/local/maven3.6.3/repository</localRepository>
  
    <pluginGroups>
    </pluginGroups>
  
  
    <proxies>
    </proxies>
  
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
  	
  	<server>
        <id>10-releases</id>
        <username>deployment</username>
        <password>deployment123</password>
      </server>
  	<server>
        <id>10-snapshots</id>
        <username>deployment</username>
        <password>deployment123</password>
      </server>
    </servers>
  
    <mirrors>
  	<mirror>
  		<id>my-repository-nexus</id>
  		<name>nexus repository</name>
  		<mirrorOf>*</mirrorOf>
  		<url>http://192.168.33.10:8081/nexus/content/groups/public</url>
  	</mirror>
  
    </mirrors>
  
    <profiles>
  	<profile>
  		<id>development</id>
  		<activation>
  			<jdk>1.8</jdk>
  			<activeByDefault>true</activeByDefault>
  		</activation>
  		
  		<repositories>
  			<snapshots>
  				<enabled>false</enabled>
  			</snapshots>
  			<id>center</id>
  			<name>Central Repository</name>
  			<url>http://192.168.33.10:8081/nexus/content/groups/public</url>
  		</repositories>
  	</profile>
    </profiles>
  
  </settings>
  ```

**结果：**

![](images\images-jekins\jenkins配置私服nexus6.png)

- jenkins 构建结果打印

![](images\images-jekins\jenkins配置私服nexus7.png)

- 私服的 snapshots仓库中有了构建的包

**补充：可以通过参数化构建选择不同的分支**



### 3 集成实现原理

![](images\images-jekins\集成实现原理.png)

#### 创建工作空间

- **maven项目进行构建时，会在 <font color=red>/root/.jenkins/workspace</font> 创建该项目的目录文件**
- ![](images\images-jekins\jenkins对maven项目进行构建的目录.png)



## 三、jenkins pipeline 核心应用

### 1 pipeline 概要

前面演示的是 使用maven 来进行自动化构建，其流程分别是：

> **构建环境准备 ==》源码下载 ==》构建 ==》存档 ==》部署**

这是一种固化的构建流程，如果你们的需求是多个项目需要进行依赖构建这种复杂的构建场景时，该怎么办？

jenkins pipeline 可以做到这一点。

Jenkins从根本上讲是一种支持多种自动化模式的自动化引擎。

Pipeline在Jenkins上添加了一套强大的自动化工具，支持从简单的连续集成到全面的连续输送Pipeline的用例。

用户可以基于他实现更为复杂的建模场景。

### 2 pipeline 基础语法

以下就是一个非常简单的 pipeline 脚本：

```bash
pipeline {
    agent any 
    stages {
        stage('Build') { 
            steps { 
                sh 'make' 
            }
        }
        stage('Test'){
            steps {
                sh 'make check'
                junit 'reports/**/*.xml' 
            }
        }
        stage('Deploy') {
            steps {
                sh 'make publish'
            }
        }
    }
}
```

- agent  表示Jenkins应该为Pipeline的这一部分分配一个执行者和工作区。

- stage   描述了这条Pipeline的一个阶段。

- steps   描述了要在其中运行的步骤 stage

- sh        执行给定的shell命令

- junit    是由JUnit插件提供的 用于聚合测试报告的Pipeline步骤。

### 3 pipeline Demo演示

**前提条件**

1. Jenkins 2.x或更高版本

2. Pipeline插件

可以通过以下任一方式创建基本Pipeline：

1. 直接在Jenkins网页界面中输入脚本。

2. 通过创建一个Jenkinsfile可以检入项目的源代码管理库。

用任一方法定义 Pipeline 的语法是一样的，从项目源码中检入 jenkinsfile 文件会更方便一些。

 

**步骤：**

![](images\images-jekins\jenkins构建流水线.png)

![](images\images-jekins\jenkins构建流水线2.png)

 

基于脚本构建 pipeline

pipeline {

  agent any

  stages {

​    stage('Checkout') {

​      steps {

​        echo 'Checkout'

​    stage('Build') {

​      steps {

​        echo 'Building'

​      }

​    }

​    stage('test'){

​      steps{

​        echo 'test'

​      }

​    }

  }

}

 

 

基于Jenkinsfile 构建

pipeline {

  agent any 

  stages {

​    stage('checkout') { 

​      steps { 

​        echo 'checkout' 

​        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'gogs_luban', url: 'http://git.jiagouedu.com/java-vip/tuling-api-gateway']]])

​      }

​    }

​    stage('build'){

​      steps {

​         echo 'build'

​         sh 'mvn clean install'

​      }

​    }

​    stage('save') {

​      steps {

​        echo 'save'

​        archiveArtifacts 'target/*.war'

​      }

​    }

  }

}

 

 

 

 
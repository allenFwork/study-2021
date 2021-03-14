# Spring Boot 2.1.X 编译

## 1. 配置源码环境

### 1.1 编译项目

1. 通过 git 的命令：

   `./mvn clean install -DskipTests -Pfast`  跳过测试类，快速编译

2. 使用 maven 的命令：

   ` mvn clean install -DskipTests -Pfast `  跳过测试类，快速编译

### 1.2 导入项目到 idea

1. 使用 File -> New -> Project from Existing Sources
2. 然后设置 maven仓库为自己编译时的仓库

- 不能使用 File -> open ，然后选择编译好的项目位置



## springboot

springboot 新特性：推断是什么项目

1. reactive

2.  servlet
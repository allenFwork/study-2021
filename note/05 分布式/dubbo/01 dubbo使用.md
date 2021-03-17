# dubbo框架全面介绍及快速使用入门

## 1. dubbo 2.6版本使用

### 1.1



### 1.2



### 1.3 dubbo管理平台

#### 1.3.1

#### 1.3.2

#### 1.3.3 使用日志

1. 添加resources下添加配置文件: log4j.properties

   ```properties
   ###set log levels###
   log4j.rootLogger=info, stdout
   ###output to the console###
   log4j.appender.stdout=org.apache.log4j.ConsoleAppender
   log4j.appender.stdout.Target=System.out
   log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
   log4j.appender.stdout.layout.ConversionPattern=[%d{dd/MM/yy HH:mm:ss:SSS z}] %t %5p %c{2}: %m%n
   ```

2. 在dubbo的配置文件中配置：

- ```xml
  <dubbo:application name="dubbo-provider"  logger="log4j"/>
  ```

#### 1.3.4 服务降级

- 配置mock属性

#### 1.3.5 动态配置

- 在管理系统中进行的动态配置优先级高于代码中的配置
- 路由配置是服务级别的
- 动态配置

### 1.4 dubbo监控平台

#### 1.4.1 

![](images\dubbo监控平台.png)

## 2.






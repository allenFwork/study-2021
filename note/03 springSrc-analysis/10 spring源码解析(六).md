查看`cglib`生成的class文件

- 命令：` java -classpath sa-jdi.jar “sun.jvm.hotspot.HSDB” `
- 实例：` java -classpath "D:\mySystem\JDK\jdk1.8.0_161\lib\sa-jdi.jar" sun.jvm.hotspot.HSDB `





spring中如果配置类添加了 @Configuration ，那么：

1. 会将该配置类进行 `cglib` 代理
2. 会将代理生成的代理类继承目标对象，**并且实现 `FactoryBean` 接口**
3. 







### Spring-BeanFactory总结（面试知识点）

#### 1. 

#### 2. 

#### 3. 

<img src="images\spring\spring的工厂初始化过程中重要的类型对象（5个）.png" style="zoom:200%;" />
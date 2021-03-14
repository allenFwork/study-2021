# Spring源码解析-工厂的初始化



3. 将普通的实体类转化为BeanDefinition类

   1.使用

   

   

   `@Import`注解中可以填写三种数据：

   - normal class 普通的类
   - importSelect
   - ImportBeanDefinitionRegistrar

   

   向spring容器中添加bean对象的方法：

   - registry(); 需要一个类，没法参与类变成bd的过程
   - scan();      需要一个类，没法参与类变成bd的过程
   - ImportBeanDefinitionRegistrar 可以参与变成bd的过程

   

   mybatis的Mapper接口，将其纳入spring容器管理的步骤：

   1. 先要有一个对象
   2. 这个对象实现了你需要纳入管理的Mapper接口
   3. 将这个对象交由spring管理，放到spring容器中

   

   

   ```java
@Import
   @ComponentScan("")
   public class AppConfig {
   	
   }
   ```



普通类                          扫描完成之后注册

importSelector         先configurationClasses; 然后再注册  loadbean

registrar                     importBeanDefinitionRegistrars 然后再注册

import普通类            先configurationClasses





- 使用了 @Configuration 的配置类 和没有使用 @Configuration 的配置类的区别：
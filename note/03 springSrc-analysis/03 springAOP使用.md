## `spring AOP` 常见面试题目

### 1. `Aop ` 是什么

​	与 `OOP `对比，面向切面，传统的 `OOP` 开发中的代码逻辑是**自上而下**的，而这些过程会产生一些横切性问题，这些横切性的问题和我们的主业务逻辑关系不大，这些横切性问题不会影响到主逻辑实现的，但是会散落到代码的各个部分，难以维护。`AOP`是处理一些横切性问题，`AOP` 的编程思想就是把这些问题和主业务逻辑分开，达到与主业务逻辑解耦的目的。使代码的重用性和开发效率更高。



### 2. `aop` 的应用场景

1. 日志记录

2. 权限验证
3. 效率检查
4. 事务管理
5. exception



### 3. `springAop` 的底层技术

|                                      | `JDK`动态代理  | `CGLIB`代理    |
| ------------------------------------ | -------------- | -------------- |
| 编译时期的织入还是运行时期的织入?    | 运行时期织入   | 运行时期织入   |
| 初始化时期织入还是获取对象时期织入？ | 初始化时期织入 | 初始化时期织入 |



### 4. `springAop` 和 `AspectJ` 的关系

#### 4.1 `Aop` 是一种概念，`springAop` 与 `AspectJ` 关系

​	`springAop`、`AspectJ` 都是 `Aop` 的实现，`SpringAop` 是通过 spring 自己的语法实现的，但是语法复杂，所以 `SpringAop` 借助了`AspectJ` 的注解，但是底层实现还是自己的

```
spring AOP提供两种编程风格：
1. @AspectJ support         ------------> 利用aspectj的注解
2. Schema-based AOP support ----------->  xml aop:config 命名空间

证明:spring,通过源码分析了,我们可以知道spring底层使用的是JDK或者CGLIB来完成的代理,并且在官网上spring给出了aspectj的文档,和springAOP是不同的
```



#### 4.2 `spring Aop` 的概念

```
aspect    :  切面, 一定要给spring去管理  抽象  aspectj -> 类

pointcut  :  切点, 表示连接点的集合      ---------------->   表
  （我的理解：PointCut是JoinPoint的谓语，这是一个动作，主要是告诉通知连接点在哪里，切点表达式决定 JoinPoint 的数量）

Joinpoint :  连接点, 目标对象中的方法    ---------------->    记录
  （我的理解：JoinPoint是要关注和增强的方法，也就是我们要作用的点）

target    :  目标对象, 原始对象

aop Proxy :  代理对象, 包含了原始对象的代码和增加后的代码的那个对象

Weaving   :  织入, 把代理逻辑加入到目标对象上的过程

advice    :  通知, 两部分组成,通知的内容和通知的位置   (位置 + logic)
通知类型:
  Before  : 连接点执行之前，但是无法阻止连接点的正常执行，除非该段执行抛出异常
  After   : 连接点正常执行之后，执行过程中正常执行返回退出，非异常退出
  After throwing  : 执行抛出异常的时候
  After (finally) : 无论连接点是正常退出还是异常退出，都会执行
  Around advice   : 围绕连接点执行，例如方法调用。这是最有用的切面方式。around通知可以在方法调用之前和之后执行自定义行为。它还负责选择是继续加入点还是通过返回自己的返回值或抛出异常来快速建议的方法执行。


Proceedingjoinpoint 和JoinPoint的区别:
Proceedingjoinpoint 继承了JoinPoint,proceed()这个是aop代理链执行的方法。并扩充实现了proceed()方法，用于继续执行连接点。JoinPoint仅能获取相关参数，无法执行连接点。
JoinPoint的方法
1.java.lang.Object[] getArgs()：获取连接点方法运行时的入参列表； 
2.Signature getSignature() ：获取连接点的方法签名对象； 
3.java.lang.Object getTarget() ：获取连接点所在的目标对象； 
4.java.lang.Object getThis() ：获取代理对象本身；
proceed()有重载,有个带参数的方法,可以修改目标方法的的参数

Introductions
perthis
使用方式如下：
@Aspect("perthis(this(com.chenss.dao.IndexDaoImpl))")
要求：
1. AspectJ对象的注入类型为prototype
2. 目标对象也必须是prototype的
原因为：只有目标对象是原型模式的，每次getBean得到的对象才是不一样的，由此针对每个对象就会产生新的切面对象，才能产生不同的切面结果。
```



## `springAop` 支持 `AspectJ`

### 1. 启用 `@AspectJ` 支持

- 使用Java Configuration启用 `@AspectJ` 支持

- 要使用Java @Configuration启用  `@AspectJ` 支持，请添加 `@EnableAspectJAutoProxy` 注释

```java
package com.study.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.study")
@EnableAspectJAutoProxy(proxyTargetClass = false) // 开启支持 @AspectJ 注释
/**
 * 默认情况下，proxyTargetClass = false，此时使用的是jdk动态代理
 *           proxyTargetClass = true时，使用的是cglib动态代理
 */
public class AppConfig {

}
```

- 使用XML配置启用 `@AspectJ` 支持

- 要使用基于`xml ` 的配置启用` @AspectJ` 支持，可以使用 `aop:aspectj-autoproxy` 元素

```xml
<!-- spring aop借助了aspectj 的语法(使用了org.springframework.stereotype.Component注解)，需要添加此依赖 -->
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.0</version>
</dependency>
```



### 2. 声明一个 `Aspect` 切面

- 申明一个 `@Aspect` 注释类，并且定义成一个bean交给Spring管理。 

```java
package com.study.aop;

import com.study.dao.DeclareParentsDemo;
import com.study.dao.IndexDao;
import com.study.dao.IndexDaoImpl;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 默认情况下，使用 @Aspect 注解的切面在spring容器中都是单例的
 */
@Component("aspect")
//@Aspect // 声明切面,此处的切面是一个类,也可以是配置文件
@Aspect("perthis(pointCutWithin())")  // aspect instantiation model 默认是单例的，此处声明为prototype原型,
@Scope("prototype")
public class LunbanAspectj {

    /**
     *  @Pointcut : 声明切点
     *  每个切点都要有对应的连接点，连接点就是需要添加代码进去的目标对象中的方法,
     * execution()中的表达式 public * com.study.dao.*.*(..)：
     *   public    ：可写可不写，代表所有的
     *   第一个 *  : 表示返回类型，不能省略
     */
    @Pointcut("execution(private * com.study.dao.*.*(..))")
    public void pointCutExecution(){

    }

    @Pointcut("within(com.study.dao.*)")
    public void pointCutWithin(){

    }

    // 指定参数类型类型,与包名和方法名无关
    @Pointcut("args(java.lang.String,java.lang.Integer)")
    public void pointCutArgs(){

    }

    @Pointcut("@annotation(com.study.annotation.Luban)")
    public void pointCutAnnotation(){

    }

    // this 表示代理对象
    @Pointcut("this(com.study.dao.IndexDaoImpl)")
    public void pointCutThis(){

    }

    // target 表示目标对象
    @Pointcut("target(com.study.dao.IndexDaoImpl)")
    public void pointCutTarget(){

    }

    /**
     * @Before 表示通知到方法前面去
     * 通知：通知的内容(logic) 和 通知的位置(location)
     * 1. 内容是方法体内的代码
     * 2. 位置通过切点来确定,例如上面的 pointCutExecution() 方法
     */
//    @Before("pointCutExecution()")
//    public void before(){
//        System.out.println("before");
//    }

    // 满足 pointCutWithin()类，但是类中满足 pointCutArgs()的方法不被切入
//    @Before("pointCutWithin() && !pointCutArgs()")
//    public void before(){
//        System.out.println("before");
//    }

//    @Before("@annotation(com.study.annotation.Luban)")
//    public void before(){
//        System.out.println("before");
//    }

//    @Before("pointCutThis()")
//    public void before(){
//        System.out.println("before");
//    }

//    @Before("pointCutWithin()")
//    public void before(JoinPoint joinPoint){
//        System.out.println("before");
//        // 获取代理对象
//        System.out.println(joinPoint.getThis()); //
//        // 获取目标对象
//        System.out.println(joinPoint.getTarget());
//    }

    /**
     * @After 表示通知到方法后面去
     * 通知：通知的内容(logic) 和 通知的位置(location)
     * 1. 内容是方法体内的代码
     * 2. 位置通过切点来确定,例如上面的 pointCutTarget() 方法
     */
//    @After("pointCutWithin()")
//    public void after(){
//        System.out.println("after");
//    }

    /**
     * @Around 环绕通知
     * 通知：通知的内容(logic) 和 通知的位置(location)
     * 1. 内容是方法体内的代码
     * 2. 位置通过切点来确定,例如上面的 pointCutTarget() 方法
     *
     * org.aspectj.lang.ProceedingJoinPoint 表示正在执行的连接点,即正在处理的方法
     * 环绕通知的意义：
     */
    @Around("pointCutWithin()")
    public void around(ProceedingJoinPoint proceedingJoinPoint){
        System.out.println(this.hashCode());
        // 连接点属于哪个类
        System.out.println("around before ...");
        try {
            // 执行下一个目标方法
            proceedingJoinPoint.proceed();
            // 执行下一个目标方法时，修改参数
//            proceedingJoinPoint.proceed(new Object[]{});
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        System.out.println("around after ...");
    }

    /**
     *  @DeclareParents(value = "com.study.dao.*", defaultImpl = IndexDaoImpl.class)
     *  public static IndexDao indexDao;
     *  表示在 com.study.dao包下的所有类都实现 IndexDao接口，并且其实现时所需要重写的方法都使用IndexDaoImpl类中的方法，
     *  所以 com.study.dao.DeclareParentsDemo 类定义时虽然没有实现IndexDao接口，但在运行时依旧可以强转为IndexDao类型。
     */
    @DeclareParents(value = "com.study.dao.*", defaultImpl = IndexDaoImpl.class)
    public static IndexDao indexDao;

}
```



### 3. 申明一个 `pointCut` 切点

- 切入点表达式由 `@Pointcut` 注释表示。
- 切入点声明由两部分组成：一个签名包含名称和任何参数，以及一个切入点表达式，该表达式确定我们对哪个方法执行感兴趣。

```java
@Pointcut("execution(* transfer(..))")  // 切入点表达式
private void anyOldTransfer() {}        // 切入点签名
```

-  切入点确定感兴趣的 join points（连接点），从而使我们能够控制何时执行通知。`Spring AOP` 只支持Spring bean的方法执行 join points（连接点），所以您可以将切入点看作是匹配Spring bean上方法的执行。 

```java
/**
 * 申明Aspect，并且交给spring容器管理
 */
@Component
@Aspect
public class UserAspect {
    /**
     * 申明切入点，匹配UserDao所有方法调用
     * execution:匹配方法执行连接点
     * within:将匹配限制为特定类型中的连接点
     * args：参数
     * target：目标对象
     * this：代理对象
     */
    @Pointcut("execution(* com.yao.dao.UserDao.*(..))")
    public void pintCut(){
        System.out.println("point cut");
    }
}
```



### 4. 申明一个Advice通知

- advice通知与 `pointcut` 切入点表达式相关联，并在切入点匹配的方法执行@Before之前、@After之后或前后运行。

```java
/**
 * 申明Aspect，并且交给spring容器管理
 */
@Component
@Aspect
public class UserAspect {
    /**
     * 申明切入点，匹配UserDao所有方法调用
     * execution匹配方法执行连接点
     * within:将匹配限制为特定类型中的连接点
     * args：参数
     * target：目标对象
     * this：代理对象
     */
    @Pointcut("execution(* com.yao.dao.UserDao.*(..))")
    public void pintCut(){
        System.out.println("point cut");
    }
    /**
     * 申明before通知,在pintCut切入点前执行
     * 通知与切入点表达式相关联，
     * 并在切入点匹配的方法执行之前、之后或前后运行。
     * 切入点表达式可以是对指定切入点的简单引用，也可以是在适当位置声明的切入点表达式。
     */
    @Before("com.yao.aop.UserAspect.pintCut()")
    public void beforeAdvice(){
        System.out.println("before");
    }
}
```





## 各种连接点 `joinPoint` 的意义:

### 1. execution

- 用于匹配方法执行 join points连接点，**最小粒度** 能确定到方法上，在 `aop` 中主要使用。

```txt
表达式：
execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)

这里问号表示当前项可以有, 也可以没有, 其中各项的语义如下
modifiers-pattern      : 方法的可见性，如public，protected；
ret-type-pattern       : 方法的返回值类型，如int，void等；
declaring-type-pattern : 方法所在类的全路径名，如com.spring.Aspect；
name-pattern           : 方法名类型，如buisinessService()；
param-pattern          : 方法的参数类型，如java.lang.String；
throws-pattern         : 方法抛出的异常类型，如java.lang.Exception；

example:
//匹配com.chenss.dao包下的任意接口和类的任意方法
@Pointcut("execution(* com.chenss.dao.*.*(..))")

//匹配com.chenss.dao包下的任意接口和类的public方法
@Pointcut("execution(public * com.chenss.dao.*.*(..))")

//匹配com.chenss.dao包下的任意接口和类的 public 无参数的方法
@Pointcut("execution(public * com.chenss.dao.*.*())")

//匹配com.chenss.dao包下的任意接口和类的第一个参数为String类型的方法
@Pointcut("execution(* com.chenss.dao.*.*(java.lang.String, ..))")

//匹配com.chenss.dao包下的任意接口和类的只有一个参数，且参数为String类型的方法
@Pointcut("execution(* com.chenss.dao.*.*(java.lang.String))")

//匹配com.chenss.dao包下的任意接口和类的只有一个参数，且参数为String类型的方法
@Pointcut("execution(* com.chenss.dao.*.*(java.lang.String))")

//匹配任意的public方法
@Pointcut("execution(public * *(..))")

//匹配任意的以te开头的方法
@Pointcut("execution(* te*(..))")

//匹配com.chenss.dao.IndexDao接口中任意的方法
@Pointcut("execution(* com.chenss.dao.IndexDao.*(..))")

//匹配com.chenss.dao包及其子包中任意的方法
@Pointcut("execution(* com.chenss.dao..*.*(..))")

关于这个表达式的详细写法,可以脑补也可以参考官网很容易的,可以作为一个看spring官网文档的入门,打破你害怕看官方文档的心理,其实你会发觉官方文档也是很容易的
https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/core.html#aop-pointcuts-examples
```

- `@execution`：表示切入到某个被添加了该注解的方法

```
由于Spring切面粒度最小是达到方法级别，而execution表达式可以用于明确指定方法返回类型，类名，方法名和参数名等与方法相关的信息，并且在Spring中，大部分需要使用AOP的业务场景也只需要达到方法级别即可，因而execution表达式的使用是最为广泛的
```



### 2. within

- 表达式的 **最小粒度** 为类

```
™表达式的最小粒度为类
// ------------
// within与execution相比，粒度更大，仅能实现到包和接口、类级别。而execution可以精确到方法的返回值，参数个数、修饰符、参数类型等
@Pointcut("within(com.chenss.dao.*)")//匹配com.chenss.dao包中的任意方法
@Pointcut("within(com.chenss.dao..*)")//匹配com.chenss.dao包及其子包中的任意方法
```

- `@within` ：表示切入到添加了该注解的某个类



### 3. `args`

- `args` 表达式的作用是匹配指定**参数类型**和**指定参数数量**的方法，与包名和类名无关

```java
/**
 * args同execution不同的地方在于：
 * 1.args匹配的是运行时传递给方法的参数类型
 * 2.execution(* *(java.io.Serializable))匹配的是方法在声明时指定的方法参数类型。
 */
//匹配运行时传递的参数类型为指定类型的、且参数个数和顺序匹配
@Pointcut("args(java.io.Serializable)")

//接受一个参数，并且传递的参数的运行时类型具有@Classified
@Pointcut("@args(com.chenss.anno.Chenss)")
```

- `@args`：找到方法的参数，判断传入的参数类型是否有被添加注解，有就切入到该方法



### 4. this 代表当前代理对象

- **`JDK` 代理时，代理对象指向<font color=red>接口</font>和<font color=red>代理类proxy</font>，`cglib` 代理时，代理对象指向接口和子类(不使用proxy)**

- ```
  问：jdk是基于聚合接口实现的(也就是基于接口实现的),而不是基于继承实现的，为什么？
  
  答：
  1.jdk动态代理，底层使用的是指向接口和Proxy代理类。
  2.jdk底层实现动态代理时，被代理的类（目标对象）是需要实现Proxy类从而得到代理类（代理对象），而java是单继承的，所以代理类就不能再继承其余的类了
  ```



### 5. target  代表当前目标对象

- 指向接口和子类

```java
1.此处需要注意的是，如果配置设置proxyTargetClass=false，或默认为false，则是用JDK代理，否则使用的是CGLIB代理
2.JDK代理的实现方式是基于接口实现，代理类继承Proxy，实现接口。
3.而CGLIB继承被代理的类来实现。
4.所以使用target会保证目标不变，关联对象不会受到这个设置的影响。
5.但是使用this对象时，会根据该选项的设置，判断是否能找到对象。

//目标对象，也就是被代理的对象。限制目标对象为com.chenss.dao.IndexDaoImpl类
@Pointcut("target(com.chenss.dao.IndexDaoImpl)")

//当前对象，也就是代理对象，代理对象时通过代理目标对象的方式获取新的对象，与原值并非一个
@Pointcut("this(com.chenss.dao.IndexDaoImpl)")

//具有@Chenss的目标对象中的任意方法
@Pointcut("@target(com.chenss.anno.Chenss)")

//等同于@targ
@Pointcut("@within(com.chenss.anno.Chenss)")
```



```
proxy模式里面有两个重要的术语
proxy Class
target Class
CGLIB和JDK有区别    JDK是基于接口   cglib是基于继承所有this可以在cglib作用
```



### 6. `annotaition`

- **作用方法级别**

```java
上述所有表达式都有@ 比如@Target(里面是一个注解类xx,表示所有加了xx注解的类,和包名无关)

注意:上述所有的表达式可以混合使用,|| && !
    
// 匹配带有com.chenss.anno.Chenss注解的方法
@Pointcut("@annotation(com.chenss.anno.Chenss)")
```

- `@annotation`：表示切入到添加了自定义某个注解的方法

1. 自定义注解

```java
package com.study.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Luban {

}
```

2. 使用注解

```java
package com.study.dao;

import com.study.annotation.Luban;
import org.springframework.stereotype.Repository;

@Repository
public class IndexDaoImpl implements IndexDao{
    @Luban
    public void query(){
        System.out.println("query");
    }
}
```

3. 设置切面

```java
package com.study.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect // 声明切面,此处的切面是一个类,也可以是配置文件
public class LunbanAspectj {

    @Pointcut("@annotation(com.study.annotation.Luban)")
    public void pointCutAnnotation(){

    }

   @Before("@annotation(com.study.annotation.Luban)")
   public void before(){
       System.out.println("before");
   }
}
```



### 7. bean

```java
@Pointcut("bean(dao1)") //名称为dao1的bean上的任意方法
@Pointcut("bean(dao*)")
```





## `Spring AOP XML` 实现方式的注意事项:

1. 在 `aop:config` 中定义切面逻辑，允许重复出现，重复多次，以最后出现的逻辑为准，但是次数以出现的次数为准
2. `aop:aspect ID` 重复不影响正常运行，依然能够有正确结果
3. `aop:pointcut ID` 重复会出现覆盖，以最后出现的为准。不同 `aop:aspect` 内出现的 `pointcut` 配置，可以相互引用

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                           http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/aop
                           http://www.springframework.org/schema/aop/spring-aop.xsd
                           http://www.springframework.org/schema/context
                           http://www.springframework.org/schema/context/spring-context.xsd">
    <!-- 定义开始进行注解扫描 -->
    <context:component-scan base-package="com.chenss"></context:component-scan>

    <!-- 定义AspectJ对象使用的逻辑类，类中提供切面之后执行的逻辑方法 -->
    <bean id="aspectAop" class="com.chenss.aspectj.Aspect"></bean>
    <bean id="aspectAop2" class="com.chenss.aspectj.Aspect2"></bean>

    <bean id="indexDao" class="com.chenss.entity.IndexDao"></bean>

    <!--在Config中定义切面逻辑，允许重复出现，重复多次，以最后出现的逻辑为准，但是次数以出现的次数为准-->
    <aop:config>
        <!-- aop:aspect ID重复不影响正常运行，依然能够有正确结果 -->
        <!-- aop:pointcut ID重复会出现覆盖，以最后出现的为准。不同aop:aspect内出现的pointcut配置，可以相互引用 -->
        <aop:aspect id="aspect" ref="aspectAop">
            <aop:pointcut id="aspectCut" expression="execution(* com.chenss.entity.*.*())"/>
            <aop:before method="before" pointcut-ref="aspectCut"></aop:before>
      fffffff
            <aop:pointcut id="aspectNameCut" expression="execution(* com.chenss.entity.*.*(java.lang.String, ..))"/>
            <aop:before method="before2" pointcut-ref="aspectNameCut"></aop:before>
        </aop:aspect>
    </aop:config>
</beans>
```

4. 实例：

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns="http://www.springframework.org/schema/beans"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:aop="http://www.springframework.org/schema/aop"
          xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                              http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
   
       <!-- 通过配置文件定义切面 -->
       <aop:config>
           <!-- 设置切点 -->
           <aop:pointcut id="allDao" expression="execution(* com.study.dao.*.*(..))"/>
   
           <!-- 定义一个切面 -->
           <aop:aspect id="aspect" ref="xmlAop">
               <!-- 设置通知 -->
               <aop:before pointcut-ref="allDao" method="before"></aop:before>
           </aop:aspect>
       </aop:config>
   
       <!-- 将实现aop的业务逻辑代码交给spring容器管理 -->
       <bean id="xmlAop" class="com.study.aop.XmlAopBean"></bean>
   
   </beans>
   ```

   ```java
   package com.study.aop;
   
   /**
    * xml配置文件形成的切面，其中通知只有位置信息，没有逻辑代码信息
    * 所以需要编写对应的类来处理逻辑代码
    */
   public class XmlAopBean {
       public void before(){
           System.out.println("xml aop before");
       }
   }
   ```

   



## `spring AOP` 的源码分析

**cglib**

![](D:\myData\notes\架构师\java\03 spring源码解析\images\cglib.png)

![](D:\myData\notes\架构师\java\03 spring源码解析\images\cglib02.png)

```
cglib封装了ASM这个开源框架,对字节码操作,完成对代理类的创建
主要通过集成目标对象,然后完成重写,再操作字节码
具体看参考ASM的语法
```



**`jdk`**

![](D:\myData\notes\架构师\java\03 spring源码解析\images\jdk-proxy.png)



![](D:\myData\notes\架构师\java\03 spring源码解析\images\jdk-proxy02.png)

```
其中最重要的两个方法

generateProxyClass通过反射收集字段和属性然后生成字节

defineClass0 jvm内部完成对上述字节的load
```



![](D:\myData\notes\架构师\java\03 spring源码解析\images\jdk-proxy03.png)

```
总结:cglib是通过继承来操作子类的字节码生成代理类,JDK是通过接口,然后利用java反射完成对类的动态创建,严格意义上来说cglib的效率高于JDK的反射,但是这种效率取决于代码功力,其实可以忽略不计,毕竟JDK是JVM的亲儿子........
```





## `spring5` 新特性

1. 使用 lambda表达式定义bean

2. 日志 `spring4` 的日志是用`jcl`，原生的`JCL` ，底层通过循环去加载具体的日志实现技术，所以有先后顺序，`spring5` 利用的是`spring-jcl`，其实就是spring自己改了`JCL`的代码具体参考视频当中讲的两者的区别



​	新特性还有其他，但是这两个比较重要，由于时间问题,其他的特性可以去网上找到相应资料，但是这两个应付面试绝对可以了，其他的特性噱头居多，实用性可能不是很大。


 
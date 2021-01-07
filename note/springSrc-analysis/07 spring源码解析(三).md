# spring 源码解析

## 1. AnnotationConfigApplicationContext

### 1.1 源码解读

- ```java
  // AnnotationConfigApplicationContext : 注解配置应用上下文
  // 把spring所有的前提环境准备好
  AnnotationConfigApplicationContext context = 
  				new AnnotationConfigApplicationContext(AppConfig.class);
  ```

- ```java
  	/**
    	 * 这个构造方法需要传入一个被 javaconfig 注解了的配置类,
    	 * 然后会把这个被注解了 javaconfig 的类 通过注解读取器 读取 继而 接卸
    	 */
    	public AnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
    		/*
    		 * 这里由于他有父类,故而会优先调用父类的构造方法,
    		 * 然后才会调用自己的构造方法,在自己的构造方法中初始化一个读取器和扫描器
      		 */
    		this(); // this(); 就是调用默认的构造方法
    
    		// annotatedClasses 此处就是指刚刚传进来的配置类 AppConfig.class
    		register(annotatedClasses);
          
    		refresh();
    	}
  ```

  - `AnnotatedBeanDefinition `: 被注解的 `BeanDefinition`
  - `BeanDefinition` : spring中用来描述bean对象的类型
  - java        -   class     -      Class
  - spring    -   bean     -     BeanDefinition

- ```java
  	/**
    	 * 这个类顾名思义是一个reader,一个读取器
    	 * 读取什么呢？ 还是顾名思义 AnnotatedBeanDefinition意思是读取一个被加了注解的bean
    	 * 这个类在构造方法中被实例化
    	 */
    	private final AnnotatedBeanDefinitionReader reader;
    
    	/**
    	 * 这是一个扫描类,扫描所有加了注解的bean
    	 * 同样是在构造方法中被实例化的
    	 */
    	private final ClassPathBeanDefinitionScanner scanner;
    
    	/**
    	 * 1.初始化一个bean的读取器和扫描器。
    	 * 2.何为 扫描器 和 注解器 参考上面的属性注释。
    	 * 3.默认构造函数,如果直接调用这个默认构造方法,需要在稍后通过调用其的register()方法
    	 *   来注册配置类(javaconfig),并调用refresh方()法刷新容器,
    	 *   触发容器对注解Bean的载入、解析和注册过程。
    	 * 4.这种使用过程在使用@Profile的注解是用过。
    	 */
    	public AnnotationConfigApplicationContext() {
    		/*
    		 * 先执行父类的构造方法super();
    		 * 创建一个读取注解的Bean定义读取器
    		 * 什么是bean定义? BeanDefinition
    		 */
    		this.reader = new AnnotatedBeanDefinitionReader(this);
    
    		this.scanner = new ClassPathBeanDefinitionScanner(this);
    	}
  ```

  - d
  
- ```
   
   ```

   





- ```java
  public interface AnnotatedBeanDefinition extends BeanDefinition {
  
  	AnnotationMetadata getMetadata();
  
  	@Nullable
  	MethodMetadata getFactoryMethodMetadata();
  
  }
  ```

- ```java
  
  /**
   * spring中用来描述 bean 的一个接口
   */
  public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {
  
  	/**
  	 * 标准单例作用域的作用域标识符： “singleton”
  	 */
  	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
  
  	/**
  	 */
  	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;
  
  	/**
  	 *
  	 */
  	int ROLE_APPLICATION = 0;
  
  	/**
  	 * ROLE_SUPPORT = 1 实际上表示：我这个Bean是用户的,是从配置文件中过来的
  	 */
  	int ROLE_SUPPORT = 1;
  
  	/**
  	 * 表示我这个Bean是spring自己的,和用户没有一毛钱关系
  	 */
  	int ROLE_INFRASTRUCTURE = 2;
  
  	/**
  	 * 设置父类的名字
  	 */
  	void setParentName(@Nullable String parentName);
  
  	/**
  	 * 获取父类的名字
  	 */
  	@Nullable
  	String getParentName();
  
  }
  ```

  - spring中分为三种类型的bean:
    1. xml 的bean
    2. 被注解的bean，使用 @Bean 得到的
    3. 被注解的bean，使用类似 @Component、@Service 等
    4. spring内部的bean
  - 



- ```java
  	/**
    	 * 1.注册单个bean给容器，
    	 *   比如有新加的类可以使用这个方法，
    	 *   但是注册之后需要手动调用refresh()方法去触发容器解析注释。
    	 *
    	 * 2.这个方法有两种用途：
    	 *   可以注册一个配置类
    	 *   也可以单独注册一个bean
    	 */
    	public void register(Class<?>... annotatedClasses) {
    		Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
    		this.reader.register(annotatedClasses);
    	}
  ```

  

![](images\spring\spring管理bean对象1.png)





### 1.2 步骤解读：

1. `AnnotationConfigApplicationContext()`方法中**第一步是执行其父类的构造方法**

   父类是`org.springframework.context.support.GenericApplicationContext`

   所以执行 `GenericApplicationContext`() 方法，**<font color=red>实例化了一个工厂: `DefaultListableBeanFactory`</font>**

2. `AnnotationConfigApplicationContext()`方法中**第二步是创建一个读取注解的Bean定义读取器**

   1. `org.springframework.context.annotation.AnnotatedBeanDefinitionReader`

      <font color=red>实例化一个`AnnotatedBeanDefinitionReader`对象</font>, `new AnnotatedBeanDefinitionReader(this);`  

      1. `org.springframework.context.annotation.AnnotationConfigUtils`

         `AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);`

         1. 

   2. 

   3. 2、ClassPathBeanDefinitionScanner，能够扫描我们bd,能够扫描一个类，并且转换成bd
      	org.springframework.context.annotation.AnnotationConfigApplicationContext#AnnotationConfigApplicationContext()
      		委托AnnotationConfigUtils
      		org.springframework.context.annotation.AnnotatedBeanDefinitionReader#AnnotatedBeanDefinitionReader()
      			

![](images\spring\DefaultListableBeanFactory类图.png)







![](images\spring中重要的6个类.png)



org.springframework.context.support.GenericApplicationContext#beanFactory









## 2. spring的三个扩展点(不止三个)

### 2.1 spring的后置处理器

#### 2.1.1 BeanPostProcessor

- bean的后置器

- org.springframework.beans.factory.config.BeanPostProcessor

- ```java
  package org.springframework.beans.factory.config;
  
  import org.springframework.beans.BeansException;
  import org.springframework.lang.Nullable;
  
  /**
   *
   * BeanPostProcessor是Spring框架通过的一个扩展点(不止一个)
   * 通过实现BeanPostProcessor接口,程序员可以插手bean实例化的过程,从而减轻了beanFactory的负担
   * 值得说明的是这个接口可以设置多个,会形成一个列表,然后依次执行
   * 比如AOP就是在bean实例后期将切面逻辑植入bean示例中的
   * AOP也正是通过BeanPostProcessor和IOC容器建立起了联系
   *
   * 由spring提供的默认的PostProcessor,spring提供了很多默认的PostProcessor,
   * 下面将一一介绍这些实现类的功能:
   * BeanPostProcessor 的使用方式(把动态代理 和 IOC、AOP 结合起来使用)
   *
   * 查看类的关系图,可以知道spring提供了以下的默认实现,以下是几个常用的:
   * 1. ApplicationContextAwareProcessor (acap)
   *   acap后置处理器的作用是,当应用程序定义的Bean实现ApplicationContextAware接口时
   *   注入ApplicationContext对象
   *   这是它的第一个作业,他还有其他作用,这里不一一举例了,可以参考源码
   *   我们可以针对ApplicationContextAwareProcessor写一个例子
   *
   * 2. InitDestroyAnnotationBeanPostProcessor
   *   用来处理自定义的初始化方法和销毁方法
   *   之前说过spring提供了3中自定义初始化和销毁方法分别是
   *     1) 通过@Bean指定init-method 和 destroy-method 方法属性
   *     2) Bean实现 InitializingBean接口 和 DisposableBean接口
   *     3) 使用 @PostConstruct 和 @PreDestroy
   *   为什么spring通过这三种方法能完成对bean生命周期的回到呢？
   *   可以通过InitDestroyAnnotationBeanPOstProcessor的源码来解释
   *
   * 3. InstantiationAwareBeanPostProcessor
   *
   * 4. CommonAnnotationBeanPostProcessor
   *
   * 5. AutowiredAnnotationBeanPostProcessor
   *
   * 6. RequiredAnnotationBeanPostProcessor
   *
   * 7. BeanValidationPostProcessor
   *
   * 8. AbstractAutoProxyCreator
   *
   */
  public interface BeanPostProcessor {
  
  	@Nullable
  	default Object postProcessBeforeInitialization(Object bean, String beanName) 
          throws BeansException {
  		return bean;
  	}
  
  	@Nullable
  	default Object postProcessAfterInitialization(Object bean, String beanName) 
          throws BeansException {
  		return bean;
  	}
  
  }
  ```

- ```java
  package com.luban.dao;
  
  import org.springframework.stereotype.Repository;
  
  import javax.annotation.PostConstruct;
  import javax.annotation.PreDestroy;
  
  @Repository
  public class IndexDao {
  
  	public IndexDao(){
  		System.out.println("IndexDao() ... ");
  	}
  
  	@PostConstruct
  	public void init() {
  		System.out.println("init() ... ");
  	}
  
  	@PreDestroy
  	public void destroy() {
  		System.out.println("destroy() ...");
  	}
  
  	public void query() {
  		System.out.println("IndexDao query() ... ");
  	}
  }
  ```

- ```java
  package com.luban.beanPostProcessor;
  
  import org.springframework.beans.BeansException;
  import org.springframework.beans.factory.config.BeanPostProcessor;
  import org.springframework.core.PriorityOrdered;
  import org.springframework.stereotype.Component;
  
  /**
   * 实现 PriorityOrdered 接口，重写 getOrder()方法，
   * 控制spring执行的顺序：getOrder()返回的数值越小,spring越早执行
   */
  @Component
  public class TestBeanPostProcessor implements BeanPostProcessor , PriorityOrdered {
  	/**
  	 * 在bean执行自定义的初始化方法之前执行
  	 */
  	@Override
  	public Object postProcessBeforeInitialization(Object bean, String beanName) 
          throws BeansException {
  		System.out.println(beanName + " postProcessBeforeInitialization() ...");
  		/**
  		 * 可以在这里使用代理,返回代理对象
  		 */
  		return bean;
  	}
  
  	/**
  	 * 在bean执行自定义的初始化方法之后执行
  	 */
  	@Override
  	public Object postProcessAfterInitialization(Object bean, String beanName) 
          throws BeansException {
  		System.out.println(beanName + " postProcessAfterInitialization() ...");
  		return bean;
  	}
  
  	@Override
  	public int getOrder() {
  		return 0;
  	}
  }
  ```

- ```java
  package com.luban.test;
  
  import com.luban.app.AppConfig;
  import com.luban.dao.IndexDao;
  import org.springframework.context.annotation.AnnotationConfigApplicationContext;
  
  public class Test {
  
  	public static void main(String[] args) {
  
  		// AnnotationConfigApplicationContext : 注解配置应用上下文
  		// 把spring所有的前提环境准备好
  		AnnotationConfigApplicationContext context =
  				new AnnotationConfigApplicationContext(AppConfig.class);
  		
  		IndexDao indexDao = (IndexDao) context.getBean("indexDao");
  		indexDao.query();
  	}
  
  }
  ```

- 执行结果：

  ```java
  一月 01, 2021 10:49:22 上午 org.springframework.context.support.AbstractApplicationContext prepareRefresh
  信息: Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@3fee733d: startup date [Fri Jan 01 10:49:22 CST 2021]; root of context hierarchy
  org.springframework.context.event.internalEventListenerProcessor postProcessBeforeInitialization() ...
  org.springframework.context.event.internalEventListenerProcessor postProcessAfterInitialization() ...
  org.springframework.context.event.internalEventListenerFactory postProcessBeforeInitialization() ...
  org.springframework.context.event.internalEventListenerFactory postProcessAfterInitialization() ...
  appConfig postProcessBeforeInitialization() ...
  appConfig postProcessAfterInitialization() ...
  IndexDao() ... 
  indexDao postProcessBeforeInitialization() ...
  init() ... 
  indexDao postProcessAfterInitialization() ...
  IndexDao query() ... 
  ```

  

### 2.2 BeanFactoryPostProcessor

- Bean工厂的后置器
- 

- ```java
  package org.springframework.beans.factory.config;
  
  import org.springframework.beans.BeansException;
  
  /**
   * spring的扩展点之一:
   * 
   * 实现该接口，可以在spring的bean创建之前修改bean的定义属性
   * spring允许BeanFactoryPostProcessor在容器实例化任何其他bean之前读取配置元数据，
   * 并可以根据需要进行修改,例如可以把bean的scope从singleton改为prototype,
   * 也可以把property的值给修改。
   *
   * 可以同时配置多个BeanFactoryPostProcessor,并通过设置“order”属性来控制
   * 各个BeanFactoryPostProcessor
   * BeanFactoryPostProcessor实在spring容器加载了bean的定义文件之后,在bean实例化之前执行的
   *
   * 可以写一个例子测试：来测试该功能
   */
  @FunctionalInterface
  public interface BeanFactoryPostProcessor {
  
  	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) 
          throws BeansException;
  
  }
  ```



### 2.3 BeanDefinitionRegistryPostProcessor





小技巧：

1. 在win10的主屏幕输入框，输入 notepad ，回车，自动打开 notepad++ 编辑器

2. ![](images\小技巧1.png)
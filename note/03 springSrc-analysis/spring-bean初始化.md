# spring bean

## 1. ImportBeanDefinitionRegistrar 

- 实现了 ImportBeanDefinitionRegistrar 接口的类，重写 registerBeanDefinitions 方法，能够自定义注入bean对象到spring容器中。

## 2. spring的容器

1.宏观理解：

- spring容器就是一个工厂，其中包含了处理器，注册器

2.微观理解：

- spring的容器仅仅就是一个Map，存放了对象的名字和实例

- ```java
  private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
  ```

## 3. getSingleton

1.第一调用getSingleton()方法，返回空

- isSingletonCurrentlyInCreation(beanName) 会返回false，spring认为还没有到需要创建对象的时候，所以这个geSingleton()会直接返回一个null

- ```java
  protected Object getSingleton(String beanName, boolean allowEarlyReference) {
  	/**
  	 * 从 Map 中获取bean，如果不为空直接返回，不再进行初始化工作
  	 * 讲道理一个程序员提供的对象这里一般都是为空的
  	 */
      Object singletonObject = this.singletonObjects.get(beanName);
      if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
          synchronized (this.singletonObjects) {
              singletonObject = this.earlySingletonObjects.get(beanName);
              if (singletonObject == null && allowEarlyReference) {
                  ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                  if (singletonFactory != null) {
                      singletonObject = singletonFactory.getObject();
                      this.earlySingletonObjects.put(beanName, singletonObject);
                      this.singletonFactories.remove(beanName);
                  }
              }
          }
      }
      return singletonObject;
  }
  ```

  

2.第二次调用getSingleton()方法

- ```java
  public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
      Assert.notNull(beanName, "Bean name must not be null");
      synchronized (this.singletonObjects) {
          Object singletonObject = this.singletonObjects.get(beanName);
          if (singletonObject == null) {
              if (this.singletonsCurrentlyInDestruction) {
                  throw new BeanCreationNotAllowedException(beanName,
                                                            "Singleton bean creation not allowed while singletons of this factory are in destruction " +
                                                            "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
              }
              if (logger.isDebugEnabled()) {
                  logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
              }
              /**
  				 * 将 beanName 添加到 singletonsCurrentlyInCreation 这样一个 set 集合中
  				 * 表示 beanName 对应的 bean 正在创建中
  				 */
              beforeSingletonCreation(beanName);
              boolean newSingleton = false;
              boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
              if (recordSuppressedExceptions) {
                  this.suppressedExceptions = new LinkedHashSet<>();
              }
              try {
                  singletonObject = singletonFactory.getObject();
                  newSingleton = true;
              }
              catch (IllegalStateException ex) {
                  // Has the singleton object implicitly appeared in the meantime ->
                  // if yes, proceed with it since the exception indicates that state.
                  singletonObject = this.singletonObjects.get(beanName);
                  if (singletonObject == null) {
                      throw ex;
                  }
              }
              catch (BeanCreationException ex) {
                  if (recordSuppressedExceptions) {
                      for (Exception suppressedException : this.suppressedExceptions) {
                          ex.addRelatedCause(suppressedException);
                      }
                  }
                  throw ex;
              }
              finally {
                  if (recordSuppressedExceptions) {
                      this.suppressedExceptions = null;
                  }
                  afterSingletonCreation(beanName);
              }
              if (newSingleton) {
                  addSingleton(beanName, singletonObject);
              }
          }
          return singletonObject;
      }
  }
  ```




## 4. spring的factory-method

- spring.xml配置文件中配置 factory-method

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
      
  	<!-- 演示工厂方法 -->
  	<bean id="factoryMethodDemo" class="com.study.spring5.service.FactoryMethodServiceImpl" factory-method="getEntity">
  	</bean>
  
  </beans>
  ```

- 有工厂方法的类

  ```java
  package com.study.spring5.service;
  
  import com.study.spring5.entity.Entity;
  
  /**
   * 工厂方法，在spring的xml配置文件中配置了这个类，
   * 并设置了getEntity为工厂方法，
   * 最终spring容器中会有 FactoryMethodServiceImpl类型的bean对象，
   * 只有 Entity类型的bean对象
   */
  public class FactoryMethodServiceImpl {
  
  	public static Object getEntity() {
  		return new Entity();
  	}
  
  }
  ```

- 工厂方法实例化的类

  ```java
  package com.study.spring5.entity;
  
  public class Entity {
  }
  ```

- 最终spring容器会管理一个 Entity类型的实例对象

## 5. spring为什么要得到构造方法

- 因为spring需要得到构造方法来实例化bean，



## 6.spring 自动装配模型

- 装配模型为No时，使用的是 byType的技术



## 7. 实例化过程中的重要类

- org.springframework.beans.factory.support.ConstructorResolver



## 8. spring-构造方法

### 8.1 默认构造方法

### 8.2 特殊构造方法



## 9. spring-循环依赖原理
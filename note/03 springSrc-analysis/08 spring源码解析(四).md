# spring源码解析





## 普通类转化为BeanDefinition的方法

- 只有通过new BeanDefinition(Class clazz)方法
- 不同的类有不同类型的new方法：
  - 注解类都是通过 new AnnotationBeanDefinition() 方法成为BeanDefinition
  - spring内部配置的类 通过 new RootBeanDefinition() 方法成为 BeanDefinifion







`@Import`注解中可以填写三种数据：

- normal class 普通的类，实例：`@Import(UserServiceImpl.class)`
- importSelect ，实例：`@Import()`
- ImportBeanDefinitionRegistrar，实例：`@Import()`





## 向spring容器中添加BeanDefinition的方法

1. `registry()` ：手动注册

- 需要一个类，**没法参与类变成bd的过程**
- 实例：`context.registry(AppConfig.class);`

2. `scan()`：扫描包注册

-  需要一个包名，扫描包里的类，**没法参与类变成bd的过程**
- `context.scan("com.study.service");`

3. ImportBeanDefinitionRegistrar 

- **可以参与变成bd的过程**
- 实例：`@Import()`





MapperScan的原理是什么？

- MapperScan所做的事情就是将接口扫描出来，然后将其变成对象，并将这个对象交给spring容器管理



## 模拟mybatis

### 1. 知识点

- A extends B ，存在如下代码：

  ```java
  class C {
      @Autowired
  	private B b;
  }
  ```

  如果spring 容器存在A类型的对象，不会将其注入到C中

- A implements B ,存在如下代码：

  ```java
  class C {
      @Autowired
  	private B b;
  }
  ```

  如果spring 容器存在A类型的对象，会将其注入到C中

### 2. 模拟-实现

#### 2.1 准备

- Mapper接口

```java
package com.study.dao;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface CountryMapper {

    @Select("select * from country")
    List<Map<String, Object>> list();

}

```

#### 2.2 实现1

- 实现类：

```java
package com.study.util;

import com.study.dao.CountryMapper;
import com.study.proxy.MyInvocationHandler;
import com.study.test.MapperScanTest;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Proxy;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        /*------------------------------------方法一:----------------------------------*/
        // 1. 扫描报下所有的接口Mapper(myBatis中的接口)

        // 2. 获取这些接口对应的每一个对象，并且这个对象实现了该mapper接口
        CountryMapper mapper = (CountryMapper) Proxy.newProxyInstance(MapperScanTest.class.getClassLoader(), new Class[]{CountryMapper.class}, new MyInvocationHandler());
        mapper.list();// 测试
        // 3. 将对象放到spring容器中
        // 3.1 得到beanDefinition
        // 获取代理对象的类名，然后通过类名获取beanDefinition
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(mapper.getClass());
        // GenericBeanDefinition基本的BeanDefinition
        GenericBeanDefinition beanDefinition = (GenericBeanDefinition) builder.getBeanDefinition();
        // 3.2 注册到spring容器中
        beanDefinitionRegistry.registerBeanDefinition("countryMapper", beanDefinition);
    }
}
```

- 测试类：

```java
package com.study.test;

import com.study.config.MyBatisSpringConfig;
import com.study.dao.CountryMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 模拟 mybatis
 */
public class MapperScanTest {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyBatisSpringConfig.class);
        context.getBean("countryMapper");

    }
}
```

- 结果出现异常，因为动态生成的代理类无法通过 `BeanDefinitionBuilder.genericBeanDefinition(mapper.getClass());` 

  生成BeanDefinition。

- 此方法不行，但是思路是正确的。

#### 2.3 实现2

- 实现类：

```java
package com.study.util;

import com.study.dao.CountryMapper;
import com.study.proxy.MyInvocationHandler;
import com.study.test.MapperScanTest;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Proxy;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        /*---------------------------------------------方法二（MyFactoryBean版本1）:-----------------------------------*/
//        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((CountryMapper.class));
//        GenericBeanDefinition beanDefinition = (GenericBeanDefinition) builder.getBeanDefinition();
//        /**
//         * 因为现在这个beanDefinition对应的class是CountryMapper.class，这是一个接口，是无法实例化的，
//         * 所以设置这个beanDefinition对应的类为MyFactoryBean2.class，从而获取这个beanDefinition的bean对象时，
//         * 就是获取MyFactoryBean.class中getObject()方法返回的对象，也就是CountryMapper接口对应的代理对象，这才是可以实例化的类型
//         */
//        beanDefinition.setBeanClass(MyFactoryBean.class);
//        beanDefinitionRegistry.registerBeanDefinition("countryMapper", beanDefinition);

    }
}
```

- 工具类：MyFactoryBean1.java

```java
package com.study.util;

import com.study.dao.CountryMapper;
import com.study.proxy.MyInvocationHandler;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * 这个类会产生两个bean对象交spring容器管理：
 * 1. MyFactoryBean 本身，对应的beanName-beanDefinition 为 &myFactoryBean-beanDefinition
 * 2. getObject()返回的对象，对应的beanName-beanDefinition 为 myFactoryBean-beanDefinition
 *
 * 版本1
 */
@Component
public class MyFactoryBean implements FactoryBean {

    Class[] classes = new Class[]{CountryMapper.class};

    @Override
    public Object getObject() throws Exception {
        Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), classes, new MyInvocationHandler());
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return CountryMapper.class;
    }

}

```

- 测试：

```java
package com.study.test;

import com.study.config.MyBatisSpringConfig;
import com.study.dao.CountryMapper;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 模拟 mybatis
 */
public class MapperScanTest {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyBatisSpringConfig.class);
        ((CountryMapper) context.getBean("countryMapper")).listjava();
        
    }
}

```

- 成功实现了mybatis的MapperScan功能，但是写死了，所以有了版本二

#### 2.4 实现3

- 实现类：

```java
package com.study.util;

import com.study.dao.CountryMapper;
import com.study.proxy.MyInvocationHandler;
import com.study.test.MapperScanTest;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Proxy;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        /*---------------------------------------------方法二（MyFactoryBean2版本2）:-----------------------------------*/
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition((CountryMapper.class));
        GenericBeanDefinition beanDefinition = (GenericBeanDefinition) builder.getBeanDefinition();
        // 通过构造方法参数来确定交由spring管理的对象
        // 设置这个beanDefinition对相应的类 实例化时所使用的构造方法，其构造参数是 com.study.dao.UserDao.class
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.study.dao.CountryMapper");
        /**
         * 因为现在这个beanDefinition对应的class是CountryMapper.class，这是一个接口，是无法实例化的，
         * 所以设置这个beanDefinition对应的类为MyFactoryBean2.class，从而获取这个beanDefinition的bean对象时，
         * 就是获取MyFactoryBean2.class中getObject()方法返回的对象，也就是CountryMapper接口对应的代理对象，这才是可以实例化的类型
         */
        beanDefinition.setBeanClass(MyFactoryBean2.class);
        beanDefinitionRegistry.registerBeanDefinition("countryMapper", beanDefinition);
    }
}

```

- 工具类：

```java
package com.study.util;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 这个类会产生两个bean对象交spring容器管理：
 * 1. MyFactoryBean 本身，对应的beanName-beanDefinition 为 &myFactoryBean-beanDefinition
 * 2. getObject()返回的对象，对应的beanName-beanDefinition 为 myFactoryBean-beanDefinition
 *
 * 版本2
 */
@Component
public class MyFactoryBean2 implements FactoryBean, InvocationHandler {

    private Class clazz;

    public MyFactoryBean2(Class clazz){
        this.clazz = clazz;
    }

    @Override
    public Object getObject() throws Exception {
        Object proxy = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, this);
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return clazz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("----------proxy------");
        return null;
    }
}
```

- 动态生成



自动装配：

- byType
- byName
- byConstructor
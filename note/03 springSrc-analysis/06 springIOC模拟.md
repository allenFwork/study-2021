# 模拟Spring IOC

## 1. 模拟BeanFactory工厂

```java
package com.study.springioc.dao;

public interface UserDao {

    public void query() ;

}
```

```java
package com.study.springioc.dao;

public class UserDaoImpl implements UserDao {
    @Override
    public void query() {
        System.out.println("--------------------UserDaoImpl find()--------------------");
    }
}
```

```java
package com.study.springioc.service;

public interface UserService {
    void find();
}
```

```java
package com.study.springioc.service;

import com.study.springioc.dao.UserDao;

public class UserServiceImpl implements UserService {

//    @Autowired
    private UserDao userDao;

//    public UserServiceImpl() {
//
//    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void find() {
        System.out.println("------------------UserServiceImpl find()------------------");
        userDao.query();
    }
}
```



```java
package com.study.springioc.exception;

/**
 * 自定义异常
 */
public class SpringException extends RuntimeException {

    public SpringException(String message) {
        super(message);
    }

}
```



```xml
<?xml version="1.0" encoding="UTF-8"?>

<!-- 模拟 spring ioc  -->
<!--
    spring 需要解决的问题：
        1. 哪些类需要spring来关联
        2. 怎么告诉spring这些类？  通过bean标签
        3. 怎么维护依赖关系？      通过set方法、constructor方法
        4. 怎么体现setter constructor
 -->
<!-- 模拟自动装配:autowired -->
<beans default-autowired="byType">
    <bean id="userDao" class="com.study.springioc.dao.UserDaoImpl"></bean>

<!--    <bean id="userService" class="com.study.springioc.service.UserServiceImpl"></bean>-->

    <!-- set方法注入 -->
<!--    <bean id="userService" class="com.study.springioc.service.UserServiceImpl">-->
<!--        &lt;!&ndash; name是类中setUserDao方法，ref是bean的id值 &ndash;&gt;-->
<!--        <property name="userDao" ref="userDao"></property>-->
<!--    </bean>-->

    <!-- 构造方法注入 -->
    <bean id="userService" class="com.study.springioc.service.UserServiceImpl">
        <!-- name的值对应的是那个类的成员变量的引用，不是构造方法的参数引用 -->
        <constructor-arg name="uerDao" ref="userDao"></constructor-arg>
    </bean>

</beans>
```



## 2. 模拟BeanFactory工厂

```java
package com.study.springioc.util;

import com.study.springioc.exception.SpringException;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 模拟 spring ioc
 */
public class BeanFactory {

    /**
     * 存储bean对象的容器
     */
    Map<String, Object> map = new HashMap<String, Object>();

    /**
     * @Description 工厂
     * @param xml 用来告诉spring需要用来管理的对象信息的配置文件路径
     */
    public BeanFactory(String xml){
        parseXml(xml);
    }

    /**
     * @descrption 用来解析xml文件 （dom4j）
     * @param xml 配置文件路径
     */
    public void parseXml(String xml){
        // 获取当前项目所在路径
        String path = this.getClass().getResource("/").getPath() + xml;
        // /G:/blue_world/Documents/study-project/study-2021-01-06/spring-ioc/target/classes/spring.xml
        File file = new File(path);
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(file);

            // beans标签
            Element elementRoot = document.getRootElement();

            // 解析是否开通了自动装配
            Attribute attribute = elementRoot.attribute("default-autowired");
            boolean flag = false;
            if (attribute != null) {
                flag = true;
            }

            // 所有的bean标签
            List<Element> beanList = elementRoot.elements();
            for(Iterator<Element> bean = beanList.iterator(); bean.hasNext(); ){
                /**
                 * setup 1 : 实例化对象
                 */
                // 获取beans标签里第一个bean标签，然后接着一直获取下一个bean标签
                Element elementBean = bean.next();

                // 获取bean的id属性
                Attribute attributeId = elementBean.attribute("id");
                String beanName = attributeId.getValue();
                // 获取bean的class属性
                Attribute attributeClass = elementBean.attribute("class");
                String className = attributeClass.getValue();

                // 加载告知spring需要管理的类，也就是bean对应的类
                Class clazz = Class.forName(className);
                //
                Object object = null;

                /*------------------------------循环bean的子标签开始：有明确写入的依赖------------------------------*/
                /**
                 * setup 2 : 维护依赖关系
                 *  看这个对象有没有依赖（判断是否有property属性标签，或者判断类是否有成员变量，这个成员变量是否通过spring注入管理）
                 *  如果有property属性标签，则表示要通过spring注入
                 */
                // iterator2 表示bean的子标签，可以是property标签，也可以是constructor-arg标签
                for(Iterator<Element> iterator2 = elementBean.elementIterator(); iterator2.hasNext(); ) {
                    /**
                     * <property name="userDao" ref="userDao"></property>
                     * 获取ref的value，通过 value（也就是bean的id）得到对象（map）
                     * 获取name的value，然后根据value值获取一个Field的对象，再通过field的set方法set那个对象
                     */
                    // property标签 或者 constructor-arg标签
                    Element elementProperty = iterator2.next();
                    // 判断是不是property标签
                    if(elementProperty.getName().equals("property")) {
                        // 由于是setter方法注入，所以此对象是有 无参构造方法的，没有特殊的构造方法
                        object = clazz.newInstance();
                        Object injectObject = map.get(elementProperty.attribute("ref").getValue());
                        String nameValue = elementProperty.attribute("name").getValue();
                        // 获取成员变量
                        Field field = clazz.getDeclaredField(nameValue);
                        // 因为对象的属性是private，私有的，所以不能直接通过set方法设置，得先设置权限
                        field.setAccessible(true);
                        field.set(object, injectObject);
                    } else if ("constructor-arg".equals(elementProperty.getName())) { // 该对象有特殊构造方法

                        // 这里需要补充，构造方法可能是多个参数 。。。
                        Object injectObject = map.get(elementProperty.attribute("ref").getValue());
                        /**
                         * 这里这个注入的成员变量类型，应该通过constructor-arg标签的name的值找到类中的成员变量，
                         * 从而获取这个成员变量的类型，然后获取的构造方法的参数应该就是这个类型，从而再获得构造方法，
                         * 最后通过constructor-arg标签的ref的值找到spring容器（map）的实例，将其作为构造方法的参数传进去创建实例
                         *
                         * 这里需要补充 。。。
                         */
                        Class injectObjectClazz = injectObject.getClass();
                        Constructor constructor = clazz.getConstructor(injectObjectClazz.getInterfaces()[0]);
                        object = constructor.newInstance(injectObject);

                    } else { // 该对象不需要注入任何的成员变量
                        object = clazz.newInstance();
                    }
                }
                /*---------------------------循环bean的子标签结束：有明确写入的依赖-----------------------------*/


                /**
                 * 上面的子标签循环中，只要有依赖的，都已经完成注入了，这个优先级也确实是高于自动装配的
                 * 现在开始进行自动装配，类中有成员变量，但是这个成员没有通过上面注入，还是null的可以通过自动装配完成注入
                 */
                /*---------------------------------------自动装配开始----------------------------------------*/
                if(flag) {
                    if(attribute.getValue().equals("byType")) { // 按照 byType 方式装配
                        // 判断类中是否有成员变量，也就是判断类中是否需要注入spring容器管理的对象
                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field: fields) {
                            // 得到属性的类型，比如String a，那么这里的field.getType()就是String.class
                            Class injectObjectClass = field.getType();
                            Object injectObject = null;
                            /**
                             * 由于是 byTYpe，所以需要遍历 map 当中的所有对象，
                             * 判断对象的类型是不是和和这个injectObjectClass相同
                             */
                            int count = 0;
                            for (String key: map.keySet()) {
                                Class temp = map.get(key).getClass().getInterfaces()[0];
                                if(temp.getName().equals(injectObjectClass.getName())) {
                                    injectObject = map.get(key);
                                    // 记录找到一个，因为可能找到多个
                                    count++;
                                }
                            }
                            if(count > 1) {
                                throw new SpringException("需要一个对象，但是找到了多个对象");
                            } else {
                                object = clazz.newInstance();
                                field.setAccessible(true);
                                field.set(object, injectObject);;
                            }
                        }

                    }
                }
                /*---------------------------------------自动装配结束----------------------------------------*/

                if(object == null) { // 没有子标签
                    object = clazz.newInstance();
                }

                map.put(beanName, object);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description 用来获取管理的对象，spring中的bean对象
     * @param beanName
     * @return
     */
    public Object getBean(String beanName){
        return map.get(beanName);
    }

}
```


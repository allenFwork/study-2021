### java自定义注解

- 自定义注解

```java
package com.study.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解
 *
 * 元注解：java自带的注解
 *  1. @Target：通过value的值用于表示该注解是用在什么上面的，
 *       ElementType.TYPE      表明使用在类上面
 *       ElementType.METHOD    表明使用在方法上面
 *       ElementType.FIELD     表明使用在成员属性(field)上面
 *       ElementType.LOCAL_VARIABLE  使用在局部变量上面
 *       ElementType.PARAMETER 表明使用在参数上面
 *       ...
 *  2. @Retention：java的注释是有生命周期的，默认情况下，生存在class文件中
 *       RetentionPolicy.SOURCE 表示该注解的生命周期只在源码中，添加了该注解的类在编译后会丢失该注解
 *       RetentionPolicy.CLASS  表示该注解的生命周期只在Class文件中，但是在运行时添加了该注解的类还是会丢失该注解
 *       RetentionPolicy.RUNTIME 表示该注解的生命周期到运行环境中，添加了该注解的类在jvm虚拟机执行的时候会使用该注解
 *       ...
 *  3. @Documented
 */
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {

    String value();
    String name() default "";

}
```

- 注解的使用

```java
package com.study.entity;

import com.study.annotation.Entity;

@Entity(value = "city", name = "南通")
public class CityEntity {
    private String id;
    private String name;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
```

```java
package com.study.util;

import com.study.annotation.Entity;

public class CommUtil {

    public static String buildQuerySqlForEntity(Object object){

        Class objcetClass = object.getClass();

        System.out.println(objcetClass.isAnnotationPresent(Entity.class)); // true
        // 步骤1: 判断传进来的参数对象是否被加了 @Entity注解
        if(objcetClass.isAnnotationPresent(Entity.class)){
            // 步骤2：得到注解
            Entity entity = (Entity) objcetClass.getAnnotation(Entity.class);
            // 步骤3：调用方法,得到对应的值
            String entityValue = entity.value();
            String entityName = entity.name();
            System.out.println(entityValue + ","+ entityName); // city,南通
        }
        return "";
    }
}
```

1. `@Target`：通过value的值用于表示该注解是用在什么上面的，

 *  `ElementType.TYPE`                           表明使用在类上面
 *  `ElementType.METHOD`                       表明使用在方法上面
 *  `ElementType.FIELD `                         表明使用在成员属性(field)上面
 *  `ElementType.LOCAL_VARIABLE`      使用在局部变量上面
 *  `ElementType.PARAMETER`                表明使用在参数上面
 *  ...

2. `@Retention`：  `java`的注释是有生命周期的，默认情况下，生存在class文件中

 *  `RetentionPolicy.SOURCE` 表示该注解的生命周期只在源码中，添加了该注解的类在编译后会丢失该注解
 *  `RetentionPolicy.CLASS`   表示该注解的生命周期只在Class文件中，但是在运行时添加了该注解的类还是会丢失该注解
 *  `RetentionPolicy.RUNTIM`E 表示该注解的生命周期到运行环境中，添加了该注解的类在`jvm`虚拟机执行的时候会使用该注解
 *  ...
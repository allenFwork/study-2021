

将普通的实体类转化为BeanDefinition类

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
public class Testing { 
    public static void main(String... args) { 
        Object obj = null; 
        if (obj instanceof Object) { 
            System.out.println("returned true"); 
        } else { 
            System.out.println("returned false"); // 返回 returned false
        } 
        System.out.println(" " + obj instanceof Object); 
        // 返回true，因为等价于"null" instanceof Object
    }
} 

```




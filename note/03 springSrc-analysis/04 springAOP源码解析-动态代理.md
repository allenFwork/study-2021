# 设计模式---proxy

## 什么是代理

- 增强一个对象的功能

- 买火车票，`app` 就是一个代理，它代理了火车站，小区当中的代售窗口



## `java` 当中如何实现代理

### 1. 代理的名词

- 代理对象： 增强后的对象

- 目标对象： 被增强的对象

**注释：它们不是绝对的，会根据情况发生变化**



### 2. `java` 实现的代理的两种办法

#### 2.1 静态代理

##### 2.1.1 继承

- 代理对象继承目标对象，重写需要增强的方法

- 实例：

  - ```java
    package com.luban.dao;
    /**
     * 目标对象
     */
    public class UserDaoImpl implements UserDao{
        public void query() {
            // 记录日志的代码不能添加在这里，破坏这个方法的单一原则，破坏类的封装性
            System.out.println("假装查询数据库！");
        }
    }
    ```

  - ```java
    package com.luban.proxyExtends;
    
    import com.luban.dao.UserDaoImpl;
    
    /**
     * 继承一个类是不需要这个类的源码的
     *
     * 1.通过继承实现代理
     *
     * 代理对象
     */
    public class UserDaoLogImpl extends UserDaoImpl {
        @Override
        public void query() {
            System.out.println("------log-------");
            super.query();
        }
    }
    ```

- 缺点：会产生代理类过多，非常复杂

##### 2.1.2 聚合

- 目标对象 和 代理对象 同时实现一个接口，代理对象当中要包含目标对象

- 它们实现同一个接口，是为了让代理类能够知道目标对象有哪些方法

- 实例：

  - ```java
    package com.luban.dao;
    /**
     * 目标对象
     */
    public class UserDaoImpl implements UserDao{
        public void query() {
            // 记录日志的代码不能添加在这里，破坏这个方法的单一原则，破坏类的封装性
            System.out.println("假装查询数据库！");
        }
    }
    ```

  - ```java
    package com.luban.dao;
    
    public interface UserDao {
        void query();
    }
    ```

  - ```java
    package com.luban.proxyCongregatea;
    
    import com.luban.dao.UserDao;
    
    /**
     * 2.使用聚合实现代理
     *
     */
    public class UserDaoLog implements UserDao {
    
        // 目标对象
        UserDao dao;
    
        // 将目标对象作为参数传入
        public UserDaoLog(UserDao dao){
            this.dao = dao;
        }
        
        @Override
        public void query() {
            System.out.println("--------log--------");
            dao.query();
        }
    }
    ```

- 缺点：同样会产生代理类过多，只不过比继承的方式少一点点

##### 2.1.3 总结：

- 使用：

  - ```java
    package com.luban.test;
    
    import com.luban.dao.UserDao;
    import com.luban.dao.UserDaoImpl;
    import com.luban.proxyCongregatea.UserDaoLog;
    import com.luban.proxyCongregatea.UserDaoPower;
    import com.luban.proxyExtends.UserDaoLogImpl;
    
    public class Test {
    
        public static void main(String[] args) {
            // 1.通过继承实现代理
            UserDaoImpl userDao = new UserDaoLogImpl();
            userDao.query();
    
            // 2.通过聚合实现代理
            UserDao targetObject = new UserDaoImpl();
            UserDao proxyObject  = new UserDaoLog(targetObject);
            proxyObject.query();
            // 实现两种逻辑的代理
            UserDao target = new UserDaoLog(new UserDaoImpl());
            UserDao proxy = new UserDaoPower(target);
            proxy.query();
        }
    
    }
    ```

- 在不确定的情况下，尽量不要去使用静态代理。因为一旦使用了，就会产生类，而一旦产生类就会爆炸。

- **代理模式** 和 **装饰者模式** 思想是一样的，只是代理模式传入的是 `Object` 对象，而 **装饰者模式** 传入的是具体对象

- 上面的2.1.2中实例具体传入了 `UserDao` 类型对象，所以这其实是 **装饰者模式**，将其改为 `Object` 就是代理模式 (多理解，表达不准确)



`java` 开发原则：

- 单一职责原则
- 



#### 2.2 动态代理

##### 2.2.1 自己模拟的动态代理：（初代版本）

```java
package com.luban.proxy;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ProxyUtil {

    /**
     * content ---> string
     * 第一步：获取 .java文件
     * 第二步：获取 .class文件
     * 第三步：new 一个对象
     *
     * jdk中
     * @return
     */
    public static Object newInstance(Object targetObject) {
        // 通过目标对象获取共同实现的接口
        Class targetInterface = targetObject.getClass().getInterfaces()[0];
        // 最终获取的代理对象
        Object proxyObject = null;
        // 换行
        String line = "\n";
        // 空格
        String tab = "\t";

        // 获取目标接口中的所有方法
        Method[] methods = targetInterface.getMethods();

        // 获取目标接口的名称
        String interfaceName = targetInterface.getSimpleName();
        // 生成 .java文件 中的字符串内容
        String content = "";
        String packageContent = "package com.google;" + line;
        String importContent = "import "+ targetInterface.getName() + ";" + line;
        String classFirstLine = "public class $Proxy implements " + interfaceName + "{" + line;
        String fieldContent = tab + "private "+ interfaceName + " target;" + line;
        String constructorContent = tab + "public " + "$Proxy" + "(" + interfaceName + " target) { " + line +
                                    tab + tab + "this.target = target;" + line +
                                    tab + "}" + line;
        String methodContent = "";
        for (Method method : methods){
            String returnTypeName = method.getReturnType().getSimpleName();
            String methodName = method.getName();
            // 获取方法的参数类型组成的数组,例如方法为 f(String a,String b)，则args[]为 String.class String.class
            Class args[] = method.getParameterTypes(); // 该方法返回的本身就是Class的对象，不是Object类型对象
//            Object args[] = method.getParameterTypes();
            String argsContent = "";
            String parameterContent = "";
            int argsCount = 0;
//            for(Object arg : args) {
            for(Class arg : args) {
//                String temp = arg.getClass().getSimpleName();
                String temp = arg.getSimpleName();
                // 参数名类似：String parameter1,String parameter2
                argsContent += temp + " parameter" + argsCount + ",";
                parameterContent += "parameter" + argsCount + ",";
                argsCount++;
            }
            // 如果有参数，就截取掉最后的逗号
            if(argsContent.length() > 0 ){
//                argsContent = argsContent.substring(0,argsContent.length()-1);
                argsContent = argsContent.substring(0, argsContent.lastIndexOf(",")-1);
            }
            if(parameterContent.length() > 0 ){
//                argsContent = argsContent.substring(0,argsContent.length()-1);
                parameterContent = argsContent.substring(0, argsContent.lastIndexOf(",")-1);
            }

            methodContent += tab + "public " + returnTypeName + " " + methodName + "(" + argsContent + ") { " + line +
                             tab + tab + "System.out.println(\"log\");" + line +
                             tab + tab + "target."+ methodName + "(" + parameterContent + ");" + line +
                             tab +"}" + line;

        }
        content += packageContent + importContent + classFirstLine + fieldContent + constructorContent + methodContent + "}";
        // 将content写入到.java文件中去，因为content是字符，所以使用字符流
        File file = new File("d:\\com\\google\\$Proxy.java");

        try {
            if(!file.exists()){
                file.createNewFile();
            }
            // 字符流
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();

            // 第二步：动态编译
            // 获取编译类
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // 获取文件管理器
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            Iterable units = fileManager.getJavaFileObjects(file);

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, units);
            task.call();
            fileManager.close();

            // ClassLoader加载类 .class
            URL[] urls = new URL[]{new URL("file:d:\\\\")};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            Class clazz = urlClassLoader.loadClass("com.google.$Proxy");
            // 获取构造方法，通过参数可以确定所要构造方法
            Constructor constructor = clazz.getConstructor(targetInterface);
            proxyObject = constructor.newInstance(targetObject);
            // clazz.newInstance(); 无法通过此方法获取代理对象，因为代理对象中没有默认构造器
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proxyObject;
    }

}
```

- 不需要手动创建类文件（因为一旦手动创建类文件，就会产生类爆炸），通过接口反射生成一个类文件，然后调用第三方的编译技术，动态编译这个产生的类文件成class文件，继而利用`UrlclassLoader` (因为这个动态产生的class不在工程当中所以需要使用`UrlclassLoader`)把这个动态编译的类加载到`jvm`当中，最后通过反射把这个类实例化。
- 缺点：
  1. 首先要生成文件
  2. 动态编译文件 class
  3. 需要一个`URLclassloader`
  4. 软件性能的最终体现在IO操作，涉及到磁盘模型会很慢
- 过程：file --> class -->  byte[] -->  Object(Class)
- **Class：类的类对象，一种对象，用来描述类的对象**
  - `Class clazz = Class.forName("xxx");` 
  - 上述代码表示：获取的 `clazz` 就是描述`xxx`的类对象，这是一个Object，是Class类型的



##### 2.2.2 `JDK` 动态代理

- 自动检测
- 通过接口反射得到字节码，然后把字节码转成class   native  `openJDK`  c++
- 虚拟机启动时，会将项目中所有预编译好的类文件（.class文件）加载到 `jvm` 中，但是通过`jdk`代理产生的类（代理类）是没有加载到 `jvm` 中的，需要`ClassLoader` 再加载一遍。
- **读 `jdk` 代理源码：**
  1. 验证
  2. 验证代理类实现的接口是不是`public` ，如果不是，就需要将代理类的包名生成和接口同样的，确定在同一个包里面，这样才能引用到接口；
  3. 生成代理类的包名都是有数子的，这是为了防止多线程情况下生成相同的包名的代理类
  4. `jdk` 直接通过接口反射得到代理类的字节码（二进制流），然后将字节码转换成 `Class` ，转换的方法是本地方法实现的 `native方法 openJDK c++语言实现的 `
- <font color=red>为什么`jdk` 不使用**继承**去实现代理？</font>
  - **因为 `jdk` 底层生成的代理类已经继承过了 `java.lang.reflect.Proxy` 类，`java`单继承，无法再继承目标对象类了**

###### 2.2.2.1 自己模拟的 `jdk` 动态代理：（二代版本）

- 生成代理类：

```java
package com.luban.proxy;

import com.luban.util.handler.CustomerInvocationHandler;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ProxyUtil2 {


    /**
     * 自己模拟的动态代理 V2.0
     * @param targetInterface 目标对象实现的接口
     * @param customerInvocationHandler 包含invoke方法
     * @return
     */
    public static Object newInstance(Class targetInterface, CustomerInvocationHandler customerInvocationHandler) {

        // 最终获取的代理对象
        Object proxyObject = null;
        // 换行
        String line = "\n";
        // 空格
        String tab = "\t";

        // 获取目标接口中的所有方法
        Method[] methods = targetInterface.getDeclaredMethods();

        // 获取目标接口的名称
        String interfaceName = targetInterface.getSimpleName();
        // 生成 .java文件 中的字符串内容
        String content = "";
        String packageContent = "package com.google;" + line;
        String importContent = "import " + targetInterface.getName() + ";" + line +
                               "import com.luban.util.handler.CustomerInvocationHandler;" + line +
                               "import java.lang.reflect.Method;" + line ;
        String classFirstLine = "public class $Proxy implements " + interfaceName + "{" + line;

        String fieldContent = tab + "private CustomerInvocationHandler customerInvocationHandler;" + line;

        /*----------------------------------------构造方法内容开始------------------------------------------*/
        String construcortContent = tab + "public $Proxy(CustomerInvocationHandler customerInvocationHandler) {" + line +
                                    tab + tab + "this.customerInvocationHandler = customerInvocationHandler;" + line +
                                    tab + "}" + line;

        /*--------------------------------------代理接口方法内容开始----------------------------------------*/
        String methodContent = "";
        for (Method method : methods){
            String returnTypeName = method.getReturnType().getSimpleName();
            String methodName = method.getName();
            // 获取方法的参数类型组成的数组,例如方法为 f(String a,String b)，则args[]为 String.class String.class
            Class args[] = method.getParameterTypes(); // 该方法返回的本身就是Class的对象，不是Object类型对象
            String argsContent = "";
            String parameterContent = "";
            int argsCount = 0;
            for(Class arg : args) {
                String temp = arg.getSimpleName();
                argsContent += temp + " parameter" + argsCount + ",";
                parameterContent += "parameter" + argsCount + ",";
                argsCount++;
            }
            // 如果有参数，就截取掉最后的逗号
            if(argsContent.length() > 0 ){
                argsContent = argsContent.substring(0, argsContent.lastIndexOf(",")-1);
            }
            if(parameterContent.length() > 0 ){
                parameterContent = argsContent.substring(0, argsContent.lastIndexOf(",")-1);
            }

            methodContent += tab + "public " + returnTypeName + " " + methodName + "(" + argsContent + ") { " + line +

                             /*-------------------------调用invoke方法实现添加的代理逻辑开始--------------------------*/
                             // 1. 获取 method
//                             tab + tab + "Method method = " + interfaceName + ".class.getDeclaredMethod(\"" + methodName + "\");" + line ;
                             tab + tab + "try {" + line +
                             tab + tab + tab + "Method method = Class.forName(\"" + targetInterface.getName() + "\").getDeclaredMethod(\"" + methodName + "\");" + line ;
                             // 2. 获取参数
                             if(returnTypeName.equals("void")) {
                                 methodContent += tab + tab + tab + "customerInvocationHandler.invoke(method, null);" + line +
                                                  tab + tab + "}";
                             } else {
                                 // 构建参数 Object[] args = {String.class, Integer.class};
                                 // 使用强转连解决返回类型不一致问题
                                 methodContent += tab + tab + tab + "return ("+returnTypeName+")customerInvocationHandler.invoke(method, new Object());" + line +
                                                  tab+ tab + "}";
                             }
                             methodContent += "catch(Exception e){\n" + line +
                                              tab + tab + "}" + line + tab + "}" + line;

        }
        /*--------------------------------------代理接口方法内容结束----------------------------------------*/

        content += packageContent + importContent + classFirstLine + fieldContent + construcortContent + methodContent + "}";
        // 将content写入到.java文件中去，因为content是字符，所以使用字符流
        File file = new File("d:\\com\\google\\$Proxy.java");

        try {
            if(!file.exists()){
                file.createNewFile();
            }
            // 字符流
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();

            // 第二步：动态编译
            // 获取编译类
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // 获取文件管理器
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            Iterable units = fileManager.getJavaFileObjects(file);

            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, units);
            task.call();
            fileManager.close();

            // ClassLoader加载类 .class
            URL[] urls = new URL[]{new URL("file:d:\\\\")};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            Class clazz = urlClassLoader.loadClass("com.google.$Proxy");
            // 获取构造方法，通过参数可以确定所要构造方法
            Constructor constructor = clazz.getConstructor(CustomerInvocationHandler.class);
            proxyObject = constructor.newInstance(customerInvocationHandler);
            // clazz.newInstance(); 无法通过此方法获取代理对象，因为代理对象中没有默认构造器
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proxyObject;
    }

}
```

- 

```java
package com.luban.util.handler;

import java.lang.reflect.Method;

/**
 * 自定义接口，代替jdk使用的的InvocationHandler接口
 */
public interface CustomerInvocationHandler {

    /**
     * @param method
     * @param args 任何方法的参数，其本质就是对象，所以使用 Object 类型
     * @return
     */
    void invoke(Method method, Object args);

}
```

- 

```java
package com.luban.util.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CustomerInvocationHandlerImpl implements CustomerInvocationHandler {

    Object targetObject;

    public CustomerInvocationHandlerImpl(Object targetObject){
        this.targetObject = targetObject;
    }

    @Override
    public void invoke(Method method, Object args) {
        try {
            System.out.println("===============CustomerInvocationHandlerImpl Log==================");
            method.invoke(targetObject, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

```



##### 2.2.3 `cglib` 动态代理



## 补充：

### 1. `io` 流

- `io` 流分为字符流 和 字节流

### 2. `StringBuffer` 与 String 优化



## 作业

### 1. 模拟 `AOP`

### 2. 模拟 `IOC`


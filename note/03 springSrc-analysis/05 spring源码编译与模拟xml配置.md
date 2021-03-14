# spring 源码解析 一

## 1. spring 源码编译

### 1.1 命令行编译



### 1.2 使用idea编译



### 1.3 使用gradle编译

#### 1.3.1 安装gradle

- 配置gradle的环境变量

- 配置gradle的仓库

  ![](images\spring编译\gradle仓库.png)

#### 1.3.2 导入源码

- idea open 选择源码文件夹

  <img src="images\spring编译\idea导入spring5源码.png" style="zoom:60%;" />

- idea 中配置 setting 的 gradle 

  ![](images\spring编译\settings-gradle配置.png)

  - Gradle user home指定了一下，不想默认下载到C盘占空间
  - Build and run里面一定要选IDEA，默认是gradle，后面tomcat无法找到war包

- gradle下载依赖，构建项目时会有报错：

  ![](images\spring编译\gradle更新项目.png)

  - 修改spring-beans.gradle配置文件

- gradle重新下载依赖，构建项目：

  ![](images\spring编译\gradle构建项目.png)

- 

- 参考：https://blog.csdn.net/yufei_email/article/details/104241276



## 2. spring 源码项目添加模块





## 模拟ioc - xml配置

- 代码：

  ```java
  package org.spring.util;
  
  import org.dom4j.Attribute;
  import org.dom4j.Document;
  import org.dom4j.DocumentException;
  import org.dom4j.Element;
  import org.dom4j.io.SAXReader;
  
  import java.io.File;
  import java.lang.reflect.Constructor;
  import java.lang.reflect.Field;
  import java.util.HashMap;
  import java.util.Iterator;
  import java.util.List;
  import java.util.Map;
  
  public class BeanFactory {
  
      /**
       * 存储bean对象的容器
       */
      Map<String, Object> map = new HashMap<String, Object>();
  
      public BeanFactory(String xml){
          parseXml(xml);
      }
  
      public void parseXml(String xml){
          // 获取当前项目所在路径
          String path = this.getClass().getResource("/").getPath()  + "" + xml;
          System.out.println(path);// /G:/blue_world/Documents/study-project/java%20frame/luban/spring/target/classes/spring.xml
          File file = new File("G:\\blue_world\\Documents\\study-project\\java frame\\luban\\spring\\src\\main\\resources\\spring.xml");
          SAXReader saxReader = new SAXReader();
          try {
              Document document = saxReader.read(file);
              Element elementRoot = document.getRootElement();
  
              // 解析是否开通了自动装配
              Attribute attribute = elementRoot.attribute("default-autowired");
              boolean flag = false;
              if (attribute != null) {
                  flag = true;
              }
  
              List<Element> allChild = elementRoot.elements();
              for(Iterator<Element> iterator = allChild.iterator(); iterator.hasNext(); ){
                  /**
                   * setup 1 : 实例化对象
                   */
                  Element elementBean = iterator.next();
                  // 将beans中的bean都拿出来存储起来
                  Attribute attributeId = elementBean.attribute("id");
                  String beanName = attributeId.getValue();
                  Attribute attributeClass = elementBean.attribute("class");
                  String className = attributeClass.getValue();
                  Class clazz = Class.forName(className);
                  Object object = null;
                  /*--------------------------------循环bean的子标签开始：有明确写入的依赖----------------------------------*/
                  /**
                   * setup 2 : 维护依赖关系
                   *  看这个对象有没有依赖（判断是否有property属性，或者判断类是否有属性）
                   *  如果有，则注入
                   */
                  for(Iterator<Element> iterator2 = elementBean.elementIterator(); iterator2.hasNext(); ) {
                      /**
                       * 得到ref的value，通过value得到对象（map）
                       * 得到name的value，然后根据只获取一个Field的对象
                       * 通过field的set方法set那个对象
                       */
                      Element elementProperty = iterator2.next();
                      if(elementProperty.getName().equals("property")) {
                          // 由于是setter方法注入，所以此对象是有无参构造方法的，没有特殊的构造方法
                          object = clazz.newInstance();
                          Object injectObject = map.get(elementProperty.attribute("ref").getValue());
                          String nameValue = elementProperty.attribute("name").getValue();
                          Field field = clazz.getDeclaredField(nameValue);
                          // 因为对象的属性是private，私有的，所以不能直接通过set方法设置，得先设置权限
                          field.setAccessible(true);
                          field.set(object, injectObject);
                      } else { // 证明有特殊构造方法
                          Object injectObject = map.get(elementProperty.attribute("ref").getValue());
                          Class injectObjectClazz = injectObject.getClass();
                          Constructor constructor = clazz.getConstructor(injectObjectClazz.getInterfaces()[0]);
                          object = constructor.newInstance(injectObject);
                      }
                  }
                  /*--------------------------------循环bean的子标签结束：有明确写入的依赖----------------------------------*/
  
                  /*------------------------------------------自动装配开始------------------------------------------------*/
                  if(flag) {
                      if(attribute.getValue().equals("byType")) { // 按照 byType 方式装配
                          // 判断是否有依赖
                          Field[] fields = clazz.getDeclaredFields();
                          for (Field field: fields) {
                              // 得到属性的类型，比如String a，那么这里的field.getType()就是String.class
                              Class injectObjectClass = field.getType();
                              Object injectObject = null;
                              /**
                               * 由于是 byTYpe，所以需要遍历 map 当中的所有对象，
                               * 判断对象的类型是不是和和这个injectObjectClass形同
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
                  /*------------------------------------------自动装配结束------------------------------------------------*/
  
  
  
                  if(object == null) { // 没有子标签
                      object = clazz.newInstance();
                  }
  
                  map.put(beanName, object);
              }
  
  
          } catch (Exception e) {
              e.printStackTrace();
          }
      }
  
      public Object getBean(String beanName){
          return map.get(beanName);
      }
  
  }
  ```

  ```java
  package org.spring.util;
  
  public class SpringException extends RuntimeException {
  
      public SpringException(String message) {
          super(message);
      }
  
  }
  ```

  

- 使用`Class类型对象的newInstance()` 来创建实例，但是该`Class`对应的类中没有无参构造器，会报以下的错误：

```console
java.lang.InstantiationException: com.study.service.UserServiceImpl
	at java.lang.Class.newInstance(Class.java:427)
	at org.spring.util.BeanFactory.parseXml(BeanFactory.java:51)
	at org.spring.util.BeanFactory.<init>(BeanFactory.java:27)
	at com.study.test.SpringTest.main(SpringTest.java:10)
Caused by: java.lang.NoSuchMethodException: com.study.service.UserServiceImpl.<init>()
	at java.lang.Class.getConstructor0(Class.java:3082)
	at java.lang.Class.newInstance(Class.java:412)
	... 3 more
```



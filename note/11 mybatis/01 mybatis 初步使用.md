# Mybatis 初步使用

## 一、原生 JDBC 使用

### 1. 使用步骤：

### 2.实例：

```java
package com.study;

//import com.mysql.cj.jdbc.Driver;

import com.mysql.jdbc.Driver;

import java.sql.*;

public class TestJdbc {

  static {
    try {
      /* jdbc原生的基础：加载驱动（加载不同厂商的驱动）
       * 此处使用的mysql的8.0.5版本，所以使用的是 com.mysql.cj.jdbc.Driver 或 com.mysql.jdbc.Driver
       * 其中，mysql的 com.mysql.jdbc.Driver extends extends com.mysql.cj.jdbc.Driver 有继承关系
       */
      Class.forName(Driver.class.getName());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8", "root", "123456");
      PreparedStatement preparedStatement = connection.prepareStatement("select * from user where id = ?");
      preparedStatement.setString(1, "1");
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        // 获取第一列的名称
        String columnName1 = resultSet.getMetaData().getColumnName(1);
        // 获取第二列的名称
        String columnName2 = resultSet.getMetaData().getColumnName(2);
        // 打印第一列字段的值
        System.out.println(columnName1 + ": " + resultSet.getString(1));
        // 打印第二列字段的值
        System.out.println(columnName2 + ": " + resultSet.getString(2));
      }
      // 关闭连接，释放资源（数据库链接是有上限的）
      resultSet.close();
      preparedStatement.close();
      connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
```

### 3. mybatis 8.0.5 版本

1. com.mysql.jdbc.Driver 继承 com.mysql.cj.jdbc.Driver
2.  com.mysql.cj.jdbc.Driver 的静态块中DriverManager 注册了 Driver 实例对象

![](images-mybatis\mybatis8.0.5驱动类1.png)

![mybatis8.0.5驱动类2](images-mybatis\mybatis8.0.5驱动类2.png)



## 二、 整合 mybaits 使用

### 1. 配置 mybatis.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

  <!-- 自定义对象的实例化行为 -->
  <!--<objectFactory type="com.study.entity.User">
    <property name="id" value="2"/>
    <property name="name" value="superGirl"/>
  </objectFactory>-->

  <!-- 和spring整合后,environment配置将废除 -->
  <environments default="development">
    <environment id="development">
      <!-- 使用 jdbc 事务管理  -->
      <transactionManager type="JDBC"></transactionManager>
      <!-- 数据库连接池 -->
      <dataSource type="POOLED">
        <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" 
                  value="jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8"/>
        <property name="username" value="root"/>
        <property name="password" value="123456"/>
      </dataSource>
    </environment>
  </environments>

  <!-- 加载 Mapper.xml 文件-->
  <mappers>
    <!-- package 指定包名 -->
    <!--<package name="com.study.mapper"/>-->
    <!-- mapper标签有三个属性：
          resource：
          url：
          class：
    -->
    <!--<mapper resource="resources/mapper/UserMapper.xml" url="" class="" ></mapper>-->
    <mapper resource="resources/mapper/UserMapper.xml"></mapper>
  </mappers>

</configuration>
```



### 2. 生成对应的映射器

1. UserMapper.class

   ```java
   package com.study.mapper;
   
   import org.apache.ibatis.annotations.Mapper;
   import org.apache.ibatis.type.Alias;
   
   import java.util.List;
   import java.util.Map;
   
   //@Alias("userMapper")
   //@Mapper
   public interface UserMapper {
   
     public List<Map<String, Object>> selectAll(Map<String, Object> map);
   
   }
   ```

2. UserMapper.xml

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
   <mapper namespace="com.study.mapper.UserMapper">
   
     <!-- 之前如何过设置过别名，此处可以直接设置 type="user" -->
     <resultMap id="test" type="com.study.entity.User"></resultMap>
   
     <sql id="columnName">
       id, name
     </sql>
   
     <select id="selectAll" parameterType="java.util.Map" resultType="com.study.entity.User">
       SELECT * FROM user
     </select>
   
     <select id="selectById" parameterType="int" resultType="com.study.entity.User">
       SELECT <include refid="columnName"/> FROM user
       WHERE id = #{id}
     </select>
   
     <select id="selectById" parameterType="int" resultType="com.study.entity.User">
       SELECT * FROM user
       WHERE id = ${id}
     </select>
   
   </mapper>
   ```

### 3. 测试

```java
package com.study;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import com.study.mapper.UserMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * mybatis的初步使用
 */
public class TestMybatis {

  public static void main(String[] args) {
    String resource = "resources/mybatis.xml";
    try {
      InputStream inputStream = Resources.getResourceAsStream(resource);
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      SqlSession sqlSession = sqlSessionFactory.openSession();
      // 目前从调用者的角度来看，与数据库打交道的对象是 SqlSession
      // 通过动态代理的方式帮我们执行sql
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
      Map<String, Object> map = new HashMap<>();
      map.put("id", 1);
      System.out.println(userMapper.selectAll(map));
      sqlSession.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
```



## 三、mybatis源码编译

直接从git上下载，通过idea导入源码项目，自动编译为maven项目




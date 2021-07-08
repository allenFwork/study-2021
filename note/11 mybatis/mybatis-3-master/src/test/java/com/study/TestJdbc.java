package com.study;

//import com.mysql.cj.jdbc.Driver;

import com.mysql.jdbc.Driver;

import java.sql.*;

/**
 * 原生jdbc使用
 */
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

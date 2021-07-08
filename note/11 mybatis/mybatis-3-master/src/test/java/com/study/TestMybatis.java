package com.study;

import com.study.entity.User;
import com.study.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * mybatis的初步使用
 */
public class TestMybatis {

  public static void main(String[] args) {
    String resource = "resources/mybatis.xml";
    try {
      InputStream inputStream = Resources.getResourceAsStream(resource);
      // mybatis.xml 解析完成，配置好了 Configuration对象；
      // 同时 mapper.xml 文件也解析完成，配置好相应的 MappedStatement对象
      SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

      // 通过 SqlSessionFactory 对象获取 SqlSession对象，默认不会自动提交
      SqlSession sqlSession = sqlSessionFactory.openSession();


      // 目前从调用者的角度来看，与数据库打交道的对象是 SqlSession
      // 通过动态代理的方式帮我们执行sql 拿到一个动态代理后的Mapper对象
      UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

      SqlSession sqlSession2 = sqlSessionFactory.openSession();
      UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);


      /*----------------------------------- 测试mybatis的数据库执行流程（开始）---------------------------------------*/
      System.out.println(userMapper.selectAll());

      // 因为一级缓存，这里不会执行两次相同的sql
      System.out.println(userMapper.selectAll());
      // 如果有二级缓存，这里不会执行两次相同的sql（一级缓存只对单个SqlSession作用）
      System.out.println(userMapper2.selectAll());

      System.out.println(userMapper.selectById(1));
      System.out.println(userMapper.findByNameAndPower("batman", "power"));
      User user = new User();
      user.setId(1);
      user.setName("superman2");
      System.out.println(userMapper.updateName(user));
      /*----------------------------------- 测试mybatis的数据库执行流程（结束）---------------------------------------*/

      // 关闭SqlSession
      sqlSession.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

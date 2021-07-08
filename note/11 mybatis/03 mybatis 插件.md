# Mybatis 插件 - 分页插件

## 前言

- 比较常见的ORM框架（mybatis 和 hibernate），无非是对数据库的增删改查操作
- 使用的大多操作是查询，当查询数据过多时，我们会采取分页的形式展示数据：
  1. 考虑到性能，每次查询都查询出所有需求的数据对性能的影响太大了
  2. 就算一次性把所有的数据展示出来，页面上太占地方了，而且用户体验不好



## 分页实现思想

### 借助数组进行分页

- 原理

  进行查询操作的时候，将数据库所有满足条件的数据 保存在临时数组中，借助 List 的 subList方法获取到分页的纪录

- 缺点

  能解决我们之前的问题2，即用户体验问题，但是不能解决性能问题，假设数据量过大的话，大量类似操作会非常拖累系统性能，不可取。

### 使用sql分页

- 缺陷：

  每一条需要的sql都需要写limit语句，而且还需要写一条count，这样代码非常冗余，且不好管理。

### 使用RowBounds分页

### 使用拦截器分页



## 自定义mybatis分页插件

### 原理：

- 拦截mabatis底层执行sql，将其执行的sql修改为分页查询sql
- 拦截的对象为 StatementHandler，拦截的方法是prepare

### 实现：

1. 自定义具体实现的插件

```java
package com.study.plugin;

import com.study.util.PageUtil;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;

/**
 * 自定义拦截器:
 *  实现 org.apache.ibatis.plugin.Interceptor 接口
 * @Intercepts 拦截器注解
 *      args：你需要mybatis传入什么参数给你;
 *      type：你需要拦截的对象;
 *      method：要拦截的方法
 */
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class MyPagePlugin implements Interceptor {

    String dataBaseType = "";

    String pageSqlId = "";

    public String getDataBaseType() {
        return dataBaseType;
    }

    public void setDataBaseType(String dataBaseType) {
        this.dataBaseType = dataBaseType;
    }

    public String getPageSqlId() {
        return pageSqlId;
    }

    public void setPageSqlId(String pageSqlId) {
        this.pageSqlId = pageSqlId;
    }

    // 拦截器里面的逻辑
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // 拿到原来应该执行的sql
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        // 通过反射获取sqlId
//        Field delegate = StatementHandler.class.getDeclaredField("delegate");
//        delegate.setAccessible(true);
//        Object o = delegate.get(statementHandler);
//        o.getClass().getDeclaredField("mappedStatement").

        // 获取sqlId
        MetaObject metaObject = MetaObject.forObject(statementHandler,
                                                     SystemMetaObject.DEFAULT_OBJECT_FACTORY,
                                                     SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                                                     new DefaultReflectorFactory());
        String sqlId = (String) metaObject.getValue("delegate.mappedStatement.id");

        /*-------------------------------- 第零步：判断是否进行分页 -------------------------------*/
        if (sqlId.matches(pageSqlId)) {

            /*--------------------------- 第一步 执行一条count语句(开始) ---------------------------*/
            ParameterHandler parameterHandler = statementHandler.getParameterHandler();
            // sql = select * from user
            // select * from user where name = #{name}
            // 执行一条count语句

            // 1.1 拿到数据库连接对象
            Connection connection = (Connection) invocation.getArgs()[0];

            //  1.2 预编译SQL语句，拿到原来绑定的sql语句 原来应该执行的sql
            String sql = statementHandler.getBoundSql().getSql();

            String countSql = "select count(0) from (" + sql + ") a";
            System.out.println(countSql);

            // 1.3 执行 count 语句，怎么返回你执行count的结果
            // 渲染参数
            PreparedStatement preparedStatement = connection.prepareStatement(countSql);
            // 参数条件交由mybatis去处理（自己调用mapper接口时传入的实参）
            parameterHandler.setParameters(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            resultSet.close();
            preparedStatement.close();
            /*--------------------------- 第一步 执行一条count语句(结束) ---------------------------*/

            /*----------  第二步 重写sql select * from 表名字 limit start , pageSize(结束) ---------*/
            // 2.1 怎么知道start和limit
            // 2.2 凭借start 和 limit
            // 获得你传入的参数
            Map<String, Object> parameterObject = (Map<String, Object>) parameterHandler.getParameterObject();
            // limit page
            PageUtil pageUtil = (PageUtil) parameterObject.get("page");
            pageUtil.setCount(count);

            // 2.3 替换原来绑定的sql 拼接分页语句(limit) 并且修改mysql本地该执行的语句
            String pageSql = getPageSql(sql, pageUtil);
            metaObject.setValue("delegate.boundSql.sql", pageSql);
            System.out.println(pageSql);
        }
        // 推进拦截器调用链
        return invocation.proceed();
    }

    // 需要你返回一个动态代理后的对象, target 就是 StatementHandler对象
    @Override
    public Object plugin(Object target) {
        // 不需要自己生成动态代理对象，可以通过方法直接获取
        return Plugin.wrap(target, this);
    }

    // 会传入配置文件内容 用户可根据配置文件自定义
    @Override
    public void setProperties(Properties properties) {
//        String xxx = properties.getProperty("xxx");
    }

    public String getPageSql(String sql, PageUtil pageUtil) {
        if (dataBaseType.equals("mysql")) {
            return sql + " limit " + pageUtil.getStart() + " , " + pageUtil.getLimit();
        } else if (dataBaseType.equals("oracle")){
            // 拼接oracle的语句
        }
        return sql + " limit " + pageUtil.getStart() + " , " + pageUtil.getLimit();
    }
}
```

2. mybatis配置分页插件

```java
package com.study.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.study.plugin.MyPagePlugin;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class MybatisConfig {

    @Bean
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setPassword("123456");
        druidDataSource.setUsername("root");
        druidDataSource.setUrl("jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8");
        druidDataSource.setMaxActive(2);
        druidDataSource.setMaxWait(200);
        return druidDataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Autowired DataSource dataSource) throws IOException {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{getMyPagePlugin()});
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String packageXMLConfigPath = PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "/mapper/*.xml";
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(packageXMLConfigPath));
        return sqlSessionFactoryBean;
    }

    public Interceptor getMyPagePlugin() {
        MyPagePlugin myPagePlugin = new MyPagePlugin();
        myPagePlugin.setDataBaseType("mysql");
        myPagePlugin.setPageSqlId(".*ByPage$");
        return myPagePlugin;
    }

}
```






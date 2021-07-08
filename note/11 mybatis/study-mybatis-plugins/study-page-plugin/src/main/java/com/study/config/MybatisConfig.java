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

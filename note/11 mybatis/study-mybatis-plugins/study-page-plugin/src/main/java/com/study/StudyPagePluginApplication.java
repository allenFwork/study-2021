package com.study;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @MapperScan后面 value 跟的是包名，不能是类名
@MapperScan("com.study.mapper")
@SpringBootApplication
public class StudyPagePluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyPagePluginApplication.class, args);
    }

}

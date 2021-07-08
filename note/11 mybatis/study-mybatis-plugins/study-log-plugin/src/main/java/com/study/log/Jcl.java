package com.study.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// commons-logging
public class Jcl {

    public static void main(String[] args) {
        // 要么 jcl 是不适用的log4j的配置文件
        // jcl 是不是底层实际就是用log4j来打印日志的
        // Log的实现类 什么时候是Log4j 什么时候是jul
        Log log = LogFactory.getLog("jcl");
        log.info("jcl");
    }

}

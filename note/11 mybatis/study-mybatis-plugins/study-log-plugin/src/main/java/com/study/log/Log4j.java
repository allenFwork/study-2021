
package com.study.log;

import org.apache.log4j.Logger;

public class Log4j {

    public static void main(String[] args) {
        // 日志对象的名称命名为log4j
        Logger logger = Logger.getLogger("log4j");
        logger.info("log4j");
    }

}

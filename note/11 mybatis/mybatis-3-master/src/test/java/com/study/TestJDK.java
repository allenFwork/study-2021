package com.study;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class TestJDK {

  public void test(String name, String age) {

  }

  public static void main(String[] args) throws NoSuchMethodException {
    Method method = TestJDK.class.getMethod("test", String.class, String.class);
    for (Parameter parameter : method.getParameters()) {
      System.out.println(parameter.getName());
    }
    /*
     * 没有加 -parameters 配置参数，那么返回 arg0 arg1
     * 加了 -parameters 配置参数，那么返回 name age
     * */
  }

}

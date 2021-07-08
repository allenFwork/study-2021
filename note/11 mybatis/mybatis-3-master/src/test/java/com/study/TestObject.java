package com.study;

public class TestObject {

  public void test() {

  }

//  @Override
//  public boolean equals(Object obj) {
//    return super.equals(obj);
//  }

  public static void main(String[] args) throws NoSuchMethodException {
    // TestObject不重写 equals 方法，那么TestObject 中有的 equals 方法就是 Object 类中的方法
    System.out.println(TestObject.class.getMethod("equals", Object.class).getDeclaringClass()); // class java.lang.Object
    System.out.println(TestObject.class.getMethod("test").getDeclaringClass()); //class com.study.TestObject
  }

}

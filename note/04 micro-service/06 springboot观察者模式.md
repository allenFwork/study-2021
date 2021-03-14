# Spring Boot

## 1.面试知识点

spring自动装配模型有四种：no 、`byName`、`byType`、`byConstructor`

spring自动装配技术只有两种：`byName`、`byType`

当spring容器中管理了两个对象 `luban0` 和 `luban1`，这两个对象的类都实现了 `Luban` 接口，如果在另外一个类中通过自动装配注入 `Luban` 类型的对象，那么：

- ```java
  @Autowired
  private Luban luban;
  ```

  此时会报错，spring容器需要一个 `Luban` 类型的对象，结果找到两个

- ```java
  @Autowired
  private Luban luban1;
  ```

  成功自动装配，不会报错

上述例子，不能说明 `@Autowired` 先使用了 `byName`、再使用 `byType` 技术完成自动装配

查看spring底层源码，最终发现是通过先 `byType`，再 `byName` 技术，一定要看源码，总结是通过哪些方法



## 2. 观察者设计模式

```
jie神说用订阅和发布来理解更好，我想了一下是的
为什么呢？因为监听器这个名词听起来是一个主动的，可实际监听器是一个被动的玩意
比如我们事件源发布一个事件，然后监听器订阅了这个事件就能做出动作。
里面涉及到三个对象，事件源，事件、监听器，大家好好理解一下
```

### 2.1 特点：

- 被观察者持有监听的观察者的引用
- 被观察者支持增加和删除观察者
- 被观察者状态改变，通知观察者

### 2.2 观察者 implements Observer 重写 update 方法

- 当被观察者发生变化时，观察者收到通知进行具体的处理
- 观察者可以随时取消

### 2.3 优点：松耦合

- 观察者增加或删除无需修改被观察者的代码，只需要调用被观察者对应的增加或删除的方法即可
- 被观察者只负责通知观察者，但无需了解观察者如何处理通知
- 观察者只需要等待被观察者的通知，无需观察被观察者相关的细节

### 2.4 通知不错过

- 由于被动接受，正常情况下，不会错过主题的改变通知，而主动获取的话，由于时机选取问题，可能会导致错过某些状态

### 2.5 java实现

java中有观察者模式使用的API：

- java.util.Observable 这是一个类      被观察者
- java.util.Observer     这是一个接口   观察者

### 2.6 开关重要性

- 可以筛选通知
- 可以撤销通知
- 可以控制通知

### 2.7 源码设计

#### 2.7.1 自定义实现

##### 2.7.1.1 设计者模式版本1

- 被观察者

```java
package com.study.observer.customer1;

import java.util.concurrent.TimeUnit;

/**
 * 被观察者 - 电影
 */
public class Movie implements Runnable{
    // 表示电影中播放到了某种关键高潮场景的状态
    private volatile boolean flag = false;

    public void play(){
        // 模拟电影播放了5秒后到了高潮场景
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flag = true;
    }

    public boolean getFlag() {
        return flag;
    }

    @Override
    public void run() {
        System.out.println("--------------被观察者（电影）开始播放--------------");
        play();
    }
}
```

- 观察者

```java
package com.study.observer.customer1;

/**
 * 观察者 - 人
 */
public class Person implements Runnable{

    private Movie movie;

    public Person(Movie movie) {
        this.movie = movie;
    }

    public void work() {
        System.out.println("---------------观察者观看电影高潮激动极了--------------");
    }

    @Override
    public void run() {
        // 一直观看着电影
        while (!movie.getFlag()) {
            System.out.println("------观察者（人） 一直监听着 被观察者（电影）---------");
        }
        // 当电影到达了高潮时，通过了上述循环，开始向下执行（处理事件），高潮处人应该做的处理
        work();
    }
}
```

- 测试

```java
package com.study.observer;

import com.study.observer.customer1.Movie;
import com.study.observer.customer1.Person;

public class ObserverTest {

    public static void main(String[] args) {
        // 1. 观察者设计模式测试-版本1
        Movie movie = new Movie();
        Person person = new Person(movie);

        Thread movieThread = new Thread(movie);
        Thread personThread = new Thread(person);

        movieThread.start();
        personThread.start();
    }

}
```

##### 2.7.1.2 设计者模式版本2

版本1的设计出来，观察者（Person）对象需要一直循环查询 被观察者（Movie）对象的属性是否为true，这样会一直占用CPU的使用，影响性能，所以需要进行改进。

改进思路：**当某个事情（事件）发生时，由被观察者（Movie）通知 观察者（Person），这样就不需要观察者一直盯着被观察者。所以被观察者需要有观察者的引用。**

- 被观察者

```java
package com.study.observer.customer2;

import java.util.concurrent.TimeUnit;

/**
 * 被观察者 - 电影
 */
public class Movie implements Runnable {

    private Person person;

    public void setPerson(Person person) {
        this.person = person;
    }

    public void play() {
        // 模拟电影播放了5秒后到了高潮场景
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        person.work();
    }

    @Override
    public void run() {
        System.out.println("--------------被观察者（电影）开始播放--------------");
        play();
    }
}
```

- 观察者

```java
package com.study.observer.customer2;

/**
 * 观察者 - 人
 */
public class Person {

    public void work() {
        System.out.println("--------------观察者观看电影高潮激动极了-------------");
    }

}
```

##### 2.7.1.3 设计者模式版本3

版本2的设计中，将抽象的事件模糊掉了，在一场电影（被观察者）中有着不同的场景，每个场景给每个人（观察者）都有不同的感受，这在版本2的设计中无法完成，所以需要对设计进行改进。

改进思路：

1. 电影中不同的场景 ---> 抽象出电影事件类（MovieEvent），用来作为所有的场景的父类
2. 观察者可以不仅仅是一个人，所以在电影（Movie）类中的观察者引用，改为集合装有多个人`List<Person>`

- 电影事件

```java
package com.study.observer.customer3;

/**
 * 抽象出来的事件
 * 用来描述事件的情况
 */
public class MovieEvent {

    // 电影的时间
    private String time;
    // 电影的场景
    private String circumstance;
    // ...

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCircumstance() {
        return circumstance;
    }

    public void setCircumstance(String circumstance) {
        this.circumstance = circumstance;
    }
}
```

- 观察者

```java
package com.study.observer.customer3;

/**
 * 观察者 - 人
 */
public interface Person {
    public void perform(MovieEvent movieEvent);
}
```

```java
package com.study.observer.customer3;

public class Man implements Person {

    @Override
    public void perform(MovieEvent movieEvent) {
        if ("高潮场景".equals(movieEvent.getCircumstance())) {
            System.out.println("------------观察者（Man）观看电影高潮激动极了-----------");
        } else {
            System.out.println("------------观察者（Man）观看电影悲伤无聊极了-----------");
        }
    }

}
```

```java
package com.study.observer.customer3;

public class Woman implements Person {

    @Override
    public void perform(MovieEvent movieEvent) {
        if ("高潮场景".equals(movieEvent.getCircumstance())) {
            System.out.println("------------观察者（Woman）观看电影高潮无聊极了-----------");
        } else {
            System.out.println("------------观察者（Woman）观看电影悲伤悲伤极了-----------");
        }
    }

}
```

- 被观察者

```java
package com.study.observer.customer3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 被观察者 - 电影
 */
public class Movie implements Runnable {

    private List<Person> people = new ArrayList<>();

    public void addPerson(Person person) {
        people.add(person);
    }

    public void play() {
        // 模拟电影播放了5秒后到了高潮场景
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MovieEvent movieEvent = new MovieEvent();
        movieEvent.setCircumstance("高潮场景");
        for (Person person : people) {
            person.perform(movieEvent);
        }

        // 模拟电影播放了5秒后到了悲伤场景
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        movieEvent.setCircumstance("悲伤场景");
        for (Person person : people) {
            person.perform(movieEvent);
        }
    }

    @Override
    public void run() {
        System.out.println("--------------被观察者（电影）开始播放--------------");
        play();
    }
}
```

- 测试

```java
package com.study.observer;

import com.study.observer.customer3.Man;
import com.study.observer.customer3.Movie;
import com.study.observer.customer3.Woman;

public class ObserverTest {

    public static void main(String[] args) {
        // 1. 观察者设计模式测试-版本1
//        Movie movie = new Movie();
//        Person person = new Person(movie);
//
//        Thread movieThread = new Thread(movie);
//        Thread personThread = new Thread(person);
//
//        movieThread.start();
//        personThread.start();

        // 2. 观察者设计模式测试-版本2
//        Movie movie = new Movie();
//        Person person = new Person();
//
//        movie.setPerson(person);
//
//        Thread movieThread = new Thread(movie);
//        movieThread.start();

        // 3. 观察者设计模式测试-版本3
        Movie movie = new Movie();
        Man man = new Man();
        Woman woman = new Woman();

        movie.addPerson(man);
        movie.addPerson(woman);

        Thread movieThread = new Thread(movie);
        movieThread.start();
    }
}
```

#### 2.7.2 通过jdk实现

- 观察者

```java
package com.study.observer.jdk;

import java.util.Observable;
import java.util.Observer;

public class Man implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("---------------- Man update()... ------------------");
    }
}

```

```java
package com.study.observer.jdk;

import java.util.Observable;
import java.util.Observer;

public class Woman implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("--------------- Woman update()... ------------------");
    }
}

```

- 被观察者

```java
package com.study.observer.jdk;


import java.util.Observable;

/**
 * 被观察者 继承 java.util.Observable
 */
public class Movie extends Observable {

    /**
     * 播放电影
     */
    public void paly() {
        // setChanged() 表示电影进行到了某个场景，状态改变了
        setChanged();
        // 通知 观察者
        notifyObservers();
    }

}
```

- 测试

```java
package com.study.observer;

import com.study.observer.jdk.Man;
import com.study.observer.jdk.Movie;
import com.study.observer.jdk.Woman;

public class ObserverTest {

    public static void main(String[] args) {
        Movie movie = new Movie();
        Man man = new Man();
        Woman woman = new Woman();

        movie.addObserver(man);
        movie.addObserver(woman);

        movie.paly();
    }

}
```





## 3. Spring中的Events

### 3.1 

事件通过**org.springframework.context.ApplicationEvent**实例来表示。这个抽象类继承扩展了**java.util.EventObject**，可以使用**EventObject中的getSource**方法，我们可以很容易地获得所发生的给定事件的对象。这里，事件存在两种类型

1. **与应用程序上下文相关联**

   所有这种类型的事件都继承自**org.springframework.context.event.ApplicationContextEvent**类。它们应用于由**org.springframework.context.ApplicationContext**引发的事件(其构造函数传入的是`ApplicationContext`类型的参数)。这样，我们就可以直接通过应用程序上下文的生命周期来得到所发生的事件：`ContextStartedEvent`在上下文启动时被启动，当它停止时启动`ContextStoppedEvent`，当上下文被刷新时产生`ContextRefreshedEvent`，最后在上下文关闭时产生`ContextClosedEvent`

2. **与request 请求相关联**

   由**org.springframework.web.context.support.RequestHandledEvent**实例来表示，当在ApplicationContext中处理请求时，它们被引发。

### 3.2 Spring如何将事件分配给专门的监听器？


这个过程由事件广播器来实现，由**org.springframework.context.event.ApplicationEventMulticaster**接口的实现表示。此接口定义了3种方法

1. **addApplicationListener()**  添加新的监听器**：定义了两种方法来添加新的监听器：**addApplicationListener(ApplicationListener<?> listener)**和**addApplicationListenerBean(String listenerBeanName)**。当监听器对象已知时，可以应用第一个。如果使用第二个，我们需要将bean name 得到listener对象(`依赖查找DL`)，然后再将其添加到`listener`列表中
2. **removeApplicationListenerBean(String listenerBeanName)** **删除监听器**：添加方法一样，我们可以通过传递对象来删除一个监听器(**removeApplicationListener(ApplicationListener<?> listener)**或通过传递bean名称。第三种方法，**removeAllListeners()**用来删除所有已注册的监听器
3. **multicastEvent(ApplicationEvent event)****将事件发送到已注册的监听器**



## 4. 小技巧

1. 在 idea 中格式化代码，快捷键：ctrl + alt + l
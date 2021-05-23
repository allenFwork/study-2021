# RPC 

- **远程、过程、调用**

- RPC是C语言中就已经有的概念

- 在java中可以将RPC理解为远程方法调用



![](images\RPC原理1.png)

- 缺点：

1. 微服务每增加一个接口，就需要配置一个Controller的requestMapping

2. 对于一个新手，看不懂在调用别的微服务接口

![](images\RPC原理2.png)

- 原理：

1. 通过代理生成一个 BInterface的实例 Proxy.newInstance();

2. 直接使用该对象调用方法，开发者不用关心调用的底层是怎么执行的



**RPC协议关注两个点：**

1. 方式：http、tcp 等
2. 数据格式：用来描述要调用的方法

rpc over http：基于http的

rpc over tcp  ：基于tcp的



## dubbo

- dubbo 能够进行负载均衡

![](images\dubbo负载均衡.png)

zookeeper适合做注册中心原因：

1. 数据存在内存中，所以性能好
2. 临时节点
3. 监听机制，能够监听数据是否改变



**序列化机制：**

1. JSON-fastjson
2. kyro


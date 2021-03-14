# Tomcat

## 1. Engine 启动

### 1.1 步骤：

1. Engine执行start方法
2. 执行 ContainerBase 的 startInternal() 方法
   1. 处理日志
   2. 处理集群
   3. 处理域
   4. **获取 Engine 的所有的子节点**
   5. 将拿到的所有 Engine 的 子节点(StandardHost 对象)交给 Future 框架，Future 启动线程池，执行每一个子节点(StandardHost 对象) 的方法
      1. 执行 StandardHost 对象的 startInternal() 方法
         1. 错误报告处理
         2. **获取 Host 的所有的子节点**
         3. 将拿到的所有 Host 子节点 (StandardContext) 交给 Future 框架，Future 启动线程池，执行每一个子节点的方法
         4. 同上一样， 。。。，直到没有子节点，也就是处理到 Wrapper 子节点





StandWrapper:

- loadOnStartup属性：
  1. `< 0`：
  2. `= 0`：
  3. `> 0`：
- 





## 2. web的请求过程

### 2.1 web的请求

- Connector



### 2.2 web的处理

- Container





## 3. Tomcat相关协议







## 4. Tomcat相关协议



## 5. Tomcat详细配置



## 6. Tomcat与Apache、Nginx集成



## 7. Tomcat性能优化


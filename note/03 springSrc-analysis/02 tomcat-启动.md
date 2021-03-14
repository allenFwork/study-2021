# Tomcat

## 1. 回顾

Tomcat 架构：

Server：代表整个server容器

- Service：服务组件，提供服务，包含一个或多个Connector 和 一个 Container组件
  - Connector：链接器
    - EndPoint：
    - Processor：
  - Container：通用容器的概念
    - Engine：Servlet引擎，所有的请求都通过他进入（责任链模式）
    - Host：主机，表示虚拟主机，可以是多个虚拟主机
    - Context：webApp
    - Wrapper：最底层，对servlet的包装
- Executor：线程池



## 2. Tomcat 的启动

### 2.1 生命周期 LifeCycle

- 所有的组件都有公用的接口 LifeCycle
- 在 LifecycleBase 中 `init()` 方法进行初始化，然后该方法中调用了 `initInternal()`方法，这个方法被所有 LifecycleBase 的实现类重写，用来进行各个组件的初始化
- ![](images\tomcat\tomcat生命周期1.png)

### 2.2 Tomcat 初始化 与 启动

- 入口：Bootstrap类中的 main 方法

#### 2.2.1 Bootstrap

1. static静态块

   ```java
   	static {
   
           /**
            * 环境变量的设置，通过启动时给虚拟机添加参数和值：
            * -Dcatalina.home=catalina-home
            * -Dcatalina.base=catalina-home
            * -Djava.endorsed.dirs=catalina-home/endorsed
            * -Djava.io.tmpdir=catalina-home/temp
            * -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager
            * -Djava.util.logging.config.file=catalina-home/conf/logging.properties
            */
   
           // Will always be non-null
           /**
            * 获取系统变量
            * userDir: G:\blue_world\Documents\study-project\study-tomcat-2929-0130
            */
           String userDir = System.getProperty("user.dir");
   
           // Home first
           /**
            * home: catalina-home
            */
           String home = System.getProperty(Constants.CATALINA_HOME_PROP);
           /**
            * homeFile：G:\blue_world\Documents\study-project\study-tomcat-2929-0130\catalina-home
            * 安装目录
            */
           File homeFile = null;
   
           /**
            * homeFile 获取tomcat的安装目录
            * 绝对路径
            * 获取了 G:\blue_world\Documents\study-project\study-tomcat-2929-0130\catalina-home 的文件对象
            */
           if (home != null) {
               File f = new File(home);
               try {
                   homeFile = f.getCanonicalFile();
               } catch (IOException ioe) {
                   // 绝对路径
                   homeFile = f.getAbsoluteFile();
               }
           }
   
           if (homeFile == null) {
               // First fall-back. See if current directory is a bin directory
               // in a normal Tomcat install
               File bootstrapJar = new File(userDir, "bootstrap.jar");
   
               if (bootstrapJar.exists()) {
                   File f = new File(userDir, "..");
                   try {
                       homeFile = f.getCanonicalFile();
                   } catch (IOException ioe) {
                       homeFile = f.getAbsoluteFile();
                   }
               }
           }
   
           if (homeFile == null) {
               // Second fall-back. Use current directory
               File f = new File(userDir);
               try {
                   homeFile = f.getCanonicalFile();
               } catch (IOException ioe) {
                   homeFile = f.getAbsoluteFile();
               }
           }
   
           /**
            * 设置安装目录：catalinaHomeFile
            */
           catalinaHomeFile = homeFile;
           System.setProperty(
                   Constants.CATALINA_HOME_PROP, catalinaHomeFile.getPath());
   
           // Then base
           String base = System.getProperty(Constants.CATALINA_BASE_PROP);
           /**
            * 设置工作目录：catalinaBaseFile
            */
           if (base == null) {
               catalinaBaseFile = catalinaHomeFile;
           } else {
               /**
                * baseFile: 获取 catalina-home 文件对象
                * 相对路径
                */
               File baseFile = new File(base);
               try {
                   baseFile = baseFile.getCanonicalFile();
               } catch (IOException ioe) {
                   baseFile = baseFile.getAbsoluteFile();
               }
               catalinaBaseFile = baseFile;
           }
           System.setProperty(
                   Constants.CATALINA_BASE_PROP, catalinaBaseFile.getPath());
       }
   ```

2. main方法

   ```java
   	public static void main(String args[]) {
   
           /**
            * 成员变量，类加载
            * private static final Object daemonLock = new Object();
            * private static volatile Bootstrap daemon = null;
            */
           synchronized (daemonLock) {
               // daemon 守护线程 一开始，demo是null
               if (daemon == null) {
                   // Don't set daemon until init() has completed 直到初始化结束才设置demon的值
                   Bootstrap bootstrap = new Bootstrap();
                   try {
                       /**
                        * init()方法中，初始化了tomcat的三个自定义类加载器 和 实例化Catalina对象
                        *  1. 初始化三个类加载器
                        *     破坏双亲委派机制，实现更高的效率
                        *
                        *  2. 给 private Object catalinaDaemon = null; 成员变量进行赋值，
                        *    通过 catalinaClassLoader类加载器通过反射实例化的 Catalina对象
                        */
                       bootstrap.init();
                   } catch (Throwable t) {
                       handleThrowable(t);
                       t.printStackTrace();
                       return;
                   }
                   // BootStrap demon 成员变量赋值
                   daemon = bootstrap;
               } else {
                   // When running as a service the call to stop will be on a new
                   // thread so make sure the correct class loader is used to
                   // prevent a range of class not found exceptions.
                   Thread.currentThread().setContextClassLoader(daemon.catalinaLoader);
               }
           }
   
           try {
               String command = "start";
               if (args.length > 0) {
                   command = args[args.length - 1];
               }
   
               if (command.equals("startd")) {
                   args[args.length - 1] = "start";
                   daemon.load(args);
                   daemon.start();
               } else if (command.equals("stopd")) {
                   args[args.length - 1] = "stop";
                   daemon.stop();
               } else if (command.equals("start")) {
                   /**
                    * setAwait方法中，将传进去的参数
                    * 通过反射，获取Catalina对象的setAwait方法，
                    * 再通过该方法给Catalina对象设置属性值
                    */
                   daemon.setAwait(true);
                   // 完成tomcat的初始化过程
                   daemon.load(args);
                   // 完成tomcat的启动过程
                   daemon.start();
                   if (null == daemon.getServer()) {
                       System.exit(1);
                   }
               } else if (command.equals("stop")) {
                   daemon.stopServer(args);
               } else if (command.equals("configtest")) {
                   daemon.load(args);
                   if (null == daemon.getServer()) {
                       System.exit(1);
                   }
                   System.exit(0);
               } else {
                   log.warn("Bootstrap: command \"" + command + "\" does not exist.");
               }
           } catch (Throwable t) {
               // Unwrap the Exception for clearer error reporting
               if (t instanceof InvocationTargetException &&
                       t.getCause() != null) {
                   t = t.getCause();
               }
               handleThrowable(t);
               t.printStackTrace();
               System.exit(1);
           }
       }
   ```

- BootStrap实例化：init 初始化阶段

  init()方法：初始化类加载器、实例化Catalina对象

- initClassLoaders()：初始化 tomcat 类加载器

  ```java
  	private void initClassLoaders() {
          try {
              // 创建 commonLoader ，欺负加载器设为null，为了打破类加载器的双亲委派机制
              commonLoader = createClassLoader("common", null);
              if (commonLoader == null) {
                  // no config file, default to this loader - we might be in a 'single' env.
                  commonLoader = this.getClass().getClassLoader();
              }
              catalinaLoader = createClassLoader("server", commonLoader);
              sharedLoader = createClassLoader("shared", commonLoader);
          } catch (Throwable t) {
              handleThrowable(t);
              log.error("Class loader creation threw exception", t);
              System.exit(1);
          }
      }
  ```

- ![](images\tomcat\tomcat类加载器.png)

#### 2.2.2 初始化 与 启动 流程

- ![](images\tomcat\tomcat初始化与启动.png)
- 初始化过程包括（调用init方法）
  1. BootStrap调用main方法，在main方法中接着调用BootStrap对象的load方法
  2. BootStrap对象的load方法中通过反射接着调用 Catalina对象的load方法
  3. Catalina对象的load方法中解析Server.xml的配置文件，接着执行 getServer().init(); 语句
     1. 在 getServer() 获取 Catalina 的 protected Server server 成员变量
     2. server 就是 StandardServer类型的实例
        - StandardServer这个对象的实例是在解析Server.xml过程中创建的
        - 除此之外，Server组件下其余的组件也创建了实例，但是都没有执行初始化
  4. 接着执行StandardServer的init()方法，实质是执行的LifecycleBase的init方法
  5. 在LifecycleBase的init方法中，接着执行LifecycleBase的initInternal()方法（空壳方法）
  6. LifecycleBase的initInternal()方法被StandardServer重写，实质执行StandardServer的initInternal()方法
  7. 在StandardServer的initInternal()方法中遍历所有的services，执行每个StandardService对象的init()方法
  8. 同上处理，依次执行 ：
     1. StandardService类型的init()方法  ->  StandardService类型的initInternal()方法
     2. StandardEngine类型的init()方法  ->  StandardEngine类型的initInternal()方法
     3. 
     4. Connector类型的init()方法           ->  Connector类型的initInternal()方法
        1. 实例化适配器 ：adapter = new CoyoteAdapter(this);
        2. 给ProtocolHandler成员变量设置适配器属性 ：protocolHandler.setAdapter(adapter);
        3. 执行 protocolHandler.init() 初始化方法
- 初始化过程中，没有 Host、Context、Wrapper的初始化过程
- 启动过程（调用start方法）：
  1. BootStrap对象的start方法中通过反射调用 Catalina对象的start方法
  2. Catalina对象的start方法中执行 getServer().start(); 语句
  3. 进入 LifecycleBase 的 start() 方法，接着执行startInternal()方法，进入 StandServer 的 startInternal() 实现方法
  4. 在StandServer 的 startInternal() 中，遍历 services 执行每一个 StandardService对象的start() 方法
  5. 在 StandardService对象的start() 方法中执行StandardEngine的start()方法 
  6. 进入 LifecycleBase 的 start() 方法，接着执行startInternal()方法，接着执行StandardEngine的startInternal()方法
  7. **在StandardEngine 的 startInternal() 方法中执行 super.startInternal(); 语句，进入ContainerBase 的 startInternal() 方**
     1. **接着实例化所有的Container的子容器**
     2. 实例化 Pipeline ，生成对应的管道值
  8. 返回到 StandardService对象的start() 方法中，接着执行Executor的start()方法
  9. 接着执行 MapperListener的 start() 方法
  10. 接着遍历所有的 Connector，执行每个Connector对象的 start() 方法
  11. 。。。
  12. 返回到 Catalina 中，执行 shutdownHook = new CatalinaShutdownHook(); 实例化钩子，用来安全关闭服务器
  13. 创建 Socekt对象一直监听浏览器请求



## 3. Tomcat相关协议







## 4. Tomcat相关协议



## 5. Tomcat详细配置



## 6. Tomcat与Apache、Nginx集成



## 7. Tomcat性能优化


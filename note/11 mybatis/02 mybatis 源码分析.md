## mybatis源码解析

### 1. 初始化 SqlSessionFactory对象

- 解析 mybatis.xml 文件，初始化 Configuration对象，创建 SqlSessionFactory 对象
- 通过 SqlSessionFactory 对象获取 SqlSession 对象
- mybatis 底层所有的 数据库处理 都依赖于 SqlSession 中方法

#### 1.1 获取SqlSession

- 通过 SqlSessionFactoryBuilder 创建 SqlSessionFactory 对象

```java
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
```

- inputstream 是 mybatis.xml 文件的字节流

![](C:/documents/study/note/java/mybatis/images-mybatis/SqlSessionFactory对象1.png)

#### 1.2 解析 mybatis.xml 配置文件

- 通过 XMLConfigBuilder 对象解析的 parse() 方法 接着调用  parseConfiguration(XNode root)方法

![](C:/documents/study/note/java/mybatis/images-mybatis/SqlSessionFactory对象2.png)

- 在此方法中依次解析处理 mybatis.xml 文件中的配置，实质就是初始化 Configuration  全局对象

- **设置mybatis的自定义日志实现 和 设置mybatis中的别名：**

  ![](C:/documents/study/note/java/mybatis/images-mybatis/mybatis的日志实现和别名.png)

  - 注册到 别名 Set集合中：

    ![](C:/documents/study/note/java/mybatis/images-mybatis/mybatis的别名注册.png)

    ```java
    private final Map<String, Class<?>> typeAliases = new HashMap<>();
    ```

- 设置 mybatis 的 jdbc 配置：

  ![](C:/documents/study/note/java/mybatis/images-mybatis/mybatis的jdbc解析配置.png)

#### 1.3 mybatis 解析 `<mappers>` 节点：

- XMLConfigBuilder中的方法依次执行，最后会进行 解析 mybatis.xml 中的 `<mappers>` 节点

![](C:/documents/study/note/java/mybatis/images-mybatis/mybatis解析mappers节点.png)

  解析过程中，通过所给路径创建对应文件的 **XMLMapperBuilder** 对象，用来解析对应的 接口映射文件(XxxMapper.xml) ：

#### 1.4 mybatis 解析 XXXMapper.xml 文件

- 读取 mybatis.xml 文件中的 `<mappers>` 节点，对其中子节点进行解析，可以找到对应 接口映射文件

##### 1.4.1 解析 `<mappers>` 根节点

![](C:/documents/study/note/java/mybatis/images-mybatis/mybatis解析mappers节点2.png)

![](C:/documents/study/note/java/mybatis/images-mybatis/mybatis解析mappers节点3.png)

##### 1.4.2 解析 增删改查 节点

![](C:/documents/study/note/java/mybatis/images-mybatis/mybatis解析增删改查节点.png)

- 解析 增删改查 节点时，如果解析失败，不会直接报错，而是会先将 解析该节点 的 语句解析对象(XMLStatementBuilder) 存放到 Configuration 的 incompleteStatements 中，全部解析完之后重新解析一遍该节点。

##### 1.4.3 statementParser.parseStatementNode()

- statementParser.parseStatementNode()： **XMLStatementBuilder对象解析 增删改查 节点**

```java
public void parseStatementNode() {
  String id = context.getStringAttribute("id");
  String databaseId = context.getStringAttribute("databaseId");

  if (!databaseIdMatchesCurrent(id, databaseId, this.requiredDatabaseId)) {
    return;
  }

  String nodeName = context.getNode().getNodeName();
  // 节点名称改为大写，并判断是否是查询功能
  SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));
  boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
  // 是否刷新缓存，默认值：增删改查刷新，查询不刷新
  boolean flushCache = context.getBooleanAttribute("flushCache", !isSelect);
  // 是否使用二级缓存，默认值：查询使用，增删改不使用
  boolean useCache = context.getBooleanAttribute("useCache", isSelect);
  /*
   * 是否需要处理嵌套查询结果，当使用 group by 语句时，是否封装成一个map对象，默认是不会封装的
   */
  boolean resultOrdered = context.getBooleanAttribute("resultOrdered", false);

  // Include Fragments before parsing
  XMLIncludeTransformer includeParser = new XMLIncludeTransformer(configuration, builderAssistant);
  // 替换 includes 标签为对应的sql标签里的值
  includeParser.applyIncludes(context.getNode());

  // 拿到 parameterType 属性，如果parameter中使用的是别名，在此解析成全限定名
  String parameterType = context.getStringAttribute("parameterType");
  Class<?> parameterTypeClass = resolveClass(parameterType);

  // 解析配置的自定义脚本语言驱动，mybatis plus就是使用这个
  String lang = context.getStringAttribute("lang");
  LanguageDriver langDriver = getLanguageDriver(lang);

  // Parse selectKey after includes and remove them.
  // 解析 selectKey
  processSelectKeyNodes(id, parameterTypeClass, langDriver);

  // Parse the SQL (pre: <selectKey> and <include> were parsed and removed)
  // 设置主键的自增规则
  KeyGenerator keyGenerator;
  String keyStatementId = id + SelectKeyGenerator.SELECT_KEY_SUFFIX;
  keyStatementId = builderAssistant.applyCurrentNamespace(keyStatementId, true);
  if (configuration.hasKeyGenerator(keyStatementId)) {
    keyGenerator = configuration.getKeyGenerator(keyStatementId);
  } else {
    keyGenerator = context.getBooleanAttribute("useGeneratedKeys",
        configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType))
        ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
  }

  // 解析Sql 根据sql文本来判断是否需要动态解析 如果没有动态sql语句，且只有 #{} 直接静态解析使用?占位符；
  // 如果有 ${}，就不解析了
  // #{}会进行预编译的，${}不会进行预编译的(会出现sql注入问题)
  SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);
  StatementType statementType = StatementType.valueOf(context.getStringAttribute("statementType", StatementType.PREPARED.toString()));
  Integer fetchSize = context.getIntAttribute("fetchSize");
  Integer timeout = context.getIntAttribute("timeout");
  String parameterMap = context.getStringAttribute("parameterMap");
  String resultType = context.getStringAttribute("resultType");
  Class<?> resultTypeClass = resolveClass(resultType);
  String resultMap = context.getStringAttribute("resultMap");
  String resultSetType = context.getStringAttribute("resultSetType");
  ResultSetType resultSetTypeEnum = resolveResultSetType(resultSetType);
  if (resultSetTypeEnum == null) {
    resultSetTypeEnum = configuration.getDefaultResultSetType();
  }
  String keyProperty = context.getStringAttribute("keyProperty");
  String keyColumn = context.getStringAttribute("keyColumn");
  String resultSets = context.getStringAttribute("resultSets");

  // 将所有解析出来的数据注入到 MappedStatement 对象中
  builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
      fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass,
      resultSetTypeEnum, flushCache, useCache, resultOrdered,
      keyGenerator, keyProperty, keyColumn, databaseId, langDriver, resultSets);
}
```

##### 1.4.4 解析sql

![](images-mybatis\解析sql节点1.png)

![](images-mybatis\解析sql节点2.png)

![](images-mybatis\解析sql节点3.png)

```java
package org.apache.ibatis.parsing;

public class GenericTokenParser {

  private final String openToken;
  private final String closeToken;
  private final TokenHandler handler;

  public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
    this.openToken = openToken;
    this.closeToken = closeToken;
    this.handler = handler;
  }

  public String parse(String text) {
    if (text == null || text.isEmpty()) {
      return "";
    }
    // search open token
    // 传入的sql节点中sql语句中没有以 “${” 开头的字符串，那么start此时为-1
    int start = text.indexOf(openToken);
    // 传入的sql节点中sql语句中没有以 “${” 开头的字符串,则直接退出此方法
    if (start == -1) {
      return text;
    }
    char[] src = text.toCharArray();
    int offset = 0;
    final StringBuilder builder = new StringBuilder();
    StringBuilder expression = null;
    // 遍历里面所有的 #{}
    do {
      if (start > 0 && src[start - 1] == '\\') {
        // this open token is escaped. remove the backslash and continue.
        builder.append(src, offset, start - offset - 1).append(openToken);
        offset = start + openToken.length();
      } else {
        // found open token. let's search close token.
        if (expression == null) {
          expression = new StringBuilder();
        } else {
          expression.setLength(0);
        }
        builder.append(src, offset, start - offset);
        offset = start + openToken.length();
        int end = text.indexOf(closeToken, offset);
        while (end > -1) {
          if (end > offset && src[end - 1] == '\\') {
            // this close token is escaped. remove the backslash and continue.
            expression.append(src, offset, end - offset - 1).append(closeToken);
            offset = end + closeToken.length();
            end = text.indexOf(closeToken, offset);
          } else {
            expression.append(src, offset, end - offset);
            break;
          }
        }
        if (end == -1) {
          // close token was not found.
          builder.append(src, start, src.length - start);
          offset = src.length;
        } else {
          builder.append(handler.handleToken(expression.toString()));
          offset = end + closeToken.length();
        }
      }
      start = text.indexOf(openToken, offset);
    } while (start > -1);
    if (offset < src.length) {
      builder.append(src, offset, src.length - offset);
    }
    return builder.toString();
  }
}
```

- 将解析 sql 节点后所获取的数据注入到 mappedStatement 对象中



### 2. 执行流程

- 获取 mapper接口对应的实例对象
- 执行对应的sql语句，封装返回的结果集

#### 2.1 获取 XXXMapper.class 接口 的 代理实例对象

```java
UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
```

- ![](C:/documents/study/note/java/mybatis/images-mybatis/sqlSession获取Mapper代理对象1.png)
- ![](C:/documents/study/note/java/mybatis/images-mybatis/sqlSession获取Mapper代理对象2.png)
- ![](C:/documents/study/note/java/mybatis/images-mybatis/sqlSession获取Mapper代理对象3.png)

#### 2.2 通过 MapperProxyFactory 对象创建 代理类对象实例

![](C:/documents/study/note/java/mybatis/images-mybatis/MapperProxyFactory创建代理mapper对象.png)

- 底层 通过 java.lang.reflect.Proxy  代理类 创建代理类

- ```java
  public class MapperProxy<T> implements InvocationHandler, Serializable { }
  ```

- org.apache.ibatis.binding.MapperProxy 实质就是 InvocationHandler 

#### 2.3 执行代理对象的方法，实质就是执行 MapperProxy 的 invoke 方法

- **org.apache.ibatis.binding.MapperProxy # invoke**

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
  try {
    // 判断正在调用的方法是不是java.lang.Object的默认方法，如果是直接调用Object的方法；
    // 否则就是代理对象自己的方法。
    // 方法没有返回时，返回的对象是 java.lang.Void 对象。
    if (Object.class.equals(method.getDeclaringClass())) {
      return method.invoke(this, args);
    } else {
      // 根据被调用接口方法的Method对象，从缓存中获取MapperMethodInvoker对象，
      // 如果没有则创建一个并放入缓存，然后调用invoke。
      // 换句话说，Mapper接口中的每一个方法都对应一个MapperMethodInvoker对象，
      // 而MapperMethodInvoker对象里面的MapperMethod保存着对应的SQL信息和返回类型以完成SQL调用。
      return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
    }
  } catch (Throwable t) {
    throw ExceptionUtil.unwrapThrowable(t);
  }
}
```

- 补充：反射中 `method.getDeclaringClass()` 的作用

  ```java
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
      System.out.println(TestObject.class.getMethod("equals", Object.class).getDeclaringClass()); 
        // class java.lang.Object
      System.out.println(TestObject.class.getMethod("test").getDeclaringClass()); 
        //class com.study.TestObject
    }
  
  }
  ```

```java
/**
 * 获取缓存中MapperMethodInvoker，如果没有则创建一个，而MapperMethodInvoker内部封装这一个MethodHandler
 */
private MapperMethodInvoker cachedInvoker(Method method) throws Throwable {
  try {
    return MapUtil.computeIfAbsent(methodCache, method, m -> {
      // 如果调用接口的是默认方法（JDK8新增接口默认方法的概念）
      if (m.isDefault()) {
        try {
          if (privateLookupInMethod == null) {
            return new DefaultMethodInvoker(getMethodHandleJava8(method));
          } else {
            return new DefaultMethodInvoker(getMethodHandleJava9(method));
          }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException
            | NoSuchMethodException e) {
          throw new RuntimeException(e);
        }
      } else {
        /*
         * 如果调用的普通方法（非default方法），则创建一个PlainMethodInvoker并放入缓存，
         * 其中MapperMethod保存对应接口方法的SQL以及入参和出参的数据类型等信息
         *
         * PlainMethodInvoker：类是Mapper接口普通方法的调用类，它实现了MethodInvoker接口。
         * 其内部封装了MapperMethod实例。
         *
         * MapperMethod：封装了Mapper接口中对应方法的信息，以及对应的SQL语句的信息；
         * 它是mapper接口与映射配置文件中SQL语句的桥梁。
         */
        return new PlainMethodInvoker(new MapperMethod(mapperInterface, method, 
                                                       sqlSession.getConfiguration()));
      }
    });
  } catch (RuntimeException re) {
    Throwable cause = re.getCause();
    throw cause == null ? re : cause;
  }
}
```

#### 2.4 执行 mapperMethod.exucute(Sqlseesion, args)

```java
@Override
public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
  // Mybatis通过动态代理，最终底层实质就是 mapperMethod.execute(sqlSession, args);
  return mapperMethod.execute(sqlSession, args);
}
```

- 执行 MapperProxy 中有多个 invoke，但最终会都会 通过 mapperMethod 对象向下执行

#### 2.5 执行 mapperMethod 的 execute方法

```java
public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    switch (command.getType()) {
      case INSERT: {
        // 将 args 进行解析，如果是多个参数，则根据 @Param 注解指的定名称，将参数转换为Map； 如果是封装实体，则不进行转换
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.insert(command.getName(), param));
        break;
      }
      case UPDATE: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.update(command.getName(), param));
        break;
      }
      case DELETE: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.delete(command.getName(), param));
        break;
      }
      case SELECT: // 查询操作
        if (method.returnsVoid() && method.hasResultHandler()) { // 方法执行结果返回为void
          executeWithResultHandler(sqlSession, args);
          result = null;
        } else if (method.returnsMany()) { // 方法执行返回多个对象，例如返回List对象
          result = executeForMany(sqlSession, args);
        } else if (method.returnsMap()) {
          result = executeForMap(sqlSession, args);
        } else if (method.returnsCursor()) {
          result = executeForCursor(sqlSession, args);
        } else {
          // 解析参数，因为SqlSession::selectOne方法参数只能传入一个，但是我们Mapper中可能传入多个参数，
          // 有可能是通过@Param注解指定参数名，所以这里需要将Mapper接口方法中的多个参数转化为一个ParamMap,
          // 也就是说如果是传入的单个封装实体，那么直接返回出来；如果传入的是多个参数，实际上都转换成了Map
          // 例如 public User findUserByIdAndUsername(int id, @Param("name") String userName) 方法，
          // 调用 findUserByIdAndUsername(1, "superman") ，
          // 那么此时 param 为 {"arg0":1, "name":"superman", "param1":1, "param2":"superman"}
          Object param = method.convertArgsToSqlCommandParam(args);
          // 可以看到动态代理的最后还是使用SqlSession操作数据库的
          result = sqlSession.selectOne(command.getName(), param);
          if (method.returnsOptional()
              && (result == null || !method.getReturnType().equals(result.getClass()))) {
            result = Optional.ofNullable(result);
          }
        }
        break;
      case FLUSH:
        result = sqlSession.flushStatements();
        break;
      default:
        throw new BindingException("Unknown execution method for: " + command.getName());
    }
    if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
      throw new BindingException("Mapper method '" + command.getName()
          + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
    }
    return result;
  }
```

```java
public Object convertArgsToSqlCommandParam(Object[] args) {
  return paramNameResolver.getNamedParams(args);
}
```

![](C:/documents/study/note/java/mybatis/images-mybatis/mybatis底层处理方法参数.png)

- 上述调用Mapper接口的方法为：`public User findUserByIdAndUsername(int id, @Param("name") String userName);`
- 调用过程： `findUserByIdAndUsername(1, "superman")` 
- **如果没有给参数设置 @Param，那么会使用jdk的方法获取参数名，但是由于jdk8存在bug，拿到的参数名变为了 arg0、arg1，所以导致mybatis报错**

#### 2.6 MapperMethod 执行底层处理

![](C:/documents/study/note/java/mybatis/images-mybatis/mapperMethod执行底层1.png)

![](C:/documents/study/note/java/mybatis/images-mybatis/SqlSession的方法.png)

- 最终底层还是 回到了 SqlSession对象的方法执行
- **接着执行 CachingExecutor 的 query 方法**

#### 2.7 执行 CachingExecutor 的 query 方法

![](images-mybatis\执行sql查询1.png)

![](images-mybatis\执行sql查询2.png)

```java
private <E> List<E> queryFromDatabase(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey key, BoundSql boundSql) throws SQLException {
    List<E> list;
    // 一级缓存localCache，缓存此时查询的key，和一个占位符
    localCache.putObject(key, EXECUTION_PLACEHOLDER);
    try {
        list = doQuery(ms, parameter, rowBounds, resultHandler, boundSql);
    } finally {
        // 去除掉 一级缓存localCache 中此时查询的key对应的数据（也就是占位符对应的数据）
        localCache.removeObject(key);
    }
    // 将此次查询的结果放入到 一级缓存localCache 中
    localCache.putObject(key, list);
    if (ms.getStatementType() == StatementType.CALLABLE) {
        localOutputParameterCache.putObject(key, parameter);
    }
    return list;
}
```

#### 2.8 给sql语句中参数赋值

![](images-mybatis\执行sql查询3.png)

![](images-mybatis\执行sql查询4.png)

```java
/**
 * SimpleExecutor 继承于 BaseExecutor
 * getConnection() 是 BaseExecutor的方法
 */
protected Connection getConnection(Log statementLog) throws SQLException {
  Connection connection = transaction.getConnection();
  if (statementLog.isDebugEnabled()) {
    return ConnectionLogger.newInstance(connection, statementLog, queryStack);
  } else {
    return connection;
  }
}
```



### 3. mybatis缓存实现原理

#### 3.1 基本概念

缓存系统是默认开启的

- mybatis的缓存分为一级缓存和二级换粗
- 缓存的实质就是底层有一个Map
- 一级缓存与二级缓存的区别：
  - 一级缓存的作用域是 单个 SqlSession，一级缓存不能关闭，默认开启
  - 二级缓存的作用域是 所有的SqlSession，缓存的单位是 namespace
  - 查询顺序，先查询二级缓存，再查询一级缓存

#### 3.2 设计思想

##### 3.2.1 一级缓存

- org.apache.ibatis.cache.impl.PerpetualCache





##### 3.2.2 二级缓存



#### 3.3 应用场景







### 4. mybatis解决问题

#### 4.1 mybatis不使用@Param报错

##### 4.1.1 问题详细：

使用myabtsi时，如果没有给多参数的方法中的参数设置 @Param，那么mybatis会报错

```java
public User findByNameAndPower(String name, String power);
```

```console
Exception in thread "main" org.apache.ibatis.exceptions.PersistenceException: 
### Error querying database.  Cause: org.apache.ibatis.binding.BindingException: Parameter 'power' not found. Available parameters are [arg1, arg0, param1, param2]
### Cause: org.apache.ibatis.binding.BindingException: Parameter 'power' not found. Available parameters are [arg1, arg0, param1, param2]
```

##### 4.1.2 原因：

```
方法的参数没有使用 @Param 注解，那么会通过jdk的getName方法获取方法中参数的名字（形参的名字），但是由于jdk的bug，会导致拿出来的参数名变为 arg0，arg1 这样的类型
```

**org.apache.ibatis.reflection.ParamNameResolver：**

```java
public ParamNameResolver(Configuration config, Method method) {
    this.useActualParamName = config.isUseActualParamName();
    final Class<?>[] paramTypes = method.getParameterTypes();
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    final SortedMap<Integer, String> map = new TreeMap<>();
    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
        if (isSpecialParameter(paramTypes[paramIndex])) {
            // skip special parameters
            continue;
        }
        String name = null;
        // 遍历方法中的参数注解 @param
        for (Annotation annotation : paramAnnotations[paramIndex]) {
            if (annotation instanceof Param) {
                hasParamAnnotation = true;
                name = ((Param) annotation).value();
                break;
            }
        }
        if (name == null) {
            // @Param was not specified.
            // 如果方法的参数没有使用 @Param 注解，那么会通过jdk的getName方法获取方法中参数的名字（形参的名字），
            // 但是由于jdk的bug，会导致拿出来的参数名变为 arg0，arg1，这样的类型
            // jdk8以后的解决方案： 启动虚拟机时，添加 -parameters 参数
            // spring mvc也有获取方法参数的方法，他不会出现这种问题，因为他是自己去解析class文件的字节码的，没有通过jdk的方法
            if (useActualParamName) {
                name = getActualParamName(method, paramIndex);
            }
            if (name == null) {
                // use the parameter index as the name ("0", "1", ...)
                // gcode issue #71
                name = String.valueOf(map.size());
            }
        }
        map.put(paramIndex, name);
    }
    names = Collections.unmodifiableSortedMap(map);
}
```

##### 4.1.3 解决方法：

```
jdk8以后的解决方案： 启动虚拟机时，添加 -parameters 参数
```

![](images-mybatis\添加参数解决jdk的getName问题.png)

- 添加完配置后，需要重新编译整个项目，否则配置不生效

- ```java
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
       */
    }
  
  }
  ```

##### 4.1.4 扩展：

spring mvc也有获取方法参数的方法，他不会出现这种问题，因为他是自己去解析class文件的字节码的，没有通过jdk的方法

#### 4.2
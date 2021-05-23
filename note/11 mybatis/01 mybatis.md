

#{}会进行预编译，将sql语句中的#{id}预编译为 ? 

- preparedStatement中的sql语句参数可以使用 ?

${}不会进行预编译

- 可能会出现sql注入，导致安全问题出现





通过动态代理拿到Mapper：

SqlSession.getMapper(XXXMapper.class);



MappedStatement：解析xml中的每个标签中的信息，然后封装成的对象

Configuration：解析xml文件，封装所有的xml信息到该对象中






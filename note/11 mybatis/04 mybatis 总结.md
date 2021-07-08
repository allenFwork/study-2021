# mybatis

1. spring 和 mybatis 整合之后，为什么一级缓存会失效？

- 原生的mybatis的一级缓存是由使用者进行操作的（使用或销毁），从SqlSeesion打开之后，一级缓存就存在了;

- spring对SqlSession进行了封装，spring不希望将SqlSession交友用户来处理，所以没有提供给用户操作的接口，每次执行完相关的数据库操作，spring都会直接将SqlSession关闭掉（销毁掉），对应的一级缓存也被销毁掉了;

2. spring开启事务后，mybatis的一级缓存能够生效，为什么？

- 因为开启事务后，不会执行完数据库的操作（每一个操作）后，就不会直接销毁SqlSession，而是会将事务中包含的多个数据库操作用一个SqlSession进行处理，所以此时mybatis的一级缓存是有效的。



3. 二级缓存需要注意的点

- 二级缓存里面的数据不能存那种一直累加到很大的
- 二级缓存是基于命名空间来的




























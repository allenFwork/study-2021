# redis

## 是什么?

是完全开源免费的，用c语言编写的，是一个单线程，高性能的（key/value）内存数据库，基于内存运行并支持持久化的nosql数据库

## 能做什么？

主要是用来做缓存，但不仅仅只能做缓存，比如：redis的计数器生成分布式唯一主键，redis实现分布式锁，队列，会话缓存，点赞，统计网站访问量。

## 怎么获取？

官网，也可以通过Linux  yum直接下载安装

## 怎么玩？

1. 安装
2. redis数据类型（api操作）
3. redis配置文件解析
4. redis的持久化
5. redis的事务
6. redis的发布订阅

7. java客户端操作（jedis）



## 一. redis的安装

**1.下载redis 5.0.5.tar.gz** 

**2.解压** 

`tar -zvxf redis 5.0.5.tar.gz`

**3.进入redis的解压目录，进行make操作**

- 如果make报错的话  大家就可以看一下是不是报没有gcc的错  如果是报没有gcc的错，那就要先安装一个gcc 

![](images\redis5.0.5解压目录.png)

![redis编译](images\redis编译.png)

![redis编译报错](images\redis编译报错.png)

**yum install gcc -c ++**

![](images\安装gcc.png)

![](images\gcc安装完成.png)

- 安装好gcc之后执行一下 <font color=red>make distclean</font> 因为前面make的时候它执行了一些东西  要先把他清掉

4.**make install**

- 查看redis默认安装位置
- /usr/local/bin

![](images\redis默认安装位置中的内容.png)

- redis-benchmark：测试redis的性能，测试写入性能
- redis-check-aof：   修复aof持久化文件
- redis-check-rdb：  修复rdb持久化文件
- redis-cli：                启动redis客户端
- redis-sentinel：      搭建哨兵集群时，用来启动哨兵节点的
- redis-server：         启动redis服务端

![](images\redis目录.png)

- redis最重要的配置文件就是 redis.conf 文件

- 创建新的redis.conf作为启动redis服务的配置文件

![](images\创建myredis文件夹存放redis.conf.png)



## 二. redis设置外网访问

     1.注释bind并且把protected-mode no
     2.使用bind
     3.设置密码
     protected-mode它启用的条件有两个，第一是没有使用bind，第二是没有设置访问密码。

1. **bind**

- bind 启动redis服务的机子的ip：只能通过这里设置的ip进行连接redis服务
- bind 0.0.0.0 ：本机如果有多个网卡，所以有多个ip，可以通过这几个ip的任何一个ip进行连接redis服务

2. **protected-mode**：保护模式

- 如果开启了，那么就只能本机连接这个redis服务，其余的都不能连接
- 如果设置了bind 或者 设置了访问密码，那么保护模式开启了，也不起作用

3. **daemonize**

- 是否以 保护进程启动redis服务

4. port

- 设置端口

5. **profile**

- redis以守护进程方式运行时，会把pid(进程号)写入到profile指定的文件中
- 一台机子上可以启动多个redis服务，那么就要配置不同的profile
- 实例，如：**profile /var/run/redis_6379.pid** 

6. **logfile**

- 用于配置redis日志文件输出的位置
- 如果指定为空字符串，那么为标准输出，输出到控制台，redis如果以守护进程方式运行，那么日志将输出到 
- 实例，如：logfile "/myredis/redis.log"

7. databse

- 配置redis默认的数据库数量 16

8. **dir** 

- rdb文件存放目录，必须是一个目录，aof文件也会保存到该目录下
- 实例，如：<font color=red>dir ./</font>   表示持久化文件存放到启动当前redis服务时的当前目录下
- 生产环境中一般是写死的一个目录，保证重启时redis的数据是正常的，不会出现启动时redis是空的数据

### 1. 启动 redis 服务

- 命令：**/usr/local/bin/redis-server ./redis.conf**

- ![](images\以当前路径下的redis.conf作为配置文件启动redis服务.png)

### 2. 查看redis的进程

![](images\查看redis服务的进程.png)

### 3. windows使用redis可视化工具

![](images\windows使用redisDesktopManager可视化工具进行连接.png)

![](images\windows可视化工具连接redis服务端成功.png)

- 连接不上可能是防火墙没有开

### 4. 客户端连接到服务端

- /usr/local/bin/redis-cli -h 192.168.33.10

![](images\redis客户端连接到redis服务端.png)

![](images\redis客户端连接redis服务端.png)



## 三. redis数据类型及api操作 

- (http://redisdoc.com/)

#### key

- keys *  
- scan  0 match  *  count  1
- exists key 判断某个key是否存在
- move key db  当前库就没有了，到指定的库中去了
- expire key  为给定的key设置过期时间
- ttl key 查看还有多少时间过期   -1表示永不过期  -2表示已过期
- type key  查看key是什么类型

### 1.数据结构

#### 1.1 string

- string是redis最基本的类型，你可以理解成与Memcached一模一样的类型，一个key对应一个value。

- string类型是二进制安全的。意思是redis的string可以包含任何数据。比如jpg图片或者序列化的对象 。

- string类型是Redis最基本的数据类型，**一个redis中字符串value最多可以是512M**

- `set  key  value ` ：设置key  value

- `get  key`：查看当前key的值

- `del  key` ： 删除key

- `append key  value` ：如果key存在，则在指定的key末尾添加；如果key不存在，则类似set

- `strlen  key`：  返回此key的长度

**以下几个命令只有在key值为数字的时候才能正常操作：**

-----------------------------------------------------

- `incr  key` ：为指定key的值加一

- `decr  key` ：为指定key的值减一

- `incrby key 数值` ：为指定key的值增加数值

- `decrby key  数值`：为指定key的值减数值

-----------------------------------------------------

- `getrange  key  0(开始位置)  -1（结束位置）`：获取指定区间范围内的值，类似between......and的关系 （0 -1）表示全部

- `setrange key 1（开始位置，从哪里开始设置） 具体值 `  ：设置（替换）指定区间范围内的值

- `setex 键 秒值 真实值  ` ：设置带过期时间的key，动态设置。

- `setnx  key   value  ` ： 只有在 key 不存在时设置 key 的值。

- `mset  key1  value  key2 value` ：同时设置一个或多个 key-value 对。

- `mget   key1   key 2`：获取所有(一个或多个)给定 key 的值。

- `msetnx   key1   value  key2   value`：同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在。

- `getset key value`：将给定 key 的值设为 value ，并返回 key 的旧值(old value)。



#### 1.2 list

- 它是一个字符串链表，left、right都可以插入添加；
- 如果键不存在，创建新的链表；
- 如果键已存在，新增内容；
- 如果值全移除，对应的键也就消失了。
- 链表的操作无论是头和尾效率都极高，但假如是对中间元素进行操作，效率就很惨淡了。

- Redis 列表是简单的字符串列表，按照插入顺序排序。
- 可以添加一个元素导列表的头部（左边）或者尾部（右边）。
- 它的底层实际是个链表

**命令：**

- lpush  key  value1  value2  将一个或多个值加入到列表头部

- rpush  key  value1  value2 将一个或多个值加入到列表底部

- lrange key  start  end  获取列表指定范围的元素   （0 -1）表示全部

- lpop key 移出并获取列表第一个元素

- rpop key  移出并获取列表最后一个元素

- lindex key index   通过索引获取列表中的元素 

- llen 获取列表长度

-  lrem key   0（数量） 值，表示删除全部给定的值。零个就是全部值   从left往right删除指定数量个值等于指定值的元素，返回的值为实际删除的数量

- ltrim key  start(从哪里开始截)  end（结束位置） 截取指定索引区间的元素，格式是ltrim list的key 起始索引 结束索引



#### 1.3 set

- Redis的Set是string类型的无序，不能重复的集合。

**命令：**

- sadd key value1 value 2 向集合中添加一个或多个成员

- smembers  key  返回集合中所有成员

- sismembers  key   member  判断member元素是否是集合key的成员

- scard key  获取集合里面的元素个数

- srem key value  删除集合中指定元素

- srandmember key  数值     从set集合里面随机取出指定数值个元素   如果超过最大数量就全部取出，

- spop key  随机移出并返回集合中某个元素

- smove  key1  key2  value(key1中某个值)   作用是将key1中执定的值移除  加入到key2集合中

- sdiff key1 key2  在第一个set里面而不在后面任何一个set里面的项(差集)

- sinter key1 key2  在第一个set和第二个set中都有的 （交集）

- sunion key1 key2  两个集合所有元素（并集）



#### 1.4 hash

- Redis hash 是一个键值对集合。
- Redis hash是一个string类型的field和value的映射表，hash特别适合用于存储对象。

- kv模式不变，但v是一个键值对

- 类似Java里面的Map<String,Object>

**命令：**

- hset  key  (key value)  向hash表中添加一个元素

- hget key  key  向hash表中获取一个元素

- hmset  key key1 value1 key2 value2 key3 value3 向集合中添加一个或多个元素

- hmget key  key1 key2 key3  向集合中获取一个或多个元素

- hgetall  key   获取在hash列表中指定key的所有字段和值

- hdel  key  key1 key2 删除一个或多个hash字段

- hlen key 获取hash表中字段数量

- hexits key key  查看hash表中，指定key（字段）是否存在

- hkeys  key 获取指定hash表中所有key（字段）

- hvals key 获取指定hash表中所有value(值)

- hincrdy key  key1  数量（整数）  执定hash表中某个字段加  数量  ，和incr一个意思

- hincrdyfloat key key1  数量（浮点数，小数）  执定hash表中某个字段加  数量  ，和incr一个意思

- hsetnx key key1 value1  与hset作用一样，区别是不存在赋值，存在了无效。



#### 1.5 zset

- Redis zset 和 set 一样也是string类型元素的集合,且不允许重复的成员。
- 不同的是每个元素都会关联一个double类型的分数。
- redis正是通过分数来为集合中的成员进行从小到大的排序。
- zset的成员是唯一的，但分数(score)却可以重复。

**命令：**

- zadd  key  score 值   score 值   向集合中添加一个或多个成员

- zrange key  0   -1  表示所有   返回指定集合中所有value

- zrange key  0   -1  withscores  返回指定集合中所有value和score

- zrangebyscore key 开始score 结束score    返回指定score间的值

- zrem key score某个对应值（value），可以是多个值   删除元素

- zcard key  获取集合中元素个数

- zcount key   开始score 结束score       获取分数区间内元素个数

- zrank key vlaue   获取value在zset中的下标位置(根据score排序)

- zscore key value  按照值获得对应的分数

### 2. 数据结构扩展

#### 2.1 Pipeline

注意：使用Pipeline的操作是非原子操作

#### 2.2 GEO

GEOADD locations 116.419217 39.921133 beijin

GEOPOS locations beijin

GEODIST locations tianjin beijin km 	计算距离

GEORADIUSBYMEMBER locations beijin 150 km  通过距离计算城市

注意：没有删除命令  它的本质是zset  （type locations） 

所以可以使用zrem key member  删除元素

zrange key  0   -1  表示所有   返回指定集合中所有value

#### 2.3 hyperLogLog

Redis 在 2.8.9 版本添加了 HyperLogLog 结构。

Redis HyperLogLog 是用来做基数统计的算法，HyperLogLog 的优点是，在输入元素的数量或者体积非常非常大时，计算基数所需的空间总是固定的、并且是很小的

在 Redis 里面，每个 HyperLogLog 键只需要花费 12 KB 内存，就可以计算接近 2^64 个不同元素的基 数。这和计算基数时，元素越多耗费内存就越多的集合形成鲜明对比。

PFADD 2017_03_06:taibai 'yes' 'yes' 'yes' 'yes' 'no'

PFCOUNT 2017_03_06:taibai    **统计有多少不同的值**

1.PFADD 2017_09_08:taibai uuid9 uuid10 uu11

2.PFMERGE 2016_03_06:taibai 2017_09_08:taibai   合并

注意：本质还是字符串 ，有容错率，官方数据是0.81% 

#### 2.4 bitmaps 位图

setbit taibai 500000 0

getbit taibai 500000 

bitcount taibai

Bitmap本质是string，是一串连续的2进制数字（0或1），每一位所在的位置为偏移(offset)。
string（Bitmap）最大长度是512 MB，所以它们可以表示2 ^ 32=4294967296个不同的位。



##### 2.4.1 原理

- redis使用命令 set key1 value1 存储数据时，底层存储的 value1字符串时，**<font color=red>是以二进制数据的形式存储的</font>**
- 例如，存储 "abc" 字符串时，存储的是对应的 **Ascii码** ： 1100001 1100010 1100011

##### 2.4.2 api

###### 2.4.2.1 **存储，修改**

- ![](images\bitmaps操作1.png)

- abc 底层存储24位，分别为 1100001 1100010 1100011
- 将他的第六位改为1，第七位改为0，结果变为 1100010 1100010 1100011
- 存储的字符串数据变为了 "bbc"

###### 2.4.2.2 扩容

- ![](images\bitmaps操作2.png)
- setbit taibai 100 0 
- 将 key为 taibai 的值扩容为100位，扩容后的位置都是0

###### 2.4.2.3 统计bit位中1的个数

- ![](images\bitmaps操作3.png)

- bitcount taibai

- 统计key为 taibai 的值对应的二进制数中有多少个1

##### 2.4.3 用途：

###### 2.4.3.1 实现点赞的功能

  1. 点赞

  2. 取消点赞

  3. 统计这条朋友圈的点赞数
  4. 查看是否点赞过该朋友圈

######   2.4.3.2 思路

  - 1个人(A)有一个朋友圈，对应的朋友圈id为1000
  - 另一个人(B)给这个人(A)，点了一个赞，那么 setbit 1000 用户id 1，**用户id必须是数字**
  - 取消点赞时，使用 setbit 1000 用户id 0，**用户id必须是数字**
  - 统计总点赞数，使用 bitcount 1000
  - 查看是否点赞，使用 getbit 1000，返回1表示点赞过了，返回0表示没点过赞



## 四. redis的持久化机制

- 说白了，就是在指定的时间间隔内，将内存当中的数据集快照写入磁盘，它恢复时是将快照文件直接读到内存
- 什么意思呢？我们都知道，内存当中的数据，如果我们一断电，那么数据必然会丢失，但是玩过redis的同学应该都知道，我们一关机之后再启动的时候数据是还在的，所以它必然是在redis启动的时候重新去加载了持久化的文件
- redis提供两种方式进行持久化，一种是**RDB持久化默认**，另外一种是**AOF（append only file）持久化**。

### 1. rdb 持久化机制

#### 1.1 rdb是什么？

- **原理是 redis 会单独 <font color=red>创建（fork）</font>一个与当前进程一模一样的<font color=red>子进程</font>来进行持久化，这个子进程的所有数据（变量，环境变量，程序程序计数器等）都和原进程一模一样，会先将数据写入到一个<font color=red>临时文件</font>中，待持久化结束了，再用这个临时文件替换上次持久化好的文件，整个过程中，主进程不进行任何的 io 操作，这就确保了极高的性能。**
- 将内存中的数据写入到磁盘，通过 .rdb 文件来描述内存中的数据

#### 1.2 这个持久化文件在哪里

- ![](images\redis.conf文件的aof配置.png)
- 配置：
  - save 900 1  ：900秒内，15分钟内，有一个数据的更改，那么就会触发 aof 持久化
  - save 300 10：300秒内，5分钟内，有十个数据的更改，那么就会触发 aof 持久化
  - save 60 1    ：60秒内，1分钟内，有一个数据的更改，那么就会触发 aof 持久化
- redis 如果开启 rdb，优化可以将 300秒 和 60秒 的配置删除掉，只留下900秒的配置
- redis 默认是开启 rdb的，如果将三个 save 都删除掉，就是关闭了 rdb 持久化机制
- 如果 redis 存在主从复制，那么redis就无法关闭 rdb 持久化机制，因为主从复制是基于这个机制的

#### 1.3 他什么时候fork子进程，或者什么时候触发rdb持久化机制

- shutdown时，如果没有开启 aof，会触发
- 配置文件中默认的快照配置
- 执行命令 save 或者 bgsave 命令时：

  -  save 是只管保存，其他不管，全部阻塞   

  - **bgsave：redis会在后台异步进行快照操作，同时可以响应客户端的请求**
- 执行 flushall 命令，清空redis数据库中所有数据时，会触发

### 2. aof 持久化机制

#### 2.1 是什么?

- 原理是将reids的**操作日志以追加**的方式写入文件，读操作是不记录的
- 通过redis协议记录命令

#### 2.2 这个持久化文件在哪里

- ![](images\redis.conf文件的aof配置.png)
- appendonly yes：表示启用 aof 持久化机制
- appendfilename "XXX.aof"：表示 aof 持久化文件所在位置

#### 2.3 触发机制（根据配置文件配置项）

- ![](images\redis.conf文件的aof配置2.png)

- no：表示等操作系统进行数据缓存同步到磁盘（快，持久化没保证）
- always：同步持久化，每次发生数据变更时，立即记录到磁盘（慢，安全）
- **everysec：表示每秒同步一次（默认值，很快，但可能会丢失一秒以内的数据）**

#### 2.4 aof 重写机制

- ![](images\redis.conf文件的aof重写配置.png)

- 当 AOF 文件增长到一定大小的时候 Redis 能够调用 bgrewriteaof 对日志文件进行重写 。当 AOF 文件大小的增长率大于该配置项时自动开启重写（这里指超过原大小的100%）
- auto-aof-rewrite-percentage 100
- 当 AOF 文件增长到一定大小的时候 Redis 能够调用 bgrewriteaof 对日志文件进行重写 。当 AOF 文件大小大于该配置项时，即64mb时，会自动开启重写
- auto-aof-rewrite-min-size 64mb

#### 2.5 redis4.0 后混合持久化机制

##### 开启混合持久化

- 4.0版本的混合持久化默认关闭的，通过 aof-use-rdb-preamble 配置参数控制，yes则表示开启，no表示禁用，5.0之后默认开启。

- 混合持久化是通过 bgrewriteaof 完成的，不同的是当开启混合持久化时，fork出的子进程先将共享的内存副本全量的以RDB方式写入aof文件，然后在将重写缓冲区的增量命令以AOF方式写入到文件，写入完成后通知主进程更新统计信息，并将新的含有RDB格式和AOF格式的AOF文件替换旧的的AOF文件。简单的说：新的AOF文件前半段是RDB格式的全量数据后半段是AOF格式的增量数据，

**优点：**混合持久化结合了RDB持久化 和 AOF 持久化的优点, 由于绝大部分都是RDB格式，加载速度快，同时结合AOF，增量的数据以AOF方式保存了，数据更少的丢失。

**缺点：**兼容性差，一旦开启了混合持久化，在4.0之前版本都不识别该aof文件，同时由于前部分是RDB格式，阅读性较差

### 3. 小总结：

#### 3.1 redis提供了rdb持久化方案，为什么还要 aof ？

- 优化数据丢失问题，rdb会丢失最后一次快照后的数据，aof丢失不会超过2秒的数据

### 3.2 如果aof和rdb同时存在，听谁的？

- aof

#### 3.3 rdb和aof优势劣势

- rdb 适合大规模的数据恢复，对数据完整性和一致性不高 ，  在一定间隔时间做一次备份，如果redis意外down机的话，就会丢失最后一次快照后的所有操作
- aof 根据配置项而定

1.官方建议   两种持久化机制同时开启，如果两个同时开启  优先使用aof持久化机制  

### 3.4 性能建议（这里只针对单机版redis持久化做性能建议）：

- 因为RDB文件只用作后备用途，只要15分钟备份一次就够了，只保留save 900 1这条规则。
- 如果Enalbe AOF，好处是在最恶劣情况下也只会丢失不超过两秒数据，启动脚本较简单只load自己的AOF文件就可以了。
- 代价一是带来了持续的IO，二是AOF rewrite的最后将rewrite过程中产生的新数据写到新文件造成的阻塞几乎是不可避免的。
- 只要硬盘许可，应该尽量减少AOF rewrite的频率，AOF重写的基础大小默认值64M太小了，可以设到5G以上。
- 默认超过原大小100%大小时重写可以改到适当的数值。

### 4 redis 进程图

#### 4.1 单机版

<img src="images\redis单机版进程图.png" style="zoom:80%;" />

- 三个客户端的写入命令同时到了redis，那么 redis 会将这三个命令进行排队，串行化执行
- 对于用户来说，没有并发，单线程操作，没有并发问题出现
- 当使用save命令时，使用的是 redis **本身的进程**进行持久化，会阻塞接受命令等操作
- 使用bgsave命令时，会创建一个新的子进程进行持久化，不会出现阻塞接受命令等情况

#### 4.2 redis 启动持久化流程

<img src="images\redis启动持久化加载流程图.png" style="zoom: 80%;" />

- rdb + aof 两种机制同时使用：
  - 先查看redis是否启用了aof持久化机制，如果启用了，就去查看是否有aof文件
  - 如果有aof文件，就直接以该文件作为数据的依据，初始化redis的数据
  - 如果没有启用aof机制，就会查看是否有rdb文件；
  - 如果有rdb文件，就直接以该文件作为数据的依据，初始化redis的数据
- **使用了aof机制，那么redis启动时，初始化数据库不回家再rdb文件**
- 如果原来的数据使用了rdb持久化机制保存了数据，这次启动要求要使用aof机制，那么就无法获取redist之前的数据了，怎么解决？
  - 启动时先关闭aof机制，启动后开启aof机制
  - 先修改 redis.conf 文件为： appendonly no
  - 再启动redis服务器
  - 接着通过客户端的命令行开启aof：`config set appendonly yes`
  - 最后再修改redis.conf 文件为： appendonly yes

#### 4.3 redis重写流程

<img src="images\redis重写流程图.png" style="zoom: 67%;" />

- 正常情况下，只有一个缓冲区，每秒钟将缓冲区的数据更改命令追加到aof文件中
- 如果启动了aof重写，那么会先判断是否已经有了重写的进程：
  - 如果有了，直接返回错误
  - 如果没有，那么就创建一个新的子进程进行aof重写：
    - aof重写的aof文件直接来自此时redis数据内存中的数据，类似此时的rdb文件
    - 生成新的aof文件时，如果又有新的数据，就使用新的缓冲区保存，等aof文件创建好了，将这些数据追加进去，最后替换旧的aof文件



## 五. 缓存几大问题

### 1. 缓存粒度控制

通俗来讲，缓存粒度问题就是我们在使用缓存时，是将所有数据缓存还是缓存部分数据？

| 数据类型 | 通用性 | 空间占用（内存空间+网络码率） | 代码维护 |
| :------: | :----: | :---------------------------: | :------: |
| 全部数据 |   高   |              大               |   简单   |
| 部分数据 |   低   |              小               | 较为复杂 |

- 缓存粒度问题是一个容易被忽视的问题，如果使用不当，可能会造成很多无用空间的浪费，可能会造成网络带宽的浪费，可能会造成代码通用性较差等情况，必须学会综合 **数据通用性、空间占用比、代码维护性** 三点评估取舍因素权衡使用。

### 2. 缓存穿透问题

- 缓存穿透是指**查询一个一定不存在的数据**，由于缓存不命中，并且出于容错考虑， 如果从存储层查不到数据则不写入缓存，这将导致这个不存在的数据每次请求都要到存储层去查询，失去了缓存的意义。

#### 2.1 造成原因有哪些：

1. 业务代码自身问题
2. 恶意攻击、爬虫等等

#### 2.2 危害

- 对底层数据源压力过大，有些底层数据源不具备高并发性。  例如 mysql 一般来说单台能够扛1000-QPS 就已经很不错了

#### 2.3 解决方案

1. **缓存空对象**

```java
public class NullValueResultDO implements Serializable{
     private static final long serialVersionUID = -6550539547145486005L;
}
 
public class UserManager {
     UserDAO userDAO;
     LocalCache localCache;
 
     public UserDO getUser(String userNick) {
          Object object = localCache.get(userNick);
          if(object != null) {
               if(object instanceof NullValueResultDO) {
                    return null;
               }
               return (UserDO)object;
          } else {
               User user = userDAO.getUser(userNick);
               if(user != null) {
                    localCache.put(userNick,user);
               } else {
                    localCache.put(userNick, new NullValueResultDO());
               }
               return user;
          }
     }          
}
```

2. **布隆过滤器**

1）**Google布隆**过滤器的缺点

- 基于 JVM 内存的一种布隆过滤器
- 重启即失效
- 本地内存无法用在分布式场景
- 不支持大数据量存储

2）**Redis布隆**过滤器

- 可扩展性Bloom过滤器：一旦Bloom过滤器达到容量，就会在其上创建一个新的过滤器
- 不存在重启即失效或者定时任务维护的成本：基于Google实现的布隆过滤器需要启动之后初始化布隆过滤器
- 缺点：
  1. 需要网络IO，性能比Google布隆过滤器低
  2. 数据库中添加了数据，布隆过滤器的数据数据也要更新
  3. 数据库中是删除了数据，布隆过滤器的数据数据无法删除对应数据对应的数组值
  4. 需要定时查询数据库替换还布隆过滤器数组数据

### 3. 缓存雪崩问题

#### 3.1 缓存雪崩定义

- **<font color=red>缓存雪崩</font> 是指 <font color=red>机器宕机</font> 或在我们 <font color=red>设置缓存时采用了相同的过期时间</font>，导致缓存在某一时刻同时失效，请求全部转发到DB，DB瞬时压力过重雪崩。**
- 缓存雪崩指**大部分数据失效**，小部分数据失效是正常的。

#### 3.2 解决方案

1. 在缓存失效后，通过加锁或者队列来控制读数据库写缓存的线程数量。比如对某个key只允许一个线程查询数据和写缓存，其他线程等待。

2. 做二级缓存，A1为原始缓存，A2为拷贝缓存，A1失效时，可以访问A2，A1缓存失效时间设置为短期，A2设置为长期

3. 不同的key，设置不同的过期时间，让缓存失效的时间点尽量均匀。

4. 如果缓存数据库是分布式部署，将热点数据均匀分布在不同搞得缓存数据库中。

- **解决机器宕机导致的缓存雪崩方案：搭建高可用的redis集群**

- **解决相同时间内大量数据过期：错开设置的过期时间**

### 4. 缓存击穿 . 热点key重建缓存问题

#### 4.1 定义

- 缓存击穿就是一个并发问题

- 缓存击穿是指缓存中没有但数据库中有的数据（一般是缓存时间到期），这时**由于并发用户特别多，同时读缓存没读到数据，又同时去数据库去取数据，引起数据库压力瞬间增大，造成过大压力**

- 我们知道，使用缓存，如果获取不到，才会去数据库里获取。但是如果是热点 key，访问量非常的大，数据库在重建缓存的时候，会出现很多线程同时重建的情况。因为高并发导致的大量热点的 key 在重建还没完成的时候，不断被重建缓存的过程，由于大量线程都去做重建缓存工作，导致服务器拖慢的情况。

#### 4.2 解决方案

##### 4.2.1 互斥锁

- 第一次获取缓存的时候，加一个锁，然后查询数据库，接着是重建缓存。这个时候，另外一个请求又过来获取缓存，发现有个锁，这个时候就去等待，之后都是一次等待的过程，直到重建完成以后，锁解除后再次获取缓存命中。

```java
public String getKey(String key){
    String value = redis.get(key);
    if(value == null){
        String mutexKey = "mutex:key:" + key; //设置互斥锁的key
        if(redis.set(mutexKey,"1","ex 180","nx")){ //给这个key上一把锁，ex表示只有一个线程能执行，过期时间为180秒
          value = db.get(key);
          redis.set(key,value);
          redis.delete(mutexKety);
  }else{
        // 其他的线程休息100毫秒后重试
        Thread.sleep(100);
        getKey(key);
  }
 }
 return value;
}
```

互斥锁的优点是思路非常简单，具有一致性，但是互斥锁也有一定的问题，就是大量线程在等待的问题。存在死锁的可能性

```java
public R redisFindCache(String key, long expire, TimeUnit unit, CacheLoadable<T> cacheLoadable, boolean b) {
    // 布隆过滤器用来解决缓存穿透问题
    if (!bloomFilter.isExist(key)){
        return new R().setCode(600).setData(new NullValueResultDO()).setMsg("非法访问");
    }
    /**
     * 为了解决redis的缓存击穿问题，使用了分布式锁
     * 将所有并发请求都执行到这行代码，redisLock.lock(key); 并停止在了这行代码
     */
    redisLock.lock(key);
    try {
        // 查询缓存
        Object redisObj = valueOperations.get(String.valueOf(key));
        // 命中缓存
        if(redisObj != null) {
            //正常返回数据
            return new R().setCode(200).setData(redisObj).setMsg("OK");
        }
        T load = cacheLoadable.load(); //查询数据库
        if (load != null) {
            valueOperations.set(key, load,expire, unit);  //加入缓存
            return new R().setCode(200).setData(load).setMsg("OK");
        }
    } finally {
        redisLock.unlock(key);
    }
    return new R().setCode(500).setData(new NullValueResultDO()).setMsg("查询无果");
}
```

##### 4.2.2 使用jdk提供的锁

- 使用 java.util.concurrent.locks.ReentrantLock 作为锁来锁住代码块

- ```java
        
  public R redisFindCache(String key, long expire, TimeUnit unit, CacheLoadable<T> cacheLoadable, boolean b) {
      // 布隆过滤器用来解决缓存穿透问题
      if (!bloomFilter.isExist(key)){
          return new R().setCode(600).setData(new NullValueResultDO()).setMsg("非法访问");
      }
  
      reentrantLock.lock();
      try {
          // 查询缓存
          Object redisObj = valueOperations.get(String.valueOf(key));
          // 命中缓存
          if(redisObj != null) {
              //正常返回数据
              return new R().setCode(200).setData(redisObj).setMsg("OK");
          }
          T load = cacheLoadable.load(); //查询数据库
          if (load != null) {
              valueOperations.set(key, load,expire, unit);  //加入缓存
              return new R().setCode(200).setData(load).setMsg("OK");
          }
      } finally {
          reentrantLock.unlock();
      }
      return new R().setCode(500).setData(new NullValueResultDO()).setMsg("查询无果");
  }
  ```

- 使用这种锁的粒度更大，所有的线程都只能单个执行；而使用分布式锁，只锁住了对应key的请求线程

### 5. redis缓存总结

| 问题     | 解决方法                         | 出现原因                       |
| -------- | -------------------------------- | ------------------------------ |
| 缓存穿透 | 缓存空对象，布隆过滤器           | 查询缓存和数据库中都没有的数据 |
| 缓存击穿 | 分布式锁                         | 数据刚好失效，此时来了并发访问 |
| 缓存雪崩 | 搭建高可用集群，错开缓存失效时间 | 大部分数据失效 或 机器宕机     |

### 6. 缓存与数据库数据一致性问题



## 六. redis 集群专题

### 1. Redis主从复制

单机有什么问题：

- 单机故障

- 容量瓶颈

- qps瓶颈

#### 1.1 redis 主从复制是什么

- 为了解决单机模式下的问题，有了主从复制模式

- 主机数据更新后根据配置和策略，自动同步到备机 的master/slaver机制，mester以写为主，slaver已读为主

#### 1.2 主从复制能干什么

1. 读写分离：主机负责写，从机负责读

2. 容灾备份

#### 1.3. 怎么做

##### 1.3.1 原则

1. 配从不配主

2. 使用命令 SLAVEOF 动态指定主从关系  ，如果设置了密码，关联后使用 config set masterauth 密码

3. 配置文件和命令混合使用时，如果混合使用，动态指定了主从，请注意一定要修改对应的配置文件

##### 1.3.2 步骤

1. 新建redis8000,redis8001,redis8002文件夹

2. 将redis.conf文件复制在redis8000下

3. 分别修改个目录下的redis.conf文件

​	  redis8000/redis.conf

​	1) bind 192.168.0.104   指定本机ip

​	2) port 8000

​	3) daemonize yes

​	4) pidfile /var/run/redis_8000.pid  

​	5) dir /myredis/redis8000

​	6) requirepass 123456

4. 把redis8000/redis.conf文件复制到redis8001,redis8002下

​	redis8001/redis.conf

​	1) %s/8000/8001/g    批量替换

​	2) replicaof 192.168.0.104 8000

​	3) masterauth 123456

​	redis8002/redis.conf

       	1.  :%s/8000/8002/g    批量替换
     	2.  replicaof 192.168.0.104 8000
     	3.  masterauth 123456

5. 分别启动8000.8001,8002实例

[root@localhost myredis]# /usr/local/bin/redis-server /myredis/redis8000/redis.conf 
[root@localhost myredis]# /usr/local/bin/redis-server /myredis/redis8001/redis.conf 
[root@localhost myredis]# /usr/local/bin/redis-server /myredis/redis8002/redis.conf 

6. 客户端连接

/usr/local/bin/redis-cli -h 192.168.0.104 -p 8000 -a 123456

/usr/local/bin/redis-cli -h 192.168.0.104 -p 8001 -a 123456

/usr/local/bin/redis-cli -h 192.168.0.104 -p 8002 -a 123456

#### 1.4 工作流程

##### 1.4.1 建立连接

1. 设置master的地址和端口号，发送 slaveof ip 指令，master会返回响应客户端，根据响应信息保存master ip port信息（连接测试）
2. 根据保存的信息创建连接master的socket
3. 周期性发送ping，master会响应pong
4. 发送指令 auth password （身份验证），master验证身份
5. 发送 slave 端口信息，master保存slave的端口号

- 

##### 1.4.2 数据同步

1. slave 发送指令 psyn2
2. master 执行bgsave
3. 在第一个slave连接时，创建命令缓存区
4. 生成RDB文件，通过socket发送给slave
5. slave接收到RDB，清空数据，执行RDB文件恢复过程
6. 发送命令告知RDB恢复已经完成（告知全量复制完成）
7. master发送复制缓冲区信息
8. slave接受信息，执行重写后恢复数据

**注意：**master会保存slave从我这里拿走多少数据，保存slave的偏移量

##### 1.4.3 命令传播

slave心跳：replconf ack {offset} 汇报slave自己的offset，获取最新数据指令，offset就是偏移量

命令传播阶段出现断网：

- 网络闪断闪连，忽略
- 短时间断网，增量
- 长时间断网，全量

全量复制核心三个要素：

1. 服务器运行id

   用于服务器之间通信验证身份，master首次连接slave时，会将自己的run_id发送给slave，slave保存此ID

2. 主服务器挤压的命令缓冲区

   先进先出队列

3. 主从服务器的复制偏移量

   用于比对偏移量，然后判断出执行权量还是增量

#### 1.5 全量复制消耗

1.bgsave时间
2.rdb文件网络传输
3.从节点请求请求数据时间
4.从节点加载rdb的时间
5.可能的aof重写时间

#### 1.6 缺点

1.由于所有的写操作都是先在Master上操作，然后同步更新到Slave上，所以从Master同步到Slave机器有一定的延迟，当系统很繁忙的时候，延迟问题会更加严重，Slave机器数量的增加也会使这个问题更加严重。

2.当主机宕机之后，将不能进行写操作，需要手动将从机升级为主机，从机需要重新制定master

简单总结：

一个master可以有多个Slave

一个slave只能有一个master

数据流向是单向的，只能从主到从

### 2. redis哨兵模式

#### 2.1 是什么，能干嘛？

- 在Redis 2.8版本开始引入。哨兵的核心功能是主节点的自动故障转移。

- 通俗来讲哨兵模式的出现是就是为了解决我们主从复制模式中需要我们人为操作的东西变为自动版，并且它比人为要更及时

#### 2.2 哨兵主要功能（做了哪些事）

监控（Monitoring）：哨兵会不断地检查主节点和从节点是否运作正常。

自动故障转移（Automatic Failover）：当主节点不能正常工作时，哨兵会开始自动故障转移操作，它会将失效主节点的其中一个从节点升级为新的主节点，并让其他从节点改为复制新的主节点。

配置提供者（Configuration Provider）：客户端在初始化时，通过连接哨兵来获得当前Redis服务的主节点地址。

通知（Notification）：哨兵可以将故障转移的结果发送给客户端。

其中，监控和自动故障转移功能，使得哨兵可以及时发现主节点故障并完成转移；而配置提供者和通知功能，则需要在与客户端的交互中才能体现。

#### 2.3 架构

哨兵节点：哨兵系统由一个或多个哨兵节点组成，哨兵节点是特殊的Redis节点，不存储数据。

数据节点：主节点和从节点都是数据节点。

#### 2.4 实现步骤（实战）

##### 2.4.1 部署主从节点

- 哨兵系统中的主从节点，与普通的主从节点配置是一样的，并不需要做任何额外配置。下面分别是主节点（port=8000）和2个从节点（port=8001/8002）的配置文件；

##### 2.4.2 部署哨兵节点

- 哨兵节点本质上是特殊的redis节点

- 3个哨兵节点的配置几乎是完全一样的，主要区别在于端口号的不同（8003/ 8004/ 8005）下面以8003节点为例介绍节点的配置和启动方式；
- 配置部分尽量简化：

```bash
#####sentinel-26379.conf

port 8003
daemonize yes
logfile "/myredis/redis8003/sentinel.log"
sentinel monitor mymaster 192.168.33.10 8000 2
```

其中，sentinel monitor mymaster 192.168.33.10 8000 2 配置的含义是：该哨兵节点以 192.168.33.10:8003 为主节点，该主节点的名称是mymaster（自定义的），最后的2的含义与主节点的故障判定有关：至少需要2个哨兵节点同意，才能判定主节点故障并进行故障转移。

###### 2.4.2.1 具体步骤

1. 复制哨兵机制的配置文件 sentinel.conf

- ![](images\redis哨兵机制配置文件复制.png)

2. 修改配置文件 **sentinel.conf**

- ![](images\redis哨兵机制sentinel.conf配置1.png)

- ![](images\redis哨兵机制sentinel.conf配置2.png)
- sentinel monitor mymaster 192.168.33.10 8000 2 表示：
  - 哨兵机制监控的redis主机是 192.168.33.10:8000 的redis服务
  - 2表示：此处使用3个机子实现哨兵机制，只有两个机子同时认为主机挂了，才确认主机挂了
  - mymaster表示：自己给主机取得名字，可以随意自定义

- ![](images\redis哨兵机制sentinel.conf配置3.png)
- 批量复制 sentinel.conf 文件

###### 2.4.2.1 哨兵节点启动

有两种方式，二者作用是完全相同的：

1. /usr/local/bin/redis-sentinel ./redis8003/sentinel.conf

- ![](images\redis哨兵机制启动.png)

2. /usr/local/bin/redis-server ./redis8003/sentinel.conf --sentinel

#### 2.5 故障转移演示

- 哨兵的监控和自动故障转移功能

- 使用kill命令杀掉主节点
- ![](images\redis哨兵机制故障转移.png)

#### 2.6 客户端（jedis）访问哨兵系统（自动故障转移功能）

```java
public static void main(String[] args)  {
        Logger logger= LoggerFactory.getLogger(TestJedisSentinel.class);
        Set<String> set=new HashSet<>();
        set.add("192.168.0.104:28000");
        set.add("192.168.0.104:28001");
        set.add("192.168.0.104:28002");
        JedisSentinelPool jedisSentinelPool=new JedisSentinelPool("mymaster",set,"123456");
        while (true) {
            Jedis jedis=null;
            try {
                jedis = jedisSentinelPool.getResource();
                String s = UUID.randomUUID().toString();
                jedis.set("k" + s, "v" + s);
                System.out.println(jedis.get("k" + s));
                Thread.sleep(1000);
            }catch (Exception e){
                logger.error(e.getMessage());
            }finally {
                if(jedis!=null){
                    jedis.close();
                }
            }
        }
    }
```

#### 2.7.基本原理

关于哨兵的原理，关键是了解以下几个概念：

主观下线：在心跳检测的定时任务中，如果其他节点超过一定时间没有回复，哨兵节点就会将其进行主观下线。顾名思义，主观下线的意思是一个哨兵节点“主观地”判断下线；与主观下线相对应的是客观下线。

客观下线：哨兵节点在对主节点进行主观下线后，会通过sentinel is-master-down-by-addr命令询问其他哨兵节点该主节点的状态；如果判断主节点下线的哨兵数量达到一定数值，则对该主节点进行客观下线。

需要特别注意的是，客观下线是主节点才有的概念；如果从节点和哨兵节点发生故障，被哨兵主观下线后，不会再有后续的客观下线和故障转移操作。



定时任务：每个哨兵节点维护了3个定时任务。定时任务的功能分别如下：

1.每10秒通过向主从节点发送info命令获取最新的主从结构；

  发现slave节点

  确定主从关系

2.每2秒通过发布订阅功能获取其他哨兵节点的信息；SUBSCRIBE  c2     PUBLISH c2 hello-redis

  交互对节点的“看法”和自身情况

3.每1秒通过向其他节点发送ping命令进行心跳检测，判断是否下线（monitor）。

  心跳检测，失败判断依据



选举领导者哨兵节点：当主节点被判断客观下线以后，各个哨兵节点会进行协商，选举出一个领导者哨兵节点，并由该领导者节点对其进行故障转移操作。

监视该主节点的所有哨兵都有可能被选为领导者，选举使用的算法是Raft算法；Raft算法的基本思路是先到先得：即在一轮选举中，哨兵A向B发送成为领导者的申请，如果B没有同意过其他哨兵，则会同意A成为领导者。选举的具体过程这里不做详细描述，一般来说，哨兵选择的过程很快，谁先完成客观下线，一般就能成为领导者。

故障转移：选举出的领导者哨兵，开始进行故障转移操作，该操作大体可以分为3个步骤：

在从节点中选择新的主节点：选择的原则是，

1.首先过滤掉不健康的从节点；

2.然后选择优先级最高的从节点（由replica-priority指定）；如果优先级无法区分，

3.则选择复制偏移量最大的从节点；如果仍无法区分，

4.则选择runid最小的从节点。

更新主从状态：通过slaveof no one命令，让选出来的从节点成为主节点；并通过slaveof命令让其他节点成为其从节点。

将已经下线的主节点（即6379）保持关注，当6379从新上线后设置为新的主节点的从节点

#### 2.8.实践建议

哨兵节点的数量应不止一个。一方面增加哨兵节点的冗余，避免哨兵本身成为高可用的瓶颈；另一方面减少对下线的误判。此外，这些不同的哨兵节点应部署在不同的物理机上。

哨兵节点的数量应该是奇数，便于哨兵通过投票做出“决策”：领导者选举的决策、客观下线的决策等。

各个哨兵节点的配置应一致，包括硬件、参数等；此外应保证时间准确、一致。



#### 2.9 总结

在主从复制的基础上，哨兵引入了主节点的自动故障转移，进一步提高了Redis的高可用性；但是哨兵的缺陷同样很明显：哨兵无法对从节点进行自动故障转移，在读写分离场景下，从节点故障会导致读服务不可用，需要我们对从节点做额外的监控、切换操作。此外，哨兵仍然没有解决写操作无法负载均衡、及存储能力受到单机限制的问题



---------------------------------

### 3. redis cluster高可用集群

#### 3.1 redis cluster 集群定义

- redis集群是一个由多个主从节点群组成的分布式服务器群，它具有复制、高可用和分片特性；
- redis cluster集群 不需要sentinel哨兵也能完成节点移除和故障转移的功能；
- 需要将每个节点设置成集群模式，这种集群模式没有中心节点，可水平扩展，据官方文档称可以线性扩展到1000个节点；
- redis cluster集群的性能和高可用性均优于之前版本的哨兵模式，且集群配置非常简单 

#### 3.1 redis cluster 集群原理

![](images\redis集群原理.png)

- 通过对key值进行 crc16 哈希算法，算出的结果再进行16384取余
- redis 的小集群中最少要有3台机子，搭建大集群过程中就已经分配好了槽位
- 槽位有 **16384**个，必须全部分配完，不同的key计算出不同的槽位值，选择不同的主机进行存储对应的数据
- 一般性能好的机子分配的槽位更多

#### 3.3 redis cluster 集群搭建

- 此处集群搭建实例采用的是 **搭建伪集群**
- 伪集群：就是在一台机子上搭建了6个redis服务组成的集群

**<font color=red>常用命令：/usr/local/bin/redis-cli --cluster help</font>**

##### 3.3.1  配置文件步骤：

1. 新建redis7000，redis7001，redis7002，redis7003，redis7004，redis7005，redis7006文件夹

- ![](images\redis集群工作空间创建1.png)

2. 将 redis.conf 文件复制在 redis 集群的工作目录下

- ![](images\redis集群工作空间创建2.png)

3. 分别修改个目录下的 redis.conf 文件

- vi /myredis/redis7000/redis.conf

	1. bind 192.168.33.10   指定本机ip

	2. port 7000                    指定reidis服务的端口号

- ![](images\redis集群-redis.conf配置1.png)

3. daemonize yes                                    指定该redis服务端以后台方式运行
4. pidfile /var/run/redis_7000.pid        指定该redis服务端启动时进程数据信息的存储位置，伪集群需要改

- ![](images\redis集群-redis.conf配置2.png)

5. logfile "/myredis/redis7000/redis.log"       指定该redis服务所产生的日志文件存放位置

- ![](images\redis集群-redis.conf配置3.png)

6. dir /myredis/redis7000                 指定该redis服务实例所产生的所有文件存放的位置

- ![](images\redis集群-redis.conf配置4.png)

6. masterauth 123456                     配置从机连接主机时，需要使用的masterauth密码

- ![](images\redis集群-redis.conf配置5.png)

7. requirepass 123456                    配置连接上redis时使用的密码

- ![](images\redis集群-redis.conf配置6.png)

8. **redis集群的配置**

redis集群的配置默认是注释掉的，需要自己开启配置：

**cluster-enabled yes     打开此redis的集群开关，表示该redis服务将以集群方式启动，会设置相应的槽位**

**cluster-config-file nodes-7000.conf       配置该redis服务用来存储集群中其他节点的信息文件**

![](images\redis集群-redis.conf配置7.png)

**cluster-require-full-coverage yes**       

- 集群中有一个主机挂掉了，然后如果该主机没有从节点，那么整合集群是否依旧可用就是通过这个来配置的
- cluster-require-full-coverage yes   表示整个集群依旧是可用的，其余主节点的redis处理没有问题
- cluster-require-full-coverage no    表示整个集群都会挂掉，无法使用

- ![](D:\myData\documents\git-repositories\study-2021\note\05 分布式\redis\images\redis集群-redis.conf配置8.png)

9. 把 redis7000/redis.conf 文件复制到 redis7001，redis7002，redis7003，redis7004，redis7005，redis7006下

- **命令：<font color=red>sed 's/7000/7001/g' redis7000/redis.conf > redis7001/redis.conf</font>**
- 将 redis7000/redis.conf 文件中所有 7000 都替换为 7001，并生成新的文件存放到 redis7001/redis.conf

- ![](images\批量修改.png)

##### 3.3.2 启动服务端节点(多个节点)

- 启动集群中的6个节点：

- ![](images\启动redis集群中的节点.png)

##### 3.3.3 客户端连接节点

- 客户端连接服务端7000节点
- ![](images\redis客户端连接服务端.png)
- 连接过程中返回了警告，用来表示出于安全方面，最好不要使用 -a 来输入密码

       	1.  :%s/8000/8002/g    批量替换
     	2.  replicaof 192.168.0.104 8000
     	3.  masterauth 123456

##### 3.3.4 节点配置1(原生的,不常用)

###### 3.3.4.1查看节点关系

- 查看当前节点再集群中与其他哪些节点相关联

- 命令：
  - 客户端连接成功后，执行 cluster nodes ；
  - 客户端没有连接时，执行 /user/local/bin/redis-cli -h 192.168.33.10 -p 7000 -a 123456 cluster nodes;
- ![](images\查看节点信息.png)
- 因为连接的是 7000 端口的服务，所以查看的是与该阶段关联的节点信息，目前只有自己本身的一个节点

###### 3.3.4.2 meet 操作

- 目前已经启动了6个redis服务节点，但是他们是孤立的，没有任何联系，通过meet进行配置

- 命令：cluster meet ip port

###### 3.3.4.3 指派槽位

- **命令：cluster addslots slot（槽位下表）**

- 查看crc16算法算出key的槽位命令：cluster keyslot key

###### 3.3.4.4. 主从配置

- **命令：cluster replicate node-id**

##### 3.3.5 节点配置2(常用)

**命令：/usr/local/bin/redis-cli -h 192.168.33.10 -p 7000 -a 123456 --cluster help**

![](images\集群命令文档.png)

- redis 5.5 的版本以后可以使用 redis-cli 命令来进行搭建集群
- 老版本的redis 需要使用 redis-trib.rb 命令来搭建集群

###### 3.3.5.1 创建节点

命令：

**/usr/local/bin/redis-cli --cluster create 192.168.33.10:7000 192.168.33.10:7001 192.168.33.10:7002 192.168.33.10:7003 192.168.33.10:7004 192.168.33.10:7005 --cluster-replicas 1 -a 123456**

- 以上述命令中所有的 ip和port 组成的节点为一个集群，将他们关联起来
- --cluster-replicas 1 ：表示1:1，换算为3:3，前面的三个为主机，后面的三个为从机（随机对应分配）
- --cluster-replicas 2 ：表示1:2，换算为2:4，一个主机，两个从机，这是不行的，因为集群中至少要三个主机
- 使用上述命令，槽位会被直接平均分配掉

- ![](images\集群节点配置.png)

###### 3.3.5.2 查看节点信息

- 命令：/bin/redis-cli -h 192.168.33.10 -p 7000 -a 123456 cluster nodes

- ![](images\查看节点相关信息.png)

- 第一列表示节点的id
- 第二列表示节点的ip和端口号，@后面的17003表示集群通信的端口，默认是加了一万
- 第三列表示这个节点的角色

#### 3.4 测试集群

![](images\集群节点测试.png)

##### 3.4.1 连接单个节点测试

- 使用 /usr/local/bin/redis-cli -h 192.168.33.10 -p 7000 -a 123456 连接到7000单个节点上，进行 set k1 v1 操作时，计算的槽位值是在 7002 上，所以无法储存，报错。

##### 3.4.2 连接集群节点测试

- 使用 /usr/local/bin/redis-cli -h 192.168.33.10 -p 7000 -a 123456 <font color=red>-c</font> 连接到7000节点上，但是这是使用的集群的方式连接的，当进行 set k1 v1 操作时，直接重定向到 7002 端口的节点上了，在进行保存操作；

- 这种集群连接的方式只有 redis-cli 可以操作，使用java客户端连接就没法这么做，没有这种 -c 的功能的。

#### 3.5 集群扩容

##### 3.5.1 添加扩容节点

命令：**/usr/local/bin/redis-cli --cluster add-node 192.168.33.10:7006 192.168.33.10:7000 -a 123456**

- 默认是新加的节点是主机
- ![](images\集群中扩容新的主机节点.png)

**/usr/local/bin/redis-cli --cluster add-node 192.168.33.10:7007 192.168.33.10:7006 --cluster-slave --cluster-master-id 30e0e70197e82ad04ee9786427c130625efe2c9e -a 123456**

- 向集群中添加新的节点，此节点以slave的身份加入
- ![](images\集群中扩容新的从节点.png)

##### 3.5.2 给节点设置槽位

命令：/usr/local/bin/redis-cli --cluster reshard 192.168.331.10:7000 -a 123456

![](images\集群扩容分配槽位.png)

- 第一次询问，给新的节点添加几个槽位
- 第二次询问，新扩容的节点是哪个，就是给那个节点设置槽位
- 第三次询问，从哪里获取槽位给新的扩容节点：
  - all 表示从已有的节点中，平均去除节点给新的扩容节点
  - done 表示自己设置好从哪几个节点取出槽位给新的扩容节点，用done来结束
- 槽位迁移到新的节点上，那么该草为对应的数据也会从原来的节点迁移到新的节点上

![](images\扩容结果.png)

#### 3.6 集群缩容

##### 3.6.1 移除缩容节点的槽位

- 命令：**/usr/local/bin/redis-cli --cluster reshard 已存在的节点ip:端口号 --cluster-from 要迁出节点ID --cluster-to 接受槽节点ID --cluster-slots 迁移槽数量 **

- 实例：/usr/local/bin/redis-cli --cluster reshard 192.168.33.10:7000 --cluster-from 30e0e70197e82ad04ee9786427c130625efe2c9e --cluster-to b4051c3b18baa679e7136eb8759b0bde4109feaf --cluster-slots 300

- ![](images\集群缩容.png)

##### 3.6.2 删除槽位结果查询

- 命令：/usr/local/bin/redis-cli -h 192.168.33.10 -p 7000 -a 123456 cluster nodes

- ![](images\集群缩容-去除槽位.png)

- 7006节点已经被去除了集群中分配给他的槽位了，接着可以直接删除该节点了

##### 3.6.3 移除节点

- 命令：/usr/local/bin/redis-cli --cluster del-node ip:端口号 节点ID

- 先删除从节点，再删除主节点
- ![](images\集群缩容-移除节点.png)

#### 3.7 总结

##### redis集群演变过程

###### 1.单机版

- **核心技术：持久化**

- 持久化是最简单的高可用方法（有时甚至不被归为高可用的手段），主要作用是数据备份，即将数据存储在硬盘，保证数据不会因进程退出而丢失。

###### 2.主从复制

- 复制是高可用Redis的基础，哨兵和集群都是在复制基础上实现高可用的。

- 复制主要实现了数据的多机备份，以及对于读操作的负载均衡和简单的故障恢复。
- 缺陷：
  1. 故障恢复无法自动化，需要人为地将当掉的主机处理掉
  2. 写操作无法负载均衡；存储能力受到单机的限制。

###### 3.哨兵

- 在复制的基础上，**哨兵实现了自动化的故障恢复，自动从 从机中选出一个作为主机**（高可用）
- **哨兵机制就是用来实现 从节点自动化替代主节点**
- 缺陷：
  1. 写操作无法负载均衡；
  2. 存储能力受到单机的限制，数据越来越多，磁盘是有限的；

###### 4.集群

- 通过集群，redis 解决了写操作无法负载均衡，以及存储能力受到单机限制的问题，实现了较为完善的高可用方案
- 哨兵机制在进行故障转移时，从机替换为主机时，是无法进行写操作的（虽然很快能处理好），但是使用了集群就可以实现只有 正在进行从机替换主机的那个 redis 服务无法进行写操作，其余的主机 redis 还是可以进行写操作的



## 七. java客户端调用redis

### 1. 工作流程

![](images\java调用redis的流程图.png)

- **java客户端(jedisCluster对象) 连接上redis集群后，会先通过 cluster nodes 获取映射关系，并保存到虚拟机内存中**
- 重定向错误后，java客户端会重新获取映射关系

### 2. java代码

```java
import com.google.common.collect.Lists;
import com.xufree.learning.redis.redis.MyRedisCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.util.JedisClusterCRC16;

import java.util.*;

public class TestRedisCluster {

    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(TestRedisCluster.class);

        Set<HostAndPort> nodesList = new HashSet<>();

        nodesList.add(new HostAndPort("192.168.33.10",7000));
        nodesList.add(new HostAndPort("192.168.33.10",7001));
        nodesList.add(new HostAndPort("192.168.33.10",7002));
        nodesList.add(new HostAndPort("192.168.33.10",7003));
        nodesList.add(new HostAndPort("192.168.33.10",7004));
        nodesList.add(new HostAndPort("192.168.33.10",7005));


        // Jedis连接池配置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大空闲连接数, 默认8个
        jedisPoolConfig.setMaxIdle(200);
        // 最大连接数, 默认8个
        jedisPoolConfig.setMaxTotal(1000);
        // 最小空闲连接数, 默认0
        jedisPoolConfig.setMinIdle(100);
        // 获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        jedisPoolConfig.setMaxWaitMillis(3000); // 设置3秒
        // 对拿到的connection进行validateObject校验
        jedisPoolConfig.setTestOnBorrow(false);

        // jedisCluster最原生的对象
        JedisCluster jedisCluster = new JedisCluster(nodesList,2000,2000,5,"123456", jedisPoolConfig);
        /**
         * 操作：原生的jedisCluster对象是没有办法使用mset这些方法的，
         * 因为 "k1","k2","k3"对应的槽位可能不在一个节点上，所以不行。
         *
         * 会报错：
         * Exception in thread "main" redis.clients.jedis.exceptions.JedisClusterOperationException:
         *      No way to dispatch this command to Redis Cluster because keys have different slots.
         */
//        System.out.println(jedisCluster.mset("k1", "v1", "k2", "v2", "k3", "v3"));
//        System.out.println(jedisCluster.mget("k1", "k2", "k3" ));

        // 自定义的 JedisCluster对象，继承于JedisCluster
        MyRedisCluster redisCluster = new MyRedisCluster(nodesList,2000,2000,5,"123456", jedisPoolConfig);
        /**
         * 操作：原生的jedisCluster对象无法使用mset方法，
         * 所以自定义了MyJedisCluster对象来重写mset方法，从而可以直接设置多个key-value值
         */
        System.out.println(redisCluster.mset("k1", "v1", "k2", "v2", "k3", "v3"));
        // mget方法没有重写，会报错
        System.out.println(redisCluster.mget("k1","k2", "k3" ));

//        while (true) {
//            try {
//                String s = UUID.randomUUID().toString();
//                jedisCluster.set("k" + s, "v" + s);
//                System.out.println(jedisCluster.get("k" + s));
//                Thread.sleep(1000);
//            }catch (Exception e) {
//                logger.error(e.getMessage());
//            }
//        }
    }
}
```

## 八. redis分布式锁

### 1. 为什么使用分布式锁？

#### 1.1 多线程

- 多线程中只需要使用java提供的锁就能解决线程安全问题

#### 1.2 多进程

- 分布式系统都是多进程的，只能用分布式锁来解决问题

### 2. 怎么实现分布式锁？

#### 2.1 普通的分布式锁

##### 2.1.1 基本命令：

- setnx k1 v1 ：设置键k1对应的值为v1，如果k1已经存在，则此指令无效；如果不存在，则有效
- expire k1 10：设置k1的过期时间为10秒
- setex k1 10 v1：等于上述两个指令的合并

![](images\分布式锁-基本命令.png)

##### 2.1.2 原理

![](images\分布式锁-原理.png)

1. 通过 setnx 来实现获取锁，通过 del 释放锁
2. 通过给锁设置过期时间，防止获取了锁的线程出现问题挂了，导致死锁问题出现
3. 通过设置守护线程来实现给锁加过期时间，解决业务没处理好，但是锁过期了的问题

##### 2.2.3 代码

```java
@Test
public void testRedis(){
    Jedis jedis = new Jedis("192.168.33.10",6379);
    SetParams setParams=new SetParams();
    setParams.ex(6); // setex 设置值的同时设置过期时间
    setParams.nx();  //
    String uuidValue = UUID.randomUUID().toString();
    String lock = jedis.set("lock", uuidValue, setParams);
//        Long setnx = jedis.setnx("lock", "value2");
//        if(setnx==1){
//            jedis.expire("lock",10);
//        }
    System.out.println(lock);
}
```



```java
package com.luban;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;

@Component
public class RedisLock implements Lock {

    @Autowired
    private JedisPool jedisPool;

    private static final String key = "lock";

    private ThreadLocal<String> threadLocal = new ThreadLocal<>();

    private static AtomicBoolean isHappened = new AtomicBoolean(true);

    // 加锁
    @Override
    public void lock() {
        boolean b = tryLock();  //尝试加锁
        if(b) {
            //拿到了锁
            return;
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 递归调用，直到拿到锁
        lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    /**
     * 尝试加锁
     * @return 返回true,表示拿到了锁；返回false,表示没有拿到锁。
     */
    @Override
    public boolean tryLock() {
        // 参数对象
        SetParams setParams = new SetParams();
        setParams.ex(2);  // 设置过期时间为2s
        setParams.nx();
        String values = UUID.randomUUID().toString();
        // 从JedisPool连接池对象中获取Jedis对象
        Jedis resource = jedisPool.getResource();
        String lock = resource.set(key, values, setParams);
//        String lock = resource.set(key,s,"NX","PX",5000);
        resource.close();
        if("OK".equals(lock)) { // 拿到了锁
            // 保存此时锁对应的value值
            threadLocal.set(values);
            if(isHappened.get()) {
                ThreadUtil.newThread(new MyRunnable(jedisPool)).start();
                isHappened.set(false);
            }
            return true;
        }
        return false;
    }

    static class MyRunnable implements Runnable{

        private JedisPool jedisPool;
        public MyRunnable(JedisPool jedisPool){
            this.jedisPool=jedisPool;
        }
        @Override
        public void run() {
            Jedis jedis = jedisPool.getResource();
            while (true){
                Long ttl = jedis.ttl(key);
                if(ttl!=null && ttl>0){
                    jedis.expire(key, (int) (ttl+1));
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    /**
     * 解锁：第一步判断设置时候的value 和 此时redis的value是否相同
     */
    @Override
    public void unlock() throws Exception {
        // lua脚本
        String script = "if redis.call(\"get\",KEYS[1])==ARGV[1] then\n" +
                        "    return redis.call(\"del\",KEYS[1])\n" +
                        "else\n" +
                        "    return 0\n" +
                        "end";
        // 通过连接池对象获取Jedis对象
        Jedis resource = jedisPool.getResource();

        // 方法1：通过Jedis对象删除锁对应的数据，但是这么操作没有原子性
//        if (resource.get(key).equals(threadLocal.get())) {
//            resource.del(key);
//        }

        // 方法2：调用lua脚本进行解锁，保证了原子性
        Object eval = resource.eval(script, Arrays.asList(key), Arrays.asList(threadLocal.get()));
        if(Integer.valueOf(eval.toString()) == 0){
            resource.close();
            throw new Exception("解锁失败");
        }

        resource.close();
    }

    @Override
    public Condition newCondition() {
        return null;
    }

}
```



#### 2.2 高可用的分布式锁

##### 2.2.1 redis推荐

1. RedLock

2. 启动3个单机版的redis实例

## 九. redis扩展应用

### 1. redis事务

#### 1.1 定义

- redis 事务就是一个命令执行的队列，将一系列预定义的命令包装成一个整体，就是一个队列。当执行的时候，一次性按照顺序执行，中间不会被打断或者干扰。

#### 1.2 应用

- 一个队列中**，一次性，顺序性，排他性** 的执行一系列命令

#### 1.3 基本操作

开启事务：multi          设置十五的开始位置，这个指令开启后，后面所有的指令都会加入到事务中

执行事务：exec           设置事务的结束位置，同时执行事务，与multi成对出现，成对使用

取消事务：discard       终止当前事务，取消multi后，exec前的所有指令

**注意：**加入事务的命令并没有立马执行，而是加入到队列中，exec命令后才执行的

#### 1.4 问题

加入和执行的事务中有错误怎么处理？

假如事务语法报错，则事务取消

执行事务报错，则队列中指令执行成功的返回成功；执行失败的返回失败，但不会影响报错后面的指令执行

**注意：**已经执行完毕的命令对应的**数据不会自动回滚**，需要程序员自己实现

1. 事务执行成功

- ![](images\redis开启事务并执行.png)

2. **语法报错**-执行了错误的命令

- ![](images\redis事务执行语法错误.png)
- 第一次的事务中有 set 123 命令，这个属于redis的语法错误，无法放到队列中，事务直接被取消了

3. **命令逻辑错误**

- ![](images\redis事务执行命令错误.png)
- 事务中的队列中有五条指令，第四条指令无法执行报错，但是不影响它后面的指令执行
- 已经执行完毕的命令对应的**数据不会自动回滚**，需要程序员自己实现
- redis 的事务，也被称为假事务

#### 1.5 监控key

watch：对key进行监控，如果在exec执行前，监控的key发生了变化，终止事务执行

unwatch：取消对所有的key进行监控

- 监控要在开启事务之前执行，否则无效

### 2. redis 发布订阅

**命令：**

publish：发布消息，语法：publish channel名称 "消息内存"

subscribe：订阅消息，语法：subscribe channel名称

psubscribe：使用通配符订阅消息，语法：psubscribe channel*名称

punsubscribe：使用通配符退订消息，语法：pubsubscribe channel*名称

unsubscribe：退订消息，语法：unsubscribe channel名称

1. 订阅

- ![](images\redis订阅消息.png)

2. 发布

- ![](images\redis发布消息.png)

- 只要一发布消息，订阅了该渠道的客户端会直接收到发布的消息

### 3. 删除策略

#### 3.1 定时删除

- <img src="images\redis定时删除策略.png" style="zoom:90%;" />

- 定时器不停地查看每个数据的时间戳，如果过期时间到了，那么就会找到该数据的地址，通过地址找到该数据在内存中的存储，将其删除掉
- 以CPU内存换redis内存

#### 3.2 惰性删除

- 在获取数据时，会查看该数据是否已经过期，如果已经过期了，就会将其删除掉，并返回查不到
- 不查询获取数据，该数据会一直存在内存中
- 以 redis 内存换CPU内存，导致redis中有大量已过期的数据

#### 3.3 定期删除

1. redis在启动的时候，读取配置文件hz的值，默认为10

2. 每秒执行hz次serverCron() ---> databasesCron() ---> actveEXpireCyle()

3. actveEXpireCyle() 对每个expires[*] 进行逐一检测，每次执行250ms/hz

4. 对某个expires[*]检测时，随机挑选N个key检查

   - 如果key超时，删除key
   - 如果一轮中删除的key的数量大于`N*25%`，循环该过程
   - 如果一轮中删除的key的数量小于等于`N*25%`，检查下一个expire[*]

   current_db用于记录actveEXpireCyle()进入哪个expires[*]执行，如果时间到了，那么下次根据current_db继续执行

- <img src="images\redis定期删除策略.png" style="zoom:85%;" />
- redis 有16个数据库，每个数据库都有自己的 expires[*] 来存储数据在内存中的地址和时间戳，例如 0号数据库对应的是 expire[0]

### 4. 逐出算法

相关配置：

maxmemory：最大可使用内存，占用物理内存的比例，默认值为0，表示不限制。生产环境一般根据需求设置，通常50%以上

maxmemory-policy：达到最大内存后，对挑选出来的数据进行删除策略

maxmemory-samples：每次选取代待删除数据的个数，选取数据时并不会全库扫描，采用随机获取数据的方式作为待检测删除数据

```
#当内存使用达到最大值时，redis使用的清除策略。有以下几种可以选择（明明有6种，官方配置文件里却说有5种可以选择？）：
# 1）volatile-lru    利用LRU算法移除设置过过期时间的key (LRU:最近使用 Least Recently Used ) 
# 2）allkeys-lru     利用LRU算法移除任何key 
# 3）volatile-random 移除设置过过期时间的随机key 
# 4）allkeys-random  移除随机key 
# 5）volatile-ttl    移除即将过期的key(minor TTL) 
# 6）noeviction      不移除任何key，只是返回一个写错误 。默认选项

# maxmemory-policy noeviction
```



## Linux vim模式下查询关键字步骤：

1. ESC
2. 输入 /关键字  ，然后回车
3. 按 N ，查找下一个

## Linux临时关闭防火墙：

 systemctl stop firewalld.service
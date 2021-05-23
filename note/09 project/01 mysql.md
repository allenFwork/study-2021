# 一. mysql安装

官网下载地址：https://dev.mysql.com/downloads/repo/yum/

## 1. 下载RPM包并上传至linux

- 官方下载地址： https://dev.mysql.com/downloads/repo/yum/ 

## 2. yum localinstall ....  RPM包名

![](D:\myData\documents\git-repositories\study-2021\note\07 database\images\centos7暗装mysql操作1.png)

![centos7暗装mysql操作2](D:\myData\documents\git-repositories\study-2021\note\07 database\images\centos7暗装mysql操作2.png)

## 3. cat /etc/yum.repos.d/mysql-community.repo

- 在YUM库中，存储了多个版本的MySQL，现在最新版的MySQL版本为8.0的，我们更新YUM库之后，默认安装的是最新版本的MySQL，其它版本的存储库是禁用的。

- 我们可以通过下面的命令来看到哪些是禁用的：
-  yum repolist all | grep mysql 
- ![](D:\myData\documents\git-repositories\study-2021\note\07 database\images\centos7查看当前默认安装的mysql版本.png)

### 	3.1  可以修改你要下载mysql的版本

- vim /etc/yum.repos.d/mysql-community.repo 
- 或者 vi /etc/yum.repos.d/mysql-community.repo

![](D:\myData\documents\git-repositories\study-2021\note\07 database\images\centos7修改安装mysql的版本.png)

- 现在默认安装 mysql 8.0 的版本
- 对其进行修改，改为安装mysql 5.5 版本
- ![](D:\myData\documents\git-repositories\study-2021\note\07 database\images\Centos7安装mysql5.5版本.png)

### 	3.2  下载的版本是   enabled=1 的

![](images\查看安装mysql的版本.png)

## 4. yum install mysql-community-server

- 执行上面的命令，开始下载

## 5. service mysqld status   查看状态   

- 下载完成后，默认是没有启动的

## 6. service mysqld start    启动

- 启动

## 7.查看密码

### 	7.1   cat /etc/my.cnf  -->log-error=/var/log/mysqld.log

- 查找日志文件路径

### 	7.2  cat /var/log/mysqld.log   查找password这行  最后的就是密码

- ![](images\查看mysql原有密码.png)

## 8.登录mysql   修改密码验证（可选，学习阶段为了方便，不然要设置一个很复杂的密码）

​	show variables like '%password%'; 

### 	8.1  set global validate_password_policy=0;

### 	8.2  set global validate_password_length=1;

## 9.修改密码  

## alter user 'root'@'localhost' identified by 'Sw19941021....';

- 原密码：g<88ywj1npBM
- 新密码：Sw19941021....

## 10 关闭防火墙

- systemctl stop firewalld

## 11. 解决ERROR 1130: Host '192.168.1.3' is not allowed to connect to this MySQL server 方法

1. 改表法。可能是你的帐号不允许从远程登陆，只能在localhost。这个时候只要在localhost的那台电脑，登入mysql后，更改 "mysql" 数据库里的 "user" 表里的 "host" 项，从"localhost"改称"%" 
   mysql -u root -pvmwaremysql>use mysql;mysql>update user set host = '%' where user = 'root';mysql>select host, user from user; 

2. 授权法。例如，你想myuser使用mypassword从任何主机连接到mysql服务器的话。 
   GRANT ALL PRIVILEGES ON *.* TO 'myuser'@'%' IDENTIFIED BY 'mypassword' WITH GRANT OPTION; 
   如果你想允许用户myuser从ip为192.168.1.3的主机连接到mysql服务器，并使用mypassword作为密码 
   GRANT ALL PRIVILEGES ON *.* TO 'myuser'@'192.168.1.3' IDENTIFIED BY 
   'mypassword' WITH GRANT OPTION;  

# 二. mysql主从复制

## 1. 准备主从机子

### 1.1 一台安装好MySql的服务器

- Centos7 上安装 MySql

### 1.2 克隆一台同样的服务器

- ![](images\克隆步骤1.png)

  ![克隆步骤2](images\克隆步骤2.png)

  ![克隆步骤3](images\克隆步骤3.png)

  ![克隆步骤4](images\克隆步骤4.png)

### 1.3 修改主机地址

- 主机 和 克隆后的主机  的IP地址是一样的所以需要修改

1. 修改网络配置文件

- ![](images\修改网络配置文件.png)

2. 修改地址 和 UUID

- ![](images\修改网路配置文件地址.png)

- 修改本机的ip地址为 192.168.33.30
- 修改 UUID 的值

### 1.4 本机地址

1. 查看本机ip地址

- ip addr
- ![](images\查看IP地址.png)

2. 重启网络

- service network restart



## 2. 主节点

### 2.1 创建用户

create user 'allen'@'192.168.33.30' identified by 'Sw19941021....';

create user 'allen'@'192.168.33.31' identified by 'Sw19941021....';

create user 'allen'@'192.168.33.%' identified by 'Sw19941021....';

### 2.2 赋予权限

```
grant replication slave on *.* to 'allen'@'192.168.33.30' identified by `Sw19941021....`;
```

如果创建要通用权限用户

```
create user 'allen'  identified by 'Sw19941021....';

grant all  on *.* to 'allen'@'%' identified by 'Sw19941021....';
```

### 2.3 启动binlog日志（my.cnf配置文件中加入）

- server-id=1    //随便指定一个id  不能与其他主机冲突
  log-bin=/var/lib/mysql/mysql-bin

- ![](images\mysql主机配置.png)

### 2.4 重启

- service mysqld restart

- ![](images\重启mysql.png)

### 2.5 查看master状态

- show master status
- ![](images\mysql主机查看状态.png)

## 3. 从节点

### 3.1 my.cnf配置文件中加入

- server-id=2
  relay-log=/var/lib/mysql/relay-bin
  relay-log-index=/var/lib/mysql/relay-bin.index
- ![](images\mysql副机配置.png)

### 3.2 登录mysql执行（建立关系）

- change master to master_host='192.168.33.30',master_port=3306,master_user='allen',master_password='Sw19941021....',master_log_file='mysql-bin.000001',master_log_pos=156;
- ![](images\MySQL关联到主机上.png)

注意：master_log_file='mysql-bin.000001',master_log_pos=0; 这两个值有时需要根据master的信息写

- 查看命令：show master status
- ![](images\mysql主机查看状态.png)

### 3.3 开始复制

- start slave; 

### 3.4 查看状态

- show slave status\G  
- ![](images\mysql的slave状态查看.png)

搭建注意点：

1.关闭防火墙或开放端口
2.修改 /var/lib/mysql/auto.cnf 文件  将uuid随便修改一下（如果是克隆虚拟机的话，会出现要UUID一致的情况）

3.修改配置文件重启

# 三. 主主复制

## 1.修改从节点

### 1.1添加配置 

log-bin=/var/lib/mysql/mysql-bin

### 1.2重启  

service mysqld restart

show variables like '%log_bin%';   //查看是否开启命令

### 1.3创建用户

create user 'taibai'@'192.168.204.%' identified by '123456';

### 1.4赋予权限

grant replication slave on *.* to 'taibai'@'192.168.204.%' identified by '123456';

show master status;   //查看master状态

## 2.修改主节点

### 1.1建立连接

- change master to master_host='192.168.204.100',master_port=3306,master_user='taibai',master_password='123456',master_log_file='mysql-bin.000001',master_log_pos=716;
- 注意,master_log_file,master_log_pos是偏移量

- 如果设置错误，需要修改，按照以下步骤：

  1、停止已经启动的绑定

  ```
  stop slave
  ```

  2、重置绑定

  ```
  reset master
  ```

  3、执行复制主机命令

  ```
  change master to master_host = '192.168.12.1' master_user = 'slave' ,master_password ='123456' ,master_log_file = 'mysql-bin.000004',master_log_pos = '881'
  ```

  4、发现此时已经不报错
  5、启动复制

  ```
  start slave
  ```

### 1.2开始复制 查看状态

- show slave status;
- start slave;

## 注意：低版本中可能会出现主键问题

配置文件中加入  
auto_increment_increment=2       //你有几个节点  （步长）
auto_increment_offset=1     //两个节点   岔开   一个指定为1  一个指定为2

## 3. 原理

![](images\mysql数据库集群原理.png)

- 通过 第三方工具 KeepAlived实现高可用，能够创建一个虚拟ip；
- 有两个主节点的mysql数据库节点，他们都有 KeepAlived插件，两个同时启动后，会抢夺它的虚拟ip，抢到的就作为平常使用的数据库节点； 

- 如果正在使用的那个mysql主节点挂了，那么另外一个主节点就会抢夺到 KeepAlived的虚拟ip，原来的子节点（slave数据库）就会绑定到这个新的主节点上；



# 四. 项目中配置多数据源

## 1. 多包配置数据源



## 2. 动态数据源



# 五. mycat

官网地址： http://www.mycat.io/

## 1.安装（liunx上要安装好jdk，mycat是java写的，所以依赖jdk）

### 1.1 官网下载安装包并上传至liunx

### 1.2 解压

### 1.3 修改/mycat/conf/wrapper.conf文件(学习环境修改，不然会因内存报错)

wrapper.java.additional.10=-Xmx1G
wrapper.java.additional.11=-Xms256M

### 1.4启动与停止





# 五. git项目地址

- https://github.com/513667225/luban-vip-project-git
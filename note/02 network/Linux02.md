## 1. SSH

### 1.1 SSH工作机制 

- ssh为Secure Shell（安全外壳协议）的缩写

- 很多ftp、pop和telnet在本质上都是不安全的

- 我们使用的Xshell6就是基于SSH的客户端实现

- SSH的服务端实现为openssh- deamon

- 在Linux上使用ssh命令： `ssh root@192.168.33.3`

![](images\Linux系统相互连接.png)

- `exit` 登出

### 1.2 SSH免密码登录

1. 生成秘钥： `ssh-keygen `

![](images\SSH1.png)

![](images\SSH2.png)

- 使用默认的SSH密钥生成方式并保存
- 私钥保存在 /root/.ssh/id_rsa 中，隐藏文件
- 公钥保存在 /root/.ssh/id_rsa.pub. 中，隐藏文件

2. **把自己的公钥拷给对方： `ssh-copy-id 192.168.33.4 `**

![](images\SSH3.png)

- **将192.168.33.3 生成的公钥 发送到了 192.168.33.4 服务器上了**

![](images\SSH4.png)

- 使用SSH命令直接连接到了 192.168.33.4 服务器上，不需要使用密码了

3. 基于ssh的文件拷贝： `scp abc.txt 192.168.33.4:/root `

![](images\SSH5.png)

4. 基于ssh的目录拷贝：需要加 -r 递归拷贝 

- `scp aaa -r 192.168.33.4:/root `

5. 远程执行命令： `ssh 192.168.33.4 "echo hello > /root/test.txt" `

![](images\SSH6.png)

![SSH7](images\SSH7.png)



## 2. 网络配置

1. 查看当前主机名：`hostname`
2. 修改当前主机名（重启后无效）：`hostname 新的主机名`

![](images\hostname.png)

3. 修改当前主机名（重启后有效）：`vi /etc/sysconfig/network`



## 3. yum工具

### 3.1 介绍

- yum类似于Maven工具，可以从中央仓库下载安装各种软件

- 当某个软件有依赖其它软件时，yum也会自动下载并安装其它软件

### 3.2 常用命令： 

1. 安装软件包：`yum install xxx -y`  

- -y 表示免确认 

2. 清除本地索引数据：`yum clear all `

3. 查找库中软件包：`yum list | grep xxx `   

![](images\yum1.png)

- | grep 后面带的是查询关键字
- 如，搜索jdk工具，执行指令：`yun list | grep jdk`

4. 列出本地所配置的仓库信息：`yum repolist `

5. 查询yum命令的其它参数：直接敲yum回车 

## 4. 补充命令

1. 解压到当前目录：`tar -zvxf jdk-8u181-linux-x64.tar.gz`
2. 解压到指定目录：`tar -zvxf jdk-8u181-linux-64.tar.gz -C /usr/local/`



## 5. 软件安装

### 5.1 安装jdk

1. 使用 Xftp6 上传 jdk-8u181-linux-x64.tar.gz 到服务器

2. 解压到指定目录：

- `tar -zxvf jdk-8u181-linux-x64.tar.gz -C /root/jdk/ `

![](images\解压jdk.png)

3. 配置环境变量 

- `vim /etc/profile `

- 在profile文件末尾添加：
  - export JAVA_HOME=/usr/local/jdk1.8.0_181/ 
  - export PATH=$PATH:$JAVA_HOME/bin 

![](images\jdk环境变量配置.png)

- **PATH=$PATH:JAVA_HOME/bin/ 中间使用<font color=red>冒号分隔</font>**

4. 使文件生效：

- `source /etc/profile`

### 5.2 安装Tomcat

1. 使用Xftp6上传apache-tomcat-8.5.34.tar.gz 
2. 解压到指定目录 

- `tar -zxvf apache-tomcat-8.5.34.tar.gz -C /root/apps/ `

3. 启动

- `[root@localhost bin]# ./startup.sh`

![](images\tomcat1.png)

![tomcat1](images\tomcat2.png)

![](images\tomcat启动.png)

4. 查看监听端口，检查8080 

- netstat -nltp 

![](images\tomcat启动2.png)

5. 关闭防火墙

- `systemctl stop firewalld`

- windows上通过调用 http://192.168.33.3:8080/ 地址无法访问虚拟机中安装的tomcat，因为防火墙的问题，需要关闭防火墙
- 关闭完服务器的防火墙后，就可以正常访问了

![](images\tomcat防火墙.png)

- 但是这样修改后，下次重启服务器后，防火墙还会开启

## 6. Shell编程

### 6.1 基本格式 

- Shell俗称壳（用来区别于核），是指”为使用者提供操作界面“的软件（命令解析器）

- Shell是用户与内核进行交互操作的一种接口，**目前最流行的Shell称为 bash Shell**

- Shell也是一门编程语言（解释型的编程语言），**即shell脚本（就是在用linux的shell命令编程）**

- 一个系统可以存在多个shell，可以通过 `cat /etc/shells` 命令查看系统中安装的shell，不同的shell可能支持的命令语法是不同的

- `[root@localhost /]# cat /etc/shells `

1. 代码写在普通文本文件中，通常以.sh为后缀名 

- ```bash
  vi hello.sh
  ```

```bash
#!/bin/bash ##表示用哪一种shell解析器来解析执行这个脚本 
echo "hello word" ##注释也可以写在这里 
##这是一行注释 
```

2. 执行脚本： ` sh hello.sh`

- 或者给脚本添加x权限，直接执行：`./hello.sh `

![](images\执行脚本.png)

### 6.2 变量 

1. 变量=值（例如A=5） 

- **<font color=red>注意：等号两侧不能有空格</font>** 

- 变量名一般习惯为大写

- 使用变量：$A 

2. 定义变量 

```bash
A=1 
```

2. 查看变量 

```bash
echo $A 
```

3. 查看当前进程中所有变量 

```bash
set 
```

4. 撤销变量

```bash
unset A 
```

5. 声明静态变量，不能unset 

```bash
readonly B=2 
```

**注意：变量中的值没有类型，全部为字符串** 

![](images\变量的使用.png)

**作业**：变量a=hello,以下选项哪个可以输出hello luban 

A、echo $a+"luban" 

B、echo a+luban 

C、echo $aluban 

D、echo $a"luban"E、echo ${a}luban 

E、echo ${a}luban 

### 6.3 算数运算 

1. 用expr 

```bash
expr $A + $B 
```

2. 赋值

```bash
C=`expr $A + $B`  
```

**<font color=red>注意中间空格</font>**

2. 用 (()) 

```bash
((1+2)) 
```

3. 赋值

```bash
A=$((1+2)) 
```

4. 自增

```bash
count=1 
((count++)) 
echo $count 
```

5. 用$[] 

```bash
a=$[1+2] 
echo $a 
```

6. 用let 

```bash
i=1 
let i++ 
let i=i+2 
```

![](images\算术运算.png)

### 6.4 扫描器 

- read 

- read parm 

- read -p "提升信息："parm 

![](images\输入符.png)

### 6.5 流程控制 

1. 语法 

```bash
if 条件 

then

执行代码 

elif 条件 

then

执行代码 

else 

执行代码 

fi 
```

2. 示例 **作业：下去把该示例敲一遍** 

```bash
#!/bin/bash
read -p "please input your name:" NAME
if [ $NAME = root ]
        then
                echo "Hello, ${NAME},welcome!"
        elif [ $NAME = luban ]
        then
                echo "Hello,${NAME},welcome!"
        else
                echo "SB,get out here!"
fi
```

**<font color=red>注意：在shell中，上一句错误不影响执行下一句</font>**

作业：以下脚本执行结果： 

```bash
#!/bin/bash 
lss 
echo "hello" 
```

### 6.6 常用判断运算符 

1. 字符串比较： 

- ```
  = 字符串是否相等
  != 字符串是否不相等
  -z 字符串长度为0返回true
  -n 字符串长度不为0返回true
  if[ 'aa' = 'bb' ];then echo "ok";else echo "not ok";fi
  if[ -n "aa" ];;then echo "ok";else echo "not ok";fi
  if[ -z "" ];;then echo "ok";else echo "not ok";fi
  ```

2. 整数比较： 

- ```
  -lt 小于
  -le 小于等于
  -eq 等于
  -gt 大于
  -ge 大于等于
  -ne 不等于
  还可以用转义的数学符号 \<
  ```

3. 文件判断： 

-d 是否为目录 

```bash
if [ -d /bin ];then echo ok;else echo notok;fi
```

-f 是否为文件 

```bash
if [ -f /bin/ls ];then echo ok;else echo notok;fi
```

### 6.7 循环控制 

- 语法：

  ```bash
  while 表达式 
  do 
  command 
  ... 
  done 
  ```

- 例如： 

```bash
i=1
while((i<3))
do
 echo $i
 let i++
done
```

### 6.8 case语句 

```bash
case $i in
start)
 echo "starting"
 ;;
stop)
 echo "stoping"
 ;;
*)
 echo "Usage:{start|stop}"
esac
```


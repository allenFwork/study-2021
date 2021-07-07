## 一. Git基本概念与核心命令掌握

### 1. Git和SVN主要区别：

1. 存储方式不一样
2. 使用方式不一样
3. 管理模式不一样

### 2 存储方式的区别

Git把内容按元数据方式存储，类似k/v数据库，而SVN是按文件(新版本svn已改成元数据存储)



```git
cd .git/objects/df/
git cat-file -p 123214132432159058408345789dsgfhjd
echo 'version1' > text.txt
git hash-object -w text.txt
```



## 二. Git核心命令使用

### 1. 创建本地仓库

![](images\images-git\git创建项目1.png)

### 2. 缓存区处理

![](images\images-git\git创建文件.png)

- 创建文件

![](images\images-git\git提交文件到缓存区并回退.png)

- 添加到缓存区，并回退缓存区的修改
- 添加多个内容到缓存区：git add -A

### 3. 本地仓库处理

![](images\images-git\git提交到本地仓库.png)

- 将修改的文件提交到本地仓库

### 4. 远程仓库同步

![](images\images-git\给远程仓库创建master分支.png)

- 将本地仓库的当前分支推送到远程仓库，并将远程仓库创建该分支为master

### 5. 分支管理

#### 5.1 查看分支

```bash
# 查看当前分支
git branch [-avv]
```

#### 5.2 创建分支

```bash
# 基于当前分支创建新分支
git branch <branch name>
# 基于选择的分支创建新分支
git branch <new branch name> <selected branch name>
# 基于提交新建分支
git branch <branch name> <commit id>
```

#### 5.3 删除分支

```bash
# 删除分支
git branch -d <branch name>
```

#### 5.4 分支切换

```bash
# 切换分支
git checkout <branch name>
# 合并分支
git merge <merge target>
```

#### 5.5 实例：

![](images\images-git\git分支管理1.png)

- 创建 project-study-2021-dev分支 和 删除 project-study-2021-dev2分支

### 6. 远程仓库管理

```bash
# 查看远程配置
git remote [-v]
# 添加远程仓库地址
git remote add origin git@github.com:allenFwork/study-2021.git
# 删除远程分支
git remote remove origin
# 上传新分支至远程
git push --set-upstream origin master
# 将本地分支与远程建立关联
git branch --track -set-upstream-to=origin/test test
```

### 7. tag管理

```bash
# 查看当前
git tag
# 创建分支
git tag <tag name> <branch>
# 删除分支
git tag -d <tag name>
```

### 8. 日志管理

```bash
# 查看当前分支下所有提交日志
git log
# 查看当前分支下所有提交日志
git log {branch}
# 单行显示日志
git log --online
# 比较两个版本的区别(master分支和master2分支比较)
git log master..master2

# 以图标的方式显示提交合并网络
git log --graph
```

![](images\images-git\git日志.png)





## 三. Git底层原理

- Git存储对像

- Git树对像

- Git提交对像

- Git引用

### 1. Git存储对象(hashMap)

Git 是一个内容寻址文件系统，其核心部分是一个简单的键值对数据库(key-value data store)，你可以向数据库中插入任意内容，它会返回一个用于取回该值的hash值。

```bash
# git 键值库中插入数据
$ echo 'superman is a good man' | git hash-object -w --stdin
15af224994968d258045098e60c2696fce6fb5ee

# 基于键获取指定内容
git cat-file -p 15af224994968d258045098e60c2696fce6fb5ee
```

Git 基于该功能 把每个文件的版本中内容都保存在数据库中，当要进行版本回滚的时候就通过其中一个键将其取回并替代。

1.模拟演示 git 版写入与回滚的过程

```bash
# 查询 objects文件夹下所有的文件，查找所有的git对象
$ find .git/objects/ -type f
```



## 四. git私服搭建

- github：最大的同性交友平台，开源的源码存储服务

- gitlab：web项目，提供了源码管理的功能

- 码云：oschina 提供的一个商业化的源码服务

- 码市：coding 提供一个商业化的源码服务

### 1. Git服务器搭建方式

#### git支持的四种通信协议

1. Local本地协议
2. ssh
3. http(Dumb、Smart)
4. git

1.1 Local本地协议

基于本地文件系统或共享（NFS）文件系统进行访问

**优点：**简单，直接使用了现有的文件权限和网络访问权限，小团队小项目建立一个这样的版本管理系统是非常轻松的一件事。

**缺点：**这种协议缺陷就是本身共享文件系统的局限，只能在局域网，而且速度也慢

**使用场景：**小团队，小项目临时搭建版本服务

演示本地协议使用方式：

```bash
# 从本地 f/git/atas 目录克隆项目
git clone /f/git/atals
# 即使是 bare仓库也可以正常下载
git init --bare 项目名
# 基于file协议克隆本地项目
git clone file:///f/git/atals/
```

#### 1.2 ssh协议

git支持利用ssh协议进行通信

**优点：**首先SSH架构相对简单、其次通过SSH访问最安全的、另外SSH协议很高效，在传输钱也会尽量压缩数据。

**缺点：**权限体系不灵活，必须提供操作系统的账户密码，哪怕是只需要读取版本

**使用场景：**小团队、小项目、临时项目

**演示基于SSH协议：**

1. 在 linux 安装 git 服务

   - 安装依赖环境

     ```bash
     yum -y install curl-devel expat-devel gettext-devel openssl-devel zlib-devel gcc perl-ExtUtils-MakeMaker
     ```

   - 下载并解压源码

     ```bash
     $ wget https://github.com/git/git/archive/v2.3.0.zip
     $ unzip v2.3.0.zip
     ```

   - 编译 安装 

     ```bash
     make prefix=/root/local/git all
     make prefix=/root/local/git install
     ```

   - 添加环境变量

     ```bash
     vim /etc/profile
     export PATH=/root/svr/git/bin:$PATH
     source /etc/profile
     ```

   - 测试是否安装成功

     ```bash
     git --version
     git version 2.3.0
     ```

2. 创建项目

   ![](\images\images-git\linux上创建裸项目.png)

3. 本地克隆项目

   ![](\images\images-git\从linux上git服务器上克隆项目.png)

4. 查看本地的公钥私钥

   ![](\images\images-git\git查看本地公钥私钥.png)

4. linux上配置.ssh目录

   ![](\images\images-git\linux上.ssh目录查看.png)

   ![](\images\images-git\linux生成公钥密钥是创建.ssh目录.png)

5. 复制本地的公钥内容，添加到 linux 服务器上的 git 服务中

   ![](\images\images-git\linux服务器上创建文件存放本地仓库的公钥.png)

   ![](\images\images-git\粘贴公钥.png)

   - cd ~/.ssh/
   - **<font color=red>vi authorized_keys</font>**  复制公钥内容，添加到 authorized_keys 文件中

6. 测试公钥是否生效

   ![](\images\images-git\本地git仓库测试公钥是否生效.png)

- 出现的问题：

  1.由于linux上安装的git不是默认的目录，本地克隆是可能出现的问题：

  ​     bash: git-upload-pack: command not found
  ​     fatal: The remote end hung up unexpectedly 

#### 1.3 http协议

Git http协议实现是依赖 WEB 容器 (apache、nginx)及cgi组件进行通信交互，并利用 WEB 容器本身权限体系进行授权验证。在 Git 1.6.6 前，只支持 http Dumb 协议，该协议只能下载不能提交，通常会配合ssh协议一起使用，ssh分配提交账号，http dumb提供只读账号。1.6.6 之后，git提供了 git-http-backend 的 CGI 用于实现接受远程推送等功能。

**优点：**解决了local与ssh权限验证单一的问题、可基于 http url 提供匿名服务，从而可以放到公网上去。而 local 与 ssh 是很难做到这一点，必须实现一个类似 github 这样的网站。

**缺点：**架设复杂一些，需要部署 WEB 服务器，和 https 证书之类的配置

**场景：**大型团队、需要对权限精准控制，需要把服务部署到公网上去

**演示 http Dumb 配置与使用：**

1. 创建服务器端版本仓库

   ```bash
   cd /data/git-repository
   git --bare init
   ```

2. nginx 静态访问配置

   - 安装nginx

     3

   - 配置 nginx.conf

     ```
     user root;
     worker_processes  1;
     
     events {
         worker_connections  1024;
     }
     
     http {
         include       mime.types;
         default_type  application/octet-stream;
     
         log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                           '$status $body_bytes_sent "$http_referer" '
                           '"$http_user_agent" "$http_x_forwarded_for"';
     
         access_log  logs/access.log  main;
         sendfile        on;
         keepalive_timeout  65;
     
         server {
             listen       80;
             server_name  git.sw.com;
     
             location / {
                 root   /opt/data/git-repository/;
             }
         }
     }
     ```

     ![](\images\images-git\配置nginx.conf.png)

3. 配置本地 C:\Windows\System32\drivers\etc\host

   ![](\images\images-git\配置host.png)

4. 本机调用测试

   <img src="\images\images-git\测试nginx.png" style="zoom: 80%;" />

   - 调用不成功原因是，linux上搭建的git是仓库，不是一个web服务，无法通过浏览器请求

5. 通过本地仓库调用测试

   ![](\images\images-git\git测试http协议.png)

   - 返回 not found ，原因是远程仓库上没有配置好 **钩子 hooks**

6. 配置远程仓库的钩子hooks，重命名钩子

   ![](\images\images-git\远程git仓库配置hooks.png)

   - **<font color=red>git update-server-info</font>**，执行完此命令，本地git就可以克隆访问了
   - ![](\images\images-git\git测试http协议2.png)

#### 1.4 git协议

**演示git协议的使用：**

```bash
cd study-project.git
# 创建一个空文件，表示开放该项目
touch git-daemon-export-ok
# 启动守护进程
$nohub git --reuseaddr --base-path=/opt/data/git-repository/ /opt/data/git-repository/ &
```



### 2. 基于gogs快速搭建企业私有Git服务

#### 2.1 gogs 介绍

Gogs 是一款开源的轻量级 Git web服务，其特点是简单易用、国际化做的相当不错。

其主要功能如下：

1. 提供 http 与 ssh 两种协议访问源码服务
2. 提供可WEB界面查看修改源码代码
3. 提供较完善的权限管理功能，其中包括组织、团队、个人等仓库权限
4. 提供简单的项目 viki 功能
5. 提供工单管理与里程碑管理

#### 2.2 gogs 安装

官网：https://gogs.io

下载：https://gogs.io/docs/installation 选择 linux amd64

文档：https://gogs.io/docs/installation/install_form_binary

安装步骤：

1. 安装 uzip

   yum install -y unzip zip

2. 解压

   unzip gogs_0.12.0_linux_amd64.zip

   ![](\images\images-git\gogs解压后安装包.png)

3. 运行

   ```bash
   # 前台运行
   ./gogs web
   # 后台运行
   $nohup ./gogs web &
   ```

   ![](\images\images-git\启动gogs.png)

4. 默认端口：3000

   初次访问 http://<host>:3000 会进到初始化页，进行引导配置

   可选择 mysql 或 sqlite 等数据

5. 首次登录gogs配置

   ![](\images\images-git\gogs首次安装1.png)

   ![gogs首次安装1](\images\images-git\gogs首次安装2.png)

6. 创建仓库

   单位·

7. 本地 git 连接仓库，并提交

   ![](\images\images-git\gogs创建仓库并提交1.png)

   ![](\images\images-git\gogs创建仓库并提交2.png)

8. 查看gogs服务器

   ![](\images\images-git\gogs服务器上查看创建的master分支和提交记录.png)

#### 2.3 gogs基本配置

##### 邮件设置

设置文件：{gogs_home}/custom/conf/app.ini

- ![](\images\images-git\gogs基本配置文件路径.png)

- D

- ```vbscript
  ENABLED=true
  HOST=smtp.qq.com:465
  FROM=allen<1042160867@qq.com>
  USER=1042160867@qq.com
  PASSWD=
  ```

- ENABLED=true 表示启用邮件服务

- HOST 为 smtp 服务器地址，（需要对应邮箱开通smtp服务 且必须为 ssl 的形式访问）

- FROM 发送人名称地址

- USER  发送账号

- PASSWD 开通smtp账号时，会有对应的授权码

重启后可直接测试

管理员登陆 ==》 控制面板 ==》 应用配置管理 ==》 邮件设置 ==》 发送测试邮件

#### 2.4 gogs定时备份和恢复

```bash
# 查看备份相关参数
./gogs backup -h
# 默认备份，备份在当前目录
./gogs backup
# 参数化备份: --target 输出目录  --database-only 只备份db
./gogs backup --target=./backupes --database-only --exclude-repo
# 恢复
./gogs restore --from=gogs-backup-202106150873.zip
```

#### 2.5 gogs定时备份与恢复

1. 自动备份脚本

   ```bash
   #！/bin/sh -e
   gogs_home="/opt/apps/gogs/"
   backup_dir="$gogs_home/backups"
   
   cd `dirname $0`
   # 执行备份命令
   ./gogs backup --target=$backup_dir
   
   echo 'backup success'
   day=7
   # 查找并删除 7 天谴的备份
   find $backup_dir -name '*.zip' -mtime +7 -type f | xargs rm -f;
   echo 'delete expire back data！'
   ```

2. 添加定时任务，每天执行备份

   ```bash
   # 打开任务编辑器
   crontab -e
   # 输入如下命令 每天凌晨4点执行 do-backup.sh 并输出日志到 bakcup.log
   00 04 * * * /opt/apps/gogs/do-backup.sh >> /opt/apps/gogs/backup.log 2>&1
   ```

   



## 五. 知识点补充

### git基本命令

```bash
# 远程仓库克隆大到本地
git clone

# 关联远程仓库
git remote add

# 拉取远程仓库内容
git pull

# 查看当前分支 -a查看所有分支 -av查看所有分支的信息 -avv 查看所有分支的信息和关系
# 查看当前分支
git branch
# 查看所有分支
git branch -a
#查看所有分支的详细信息
git branch -av

# 创建一个分支 基于当前分支
git branch xxx

# 基于oldType创建分支
git branch newBranch oldType

# 删除分支
git branch -d xxx

# 查看文件内容
git cat-file-p commitid

# 查看对象类型 blob commit tree
git cat-file -t commitid
```



### github

#### 1. 公钥 私钥

- githup创建了仓库后，需要公钥私钥 来允许 本地上传数据到仓库里

- 生成公钥私钥：

  `$ ssh-keygen -t rsa -C "email"`  // public key for push

- ![生成成功图片](images\git生成SSH公钥私钥.png)

- 

#### 2. 连接远程仓库

- `git remote add 远程仓库的别名 远程仓库的路径地址`

- 实例：

  `git remote add study-2021 git@github.com:allenFwork/study-2021.git`

#### 3. 将本地仓库同步到远程仓库

- `git push -u reomoteBranch localBranch`

- 实例：

  `$ git push -u origin main`

  远程仓库别名 origin ，本地仓库为 main 分支





### 分支

查看分支

```bash

```

创建分支

```bash
# 基于当前分支创建新分支 newBranchName
git branch newBranchName

# 基于某个分支创建新分支 (基于oldBranchName分支创建了新分支newBranchName)
git branch newBranchName oldBranchName

# 基于远程分支创建新分支: 先切换到远程分支，再基于当前分支创建新分支
git checkout origin(远程分支别名)
git branch newBranchName
```

切换分支

```
git checkout branchName
```



选择



### git原理

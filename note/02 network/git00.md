# 工程化专题之Git

## 1. 认识git

3W who why when

### 1.1 SVN架构

  ![](images\images-git\01.png)

- version1中有 A B C三个文件（初始版本的文件）
- <font color=red>**version2中有对A进行修改，存储修改的操作，B和C没有变，不存储**</font>

### 2. git架构

![02](images\images-git\02.png)

- version1中有 A B C三个文件（初始版本的文件）
- <font color=red>**version2中有对A进行修改，存储修改后的文件A1，B和C没有变，存储指向B和C的链接**</font>

### 3. git 和 svn 的区别

- svn是集中式的，git是分布式
- svn每次存储的是变化，git每次存储的是变化后的文件



## 2. 问题

### 2.1 push/pull 需要联网吗？

- 不一定，可以本地使用

### 2.2 如果server硬盘坏了 怎么办？

- 服务器集群备份

### 2.3 git 保证完整性

- SH1算法加密保证安全和完整性，commit的Id
- 如果想要进行版本管理，使用 tag



## 3. 安装

### 3.1 下载地址：

1. https://git-scm.com/book/en/v2/Getting-Started-Installing-Git
2. http://windows.github.com
3. http://mac.github.com
4.  http://git-scm.com/download/linux 

### 3.2 配置git config

1. **git config –-global user.name ‘xx’**

2. **git config –-global user.email ‘xx’**

3. githup创建了仓库后，需要公钥私钥 来允许 本地上传数据到仓库里

- 命令：**ssh-keygen -t rsa -C '1042160867@qq.com'**
- ![生成成功图片](images\git生成SSH公钥私钥.png)



### 3.3 git 多账户切换

1. 在 git的安装目录下新建一个文件conf，没有后缀名

   <img src="images\images-git\03.png" style="zoom:50%;" />

   <img src="images\images-git\04.png" alt="04" style="zoom:50%;" />

2. 

ssh-keygen -t rsa -C 'james@gupaoedu.com



## 4. git 常用命令

1. git status  

- 没事 status一下，查看需要做的操作

2. git remote

- git clone [git@xxx.com](mailto:git@xxx.com)

- 把本地项目推送到远端
  - git init
  - git remote add origin git@git.oschina.net:gupaoedu_com_vip/test10.git
  - git push -u origin master

3. 将当前目录下所有添加的步骤添加到 Stagging Area 

   **命令：<font color=red>git add .</font>**

4. git pull 

5. git push

6. git checkout 

- 切新分支
- 撤消更改

6. git merge

7. git rebase 

8. git tag 版本



## 知识点

1. 翻墙工具：vpnso (2017年 100元1年，支持家庭和公司使用)


#### git工作模式





#### git初始化





#### git基本命令









#### github

##### 1. 公钥 私钥

- githup创建了仓库后，需要公钥私钥 来允许 本地上传数据到仓库里

- 生成公钥私钥：

  `$ ssh-keygen -t rsa -C "email"`  // public key for push

- ![生成成功图片](images\git生成SSH公钥私钥.png)

- 

##### 2. 连接远程仓库

- `git remote add 远程仓库的别名 远程仓库的路径地址`

- 实例：

  `git remote add study-2021 git@github.com:allenFwork/study-2021.git`

##### 3. 将本地仓库同步到远程仓库

- `git push -u reomoteBranch localBranch`

- 实例：

  `$ git push -u origin main`

  远程仓库为 origin 分支，本地仓库为 main 分支



#### 分支

查看分支

创建分支





#### git原理






#### git工作模式





#### git初始化





#### git基本命令

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

  远程仓库别名 origin ，本地仓库为 main 分支



#### 分支

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



#### git原理




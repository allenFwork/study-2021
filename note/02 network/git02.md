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




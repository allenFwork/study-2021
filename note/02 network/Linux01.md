# 1.虚拟机网路配置

![](images\虚拟机NAT模式.png)

1. 192.168.33.2是windows的真实地址，在windows系统中配置该地址对应的网关地址 192.168.33.1

2. 虚拟机配置了虚拟网卡，地址为 192.168.33.1;调用windows的192.168.33.2时，会通过虚拟机的网关找到Linux系统的配置的对应IP地址，从而调用虚拟机中该地址对应的系统

3. Linux系统配置一个IP地址 对应到虚拟机网关 19.2.168.33.1，从而可以使用虚拟机网关调用该系统

## 1.1 配置虚拟机的网关设置

![](\images\虚拟机网络配置-NET模式1.png)

![](\images\虚拟机网络配置-NET模式2.png)

![](\images\虚拟机网络配置-NET模式.png)

- 虚拟机点击 编辑  -->  虚拟网络编辑器  -->  选择对应的模式  -->  Net设置  --> 编辑网关地址

## 1.2  修改网络配置文件 

![](\images\Centos服务器设置ip和网关地址.png)

- 设置 对应服务器中的配置信息
- 配置文件地址  `` /etc/sysconfig/network-scripts/ifcfg-ens33 ``

##  1.3 重启网络服务 

- 执行 ``service network restart `` 命令



# 2. 配置windows连接的网关

## 2.1 配置windows到虚拟机网关的链接

![](\images\windows配置连接网关1.png)

![](\images\windows配置连接网关2.png)

- **VMware Network Adapter VMnet1 和 VMware Network Adapter VMnet8 就是虚拟机设置的虚拟网卡**
- 因为虚拟机中使用使用 VMnet8 进行配置网关，所以windows中也选择它配置网关

![](\images\windows配置连接网关3.png)

## 2.2 检查连接状况

![](\images\windows核查.png)

# 3.配置Linux连接的网关

![](images\Linux系统网关配置.png)

![Linux系统网关配置2](images\Linux系统网关配置2.png)

- **<font color=red>在 /etc/sysconfig/network-scripts/ifcfg-ens33 文件中修改网络ip设置</font>**
- 配置服务器的IP地址：IPADDR=192.168.33.3
- 配置子网掩码：NETMASK=192.168.33.1
- 配置网关地址：GATEWAY=192.168.33.1
- **DNS需要配置，如果不配置，Linux只能链接内部网络，不能连接外部网络**

![](images\Linux系统网关配置3.png)

![Linux系统网关配置4](images\Linux系统网关配置4.png)

- 配置好ip地址，需要重启服务，命令：`service network restart`

![](images\Linux系统网关配置5.png)

- 服务器连接网络

![](images\windows测试是否能够联通Linux.png)



<img src="images\Linux连接外网测试.png"/>

- Linux 连接外网测试

# 4. 知识点

## 4.1 虚拟机

1. NAT 网络模式：虚拟机给Linux系统虚拟出一个网卡，进行网络连接

2. `SATA` 机械硬盘作为的磁盘

   `NVMe` 固态硬盘作为磁盘

## 4.2 Linux

### 4.2.1 通过 `XShell` 连接 Linux 服务器

1. 连接服务器

![](images\XShell连接.png)

- 配置 连接的主机 点击连接

2. 连接成功

![](images\XShell连接成功.png)

- SSH协议连接到 `192.168.33.3:22` 成功

### 4.2.2 常用命令

1. 进入根目录：`cd /`

2. 查看目录：`ls`
3. 带参数详细查看目录：`ls -l`
4. 简化命令详细查看目录：`ll`

![](images\常用命令1.png)

5. 进入目录：`cd 目录名称`
6. 回到上级目录：`cd ..`
7. 创建文件夹：`mkdir 文件夹名称`
8. 创建多级文件夹：`mkdir -p a/b/c` 直接创建了3级的文件夹 a/b/c

![](images\常用命令2.png)

9. 删除文件/目录：rm -r 目录 可以删除有多个级别的文件夹

![](images\常用命令3.png)

- 使用 `rm`  只能删除一级目录，不能删除多级目录
- 使用 `rm -r a/b/c` 删除多级目录时，需要不断验证
- 使用 `rm -rf a/b/c` 直接删除多级目录，不需要验证

10. 输出到控制台：echo 内容

11. 查看文件内容：cat 文件

![](images\常用命令4.png)

- `ls > first.txt` 将 ls 命令查询出来的结果写入到 first.txt 文件中去
- `cat first.txt`：查看 first.txt 的内容

12. 使用编辑器修改文件：`vi 文件命`

![常用命令5](images\常用命令5.png)

![常用命令6](images\常用命令6.png)

![常用命令7](images\常用命令7.png)

- 使用 `vi 文件名` 进入文件，进行编辑
- 进入文件后，使用 Ins 键 进入编辑状态
- 使用 ESC 推出编辑状态
- 使用 shieft + : 进入命令输入状态，输入 `wq` ，表示保存并推出编辑器

13. 复制文件：`cp 源文件 目标文件`
14. 删除文件：`rm -f 文件`

### 4.2.3 用户权限

1. 添加用户：
   - `useradd 用户名`
   - `passwd 密码`
2. 通过 ls 可以查看目录或文件的权限

![](images\权限1.png)

- first.txt 文件是用 root 账号创建的，从查看的结果：
  - 该文件对于 root账户 具有**可读、可写、不可执行** 的权限
  - 该文件对于 root账户所属组的其余账户 具有**可读、不可写、不可执行** 的权限
  - 该文件对于 上述两种以外的其余账户 具有**可读、不可写、不可执行** 的权限

3. 通过 allen 账户尝试修改 first.txt文件 内容

![](images\权限2.png)

![权限3](images\权限3.png)

- 修改文件时，提示没有权限，该文件为只读文件

- 尝试通过加 ! 强制执行，同样无法修改成功

  ![](images\权限4.png)

4. 修改用户对于文件的权限

   1. 添加用户对文件的修改权限：`chmod +w 文件`
   2. 删除用户对文件的修改权限：`chmod -w 文件`

   ![](images\权限5.png)

### 4.2.4 环境变量

1. **<font color=red>/etc/profile , Linux中环境变量配置在 profile 文件中</font>**

2. 当使用 jdk 时，需要在环境变量中配置 jdk 的 JAVA_HOME 等变量：

![](images\环境变量.png)

- 在 profile 文件中，通过 export 添加变量，中间冒号隔开

### 4.2.5 切换用户

1. 在使用 allen 账户时，权限不够，切换到 root 账户

![](images\权限6.png)

![](images\权限7.png)

- **通过 `su root` 命令，切换到 root 账号，要输入root账户的密码**

- 从root账户切换到普通用户时，使用 `su 用户名` ，不需要使用密码
- **使用 `exit` 命令，退出切换的用户**

2. `su` 切换账号，需要知道账号密码，这不安全，所以使用 `sudo` 命令

![](images\权限8.png)

![](images\权限9.png)

- 使用 `sudo vi /etc/profile ` 命令给 allen 账号赋予编辑 /etc/profile 文件的权限
- 使用该命令时，会有温馨提示，三句话
- 执行该命令过程中，发现 /etc/sudoers 配置文件中没有配置 allen 的 `sudo` 权限，所以执行失败，并且上报此次信息
- 通过root账号，修改 /etc/sudoers 配置文件，给 allen 添加 `sudo` 命令权限
- 使用 `sudo` 命令，只需要输入自己的账号密码

### 4.2.6 查看Linux内核版本

- 指令： 

  ```bash
  uname -r
  ```

- ![](images\linux内核查询.png)
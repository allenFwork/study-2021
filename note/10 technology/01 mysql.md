修改windows注册表，windows+r  在运行对话框输入regedit，点击确定，进入注册表编辑器。选择HKEY_LOCAL_MACHINE ->SYSTEM -> CurrentControlSet -> services ->MySQL，修改ImagePath的路径为"D:\mysql-5.5-winx64\bin\mysqld" MySQL，原来是"D:\mysql-5.0-winx64\bin\mysqld" MySQL。

![](images\windows注册表修改-mysql.png)

"D:\mySystem\MySQL_Server_5.0\bin\mysqld-nt" --defaults-file="D:\mySystem\MySQL_Server_5.0\my.ini" MySQL
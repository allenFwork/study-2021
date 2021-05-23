 首先以root身份登录到MySQL服务器中。

$ mysql -u root -p

当验证提示出现的时候，输入MySQL的root帐号的密码。

创建一个MySQL用户

使用如下命令创建一个用户名和密码分别为"myuser"和"mypassword"的用户。

mysql> CREATE USER 'myuser'@'localhost' IDENTIFIED BY 'mypassword';

一旦用户被创建后，包括加密的密码、权限和资源限制在内的所有帐号细节都会被存储在一个名为user的表中，这个表则存在于mysql这个特殊的数据库里。

运行下列命令，验证帐号是否创建成功

mysql> SELECT host, user, password FROM mysql.user WHERE user='myuser';

赋予MySQL用户权限

一个新建的MySQL用户没有任何访问权限，这就意味着你不能在MySQL数据库中进行任何操作。你得赋予用户必要的权限。以下是一些可用的权限：

ALL: 所有可用的权限
CREATE: 创建库、表和索引
LOCK_TABLES: 锁定表
ALTER: 修改表
DELETE: 删除表
INSERT: 插入表或列
SELECT: 检索表或列的数据
CREATE_VIEW: 创建视图
SHOW_DATABASES: 列出数据库
DROP: 删除库、表和视图

运行以下命令赋予"myuser"用户特定权限。

mysql> GRANT <privileges> ON <database>.<table> TO 'myuser'@'localhost';

以上命令中，<privileges> 代表着用逗号分隔的权限列表。如果你想要将权限赋予任意数据库（或表），那么使用星号（*）来代替数据库（或表）的名字。

例如，为所有数据库/表赋予 CREATE 和 INSERT 权限：

mysql> GRANT CREATE, INSERT ON *.* TO 'myuser'@'localhost';

验证给用户赋予的全权限：

mysql> SHOW GRANTS FOR 'myuser'@'localhost';

将全部的权限赋予所有数据库/表：

mysql> GRANT ALL ON *.* TO 'myuser'@'localhost';

你也可以将用户现有的权限删除。使用以下命令废除"myuser"帐号的现有权限：

mysql> REVOKE <privileges> ON <database>.<table> FROM 'myuser'@'localhost';

为用户添加资源限制

在MySQL中，你可以为单独的用户设置MySQL的资源使用限制。可用的资源限制如下：

MAX_QUERIES_PER_HOUR: 允许的每小时最大请求数量
MAX_UPDATES_PER_HOUR: 允许的每小时最大更新数量
MAX_CONNECTIONS_PER_HOUR: 允许的每小时最大连接（LCTT译注：其与 MySQL全局变量： max_user_connections 共同决定用户到数据库的同时连接数量）数量
MAX_USER_CONNECTIONS: 对服务器的同时连接量

使用以下命令为"myuser"帐号增加一个资源限制：

mysql> GRANT USAGE ON <database>.<table> TO 'myuser'@'localhost' WITH <resource-limits>;

在 <resource-limits> 中你可以指定多个使用空格分隔开的资源限制。

例如，增加 MAXQUERIESPERHOUR 和 MAXCONNECTIONSPERHOUR 资源限制：

mysql> GRANT USAGE ON *.* TO 'myuser'@'localhost' WITH MAX_QUERIES_PER_HOUR 30 MAX_CONNECTIONS_PER_HOUR 6;

验证用户的资源限制：

mysql> SHOW GRANTS FOR 'myuser'@'localhost;

创建和设置一个MySQL用户最后的一个重要步骤：

mysql> FLUSH PRIVILEGES;

如此一来更改便生效了。现在MySQL用户帐号就可以使用了。 
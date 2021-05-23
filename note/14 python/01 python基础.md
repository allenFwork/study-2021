## 一. 基本类型 

### 1. Number

#### 1.1 整形和浮点型

##### 1.1.1 计算：

- /  表示转化为浮点型计算，

- // 表示整除计算

```python
>>> type(2/2)
<class 'float'>
>>> 2/2
1.0
>>> type(2//2)
<class 'int'>
>>> 2//2
1
>>> 2.2//2
1.0
>>> 1//2
0
```

##### 1.1.2 进制：

###### 1.1.2.1 进制的表示

10进制，2进制，8进制，16进制

二进制：0b10

```python
>>> 0b10
2
>>> 0b11
3
```

八进制：0o10

```python
>>> 0o11
9
>>> 0o12
10
```

十六进制：0x1F

```python
>>> 0x1F
31
>>> 0x20
32
```

###### 1.1.2.2 进制转换

转二进制：bin()

```python
>>> bin(10)
'0b1010'
>>> bin(0o10)
'0b1000'
>>> bin(0x1F)
'0b11111'
```

转十进制：int()

```python
>>> int(0b1100)
12
>>> int(0o11)
9
>>> int(0x11)
17
```

转十六进制：hex()

```python
>>> hex(0b11)
'0x3'
>>> hex(0o11)
'0x9'
>>> hex(10)
'0xa'
```

转八进制：oct()

```python
>>> oct(0b11)
'0o3'
>>> oct(0x11)
'0o21'
>>> oct(11)
'0o13'
```

#### 1.2 bool 布尔类型

```python
>>> type(True)
<class 'bool'>
>>> type(False)
<class 'bool'>
>>> int(True)
1
>>> int(False)
0
>>> bool(0)
False
>>> bool(1)
True
>>> bool(1.1)
True
>>> bool(-1.1)
True
>>> bool('abc')
True
>>> bool('')
False
>>> bool([1,2,3])
True
>>> bool([])
False
>>> bool({1,2,3})
True
>>> bool({})
False
>>> bool(None)
False
```

- 非空一般被认为是True
- 空值一般被认为是False

#### 1.3 complex 复数

```python
>>> 36j
36j
```



### 2. 组

#### 2.1 str 字符串

##### 2.1.1 表示方式

**双引号或单引号包裹**

```python
>>> "Hello World!"
'Hello World!'
>>> 'Hello Wrold!'
'Hello Wrold!'
>>> type("Hello World!")
<class 'str'>
>>> "let's go"
"let's go"
```

**使用三个单引号或使用三个双引号**

```python
>>> """
... Hello World!
... This is superman.
... Who are you?
... """
'\nHello World!\nThis is superman.\nWho are you?\n'
>>> '''
... Hello World!
... This is batman.
... Who are you?
... '''
'\nHello World!\nThis is batman.\nWho are you?\n'
```

##### 2.1.2 print()函数特性

```python
>>> "hello world\nhello world"
'hello world\nhello world'
>>> print("hello world\nhello world")
hello world
hello world
```

- 直接输入，\n不表示转移换行，是字符串的一部分，不会换行

##### 2.1.3 技巧

使用单引号+回车键实现输入时字符串换行

```python
>>> "hello\
... world"
'helloworld'
```

##### 2.1.4. 转义字符

###### 2.1.4.1 含义：特殊的字符

- 无法“看见”的字符，例如换行
  - \n 换行
  - \t 横向制表符
  - \r 回车
- 与语言本身语法有冲突的字符

###### 2.1.4.2 使用 \ 转义

###### 2.1.4.3 使用 r或R 取消转义

```python
>>> print("c:\network\firewok")
c:
etworkirewok
>>> print("c:\\network\\firework")
c:\network\firework
>>> print(r"c:\network\framework")
c:\network\framework
```

##### 2.1.5. 字符串api

1.合并两个字符串

```python
>>> "helllo" + "world"
'hellloworld'
>>> "hello" * 3
'hellohellohello'
>>> "hello" * "world"
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
TypeError: can't multiply sequence by non-int of type 'str'
```

2.获取字符串中的指定下标的字符

```python
>>> "hello world"[0]
'h'
>>> "hello world"[1]
'e'
>>> "hello world"[2]
'l'
>>> "hello world"[3]
'l'
>>> "hello world"[4]
'o'
>>> "hello world"[-1]
'd'
>>> "hello world"[-2]
'l'
>>> "hello world"[-3]
'r'
```

3.截取字符串中的指定字符串

```python
# 截取字符串 "hello world" 中的 "hello"
>>> "hello world"[0:4]
'hell'
>>> "hello world"[0:5]
'hello'
>>> "hello world"[6:10]
'worl'
>>> "hello world"[6:11]
'world'
>>> "hello world"[6:20]
'world'
>>> "hello world"[6:-1]
'worl'
>>> "hello world"[6:]
'world'
>>> "hello world"[-5:]
'world'
```

- 截取注意含头，不含尾

- 截至的下标超出了返回，会默认直到最后一位字符

- 通过负数无法截取到最后一位，直接不输入

4. 通过ord查看字符的ascii码值

```python
>>> ord('a')
97
>>> ord('A')
65
>>> ord('abc')
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
TypeError: ord() expected a character, but string of length 3 found
```



#### 2.2 列表

##### 2.2.1 定义

```python
>>> type([1,2,3,4,5,6])
<class 'list'>
>>> type(["hello","world",1,2])
<class 'list'>
>>> type(["hello","world",1,2,True,False])
<class 'list'>
>>> type([1,2,[3,4],[True,False]])
<class 'list'>
```

- 列表中的类型可以不是同一种
- 列表中可以嵌套列表

##### 2.2.2 列表的基本操作

###### 2.2.2.1 获取列表中的元素

```python
>>> ["1号","2号","3号","4号"][2]
'3号'
>>> ["1号","2号","3号","4号"][0:1]
['1号']
>>> ["1号","2号","3号","4号"][0:2]
['1号', '2号']
>>> ["1号","2号","3号","4号"][0:]
['1号', '2号', '3号', '4号']
```

- 通过 [X:X] 获取的结果就是列表，即使只有一个元素

###### 2.2.2.2 列表合并

```python
>>> ["1号","2号","3号","4号"] + ["5号"]
['1号', '2号', '3号', '4号', '5号']
>>> [1,2] * 2
[1, 2, 1, 2]
>>> ["1号","2号"] * 2
['1号', '2号', '1号', '2号']
```

#### 2.3 元组

##### 2.3.1 定义

```python
>>> (1,2,3,4,5)
(1, 2, 3, 4, 5)
>>> (1,'-1',True)
(1, '-1', True)
>>> (1,2,3,4)[0]
1
>>> (1,2,3) * 2
(1, 2, 3, 1, 2, 3)
>>> type((1,2,3))
<class 'tuple'>
```

- 元组类型是 tuple

- ```python
  >>> type((1))
  <class 'int'>
  >>> (1)
  1
  ```

- 默认()只有一个元素，那么()认为是

```python
>>> type(())
<class 'tuple'>
>>> type((1,))
<class 'tuple'>
```

- 定义只有一个元素的元组，需要添加一个逗号

#### 2.4 补充(小知识点)

##### 3.1 in

```python
>>> 3 in [1,2,3,4,5]
True
>>> 3 not in [1,2,3,4,5]
False
```

##### 3.2 函数

1. len()   max()   min()

```python
>>> len([1,2,3,4,5,6])
6
>>> max([1,2,3,4,5,6])
6
>>> min([1,2,3,4,5,6])
1
```

2. ord()：查看字符对应的ascii码值

```python
>>> ord('w')
119
>>> ord('a')
97
>>> ord('0')
48
```



#### 2.5 集合 set

- 集合是无序的，序列是有序的（list，tuple，str都是有序的）

##### 2.5.1 定义

特性：无序、不可重复

```python
>>> type({1,2,3,4,5})
<class 'set'>
>>> {1,2,3,4,5,6}[0]
<stdin>:1: SyntaxWarning: 'set' object is not subscriptable; perhaps you missed a comma?
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
TypeError: 'set' object is not subscriptable
>>> {1,2,3,4,5,1,2,3,4,5}
{1, 2, 3, 4, 5}
>>> len({1,2,3,4,5})
5
>>> 1 in {1,2,3}
True
>>> 1 not in {1,2,3}
False
>>> type(set())
<class 'set'>
>>> type({})
<class 'dict'>
```

- 定义空集合 set()，不能使用{}

##### 2.5.2 集合的 api

##### 2.5.2.1 求两个集合的差集

```python
>>> {1,2,3,4,5,6} - {3,4}
{1, 2, 5, 6}
>>> [1,2,3,4,5,6] - [3,4]
Traceback (most recent call last):
  File "<stdin>", line 1, in <module>
TypeError: unsupported operand type(s) for -: 'list' and 'list'
```

- 序列没有办法求差

##### 2.5.2.2 求两个集合的交集

```python
>>> {1,2,3,4,5} & {3,4}
{3, 4}
>>> {1,2,3,4,5} & {6}
set()
```

##### 2.5.2.3 合并两个集合

```python
>>> {1,2,3,4,5,6} | {3,6,7}
{1, 2, 3, 4, 5, 6, 7}
```

#### 2.6 字典 dict

##### 2.6.1 定义

```python
>>> {1:1,2:2,3:3}
{1: 1, 2: 2, 3: 3}
>>> {"Q":"新月打击","W":"苍白之瀑"}
{'Q': '新月打击', 'W': '苍白之瀑'}
>>> type({1:1,2:2,3:3})
<class 'dict'>
```

##### 2.6.2 api

```python
>>> {"Q":"新月打击","W":"苍白之瀑"}
{'Q': '新月打击', 'W': '苍白之瀑'}
>>> {"Q":"新月打击","W":"苍白之瀑"}["Q"]
'新月打击'
>>> {"Q":"新月打击","W":"苍白之瀑","W":"月之降临"}["Q"]
'新月打击'
>>> {"Q":"新月打击","W":"苍白之瀑","W":"月之降临"}["W"]
'月之降临'
>>> {"Q":"新月打击","W":"苍白之瀑","W":"月之降临"}
{'Q': '新月打击', 'W': '月之降临'}
```

##### 2.6.3 知识点：

###### 2.6.3.1 key和value

**key的值必须是不可变的类型**

value的值可以是所有的数据类型

- int、str 是不可变的类型

###### 2.6.3.2 定义空的字典

```python
>>> type({})
<class 'dict'>
```

### 3. 基本数据类型总结

![](images\python基本类型.png)



## 二. 变量与运算符

### 1. 变量

#### 1.1 命名规则

- 变量名首字母不能是数字
- 变量名只能由 字母、数字、下划线组成
- 系统的关键字，不能使用在变量名中，保留关键字，例如 and 就不能作为变量名
- 变量名是区分大小写的

**补充：类似type这些字不是系统的关键字，但是最好不要作为变量来使用，因为将type作为变量使用后，截下来如果需要使用type作为方法来调用，那么就会失败**

#### 1.2 python中的变量是没有类型的

- 动态类型变量

- ```python
  >>> a = 1
  >>> a
  1
  >>> a = '1'
  >>> a
  '1'
  ```

- ```python
  >>> a = 1
  >>> b = a
  >>> a = 3
  >>> b
  1
  ```

- 变量b的值依旧是1，没有变成3

- ```python
  >>> a = [1,2,3,4,5]
  >>> b = a
  >>> b
  [1, 2, 3, 4, 5]
  >>> a[0] = '1'
  >>> b
  ['1', 2, 3, 4, 5]
  ```

- 与上面的例子相比，b发生了变化

#### 1.3 变量使用过程中出现的问题

##### 1.3.1 问题1

a = 1  将变量a指向数字1

b = a 将变量b执行数字1

a = 3 将变量a执行数字3

##### 1.3.2 问题2

a = [1,2,3] 将变量a指向列表[1,2,3]

b = a          将变量b指向列表[1,2,3] ，就是a指向的那个列表

a[0] = '1'    将a指向的列表进行修改

##### 1.3.3 总结

1. 值类型 和 引用类型

- **int str tuple 不可改变，值类型**

- list set dict   可变，引用类型
- **int 是值类型，list是引用类型**

2. id()显示变量在内存中的地址

- ```python
  >>> b = "hello"
  >>> id(b)
  2582182269360
  >>> b = b + " python"
  >>> id(b)
  2582182269552
  ```

3. list 与 tuple 的区别

- ```python
  >>> a = (1,2,3)
  >>> a[0] = '1'
  Traceback (most recent call last):
    File "<stdin>", line 1, in <module>
  TypeError: 'tuple' object does not support item assignment
  >>> a = (1,2,3)
  >>> a.append(4)
  Traceback (most recent call last):
    File "<stdin>", line 1, in <module>
  AttributeError: 'tuple' object has no attribute 'append'
  >>> b = [1,2,3,4]
  >>> b.append(5)
  >>> b
  [1, 2, 3, 4, 5]
  ```

- **tuple是不可变的，既不支持修改，也不支持追加元素**

- **list是可变的，既可以修改元素的值，也可以追加元素**

- ```python
  >>> a = (1,2,3,[4,5,6])
  >>> a[3][1]
  5
  >>> a
  (1, 2, 3, [4, 5, 6])
  >>> a[3].append(7)
  >>> a
  (1, 2, 3, [4, 5, 6, 7])
  ```

### 2. 运算符

#### 2.1 算术运算符

- +

- -

- *

- /

- 冥次方:

  ```python
  # 2的平方 : 2**2
  # 2的3次方: 2**3
  # 2的4次方: 2**4
  >>> 2**2
  4
  >>> 2**3
  8
  >>> 2**4
  16
  ```

#### 2.2 赋值运算符

**为了给变量赋值**

- =   : 赋值

- += : 先做加法运算,再做赋值

- -=  : 先做减法运算,再做赋值

- /=  : 先做除法运算,再做赋值

- %=: 先做取模运算,再做赋值

- ```python
  >>> b = 2
  >>> a = 3
  >>> b = b + a
  >>> print(b)
  5
  >>> b -= a
  >>> print(b)
  2
  >>> b*=a
  >>> print(b)
  6
  ```

#### 2.3 关系运算符(比较运算符)

**比较之后返回bool类型**

- ==
- !=
- `>`
- `<`
- `>=`
- `<=`

**实例：**

```python
>>> b = 1
>>> b += b > 1
>>> print(b)
1
>>> b = 1
>>> b += b >= 1
>>> print(b)
2
>>> int(True)
1
>>> int(False)
0
```

- **先进行了 b >= 1 判断，返回 True**
- 接着处理 b += True，等价于 b = 1 + True，等价于 b = 1 + 1
- 所以打印出b的值是2

#### 2.4 逻辑运算符

**主要用于操作布尔类型，返回类型也是bool类型**

- and    且（与）
- or       或
- not     非

**实例：**

```python
>>> True and True
True
>>> True and False
False
>>> True or False
True
>>> False or False
False
>>> not True
False
>>> not False
True
```

**演化：**

```python
>>> 1 and 1
1
>>> 'a' and 'b'
'b'
>>> 'a' or 'b'
'a'
>>> not 'a'
False
```

- not 'a'  等价于 not true

```python
>>> not 0
True
>>> not 1
False
>>> not 0.1
False
>>> not 0.0
True
```

- 对于整数和浮点数，0被认为是False，非0被认为是True

```python
>>> not 'a'
False
>>> not ''
True
>>> not '0'
False
```

- 对于字符串类型，空字符串是False，其余的都被认为是True

```python
>>> not []
True
>>> not [1]
False
```

- 对于列表类型，空的列表被认为是False，非空的列表被认为是True
- tuple、set 和 dict 拥有与列表类型一样的性质

```python
>>> 0 and 1
0
>>> 'a' and 'b'
'b'
>>> 'b' and 'a'
'a'
>>> 1 and 2
2
>>> 2 and 1
1
>>> [] and [1]
[]
>>> [1] and []
[]
>>> [] or [1]
[1]
>>> 1 or 2
1
>>> 2 or 1
2
```

- and 计算机会将两边的内容都进行读取判断
- or    计算机也是从左向右开始读取，如果第一个为True，既不用向后读取了

#### 2.5 成员运算符

**用来判断元素是否在另一个一组元素里，返回是布尔类型**

- in         判断在
- not in  判断不在

**实例：**

```python
>>> a in [1,2,3,4,5]
True
>>> b = 6
>>> b in [1,2,3,4,5]
False
>>> b not in [1,2,3,4,5]
True
>>> c = 'h'
>>> c in 'hello'
True
>>> d = 'value'
>>> d in {'key':'value'}
False
>>> d = 'key'
>>> d in {'key':'value'}
True
```

- 成员运算符对列表、集合、元组、字符串都起作用
- 成员运算符对字典的的key起作用

#### 2.6 身份运算符

**返回结果是布尔类型**

- is          
- is not   

**实例：**

```python
>>> a = 1
>>> b = 2
>>> a is b
False
>>> a = 1
>>> b = 1.0
>>> a is b
False
>>> a == b
True
```

**演化：**

```python
>>> a = [1,2,3]
>>> b = [2,1,3]
>>> a == b
False
>>> a is b
False
>>> a = {1,2,3}
>>> b = {2,1,3}
>>> a == b
True
>>> a is b
False
>>> a = (1,2,3)
>>> b = (2,1,3)
>>> a == b
False
>>> a is b
False
```

- 集合是无序的，所以只要内部元素一致，== 判断就是True，但是 is 不一定，因为存储的内存地址不一样
- 元组属于序列，是有序的，所以即使内部元素一致，但位置不一致， == 判断是False，is判断也是False

```python
>>> c = (1,2,3)
>>> d = (1,2,3)
>>> c == d
True
>>> c is d
False
>>> id(c)
2406909062272
>>> id(d)
2406909484864
```

**补充：类型判断**

```python
>>> a = "hello"
>>> type(a) == str
True
>>> isinstance(a,str)
True
>>> isinstance(a,(int,str,float))
True
>>> isinstance(a,(int,float))
False
```

- isinstance(a,str)：判断a是否是str类型
- isinstance(a,(int,str,float))：判断a是否是int,str,float中一种类型

**对象的三个特征：id、value、type**

- 三个特征的判断：is、==、isinstance

#### 2.7 位运算符

**把数字当作二进制数进行运算**

- &    ：按位与
- |     ：按位或
- ^     ：按位异或
- ~     ：按位取反
- <<   ：左移动
- `>>`  ：右移动

**实例：**

```python
>>> 1 & 2
0
>>> 2 & 3
2
>>> 1 & 7
1
```

- 先将数值转换为二进制数，再进行位运算

### 3.  表达式

1. 什么是表达式

表达式(Expression)是运算符(operator)和操作数(operand)所构成的序列

2. 表达式实例

```python
>>> 1 + 1 + 1 + 1
4
>>> 1 + 2 * 3
7
>>> 1 * 2 + 3
5
>>> a = 1 + 2
>>> a =1
>>> b = 2
>>> c = a and b or c
>>> print(c)
2
```

- 上述都是表达式，即使是 a = 1 + 2

3. 思考题

```python
>>> a = 1
>>> b = 2
>>> c = 3
>>> print(a + b * c)
7
>>> print(a or b and c)
1
```

- **a or b and c 的运算顺序，先进行 b and c 的且运算，再进行 or 运算**
- **<font color=red>逻辑运算符的优先级，and的优先级高于or</font>**
- 表达式从左向右依次执行，也被称为**左结合**

```python
>>> a = 1
>>> b = 3
>>> c = a or b
>>> print(c)
1
```

- 

```python
>>> a = 1
>>> b = 2
>>> c = 2
>>> not a or b + 2 == c
False
```

- **优先级：算术运算符 > 比较运算符 > 逻辑运算符**
- **逻辑运算符优先级：not > and > or**
- not a or b + 2 == c 等价于 (not a) or ((b + 2)  ==  c)

### 4. 第一个python程序

1. 编写pyton文件

- 在安装 [Python](http://c.biancheng.net/python/) 后，会自动安装一个 IDLE，它是一个 Python Shell (可以在打开的 IDLE 窗口的标题栏上看到），程序开发人员可以利用 Python Shell 与 Python 交互。
- 单击系统的开始菜单，然后依次选择“所有程序 -> Python 3.6 -> IDLE (Python 3.6 64-bit)”菜单项，即可打开 IDLE 窗口
- File -> New File -> 编写程序

2. 通过命令行执行python文件中的代码

### 5. python编辑器推荐

- pycharm
- vscode
- sublime

IDE：Integrated Development Environment

#### 5.1 vscode开发环境配置

##### 5.1.1 插件

![](images\vscode插件使用.png)

- 微软官网vscode插件地址：https://marketplace.visualstudio.com/vscode

##### 5.1.2 settings

- File -> preferences -> settings 中配置

##### 5.1.3 快捷键打开命令行

- 使用 ctr + ~ 开启或关闭命令行

##### 5.1.4 技巧

1. 使用 ctrl + p 能够直接检索文件



## 三. 分支、循环、条件和枚举

### 1. 条件控制

```python
# 单行注释
'''
多行注释，使用的是三个单引号，有点像字符串
'''
mood = False
if mood :
    print('go to left ... ') # 前面有四个空格
else :
    print("go to right ...")
```

```python
''' 模块说明 '''

ACCOUNT  = 'superman'
PASSWORD = "123456"

# constant 表示常量

print("please input your account")
user_account = input()

print("please input your password")
user_password = input()

if ACCOUNT == user_account and PASSWORD == user_password:
    print("success") # 前面要空有4个空格
else:
    print("fail")

```

- python中用缩进来表示同级别的代码块

```python
a = input() # 从终端输入的是字符串类型
print(type(a))
a = int(a)
if a == 1:
    print("a is 1")
elif a == 2:
    print("a is 2")
elif a == 3:
    print("a is 3")
else:
    print("I don't know that a is what.")
```

### 2. 循环控制

1. while循环

```python
counter = 1

while counter <= 10:
    counter += 1
    print(counter)
else:
    print("counter is larger than 10")
```

2. for循环

```python
# 主要是用来遍历/循环 鸡、、序列或集合、字典
a = ['apple', 'orange', 'banana', 'grape']
for x in a:
    print(x)

print("==============================================")

# 遍历二级列表
b = [['apple', 'orange', 'banana', 'grape'], (1,2,3)]
for x in b:
    for y in x:
        print(y)

# for循环可以与else一起使用
for x in a:
    print(x,end='')
else:
    print("fruit is gone")

# 使用 for循环 + range() 来限定执行代码块次数
## 只打印到9，从0开始到9
for x in range(0, 10):
    print(x)
## 从0开始，每次加2，要小于10（递增）
for x in range(0, 10, 2):
    print(x, end=' | ')
else:
    print()
## 从10开始，每次减2，要大于0（递减）
for x in range(10, 0, -2):
    print(x, end=' | ')
```

3. 练习题

```python
'''
思考题：打印列表a中相隔的元素
'''
# 方法一：通过for循环
a = [1,2,3,4,5,6,7,8]
for x in range(0, len(a), 2):
    print(a[x], end=' | ')

print("========================")

# 方法二：通过a[x:y:z]表示从a序列的x位开始，获取到y位，间隔2个获取一下
b = a[0:len(a):2]
print(b)
```

### 3. 



## 四. python组织结构

- 包
  - 每个包中可以有多个模块
  - **python中每个文件夹就是一个包，在java中每个jar是一个包**
  - 通过在文件夹中建立一个 `__init__.py` 文件，确定该文件夹是一个包

- 模块
  - 一个模块中包含多个类
  - 通过 **包名.模块名** 作为命名空间来区分每个模块

- 类
  - 函数变量最好写在类中

- 函数、变量

### 1. 导入模块

```python
# # 引入模块：方法一
# import c9 as packageC9
# print(packageC9.a)
# import seven_child_module.c9 as childC9
# print(childC9.b)

# 引入模块或具体的变量:方法二
# from seven_child_module import c9
# print(c9.b)
# from c9 import a
# print(a)
# 一次性将模块中所有的变量、函数全部导入进来
from seven_child_module.c9 import *
print(b)
print(d)
print(c)
```

#### 1.1  `__init__py` 进行批量导入

- seven_child_module的包下的 `__init__.py` 文件

  ```python
  # __init__.py 可以作为批量导入使用
  import sys
  import datetime
  import io
  
  # __all__ = ['c9']
  ```

- 批量导入的模块

  ```python
  # from seven_child_module import *
  # print(c9.b)
  # print(c10.e)
  
  # 通过使用 __init__.py 解决批量导入问题
  # import sys
  # import datetime
  # import io
  # print(sys.path)
  
  import seven_child_module as common_module
  print(common_module.sys.path)
  ```

注意：

1. 包和模块的导入是不会被重复导入的
2. 避免循环导入
3. 在项目中只要打入了某个模块，那么模块中的代码就会执行一遍，即使项目中多次导入该模块，也只会执行一次该模块的代码，不会执行多次

### 2. 技巧

- 可以使用 \ 来换行，也可以使用 () 来换行



## 五. 函数

- 功能性
- 隐藏细节
- 避免编写重复的代码

1. 定义

```python
# 定义函数
def function_name(parameter_list):
    pass
```

- 参数列表可以没有
- return value None

2. 实例：

```python
# 编写函数：
## 1) 实现两个数字相加
def addFunction(parameter0, parameter1):
    result = parameter0 + parameter1
    return result
## 2) 打印输入的参数
def printResult(code):
    print(code)

# 调用函数：python是解释型语言,只能从上往下执行，如果在定义函数之前就调用函数，报错
result = addFunction(1,2)
printResult(result)
```

```python
def damage(skill1, skill2):
    damage1 = skill1 * 3
    damage2 = skill2 * 2 + 10
    return damage1, damage2 # 默认返回为元组类型

damages = damage(4, 6)
print(damages)
# 序列解包
skill1_damage, skill2_damage = damage(3, 5)
print(skill1_damage, skill2_damage)
```

3. 序列解包

```python
a,b,c = 1,2,3
print(a, b, c)

d = 4,5,6
print(type(d))
print(d)

# 序列解包
e,f,g = d
print(e, f, g)

# 补充：
a,b,c = 1,1,1
# 等价于 
a = b = c = 1
```

4. 函数参数

```python
# 参数
# 1.必须参数
# 2.关键字参数

def add(x, y): # 形式参数，简称形参
    result = x = y
    return result

c = add(y=3, x=2) # 通过关键字参数，可以指出对应的参数对应的值，并将其传入
```

```python
def print_student_files(name, gender, age, college):
    print('我叫' + name)
    print('我今年' + str(age) + '岁了')
    print('我是' + gender + '生')
    print('我在' + college + '上学')

print_student_files('superman', '男', 18, '人民路小学')

print('----------------------分割线--------------------------')

# 设置默认参数，调用可以不传该参数的值
def print_student_files2(name, gender='男', age=18, college='人民路小学'):
    print('我叫' + name)
    print('我今年' + str(age) + '岁了')
    print('我是' + gender + '生')
    print('我在' + college + '上学')

print_student_files2('supergirl', '女', 16)
print('----------------------分割线--------------------------')
print_student_files2('batman')
print('----------------------分割线--------------------------')

## 默认参数的中的坑
# 1. 默认参数后不能跟着非默认参数，编译不通过
## def print_student_files2(name, gender='男', age=18, college='人民路小学', teacher): 定义错误
# 2. 通过关键字参数来选择默认参数赋值
print_student_files2('The Flash', age = 20)
# 3. 不能在默认参数中 即使用关键字参数，又使用普通参数调用
## print_student_files2('The Flash', gender='男', 20, college='人民路小学')
```

## 六. 面向对象

### 1. 类

#### 1.1 类的定义与初步使用

```python
# 面向对象
# 有意义的面向兑现的代码
# 类 = 面向对象
# 类、对象
# 类最基本的作用：封装

# 推荐：
# 1. 类名首字母大写
# 2. 多个单词组成类名，每个单词都是首字母大写，其余小写

# 类定义：
class Student():
    name = ''
    age = 0

    def print_file(self):
        # print('name is ' + name) # 错误，会报 name 没有被定义的错误
        print('name is ' + self.name)
        print('age is ' + str(self.age))

    # print_file() python的类内部是不能调用类的方法的


# 使用类，需要先实例化类，再调用类的方法(不推荐在类所在的模块进行实例化并调用)
student = Student() # 实例化
student.print_file() # 调用方法


# 一个模块内可以定义多个类
class StudentHomework():
    homework_name = ''
```

#### 1.2 类的调用使用

```python
# 从模块c1中导入Student类
from c1 import Student

# 实例化类
student = Student()
student.print_file()
```

#### 1.3 类的实例化

##### 1.3.1 实例化



##### 1.3.2 构造函数

- 定义格式为:

  ```python
  def __init__(self):
      psss
  ```

  

- python构造函数只能返回None类型

























### api

3. 计算`str`包含多少个字符，可以用`len()`函数：

```python
>>> len('ABC')
3
>>> len('中文')
2
```

4. `len()`函数计算的是`str`的字符数，如果换成`bytes`，`len()`函数就计算字节数：

```python
>>> len(b'ABC')
3
>>> len(b'\xe4\xb8\xad\xe6\x96\x87')
6
>>> len('中文'.encode('utf-8'))
6
```







当Python解释器读取源代码时，为了让它按UTF-8编码读取，我们通常在文件开头写上这两行：

```python
#!/usr/bin/env python3
# -*- coding: utf-8 -*-
```

第一行注释是为了告诉Linux/OS X系统，这是一个Python可执行程序，Windows系统会忽略这个注释；

第二行注释是为了告诉Python解释器，按照UTF-8编码读取源代码，否则，你在源代码中写的中文输出可能会有乱码。

申明了UTF-8编码并不意味着你的`.py`文件就是UTF-8编码的，必须并且要确保文本编辑器正在使用UTF-8 without BOM编码：







在Python中，采用的格式化方式和C语言是一致的，用`%`实现，举例如下：

```python
>>> 'Hello, %s' % 'world'
'Hello, world'
>>> 'Hi, %s, you have $%d.' % ('Michael', 1000000)
'Hi, Michael, you have $1000000.'
```

你可能猜到了，`%`运算符就是用来格式化字符串的。在字符串内部，`%s`表示用字符串替换，`%d`表示用整数替换，有几个`%?`占位符，后面就跟几个变量或者值，顺序要对应好。如果只有一个`%?`，括号可以省略。

常见的占位符有：

| 占位符 | 替换内容     |
| :----- | :----------- |
| %d     | 整数         |
| %f     | 浮点数       |
| %s     | 字符串       |
| %x     | 十六进制整数 |


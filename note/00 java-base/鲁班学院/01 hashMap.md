hashMap的底层table数组的大小是2幂次方数



二进制表示2的幂次方数：

1  ------------------------  0000 0001

2  ------------------------  0000 0010

4  ------------------------  0000 0100

8  ------------------------  0000 1000

16 ------------------------ 0001 0000

**二进制的bit位上只有一位是1，其余都是0**





 Integer.highestOneBit(int x); 找到小于等于参数的2的幂次方数

```java
public static int highestOneBit(int i) {
    // HD, Figure 3-1
    i |= (i >>  1);
    i |= (i >>  2);
    i |= (i >>  4);
    i |= (i >>  8);
    i |= (i >> 16);
    return i - (i >>> 1);
}
```

例如：

17                  0001 0001

`>>1`               0000 1000

|(或运算)       0001 1001

`>>2`               0000 0110

|(或运算)       0001 1111

`>>4`               0000 0001

|(或运算)       0001 1110

`>>8`               0000 0000

|(或运算)       0001 1110

`>>16`             0000  0000

|(或运算)       0001 1111



减去右移一位 0001 1111

​                        0000 1111

​                        0001  0001
# Base64Util
将 Base64 解码生成文件保存到指定路径；将文件转换成 Base64 码

- 引入

```xml
<!-- https://mvnrepository.com/artifact/icu.xuyijie/Base64Utils -->
<dependency>
    <groupId>icu.xuyijie</groupId>
    <artifactId>Base64Utils</artifactId>
    <version>1.2.3</version>
</dependency>
```
- 使用

```java
// 将文件编码成Base64，可传入文件全路径，或者一个 File 对象
String s = Base64Util.transferToBase64("D:/下载/Screenshot_20221008-090627.png");
File file = new File(filePath);
String s = Base64Util.transferToBase64(file);
System.out.println(s);
// 将Base64转换成文件保存到指定位置，可传入文件全路径或者分别传入保存位置和文件名
String s1 = Base64Util.generateFile(s, "D:/下载/aaa.png");
String s1 = Base64Util.generateFile(s, "D:/下载", "aaa.png");
System.out.println(s1);
```

# Base64Util
将 Base64 解码生成文件保存到指定路径；将文件转换成 Base64 码

- 引入

```xml
<!-- https://mvnrepository.com/artifact/icu.xuyijie/Base64Utils -->
<dependency>
    <groupId>icu.xuyijie</groupId>
    <artifactId>Base64Utils</artifactId>
    <version>1.3.3</version>
</dependency>
```
- 使用

```java
// 将文件编码成Base64，可传入文件全路径，或者一个 File 对象，第二个参数是是否生成前缀
String s = Base64Utils.transferToBase64("D:/下载/Screenshot_20221008-090627.png", false);
File file = new File(filePath);
String s = Base64Utils.transferToBase64(file, false);
System.out.println(s);
// 将Base64转换成文件保存到指定位置，可传入文件全路径或者分别传入保存位置和文件名，路径分隔符可使用/或\
String s1 = Base64Utils.generateFile(s, "D:\\下载\\aaa.png");
String s1 = Base64Utils.generateFile(s, "D:/下载", "aaa.png");
System.out.println(s1);
//也可以从base64获取文件对象流，或者文件类型
File file = Base64Utils.getFile("D:/下载/a.png");
FileInputStream fileInputStream = Base64Utils.getFileStream("D:/下载/a.png");
String fileType = Base64Utils.getFileType("传入base64Str");
```

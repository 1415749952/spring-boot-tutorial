十四、定制自已专属的启动Banner
---

### 目标

实现自定义Banner

### 操作步骤
#### 添加依赖
引入 Spring Boot Starter 父工程

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.5.RELEASE</version>
</parent>
```

引入 `spring-boot-starter-web` 依赖
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```
#### 自定义 Banner
spring-boot 支持多种方式设置 banner，也可以不输出 banner

##### 字符串形式

在 `src/main/resources` 路径下新建一个 banner.txt 文件，在 banner.txt 中填写需要打印的字符串内容即可。

方法很简单，但首先得绘制一个有意思的字符串，比如如下的 HelloWorld：
```
.__           .__  .__                               .__       .___
|  |__   ____ |  | |  |   ____   __  _  _____________|  |    __| _/
|  |  \_/ __ \|  | |  |  /  _ \  \ \/ \/ /  _ \_  __ \  |   / __ | 
|   Y  \  ___/|  |_|  |_(  <_> )  \     (  <_> )  | \/  |__/ /_/ | 
|___|  /\___  >____/____/\____/    \/\_/ \____/|__|  |____/\____ | 
     \/     \/                                                  \/ 
```

一般情况下，我们会借助第三方工具帮忙转化内容：
 - 文字转化成字符串：<http://www.network-science.de/ascii/>
 - 图片转化成字符串：<http://www.degraeve.com/img2txt.php>

banner.txt配置
 - ${AnsiColor.BRIGHT_RED}：设置控制台中输出内容的颜色
 - ${application.version}：用来获取MANIFEST.MF文件中的版本号
 - ${application.formatted-version}：格式化后的${application.version}版本信息
 - ${spring-boot.version}：Spring Boot的版本号
 - ${spring-boot.formatted-version}：格式化后的${spring-boot.version}版本信息

##### 图片形式

将图片重命名为 `banner.xxx`(xxx 为图片后缀名，比如 banner.gif)，放在 `src/main/resources` 目录下

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 结束语

更换 spring-boot 原本的 banner 为个性化的输出，可以输出公司 Logo 或者项目 Logo 什么的，增加参与成员对公司、项目、品牌的认同感

### 参考
 - <http://www.ityouknow.com/springboot/2018/03/03/spring-boot-banner.html>
 - <https://www.cnblogs.com/huanzi-qch/p/9916784.html>
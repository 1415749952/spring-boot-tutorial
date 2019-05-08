第五章：SpringBoot切换Tomcat服务器为Undertow
---

> 在相同资源使用量的情况下 undertow 有较好的吞吐量和较低的访问时延

### 课程目标

替换 SpringBoot 的启动服务器 Tomcat 服务器为 Undertow 服务器

### 操作步骤

#### 添加依赖

在引入 spring-boot-starter-web 依赖时，剔除出对 spring-boot-starter-tomcat 的依赖，然后再添加对 spring-boot-starter-undertow 的依赖。

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-tomcat</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-undertow</artifactId>
    </dependency>
</dependencies>
```

### 验证结果

启动项目，查看启动日志

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

Undertow 是一个比 Tomcat 性能更好的服务器，这是一个优化项。

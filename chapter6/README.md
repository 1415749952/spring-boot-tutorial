六、配置高性能服务器Undertow
---

### 相关知识

undertow 是一个服务器，在相同资源使用量的情况下 undertow 比 tomcat 有更好的吞吐量和较低的访问时延

### 目标

替换 SpringBoot 的启动服务器 Tomcat 服务器为 Undertow 服务器

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

在引入 `spring-boot-starter-web` 依赖时，剔除出对 `tomcat` 的依赖，然后再添加对 `undertow` 的依赖用于替换 `tomcat`。

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

启动项目，查看启动日志，可以看到如下两行

```
Undertow started on port(s) 8080 (http) with context path ''
Started Application in 2.763 seconds (JVM running for 3.658)
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 结束语

Undertow 是一个比 Tomcat 性能更好的服务器，这是一个优化项。

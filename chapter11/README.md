第十一章：SpringBoot整合Mybatis-plus
---

### 相关知识

mybatis-plus 已经完成了对 mybatis 的集成，并且提供了通用的 mapper 以及 service，还可以通过模板生成相关代码。

Mybatis-plus 官网：<https://mp.baomidou.com/>

### 课程目标

完成 SpringBoot 对 mybatis-plus 的整合，实现对数据库表的增删查改操作。

### 操作步骤

#### 添加 Maven 依赖

引入 Spring Boot Starter 父工程

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.5.RELEASE</version>
</parent>
```

引入 `spring-boot-starter-web`、`lombok`、`mybatis-plus`、`mysql` 的依赖

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

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>

    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.1.0</version>
    </dependency>

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

#### 配置

在 `application.yml` 配置文件中添加 mysql 的数据源配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.247.130:3306/test
    driver-class-name: com.mysql.jdbc.Driver
    username: app
    password: 123456
```

#### 编码

##### 编写实体类

```java
@Data
public class User {

    private Long id;
    private String name;
    private Integer sex;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

}
```

##### 编写Mapper类

```java
@Mapper
public interface UserRepository extends BaseMapper<User> {}
```

### 验证结果

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结




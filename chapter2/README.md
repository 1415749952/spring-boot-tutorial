第二章：SpringBoot集成SpringBootJPA完成CURD
---

### 课程目标

在第一章中，我们使用 SpringBoot 创建了一个最简单的 Web 项目，这一章将在第一章的基础上，完成跟数据库的结合，实现对数据库表的增删查改操作。

### 操作步骤

#### 创建表

在 mysql 的 test 库中创建表 user，脚本如下，其中主键设置为自动增长。

```sql
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(64) NOT NULL COMMENT '名称',
  `sex` TINYINT(2) UNSIGNED NOT NULL DEFAULT '0' COMMENT '性别',
  `birthday` DATE NOT NULL DEFAULT '0000-00-00' COMMENT '出生日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户';
```

#### 构建项目

通过 File -> New -> Module，选择创建一个 Maven 项目。

```
<groupId>com.mhkj.course</groupId>
<artifactId>chapter2</artifactId>
<version>1.0.0-SNAPSHOT</version>
```

#### 添加依赖

在 pom.xml 中，添加项目依赖，

 - 因为是 web 项目，添加对 `spring-boot-starter-web` 的依赖。
 - 因为需要使用 mysql 数据库，添加对 `mysql-connector-java` 的依赖
 - 因为需要使用 JPA 作为持久层，添加对 `spring-boot-starter-data-jpa` 的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

#### 配置数据源

SpringBoot 的默认配置文件为 application.properties(或者 application.yml)，本课程使用 application.yml。

在 src/main/resources 目录下添加 application.yml 文件，在文件中添加如下配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.247.130:3306/test
    driver-class-name: com.mysql.jdbc.Driver
    username: app
    password: 123456

  jpa:
    database: mysql
    # 显示后台处理的SQL语句
    show-sql: true
    # 自动检查实体和数据库表是否一致，如果不一致则会进行更新数据库表
    hibernate:
      ddl-auto: none
```

#### 创建实体

创建实体类 User

 - 类上添加注解 @Entity
 - 主键添加注解 @Id
 - 主键自增添加注解 @GeneratedValue
 - 日期格式添加注解 @DateTimeFormat(pattern = "yyyy-MM-dd")，用于定义前端入参格式

```java
@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer sex;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    // 省略 get & set 方法
}
```

#### 创建 Repository

JPA 提供的 JpaRepository 接口已经实现了对单表的增删查改操作以及一些其它常用的方法。

创建接口 UserRepository，继承 JpaRepository 接口，内容如下

```java
public interface UserRepository extends JpaRepository<User, Long> {}
```

#### 创建前端交互接口

创建 controller 类，实现增删查改交换接口

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public List<User> add(User user) {
        userRepository.save(user);
        return userRepository.findAll();
    }

    @PostMapping("/delete")
    public List<User> delete(Long id) {
        userRepository.deleteById(id);
        return userRepository.findAll();
    }

    @PostMapping("/list")
    public List<User> list() {
        return userRepository.findAll();
    }

    @PostMapping("/update")
    public List<User> update(User user) {
        userRepository.save(user);
        return userRepository.findAll();
    }

}
```

### 验证结果

> 因为本章内容需要使用 post 提交方式，故不能在地址栏简单地输入地址进行请求，建议使用 postman 进行操作。

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

JPA 底层使用的是 Hibernate 框架，已经封装了对单表的各种操作，只需要实现 JpaRepository 接口即可以获得对当前表的各种操作方法，这使得使用 JPA 进行数据库的 CURD 操作变得非常简单。
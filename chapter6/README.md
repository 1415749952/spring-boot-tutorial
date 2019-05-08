第六章：SpringBoot集成Lombok让项目更简洁
---

在上一章中，我们使用 SpringBoot 集成 JPA 进行对数据库表的 CURD 操作，在创建表所对应的实体时，需要为实体所有的字段生成 get / set 方法。
这部分代码格式固定，每次都修改都需要重新生成，而且非常占地方，影响代码可看性。
Lombok 就是为了解决这个问题。

### Lombok简介

Lombok官网：<https://projectlombok.org/>

#### 常用注解

一、@Setter | @Getter

提供无参构造方法以及 getter、setter 方法

```
@Getter
@Setter
public class User1 {

    private Long id;

    private String username;

}
```

二、@ToString

提供无参构造方法以及 toString 方法

 - includeFieldNames 是否包含属性名
 - exclude 排除指定属性
 - callSuper 是否包含父类属性

```
@ToString
public class User2 {

    private Long id;

    private String username;

}
```

三、@EqualsAndHashCode

提供无参构造方法以及 equals、hashCode 方法

```
@EqualsAndHashCode
public class User3 {

    private Long id;

    private String username;

}
```

四、@AllArgsConstructor

提供一个全参数的构造方法，默认不提供无参构造

```
@AllArgsConstructor
public class User4 {

    private Long id;

    private String username;

}
```

五、@NoArgsConstructor

提供一个无参构造

```
@NoArgsConstructor
public class User5 {

    private Long id;

    private String username;

}
```

六、@Data

结合了@ToString，@EqualsAndHashCode，@Getter、@Setter、@NoArgsConstructor

- staticConstructor 生成静态工厂方法的方法名，如果设置了该参数，则生成的无参构造方法将被置为私有的。

```
@Data
public class User4 {

    private Long id;

    private String username;

}
```

七、@Slf4j

提供 org.slf4j.Logger 变量，变量名为 log

```
@Slf4j
@RestController
public class UserController {

    @GetMapping("/listUser")
    public List<User1> listUser() {
        log.error("log with lombok");
        return null;
    }

}
```

### 课程目标

熟悉并学会使用 lombok，简化项目代码

### 操作步骤

#### 安装插件

File -> settings，打开 Idea 的设置界面，从左侧栏选择 Plugins 选项，再在右侧查询 lombok，点击安装。

#### 构建项目

在上一章的代码基础上进行操作

#### 添加依赖

添加对 lombok 的依赖

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.16.22</version>
    <scope>compile</scope>
</dependency>
```

#### 为实体类减负

将 User 类的 get / set 方法全部删除，在类上添加注解 @Data，修改后的 User 类代码如下，可以看到整个类的代码变得非常清爽。

```java
@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer sex;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

}
```

### 验证结果

同上一章一样，使用 postman 进行接口调用

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

Lombok 只是为了减少一些无关紧要的代码，减少编码的工作量，并不会减少程序的复杂度。
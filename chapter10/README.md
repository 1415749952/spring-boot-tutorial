第十章：SpringBoot整合MapStruct简化参数映射
---

### 相关知识

MapStruct官网：<http://mapstruct.org>

### 课程目标

SpringBoot 整合 MapStruce 以及 Lombok

### 操作步骤

#### 安装插件

File -> Settings 打开设置界面，选择 plugins 进入插件界面，搜索 MapStruce 进行安装。

#### 设置IDE

File -> Settings 打开设置界面，
选择 Build,Execution,Deployment -> Compiler -> Annotation Processors 进入设置界面，
勾选 enable annotation processing

#### 添加 Maven 依赖

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-jdk8</artifactId>
    <version>1.3.0.Final</version>
</dependency>

<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.3.0.Final</version>
    <scope>provided</scope>
</dependency>
```

#### 添加 Maven 插件

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.3.0.Final</version>
            </path>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

#### 添加 BO 对象接收页面传参

```java
@Getter
@Setter
public class UserBO {
    private String username;
    private LocalDate birthday;
    private Integer level;
    private Integer sex;
}
```

#### 添加 Dto 对象，用于业务操作

```java
@Data
public class UserDto {
    private Long id;
    private String name;
    private LocalDate birthday;
    private Integer level;
    private Integer sex;
    private String password;
    private String salt;
}
```

#### 添加 Mapper 接口，设置映射规则

类上必须有 @Mapper 注解，映射规则使用 @Mapping 注解进行设置

```java
@Mapper
public interface UserMapper {

    UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "username", target = "name")
    UserDto bo2Dto(UserBO bo);

}
```

#### 调用 Mapper，将 BO 数据复制至 DTO

```java
@RestController
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @RequestMapping("/register")
    public UserDto doRegister(@RequestBody UserBO bo) {
        return userService.doSomething(UserMapper.MAPPER.bo2Dto(bo));
    }

}
```

### 验证结果

启动服务，调用 `/register` 接口，查看数据是否成功复制至 Dto 对象。

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

MapStruct 释放掉大量的 get、set 方法，改为编译时自动生成，所以只是精简了项目代码。

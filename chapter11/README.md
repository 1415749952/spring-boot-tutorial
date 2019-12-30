第十一章：SpringBoot整合Swagger2
---

Swagger 是一个规范和完整的框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务。

### 相关知识

Swagger官网：<https://swagger.io>

#### 常用注解： 
 - @Api 用于类，表示标识这个类是swagger的资源
 - @ApiOperation 用于方法，表示一个http请求的操作
 - @ApiParam 用于方法，参数，字段说明，表示对参数的添加元数据（说明或是否必填等）
 - @ApiModel 用于类，表示对类进行说明，用于参数用实体类接收
 - @ApiModelProperty 用于方法，字段，表示对model属性的说明或者数据操作更改
 - @ApiIgnore 用于类，方法，方法参数，表示这个方法或者类被忽略
 - @ApiImplicitParam 用于方法，表示单独的请求参数
 - @ApiImplicitParams 用于方法，包含多个 @ApiImplicitParam

### 课程目标

在 [第十章：SpringBoot整合MapStruct简化参数映射](https://gitee.com/gongm_24/spring-boot-tutorial/tree/master/chapter10) 的代码基础上，
整合 Swagger2 实现自动生成文档

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

添加 `Swagger2` 的依赖

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```

添加后的整体依赖如下

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
        <scope>provided</scope>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>2.9.2</version>
    </dependency>

    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>2.9.2</version>
    </dependency>
</dependencies>
```

### 编码

1. 新建配置类

```java
/**
 * @Configuration 用于启动自动加载
 * @EnableSwagger2 用于开启 Swagger
 */
@Configuration
@EnableSwagger2
public class SwaggerAutoConfiguration {

    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 本例采用指定扫描的包路径来定义指定要建立API的目录。
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 控制暴露出去的路径下的实例
                // 如果某个接口不想暴露,可以使用以下注解
                // @ApiIgnore 这样,该接口就不会暴露在 swagger2 的页面下
                .apis(RequestHandlerSelectors.basePackage("com.mhkj.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * 访问地址：http://项目实际地址/swagger-ui.html
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("接口文档")
                .version("1.0.0-SNAPSHOT")
                .build();
    }

}
```

2. 为接口添加 swagger 注解

```java
@RestController
@Slf4j
@Api("用户管理")
public class UserController {

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping("/register")
    public UserBO register(@RequestBody @ApiParam(name = "注册对象", value = "JSON对象", required = true) UserBO userBO) {
        return userBO;
    }

}
```

3. 为入参添加 swagger 注解

```java
@Getter
@Setter
@ApiModel("用户注册对象")
public class UserBO {

    @ApiModelProperty(value = "手机号", required = true)
    private String mobile;

    @ApiModelProperty(value = "密码", required = true)
    private String upwd;

}
```

### 验证结果

访问 http:://localhost:8080/swagger-ui.html，即可看到 API 文档


### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

Swagger 可以实时生成文档，保证文档的时效性，这有助于前后端联合开发、微服务联合开发等

第一章：SpringBoot快速构建Web应用
---

### 本章目标

使用 SpringBoot 完成一个简单的 Web 应用程序开发，初步体验 SpringBoot 快速、简洁的特性。

### 操作步骤

#### 构建项目

打开 Idea，依次选择 File -> New -> Module，打开 Module 面板。

左侧栏选择 Maven，点击下一步。

填写 groupId、artifactId、version，点击下一步。

填写项目名，一般保持与 artifactId 一致，点击完成。

##### 项目目录结构

 - src/main/java 存放项目源码
 - src/main/resources 存放项目配置文件
 - src/test/java 存放测试用例

#### 添加依赖

引入 Spring Boot Starter 父工程

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.5.RELEASE</version>
</parent>
```

引入 `spring-boot-starter-web` 的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

#### 编码

1. 编写 controller 接口

创建 HelloSpringBootController 类，内容如下：

```java
@RestController
public class HelloSpringBootController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello SpringBoot";
    }

}
```

其中，
@RestController 相当于在 HelloSpringBootController 类上添加 @Controller 注解，以及对类中每一个方法添加 @ResponseBody 注解。
@GetMapping 相当于在 hello 方法上添加 `@RequestMapping(method = RequestMethod.GET)` 注解。


2. 编写项目启动类

创建 Application 类，内容如下

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

其中 @SpringBootApplication 注解用于标记该类为项目启动类，而 main 方法中使用 `SpringApplication.run(Application.class, args);` 进行项目启动。
SpringBoot 还提供了其它启动方式，并且可以设置启动参数及配置，但目前只要知道通过这样就可以启动 SpringBoot 项目即可。

> 至此，项目已经搭建完毕，通过执行启动类的 main 方法即可以启动 SpringBoot 的 Web 项目了。

### 验证结果

#### 启动项目

在 Application 类上右键，选择 Run Application，项目正式启动，查看启动日志，可以看到 SpringBoot 项目默认使用 tomcat 容器，启动端口为 8080。
访问地址 `http://localhost:8080/hello`，可以看到输出 `Hello SpringBoot`。

### 代码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

使用 SpringBoot 搭建 Web 项目，其实内部还是 SpringMVC，但是，
使用 SpringBoot 搭建 Web 项目比起曾经使用 SpringMVC 一步一步搭建项目明显快捷得多。
所以，SpringBoot 并不是什么新技术，只是一个开发的脚手架，帮助开发者更快速地开发。

### 扩展

#### 打包部署

在 IDE 中，我们可以使用 main 方法直接运行项目，但是将项目部署至服务器上运行，则不能如此。SpringBoot 提供了一个 Maven 插件，用于将项目要包成一个可以执行的 jar 包。

在 pom.xml 中添加 Maven 插件

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

执行 maven install 后，项目会被打包成 jar 包，通过如下命令即可启动项目

```
java -jar chapter1-1.0.0-SNAPSHOT.jar
```


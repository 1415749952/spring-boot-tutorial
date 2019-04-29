第一章：SpringBoot快速构建Web应用
---

### 课程目标

使用 SpringBoot 完成一个简单的 Web 应用程序开发，初步体验 SpringBoot 快速、简洁的特性。

### 操作步骤

#### 构建 Maven 项目

打开 Idea，依次选择 File -> New -> Module，打开 Module 面板。

左侧栏选择 Maven，点击下一步。

填写 groupId、artifactId、version，点击下一步。

填写项目名，一般保持与 artifactId 一致，点击完成。

##### 项目目录结构

 - src/main/java 存放项目源码
 - src/main/resources 存放项目配置文件
 - src/test/java 存放测试用例

#### 添加对 SpringBoot 的依赖

找到项目目录下的 pom.xml 文件，添加以下内容

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.5.RELEASE</version>
</parent>
```

#### 添加对 Web 的依赖

在 pom.xml 文件，添加以下内容

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

#### 创建项目启动类

SpringBoot 采用 main 方法进行项目启动。

在 src/main/java 目录下创建 Application.java 类，内容如下

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

其中 @SpringBootApplication 注解用于标记该类为项目启动类，而 main 方法中使用 `SpringApplication.run(Application.class, args);` 进行项目启动。
其实 SpringBoot 还提供了其它启动方式，并且可以设置启动参数及配置，但目前只要知道通过这样就可以启动 SpringBoot 项目即可。

至此，项目已经搭建完毕，通过执行启动类的 main 方法即可以启动 SpringBoot 的 Web 项目了。

##### 启动项目

在 Application 类上右键，选择 Run Application，项目正式启动，查看启动日志，可以看到 SpringBoot 项目默认使用 tomcat 容器，启动端口为 8080。

#### 编写HelloSpringBootController

在 Application 类的当前目录或者子目录下创建类 HelloSpringBootController.java，内容如下：

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

### 验证结果

重新启动项目，访问地址`http://localhost:8080/hello`，可以看到输出`Hello SpringBoot`。

至此，使用 SpringBoot 编写的第一个接口就完成了。

### 代码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

使用 SpringBoot 搭建 Web 项目，其实内部还是 SpringMVC，但是，
使用 SpringBoot 搭建 Web 项目比起曾经使用 SpringMVC 一步一步搭建项目明显快捷得多。
所以，SpringBoot 并不是什么新技术，只是一个开发的脚手架。
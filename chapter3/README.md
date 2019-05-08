第三章：SpringBoot项目中配置拦截器
---

关于拦截器的相关知识可参考：<https://jinnianshilongnian.iteye.com/blog/1670856>

### 课程目标

学会在 SpringBoot 的项目中怎么配置拦截器

### 操作步骤

#### 实现拦截器

```java
@Slf4j
public class TraceInterceptor implements HandlerInterceptor {

    /**
     * 预处理回调函数
     * 进入Controller之前执行
     * 如果返回 true，则进入下一个拦截器，所有拦截器全部通过，则进入 Controller 相应的方法
     * 如果返回 false，则请求被拦截。
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        log.info("<-----------------------------------");
        log.info("request uri: {}", uri);
        log.info("----------------------------------->");
        return true;
    }

    /**
     * 后处理回调方法
     * 经过Controller处理之后执行
     * 在此处可以对模型数据进行处理或对视图进行处理
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("entry postHandler");
    }

    /**
     * 整个请求处理完毕回调方法，即在视图渲染完毕时回调
     * 在此处可以进行一些资源清理
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("entry afterCompletion");
    }
}
```

#### 注册拦截器

实现 WebMvcConfigurer 接口对 SpringMvc 进行个性化配置。

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceInterceptor());
    }

    @Bean
    public TraceInterceptor traceInterceptor() {
        return new TraceInterceptor();
    }

}
```

### 验证结果

编写一个接口

```java
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "hello world";
    }

}
```

启动服务，本地访问 `/hello` 接口，查看日志输出

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

使用 SpringMVC 时是使用 XML 进行注册，SpringBoot 则推荐使用代码进行注册，最终结果其实是一样的，所以只需要知道操作步骤即可。
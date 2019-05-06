第七章：SpringBoot全局异常处理
---

在上一章，我们使用了 hibernate-validator 对参数进行校验，
如果校验失败，Spring 会使用默认的异常处理器对校验失败抛出的异常进行处理，并返回给调用端，
但如果希望返回的格式是自定义的格式，则需要自行设置全局异常处理器

### 课程目标

对 SpringBoot 项目中的所有异常进行响应，处理成统一的格式输出。

### 操作步骤

#### 构造全局统一出参

所有接口返回的结构一致

```java
@Getter
@Setter
public class RestData<T> {

    private static final String SUCCESS_INFO = "操作成功";
    private static final String FAIL_INFO = "操作失败";

    // 接口调用状态，true表示调用成功，false表示调用失败
    private boolean status;
    // 提示信息，如果没有设置，则根据status值使用默认提示
    private String info;
    // 返回数据
    private T data;

    public static <T> RestData<T> build() {
        return new RestData<>();
    }

    public RestData() {}

    private RestData(boolean status, String info) {
        this.status = status;
        this.info = info;
    }

    /**
     * 操作是否成功
     *
     * @return true | false
     */
    public boolean isSuccess() {
        return this.status;
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> success() {
        return this.success(null);
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> success(String info) {
        return success(info, null);
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> success(String info, T data) {
        if (info != null && !info.isEmpty()) {
            this.status = true;
            this.info = info;
            this.data = data;
            return this;
        } else {
            return success(SUCCESS_INFO, data);
        }
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> error() {
        return this.error(null);
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> error(String info) {
        return error(info, null);
    }

    /**
     * 操作成功，组装返回数据
     */
    public RestData<T> error(String info, T data) {
        if (info != null && !info.isEmpty()) {
            this.status = false;
            this.info = info;
            this.data = data;
            return this;
        } else {
            return error(FAIL_INFO, data);
        }
    }

}
```

#### 构建全局异常处理器

由于不同的参数接收器，在进行参数校验及数据转换时会抛出不同的异常，在这里只对 JSON 格式的传参模式进行了处理。

```java
@Slf4j
@RestControllerAdvice
public class RestExceptionHandler {

    /**
     * 校验错误拦截处理
     * 使用 @RequestBody 接收入参时，校验失败抛 MethodArgumentNotValidException 异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public RestData handler(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException handler", e);
        BindingResult bindingResult = e.getBindingResult();
        if (bindingResult.hasFieldErrors()) {
            return RestData.build().error(bindingResult.getFieldError().getDefaultMessage());
        }
        return RestData.build().error("parameter is not valid");
    }

    /**
     * 校验错误拦截处理
     * 使用 @RequestBody 接收入参时，数据类型转换失败抛 HttpMessageConversionException 异常
     */
    @ExceptionHandler(value = HttpMessageConversionException.class)
    public RestData handler(HttpMessageConversionException e) {
        log.error("HttpMessageConversionException handler", e);
        return RestData.build().error(e.getMessage());
    }

    /**
     * 全局异常处理
     */
    @ExceptionHandler(value = Exception.class)
    public RestData handler(Exception e) {
        log.error("exception handler", e);
        return RestData.build().error(e.getMessage());
    }

}
```

#### 编写接口

```java
@RestController
public class UserController {

    @RequestMapping("/register")
    public String doRegister(@Valid @RequestBody UserBO bo) {
        return "success";
    }

}
```

### 验证结果

启动服务，使用 postman 访问 `/register`，查看返回结果。

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

通过全局异常处理，可以保证系统在出现异常的情况下，出参结构是统一的，结合接口正常情况下的返回结构，就可以保证整个系统的出参结构一致，这种方式不管是跟前端交互还是跟其它系统交互，都很重要。

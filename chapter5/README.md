第五章：SpringBoot项目中配置Filter
---

### 课程目标

学会在 SpringBoot 项目中配置 Filter，实现对请求出入参的日志打印。

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

引入 `spring-boot-starter-web` 的依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

#### 编码

1. 编写 Filter

实现对 application/json 请求的拦截，因为请求内容使用流，所以需要多一次封装

```java
public class TraceFilter extends GenericFilterBean {

    private static final Logger log = LoggerFactory.getLogger(TraceFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        long startTime = System.currentTimeMillis();
        // 封装流
        TraceServletRequestWrapper requestWrapper = new TraceServletRequestWrapper(request);
        TraceServletResponseWrapper responseWrapper = new TraceServletResponseWrapper(response);
        // 执行
        chain.doFilter(requestWrapper, responseWrapper);
        long endTime = System.currentTimeMillis();
        // 执行日志打印
        String requestBody = getRequestBody(requestWrapper);
        String responseBody = getResponseBody(responseWrapper);
        StringBuilder builder = new StringBuilder();
        if (requestBody == null || requestBody.isEmpty()) {
            requestBody = "{}";
        }
        if (responseBody == null || responseBody.isEmpty()) {
            responseBody = "{}";
        }
        builder.append("请求url:").append(request.getRequestURL()).append("|");
        builder.append("耗时:").append(endTime - startTime).append("-");
        builder.append("请求内容:").append(requestBody).append(",");
        builder.append("返回内容:").append(responseBody).append(",");
        logger.info(builder.toString());
    }

    private String getResponseBody(TraceServletResponseWrapper responseWrapper) {
        TraceServletOutputStream traceOutputStream = responseWrapper.getTraceOutputStream();
        String content;
        if ((null != traceOutputStream) && (content = traceOutputStream.getContent()) != null && !content.isEmpty()) {
            return new String(content.getBytes(), StandardCharsets.UTF_8);
        }
        return null;
    }

    private String getRequestBody(TraceServletRequestWrapper requestWrapper) {
        TraceServletInputStream traceInputStream = requestWrapper.getTraceInputStream();
        if (traceInputStream != null) {
            return new String(traceInputStream.getContent().getBytes(), StandardCharsets.UTF_8);
        }
        return null;
    }

}
```

请求封装

```java
public class TraceServletRequestWrapper extends HttpServletRequestWrapper {

    TraceServletInputStream traceInputStream;

    public TraceServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (traceInputStream == null) {
            traceInputStream = new TraceServletInputStream(super.getInputStream());
        }
        return traceInputStream;
    }

    public TraceServletInputStream getTraceInputStream() {
        return traceInputStream;
    }

}
```

响应封装

```java
public class TraceServletResponseWrapper extends HttpServletResponseWrapper {

    private TraceServletOutputStream traceOutputStream;
    private HttpServletResponse response;

    public TraceServletResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (null == traceOutputStream) {
            traceOutputStream = new TraceServletOutputStream(super.getOutputStream());
        }
        return traceOutputStream;
    }

    public TraceServletOutputStream getTraceOutputStream() {
        return traceOutputStream;
    }

}
```

输入流封装

```java
public class TraceServletInputStream extends ServletInputStream {

    ServletInputStream servletInputStream;

    private StringBuilder buffer;

    public TraceServletInputStream(ServletInputStream inputStream) {
        this.servletInputStream = inputStream;
        buffer = new StringBuilder();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        int data = servletInputStream.read();
        buffer.append((char)data);
        return data;
    }

    @Override
    public int read(byte b[]) throws IOException {
        int data = servletInputStream.read(b);
        if(data > 0) {
            buffer.append(new String(b));
        }
        return data;
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        int data = servletInputStream.read(b, off, len);
        if(data > 0) {
            buffer.append(new String(b, off, data));
        }
        return data;
    }

    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        int data = servletInputStream.readLine(b, off, len);
        if(data > 0) {
            buffer.append(new String(b, off, data));
        }
        return data;
    }

    public String getContent() {
        return buffer.toString();
    }

}
```

输出流封装

```java
public class TraceServletOutputStream extends ServletOutputStream {

    private ServletOutputStream outputStream;

    private StringBuilder buffer;

    public TraceServletOutputStream(ServletOutputStream outputStream) {
        this.outputStream = outputStream;
        buffer = new StringBuilder();
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        super.write(b, off, len);
        if(b.length > 0) {
            buffer.append(new String(b, off, len));
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
        if(b.length > 0) {
            buffer.append(new String(b));
        }
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
    }

    public String getContent() {
        return buffer.toString();
    }

}
```

2. 注册 Filter

```java
@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean traceFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new TraceFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

}
```

3. 编写 controller 接口

使用 @RequestBody 方式接收参数

```java
@RestController
public class HelloController {

    @PostMapping("/hello")
    public Map<String, Object> hello(@RequestBody Map<String, Object> map) {
        return map;
    }

}
```

4. 编写项目启动类

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

### 验证结果

#### 使用 Postman 进行请求

启动服务，使用 postman 进行请求，请求体为 `{"name":"user1","sex":1,"birthday":"2000-01-01"}`

#### 使用测试用例验证

因为使用测试用例时不会主动加载 Filter，所以需要在启动测试用例环境时进行注册

```java
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = Application.class)
public class FilterTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new TraceFilter())
                .build();
    }

    @Test
    public void test() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                .post("/hello")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"name\":\"user1\",\"sex\":1,\"birthday\":\"2000-01-01\"}")
        )
        .andDo(MockMvcResultHandlers.print())
        .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
```

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

Filter 用途很多，本文主要不是讲解 Filter 如何使用，而是讲解如何在 SpringBoot 项目中进行 Filter 的注册。

Filter 注册主要是依赖于 FilterRegistrationBean 类，这只是一种方式，另外 Filter 也可以通过 @WebFilter 注解进行自动注册。

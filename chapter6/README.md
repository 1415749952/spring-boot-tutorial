第六章：SpringBoot项目中使用hibernate-validator检查参数
---

`spring-boot-starter-web` 项目中默认已经集成了 `hibernate-validator`

### 相关知识

```
@AssertTrue     // 用于 boolean 字段，该字段只能为 true
@AssertFalse    // 用于 boolean 字段，该字段只能为 false
@DecimalMax     // 用于 Number 字段，只能小于或等于该值
@DecimalMin     // 用于 Number 字段，只能大于或等于该值
@Digits(integer=2,fraction=20) // 检查是否是数字，校验整数位及小数位
@Future         // 检查该字段的日期是否是属于将来的日期
@Length(min=2,max=6)           // 用于字符串，检查字段长度是否在指定范围内
@Max            // 用于 Number 字段，只能小于或等于该值
@Min            // 用于 Number 字段，只能大于或等于该值
@NotNull        // 该字段不能为空
@NotEmpty       // 用于字符串，该字段不能为空字符串
@NotBlank       // 用于字符串，该字段不能为空字符串，忽略空格
@Null           // 该字段必须为空
@Size(min=2,max=4)  // 用于字符串、数组、集合、Map等，检查该字段的size是否在指定范围
```

### 课程目标

学习在 SpringBoot 项目中如何使用 hibernate-validator 进行入参校验

### 操作步骤

#### 添加校验规则

```java
@Getter
@Setter
public class UserBO {

    /**
     * 用户名，长度在6-16个字符之间，必须参数
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 6, max = 16, message = "用户名长度必须在6-16个字符之间")
    private String username;

    /**
     * 出生日期，格式为 yyyy-MM-dd，必须为过去的日期，不必须参数
     */
    @Past(message = "出生日期必须早于当前日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 等级，整数，0-5之间，必须参数
     */
    @NotNull(message = "用户等级不能为空")
    @Min(value = 0, message = "用户等级最小为0")
    @Max(value = 5, message = "用户等级最大为5")
    @Digits(integer = 1, fraction = 0, message = "用户等级必须为整数")
    private Integer level;

}
```

#### 在接口处使用校验

在参数前面添加 @Valid 注解，通过 BindingResult 对象接收校验结果

```java
@RestController
public class UserController {

    @RequestMapping("/register")
    public String doRegister(@Valid UserBO bo, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = result.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
                sb.append(",");
            }
            return sb.toString();
        }
        return "success";
    }

}
```

### 验证结果

启动服务，访问地址 `http://localhost:8080/register?username=1&birthday=2020-01-01&level=6`，可以看到校验失败的错误提示。

### 源码地址

本章源码 : <https://gitee.com/gongm_24/spring-boot-tutorial.git>

### 总结

一个安全的接口需要对每一个入参进行校验，以保证参数合法性。

### 扩展

#### 自定义校验

1. 创建一个注解，其中 @Constraint 用于指向该注册将使用的自定义校验类

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = ValueRangeValidator.class)
public @interface ValueRange {

    String[] values();

    String message() default "值不正确";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
```

2. 创建自定义校验类

```java
public class ValueRangeValidator implements ConstraintValidator<ValueRange, Object> {

    private String[] values;

    @Override
    public void initialize(ValueRange constraintAnnotation) {
        values = constraintAnnotation.values();
    }

    /**
     * 校验函数
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (String s : values) {
            if (Objects.equals(s, value)) {
                return true;
            }
        }
        return false;
    }

}
```

3. 使用自定义校验

```java
@Getter
@Setter
public class UserBO {

    /**
     * 用户名，长度在6-16个字符之间，必须参数
     */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 6, max = 16, message = "用户名长度必须在6-16个字符之间")
    private String username;

    /**
     * 出生日期，格式为 yyyy-MM-dd，必须为过去的日期，不必须参数
     */
    @Past(message = "出生日期必须早于当前日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * 等级，整数，0-5之间，必须参数
     */
    @NotNull(message = "用户等级不能为空")
    @Min(value = 0, message = "用户等级最小为0")
    @Max(value = 5, message = "用户等级最大为5")
    @Digits(integer = 1, fraction = 0, message = "用户等级必须为整数")
    private Integer level;

    /**
     * 限定性别的值只能是 0、1、2
     */
    @ValueRange(values = {"0", "1", "2"})
    private Integer sex;

}
```

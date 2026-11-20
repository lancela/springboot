package org.example.springboot.exception;


import org.example.springboot.common.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
// 告诉 Spring Boot 这个类负责统一处理 Controller 中发生的异常，并将处理结果转换为 JSON。
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理所有未单独处理的异常
     */
    // 表示当前方法可以处理 Exception 类型的异常。
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 在控制台打印异常，方便开发人员排查
        e.printStackTrace();

        return Result.error("服务器内部错误");
    }

    /**
     * 处理算术异常
     * 这个方法必须和原来的 handleException() 同时保留。
     * Spring 会优先选择更匹配异常类型的处理方法。
     */
    @ExceptionHandler(ArithmeticException.class)
    public Result<Void> handleArithmeticException(
            ArithmeticException e
    ) {
        return Result.error("数学运算异常");
    }
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(
            BusinessException e
    ) {
        return Result.error(e.getMessage());
    }

    /**
     * 处理请求对象参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(
            MethodArgumentNotValidException e
    ) {
//        e.getBindingResult()  获取参数校验结果。
//        .getFieldErrors()获取校验失败的字段。
//        .get(0) 取第一个错误。
//        .getDefaultMessage()获取我们在注解中设置的错误提示。
        String message = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        return Result.error(message);
    }
}

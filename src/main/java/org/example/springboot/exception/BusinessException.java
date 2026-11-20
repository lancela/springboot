package org.example.springboot.exception;

/**
 * 自定义业务异常
 * RuntimeException 是 Java 中的运行时异常。
 * 继承它之后，我们就可以在业务代码中直接使用：
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        // super(message) 会把错误信息传递给父类。
        super(message);
    }
}

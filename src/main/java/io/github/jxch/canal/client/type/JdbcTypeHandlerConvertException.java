package io.github.jxch.canal.client.type;

public class JdbcTypeHandlerConvertException extends RuntimeException {
    public JdbcTypeHandlerConvertException(String message) {
        super(message);
    }

    public JdbcTypeHandlerConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}

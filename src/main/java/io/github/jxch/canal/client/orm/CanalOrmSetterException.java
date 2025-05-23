package io.github.jxch.canal.client.orm;

public class CanalOrmSetterException extends RuntimeException {
    public CanalOrmSetterException(String message) {
        super(message);
    }

    public CanalOrmSetterException(String message, Throwable cause) {
        super(message, cause);
    }
}

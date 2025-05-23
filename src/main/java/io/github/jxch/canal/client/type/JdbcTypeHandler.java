package io.github.jxch.canal.client.type;

import java.sql.JDBCType;

public interface JdbcTypeHandler {

    boolean support(JDBCType jdbcType, String value);

    Object convert(JDBCType jdbcType, String value);


}

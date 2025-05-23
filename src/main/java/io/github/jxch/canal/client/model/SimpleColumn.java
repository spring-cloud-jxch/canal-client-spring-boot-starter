package io.github.jxch.canal.client.model;

import java.sql.JDBCType;

public class SimpleColumn {
    private String name;
    private String value;
    private JDBCType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JDBCType getType() {
        return type;
    }

    public void setType(JDBCType type) {
        this.type = type;
    }
}

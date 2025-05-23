package io.github.jxch.canal.client.model;

import com.alibaba.fastjson2.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.jxch.canal.client.orm.CanalOrm;
import io.github.jxch.canal.client.orm.DefaultCanalOrmSetter;
import io.github.jxch.canal.client.type.JdbcTypeHandlers;

import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleColumns {
    private List<String> names = new ArrayList<>();
    private List<JDBCType> types = new ArrayList<>();
    private List<List<String>> values = new ArrayList<>();

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<JDBCType> getTypes() {
        return types;
    }

    public void setTypes(List<JDBCType> types) {
        this.types = types;
    }

    public List<List<String>> getValues() {
        return values;
    }

    public void setValues(List<List<String>> values) {
        this.values = values;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return names.isEmpty();
    }

    @JsonIgnore
    public Integer getColIndexByName(String name) {
        int colIndex = names.indexOf(name);
        if (colIndex < 0) {
            throw new IllegalArgumentException("没有这个列名: " + name + ". 存在的列名：" + JSON.toJSONString(names));
        }
        return colIndex;
    }

    @JsonIgnore
    public List<String> getValuesByName(String name) {
        int colIndex = getColIndexByName(name);
        return values.stream().map(list -> list.get(colIndex)).collect(Collectors.toList());
    }

    @JsonIgnore
    public <T> List<T> getValuesByName(String name, Class<T> clazz) {
        return getValuesByName(name).stream().map(clazz::cast).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Object> getConvertValuesByName(String name) {
        JDBCType jdbcType = getJDBCTypeByName(name);
        return getValuesByName(name).stream().map(value -> JdbcTypeHandlers.convert(jdbcType, value)).collect(Collectors.toList());
    }

    @JsonIgnore
    public Object getConvertValueByName(String name, int index) {
        return JdbcTypeHandlers.convert(getJDBCTypeByName(name), getValuesByName(name).get(index));
    }

    @JsonIgnore
    public <T> T getConvertValueByName(String name, int index, Class<T> clazz) {
        return DefaultCanalOrmSetter.cast(JdbcTypeHandlers.convert(getJDBCTypeByName(name), getValuesByName(name).get(index)), clazz);
    }

    @JsonIgnore
    public <T> List<T> getConvertValuesByName(String name, Class<T> clazz) {
        JDBCType jdbcType = getJDBCTypeByName(name);
        return getValuesByName(name).stream().map(value -> JdbcTypeHandlers.convert(jdbcType, value)).map(clazz::cast).collect(Collectors.toList());
    }

    @JsonIgnore
    public JDBCType getJDBCTypeByName(String name) {
        return types.get(getColIndexByName(name));
    }

    @JsonIgnore
    public boolean hasName(String name) {
        return names.contains(name);
    }

    @JsonIgnore
    public <T> List<T> getModel(Class<T> clazz) {
        return CanalOrm.convert(this, clazz);
    }

    @JsonIgnore
    public static SimpleColumns byColumnsList(List<List<CanalEntry.Column>> columnsLists) {
        SimpleColumns simpleColumns = new SimpleColumns();
        if (!columnsLists.isEmpty()) {
            List<String> names = columnsLists.get(0).stream().map(CanalEntry.Column::getName).collect(Collectors.toList());
            List<JDBCType> types = columnsLists.get(0).stream().map(CanalEntry.Column::getSqlType).map(JDBCType::valueOf).collect(Collectors.toList());
            List<List<String>> values = columnsLists.stream().map(list -> list.stream().map(CanalEntry.Column::getValue).collect(Collectors.toList())).collect(Collectors.toList());
            simpleColumns.setNames(names);
            simpleColumns.setTypes(types);
            simpleColumns.setValues(values);
        }
        return simpleColumns;
    }

}

package io.github.jxch.canal.client.model;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;
import java.util.stream.Collectors;

public class CanalModel {
    private final CanalEntry.Entry entry;

    public CanalModel(CanalEntry.Entry entry) {
        this.entry = entry;
    }

    public CanalEntry.Entry getEntry() {
        return entry;
    }

    @JsonIgnore
    public String getSchema() {
        return entry.getHeader().getSchemaName();
    }

    @JsonIgnore
    public String getTable() {
        return entry.getHeader().getTableName();
    }

    @JsonIgnore
    public CanalEntry.EventType getEventType() {
        return entry.getHeader().getEventType();
    }

    @JsonIgnore
    public CanalEntry.RowChange getRowChange() {
        try {
            return CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    public List<CanalEntry.RowData> getRowDatasList() {
        return getRowChange().getRowDatasList();
    }

    @JsonIgnore
    public List<List<CanalEntry.Column>> getAfterColumnsLists() {
        return getRowDatasList().stream().map(CanalEntry.RowData::getAfterColumnsList).collect(Collectors.toList());
    }

    @JsonIgnore
    public SimpleColumns getAfterSimpleColumns() {
        return SimpleColumns.byColumnsList(getAfterColumnsLists());
    }

    @JsonIgnore
    public boolean hasAfterColumnsLists() {
        return !getAfterSimpleColumns().isEmpty();
    }

    @JsonIgnore
    public <T> List<T> getAfterModels(Class<T> clazz) {
        return getAfterSimpleColumns().getModel(clazz);
    }

    @JsonIgnore
    public List<List<CanalEntry.Column>> getBeforeColumnsLists() {
        return getRowDatasList().stream().map(CanalEntry.RowData::getBeforeColumnsList).collect(Collectors.toList());
    }

    @JsonIgnore
    public SimpleColumns getBeforeSimpleColumns() {
        return SimpleColumns.byColumnsList(getBeforeColumnsLists());
    }

    @JsonIgnore
    public boolean hasBeforeColumnsLists() {
        return !getBeforeSimpleColumns().isEmpty();
    }

    @JsonIgnore
    public <T> List<T> getBeforeModels(Class<T> clazz) {
        return getBeforeSimpleColumns().getModel(clazz);
    }

}

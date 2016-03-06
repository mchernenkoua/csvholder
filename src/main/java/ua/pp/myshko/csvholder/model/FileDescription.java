package ua.pp.myshko.csvholder.model;

import java.util.List;

/**
 * @author M. Chernenko
 */
public class FileDescription {

    private String fileName;
    private String tableName;
    private List<ColumnMapping> columns;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<ColumnMapping> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnMapping> columns) {
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDbFieldNameByIndex(int index) {
        return columns.get(index).getDbFieldName();
    }
}

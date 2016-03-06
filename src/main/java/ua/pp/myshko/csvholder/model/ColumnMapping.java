package ua.pp.myshko.csvholder.model;

/**
 * @author M. Chernenko
 */
public class ColumnMapping {

    private String fileColumnName;
    private String dbFieldName;

    public ColumnMapping() {

    }

    public ColumnMapping(String fileColumnName, String dbFieldName) {
        this.fileColumnName = fileColumnName;
        this.dbFieldName = dbFieldName;
    }

    public String getFileColumnName() {
        return fileColumnName;
    }

    public void setFileColumnName(String fileColumnName) {
        this.fileColumnName = fileColumnName;
    }

    public String getDbFieldName() {
        return dbFieldName;
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }


}

package ua.pp.myshko.csvholder.model;

import java.util.Arrays;
import java.util.List;

/**
 * @author M. Chernenko
 */
public class FileLine {

    List<String> dataValue;

    public FileLine() {

    }

    public FileLine(String[] dataValue) {
        this.dataValue = Arrays.asList(dataValue);
    }

    public List<String> getDataValue() {
        return dataValue;
    }

    public void setDataValue(List<String> dataValue) {
        this.dataValue = dataValue;
    }
}

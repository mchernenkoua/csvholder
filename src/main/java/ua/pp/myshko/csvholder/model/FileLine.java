package ua.pp.myshko.csvholder.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author M. Chernenko
 */
public class FileLine {

    Map<String, String> dataValue;

    public FileLine(String[] dataValue, List<String> columns) {

        this.dataValue = new HashMap<>();
        for (int i = 0; i < dataValue.length; i++) {
            this.dataValue.put(columns.get(i), dataValue[i]);
        }
    }

    public String getDataValue(String column) {
        return dataValue.get(column);
    }

    public  Map<String, String> getDataValues() {
        return dataValue;
    }
}

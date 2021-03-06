package ua.pp.myshko.csvholder.services;

import ua.pp.myshko.csvholder.CSVHolderException;
import ua.pp.myshko.csvholder.model.ColumnMapping;
import ua.pp.myshko.csvholder.model.FileDescription;

import java.util.List;

/**
 * @author M. Chernenko
 */
public interface CSVHolderService {

    FileDescription receiveFileDescription(String filePath) throws CSVHolderException;

    void saveData(List<ColumnMapping> columnList, String filePath, String tableNameString) throws CSVHolderException;
}

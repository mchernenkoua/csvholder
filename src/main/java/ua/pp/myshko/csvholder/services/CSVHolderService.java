package ua.pp.myshko.csvholder.services;

import ua.pp.myshko.csvholder.model.ColumnMapping;
import ua.pp.myshko.csvholder.model.FileDescription;
import ua.pp.myshko.csvholder.model.FileLine;

import java.util.Collection;
import java.util.List;

/**
 * @author M. Chernenko
 */
public interface CSVHolderService {

    List<String> readFileTitle(String filePath) throws CSVHolderException;

    List<FileLine> readData(String filePath) throws CSVHolderException;

    FileDescription findFileDescription(String filePath);

    Collection<? extends String> getTableList();

    void saveData(List<FileLine> fileLines, FileDescription fd) throws CSVHolderException;

    void saveMapping() throws CSVHolderException;

    FileDescription addFileDescription(String filePath, List<ColumnMapping> columnList, String tableName);

}

package ua.pp.myshko.csvholder.services;

import ua.pp.myshko.csvholder.CSVHolderException;
import ua.pp.myshko.csvholder.model.ColumnMapping;

import java.util.Collection;

/**
 * @author M. Chernenko
 */
public interface EntityMapper {
    void saveMapping(String fileName, String tableName, Collection<ColumnMapping> columns) throws CSVHolderException;
}

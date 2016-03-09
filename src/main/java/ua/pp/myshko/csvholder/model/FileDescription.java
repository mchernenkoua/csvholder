package ua.pp.myshko.csvholder.model;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ua.pp.myshko.csvholder.CSVHolderException;
import ua.pp.myshko.csvholder.utils.HibernateUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author M. Chernenko
 */
public class FileDescription {

    private static final String WORD_REGEXP = ";";
    public static final int HEADER_SIZE = 1;

    private String fileName;
    private String tableName;
    private Map<String, ColumnMapping> columns;

    public FileDescription(String filePath) {
        this.fileName = filePath;
    }

    public void init() {
        tableName = Paths.get(fileName).getFileName().toString();
        tableName = tableName.replaceFirst("\\..+", "");
        columns = new LinkedHashMap<>();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFileName() {
        return fileName;
    }

    public Collection<ColumnMapping> getColumns() {
        return columns.values();
    }

    public String getTableName() {
        return tableName;
    }

    public void updateColumns() throws CSVHolderException {
        List<String> columnNames = readFileTitle(fileName);

        Map<String, ColumnMapping> oldColumns = new HashMap<>();
        oldColumns.putAll(columns);

        columns.clear();
        for(String name : columnNames) {
            ColumnMapping columnMapping = oldColumns.get(name);
            if (columnMapping == null) {
                columnMapping = new ColumnMapping(name, name);
            }
            columns.put(name, columnMapping);
        };
    }

    protected List<String> readFileTitle(String filePath) throws CSVHolderException {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {

            return reader.lines().limit(HEADER_SIZE)
                    .flatMap(line -> Stream.of(line.split(WORD_REGEXP)))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new CSVHolderException(e);
        }
    }

    public void saveFileDataToDB() throws CSVHolderException {

        Path path = Paths.get(getFileName());

        HibernateUtil.updateEntityMapping(this);

        List<String> columnList = columns.values().stream()
                .map(column -> column.getDbFieldName())
                .collect(Collectors.toList());

        try (
            BufferedReader reader = Files.newBufferedReader(path);
            Session session = HibernateUtil.openSession();
                ) {

            Transaction transaction = session.beginTransaction();

            reader.lines().skip(HEADER_SIZE)
                    .map(line -> new FileLine(line.split(WORD_REGEXP), columnList))
                    .forEach(fileLine -> {
                        session.saveOrUpdate(getTableName(), fileLine.getDataValues());
                    });

            transaction.commit();

        } catch (HibernateException | IOException e) {
            throw new CSVHolderException(e);
        }
    }
}

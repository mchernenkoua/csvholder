package ua.pp.myshko.csvholder.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ua.pp.myshko.csvholder.model.ColumnMapping;
import ua.pp.myshko.csvholder.model.FileDescription;
import ua.pp.myshko.csvholder.model.FileLine;
import ua.pp.myshko.csvholder.services.CSVHolderException;
import ua.pp.myshko.csvholder.services.CSVHolderService;
import ua.pp.myshko.csvholder.utils.HibernateUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author M. Chernenko
 */
public class CSVHolderServiceImpl implements CSVHolderService {

    private static final String WORD_REGEXP = ";";
    public static final int HEADER_SIZE = 1;

    private Gson gson;
    private String propertiesFileName;
    private List<FileDescription> fdList;

    public void init(String propertiesFileName) throws CSVHolderException {

        this.propertiesFileName = propertiesFileName;

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        fdList = new ArrayList<>();
        File file = new File(propertiesFileName);
        if(file.exists()) {
            try (
                FileReader fileReader = new FileReader(file);
                JsonReader reader = new JsonReader(fileReader);
                    ) {
                fdList = Arrays.asList(gson.fromJson(reader, FileDescription[].class));
            } catch (IOException e) {
                throw new CSVHolderException(e);
            }
        }
    }

    @Override
    public void saveMapping() throws CSVHolderException {

        String json = gson.toJson(fdList);

        try (
            FileWriter fileWriter = new FileWriter(propertiesFileName);
            BufferedWriter bw = new BufferedWriter(fileWriter);
                ) {
            bw.write(json);
        } catch (IOException e) {
            throw new CSVHolderException(e);
        }
    }

    @Override
    public FileDescription addFileDescription(String filePath, List<ColumnMapping> columnList, String tableName) {

        FileDescription fd = findFileDescription(filePath);
        if(fd == null) {
            fd = new FileDescription();
            fd.setFileName(filePath);
            fdList.add(fd);
        }
        fd.setTableName(tableName);
        fd.setColumns(new ArrayList<>(columnList));
        return fd;
    }

    @Override
    public List<String> readFileTitle(String filePath) throws CSVHolderException {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {

            return reader.lines().limit(HEADER_SIZE)
                    .flatMap(line -> Stream.of(line.split(WORD_REGEXP)))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new CSVHolderException(e);
        }
    }

    @Override
    public void saveData(List<FileLine> fileLines, FileDescription fd) throws CSVHolderException {

        try (Session session = HibernateUtil.openSession()) {
            Transaction transaction = session.beginTransaction();

            fileLines.forEach(fileLine -> {
                Map<String, String> values = new HashMap<>();
                List<String> dataValues = fileLine.getDataValue();
                dataValues.forEach(
                        dataValue -> {
                            int index = dataValues.indexOf(dataValue);
                            String dbFieldName = fd.getDbFieldNameByIndex(index);
                            values.put(dbFieldName, dataValue);
                        });
                session.saveOrUpdate(fd.getTableName(), values);
            });

            transaction.commit();

        } catch (HibernateException e) {
            throw new CSVHolderException(e);
        }
    }

    @Override
    public List<FileLine> readData(String filePath) throws CSVHolderException {

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {

            return reader.lines().skip(HEADER_SIZE)
                    .map(line -> new FileLine(line.split(WORD_REGEXP)))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new CSVHolderException(e);
        }
    }

    @Override
    public FileDescription findFileDescription(String filePath) {
        return fdList.stream()
                .filter(fileDescription -> fileDescription.getFileName().equals(filePath))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Collection<? extends String> getTableList() {
        return fdList.stream()
                .map(FileDescription::getTableName)
                .collect(Collectors.toList());
    }

}

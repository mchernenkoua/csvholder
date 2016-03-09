package ua.pp.myshko.csvholder.services.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import ua.pp.myshko.csvholder.model.ColumnMapping;
import ua.pp.myshko.csvholder.model.FileDescription;
import ua.pp.myshko.csvholder.CSVHolderException;
import ua.pp.myshko.csvholder.services.CSVHolderService;

import java.io.*;
import java.util.*;

/**
 * @author M. Chernenko
 */
public class CSVHolderServiceImpl implements CSVHolderService {

    private Gson gson;
    private String propertiesFileName;
    private Map<String, FileDescription> fdMap;

    public void init(String propertiesFileName) throws CSVHolderException {

        this.propertiesFileName = propertiesFileName;

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        fdMap = new HashMap<>();
        File file = new File(propertiesFileName);
        if(file.exists()) {
            try (
                FileReader fileReader = new FileReader(file);
                JsonReader reader = new JsonReader(fileReader);
                    ) {
                FileDescription[] fdArray = gson.fromJson(reader, FileDescription[].class);
                for(FileDescription fd : fdArray) {
                    fdMap.put(fd.getFileName(), fd);
                }
            } catch (IOException e) {
                throw new CSVHolderException(e);
            }
        }
    }

    public void saveMapping() throws CSVHolderException {

        String json = gson.toJson(fdMap.values());

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
    public FileDescription receiveFileDescription(String filePath) throws CSVHolderException {

        FileDescription fd = fdMap.get(filePath);
        if (fd == null) {
            fd = new FileDescription(filePath);
            fd.init();
            fdMap.put(filePath, fd);
        }
        fd.updateColumns();
        return fd;
    }

    @Override
    public void saveData(List<ColumnMapping> columnList, String filePath, String tableNameString) throws CSVHolderException {
        FileDescription fd = fdMap.get(filePath);
        fd.setTableName(tableNameString);
        saveMapping();
        fd.saveFileDataToDB();
    }
}

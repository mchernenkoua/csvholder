package ua.pp.myshko.csvholder.app;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import ua.pp.myshko.csvholder.model.ColumnMapping;
import ua.pp.myshko.csvholder.model.FileDescription;
import ua.pp.myshko.csvholder.services.CSVHolderException;
import ua.pp.myshko.csvholder.services.CSVHolderService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author M. Chernenko
 */
public class Controller {

    public static final String OPEN_FILE_DIALOG_TITLE = "Open CSV-file";

    private File file;
    private CSVHolderService csvHolderService;

    @FXML private Parent root;
    @FXML private TextField fileName;
    @FXML private ComboBox<String> tableName;
    @FXML private TextArea messageArea;
    @FXML private TableView<ColumnMapping> table;
    @FXML private TableColumn<ColumnMapping, String> fileColumnName;
    @FXML private TableColumn<ColumnMapping, String> dbFieldName;

    public void setCsvHolderService(CSVHolderService csvHolderService) {
        this.csvHolderService = csvHolderService;
    }

    @FXML
    protected void handleSelectFileButtonAction(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(OPEN_FILE_DIALOG_TITLE);
        if (file != null) {
            fileChooser.setInitialDirectory(new File(file.getParent()));
        }
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("csv-files", "*.csv")
        );
        file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            fileName.setText(file.getAbsolutePath());
            if (table.getItems().size() == 0) {
                try {
                    reloadFileDescription();
                } catch (IOException e) {
                    handleException(e);
                }
            }
        }
    }

    @FXML
    protected void handleReadFileButtonAction(ActionEvent actionEvent) {
        if (file != null) {
            if (table.getItems().size() > 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation Dialog");
                alert.setHeaderText("Table not empty, ");
                alert.setContentText("Continue?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    try {
                        reloadFileDescription();
                    } catch (IOException e) {
                        handleException(e);
                    }
                }
            }
        }
    }

    @FXML
    protected void handleSaveDataButtonAction(ActionEvent actionEvent) {

        List<ColumnMapping> columnList = table.getItems()
                                                .stream()
                                                .collect(Collectors.toList());

        String filePath = file.getPath();
        FileDescription fd = csvHolderService.addFileDescription(filePath, columnList, tableName.getValue());

        try {
            csvHolderService.saveMapping();
            csvHolderService.saveData(csvHolderService.readData(filePath), fd);
        } catch (CSVHolderException e) {
            handleException(e);
        }
    }

    private void reloadFileDescription() throws IOException {

        ObservableList<ColumnMapping> items = table.getItems();
        items.remove(0, items.size());

        initTable();

        FileDescription fd = csvHolderService.findFileDescription(file.getPath());
        if (fd != null) {
            tableName.setValue(fd.getTableName());
        } else {
            tableName.setValue(getShortFileName());
        }
        tableName.getItems().addAll(csvHolderService.getTableList());

        try {
            List<String> columns = csvHolderService.readFileTitle(file.getPath());
            columns.forEach(column -> items.add(new ColumnMapping(column, column)));
        } catch (CSVHolderException e) {
            handleException(e);
        }

    }

    protected void handleException(Exception e) {
        // TODO log error
        e.printStackTrace();
        messageArea.setText(messageArea.getText() + "\n" + e.getMessage());
    }

    private void initTable() {
        fileColumnName.setCellValueFactory(new PropertyValueFactory<>("fileColumnName"));
        dbFieldName.setCellValueFactory(new PropertyValueFactory<>("dbFieldName"));

        dbFieldName.setCellFactory(TextFieldTableCell.forTableColumn());
        dbFieldName.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<ColumnMapping, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<ColumnMapping, String> t) {
                        int row = t.getTablePosition().getRow();
                        t.getTableView().getItems()
                                .get(row)
                                .setDbFieldName(t.getNewValue());
                    }
                }
        );
    }

    public String getShortFileName() {
        return file.getName().replaceFirst("\\..+", "");
    }
}

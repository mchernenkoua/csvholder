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

/**
 * @author M. Chernenko
 */
public class Controller {

    public static final String OPEN_FILE_DIALOG_TITLE = "Open CSV-file";

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

    public void initController() {
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

    @FXML
    protected void handleSelectFileButtonAction(ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(OPEN_FILE_DIALOG_TITLE);
        File file = new File(fileName.getText());
        if (file.exists()) {
            fileChooser.setInitialDirectory(file.getParentFile());
        }
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("csv-files", "*.csv")
        );
        file = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (file != null) {
            fileName.setText(file.getAbsolutePath());

            try {
                reloadFileDescription();
            } catch (IOException e) {
                handleException(e);
            }
        }
    }

    @FXML
    protected void handleReadFileButtonAction(ActionEvent actionEvent) {

        if (table.getItems().size() > 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Table not empty, ");
            alert.setContentText("Do you want refill table?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK){
                return;
            }
        }

        try {
            reloadFileDescription();
        } catch (IOException e) {
            handleException(e);
        }
    }

    @FXML
    protected void handleSaveDataButtonAction(ActionEvent actionEvent) {

        List<ColumnMapping> columnList = table.getItems();
        String filePath = fileName.getText();
        String tableNameString = tableName.getValue();

        try {
            csvHolderService.saveData(columnList, filePath, tableNameString);
        } catch (CSVHolderException e) {
            handleException(e);
        }
    }

    private void reloadFileDescription() throws IOException {

        ObservableList<ColumnMapping> items = table.getItems();
        String filePath = fileName.getText();

        items.clear();

        try {
            FileDescription fd = csvHolderService.receiveFileDescription(filePath);
            tableName.setValue(fd.getTableName());
            fd.getColumns().forEach(column -> items.add(column));
        } catch (CSVHolderException e) {
            handleException(e);
        }

    }

    protected void handleException(Exception e) {
        // TODO log error
        e.printStackTrace();
        messageArea.setText(messageArea.getText() + "\n" + e.getMessage());
    }
}

package ua.pp.myshko.csvholder.app;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ua.pp.myshko.csvholder.services.impl.CSVHolderServiceImpl;

import java.net.URL;
import java.nio.file.Paths;

/**
 * @author M. Chernenko
 */
public class Main extends Application {

    public static final String FXML_LOCATION = "/main.fxml";
    public static final String PROP_LOCATION = "testData/prop.json";
    public static final String APP_TITLE = "CSV holder";

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL formDescription = getClass().getResource(FXML_LOCATION);
        FXMLLoader fxmlLoader = new FXMLLoader(formDescription);
        Parent root = fxmlLoader.load();
        initStage(primaryStage, root);

        Controller controller = fxmlLoader.getController();
        controller.initController();
        CSVHolderServiceImpl csvHolderService = new CSVHolderServiceImpl();
        String propFileName = Paths.get(PROP_LOCATION).toAbsolutePath().toString();
        csvHolderService.init(propFileName);
        controller.setCsvHolderService(csvHolderService);
    }

    private void initStage(Stage primaryStage, Parent root) {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(new Scene(root, 500, 275));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    stop();
                } catch (Exception e) {
                    //TODO log error
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}

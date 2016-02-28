package mp3.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mp3.controllers.MainController;
import mp3.util.DbManager;

import java.net.URL;

/**
 * Entry point. Extends application - basic requirement according to the JavaFX app structure
 */
public class Main extends Application{

    /**
     * The initial method.
     * @param primaryStage stage to show
     * @throws Exception
     */
        @Override
        public void start(Stage primaryStage) throws Exception{
            //initialize database vendor driver class (connected with a library)
            Class.forName("org.sqlite.JDBC");
            //initialize database manager
            DbManager.getInstance().init();
            //load the view
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("main.fxml"));
            //get the root
            Parent root = loader.load();
            //retrieve controller
            MainController controller = loader.getController();
            //set scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getClassLoader().getResource("style.css").toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("MP3 Player");
            //initialize controller
            controller.init();
            //show stage
            stage.show();
        }


    /**
     * Entry point of the app
     * @param args command line arguments
     */
        public static void main(String[] args) {
            launch(args);
        }
}

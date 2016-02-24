package mp3.views;

import com.sun.glass.events.WindowEvent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mp3.controllers.MainController;

/**
 * Created by Spring on 2/24/2016.
 */
public class Main extends Application{

        @Override
        public void start(Stage primaryStage) throws Exception{

            FXMLLoader loader = new FXMLLoader(Main.class.getResource("main.fxml"));
            Parent root = loader.load();
            MainController controller = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("MP3 Player");
            controller.init();
            stage.show();
        }


        public static void main(String[] args) {
            launch(args);
        }
}

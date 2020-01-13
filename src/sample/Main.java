package sample;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUI/main.fxml"));

        primaryStage.setTitle("Hello World");
        primaryStage.setFullScreen(true);
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.show();
        root.getStylesheets().add("sample/resources/playlistbutton.css");
        root.getStylesheets().add("sample/resources/scrollbar.css");
    }


    public static void main(String[] args) {
        launch(args);
    }

}

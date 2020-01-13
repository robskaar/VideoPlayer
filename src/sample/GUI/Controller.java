package sample.GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private MediaView mediaV;

    @FXML private Button playPause;
    @FXML private Button mute;
    @FXML private Button searchButton;

    @FXML private ToolBar playBar;

    @FXML
    private ChoiceBox<?> paneChoice;

    @FXML
    private TextField searchBox;

    @FXML
    private TilePane mediaviewTilePane;

    @FXML
    private MediaView mediaView1;

    private MediaPlayer mp;
    private Media me;

    private boolean playing = false;          // Used to alter if play button shall play or pause


    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources) {
        // Build the path to the location of the media file
        String path = new File("src/sample/Media/Black Mirror - Crocodile _ Official Trailer _ Netflix.mp4").getAbsolutePath();
        System.out.println(path);
        // Create new Media object (the actual media content)
        me = new Media(new File(path).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);
        //
        mediaView1.setMediaPlayer(mp);
        mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        //mp.setAutoPlay(false);
        //playBar.setVisible(false);    // Playbar initially not visible. Visible on mouse enter
        searchButton.getStyleClass().add("button-searchButton");



    }

    /*@FXML
    /**
     * Handler for the play button
     */
    /*private void handlePlay() {
        // Play the mediaPlayer with the attached media

        if(!playing){
            mp.play();
            playing = true;
            playPause.setText("||");
        }
        else{
            mp.pause();
            playing = false;
            playPause.setText(">");
        }

    }
    */

  /*  @FXML

    private void showPlayBar(){
        playBar.setVisible(true);
    }

    @FXML

    private void hidePlayBar(){
        playBar.setVisible(false);
    }*/

public void createMediaView(String urlPath){
    String path = new File(urlPath).getAbsolutePath();

}


    public void mousePressed() {
    }
}

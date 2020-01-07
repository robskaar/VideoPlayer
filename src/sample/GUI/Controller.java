package sample.GUI;

import javafx.scene.control.ToolBar;
import javafx.scene.media.*;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private MediaView mediaV;

    @FXML private Button playPause;
    @FXML private Button mute;

    @FXML private ToolBar playBar;

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
        mediaV.setMediaPlayer(mp);
        // mp.setAutoPlay(true);
        // If autoplay is turned of the method play(), stop(), pause() etc controls how/when medias are played
        mp.setAutoPlay(false);

        playBar.setVisible(false);    // Playbar initially not visible. Visible on mouse enter

    }

    @FXML
    /**
     * Handler for the play button
     */
    private void handlePlay() {
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

    @FXML

    private void showPlayBar(){
        playBar.setVisible(true);
    }

    @FXML

    private void hidePlayBar(){
        playBar.setVisible(false);
    }




}

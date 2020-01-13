package sample.GUI;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.*;
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.Duration;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

public class playerController implements Initializable {

    @FXML private MediaView mediaV;
    @FXML private Button test;
    @FXML private VBox settings;
    @FXML private ToolBar playBar;
    @FXML private Slider volumeSlider;
    @FXML private Slider videoProgressSlider;
    @FXML private Slider playbackSpeedSlider;
    @FXML private ImageView volumeMuted;
    @FXML private ImageView volumePlaying;
    @FXML private ImageView playButton;
    @FXML private ImageView pauseButton;
    @FXML private ImageView mainMenuButton;
    @FXML private ImageView settingsButton;
    @FXML private Label timeViewer;


    private MediaPlayer mp;
    private Media me;

    private double savedVolumeSlider = 50;                      // Stores previous volume of video before muting
    private final double PLAYBAR_FADE_OPACITY = 0.6;            // Opacity goal of objects that get faded in
    private final int PLAYBAR_FADE_IN = 200;                    // Time to fade in objects
    private final int PLAYBAR_FADE_OUT = 1000;                  // Time to fade out objects


    public playerController(){
    }

    /**
     * This method is invoked automatically in the beginning. Used for initializing, loading data etc.
     *
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources) {

        // Build the path to the location of the media file
        String path = new File("src/sample/Media/Black Mirror - Crocodile _ Official Trailer _ Netflix.mp4").getAbsolutePath();
        // Create new Media object
        me = new Media(new File(path).toURI().toString());
        // Create new MediaPlayer and attach the media to be played
        mp = new MediaPlayer(me);

        mediaV.setMediaPlayer(mp);

        mp.setAutoPlay(false);

        mp.setOnEndOfMedia(()->{
            // If more videos play
            // Else return to main menu
        });

        volumeSlider.setValue(50);        // Default volume 50%
        playbackSpeedSlider.setValue(1);  // Default speed normal

        volumeSliderListener();      // Start video slider listener
        videoStatusListener();
        timeListener();              // Start time listener
        playbackSliderListener();

    }

    /**
     * Handler for the play/pause button
     *
     */
    @FXML
    private void handlePlay() {
        // Play the mediaPlayer with the attached media

        videoProgressSlider.setMax(mp.getTotalDuration().toSeconds());   // Set slider length to video length

        if(mp.getStatus() != MediaPlayer.Status.PLAYING){
            mp.play();

        }
        else{
            mp.pause();
        }

    }

    /**
     * This method hides/shows setting option, when option button is pressed
     */
    @FXML
    private void handleSettings(){

        if(settings.isVisible()){
            settings.setVisible(false);
        }
        else{
            settings.setVisible(true);
        }


    }

    /**
     * This method handles the mute/unmute button
     */
    @FXML
    public void muteHandler(){

        if(mp.getVolume() > 0){
            savedVolumeSlider = volumeSlider.getValue();  // Save previous volume before muting
            volumePlaying.setVisible(false);              // Set mute icon
            volumeMuted.setVisible(true);
            volumeSlider.setValue(0);
        }
        else{
            volumeMuted.setVisible(false);                  // Set not muted icon
            volumePlaying.setVisible(true);
            volumeSlider.setValue(savedVolumeSlider);    // Return to previous volume from before muting
        }

    }

    /**
     * This method makes program return to main menu
     */
    @FXML
    public void handleMainMenuButton(){
        System.out.println("Returned to main menu");
    }

    /**
     * This method enables user to click on time slider and choose new place in video
     */
    @FXML
    public void handleClickOnVideoSlider(){
        mp.seek(Duration.seconds(videoProgressSlider.getValue()));
    }

    /**
     * This method plays a new video
     * @param path path to the media that should be played
     */
    private void mediaPicker(String path){

        mp.stop();  // Stop previous video

        // Build the path to the location of the media file
        String videoPath = new File(path).getAbsolutePath();

        // Create new Media object (the actual media content)
        me = new Media(new File(videoPath).toURI().toString());

        mp = new MediaPlayer(me);

        mediaV.setMediaPlayer(mp);

        mp.setAutoPlay(true);

        timeListener();
        videoStatusListener();
        volumeSliderListener();      // Start video slider listener
        playbackSliderListener();
    }

    /**
     * This method is an example of a thumbnail for a video.
     */
    @FXML
    private void newVideoButton(){
        mediaPicker("src/sample/Media/DHL - Antarctica.mp4");
    }

    /**
     * Shows playbar when mouse enters area
     */
    @FXML
    private void showPlayBar(){

        // Fade in playbar if mouse has entered area
        // Unless the video is paused or settings is enabled. In that case bar is already showing
        if(mp.getStatus() == MediaPlayer.Status.PLAYING && !settings.isVisible()){
            fadeObjects(0, PLAYBAR_FADE_OPACITY,playBar, PLAYBAR_FADE_IN);
        }
    }

    /**
     * Hides playbar when mouse is exits area
     */
    @FXML
    private void hidePlayBar(){

        // Hide playbar when mouse exits area, unless settings is enabled
        if(mp.getStatus() == MediaPlayer.Status.PLAYING && !settings.isVisible()){
            fadeObjects(PLAYBAR_FADE_OPACITY,0,playBar, 200);
        }
    }

    /**
     * This method can fade in/out Nodes. Ex. the playbar
     * @param from Opacity level to fade from ( 0 to 1 )
     * @param to   Opacity level to fade to ( 0 to 1)
     * @param obj  fxml object that will be affected by the fade
     * @param duration  duration of the fade
     */
    private void fadeObjects(double from, double to, Node obj, int duration){
        FadeTransition ft = new FadeTransition(Duration.millis(duration),obj);
        ft.setFromValue(from);
        ft.setToValue(to);
        ft.play();
    }

    /**
     * This method observes time when a video is playing
     * Also using time to update timer and video slider
     */
    public void timeListener(){

        // Listening when a video is playing

        mp.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {

                // Updates video slider to current time stamp
                videoProgressSlider.setValue(mp.getCurrentTime().toSeconds());

                // Prints current and total time of video
                timeViewer.setText(String.format("%02d:%02.0f / %02d:%02.0f",
                        (int)mp.getCurrentTime().toMinutes(),
                        mp.getCurrentTime().toSeconds() % 60,
                        (int)mp.getTotalDuration().toMinutes(),
                        mp.getTotalDuration().toSeconds() % 60));
            }
        });




    }

    /**
     * This method listens for the status of the video and updates play/pause icon
     */
    public void videoStatusListener(){

        mp.statusProperty().addListener(new ChangeListener<MediaPlayer.Status>() {
            @Override
            public void changed(ObservableValue<? extends MediaPlayer.Status> observable, MediaPlayer.Status oldValue, MediaPlayer.Status newValue) {

                if(mp.getStatus() == MediaPlayer.Status.PLAYING){
                    pauseButton.setVisible(true);
                    playButton.setVisible(false);
                }
                else if(mp.getStatus() == MediaPlayer.Status.PAUSED){
                    playButton.setVisible(true);
                    pauseButton.setVisible(false);
                }
            }
        });
    }

    /**
     * This method listens for changes to
     */
    public void volumeSliderListener(){

        // Listening for changes to the volume slider

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                mp.setVolume(volumeSlider.getValue()/100);    // Set volume according to slider

                if(volumeSlider.getValue() == 0){             // Set mute icon
                    volumePlaying.setOpacity(0);
                    volumeMuted.setOpacity(100);
                }
                else{                                         // Set not mute icon
                    volumeMuted.setOpacity(0);
                    volumePlaying.setOpacity(100);
                }

            }
        });
    }

    /**
     * This method adjusts video playback speed on change from slider
     */
    public void playbackSliderListener(){

        playbackSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                mp.setRate(playbackSpeedSlider.getValue());

            }
        });
    }

    /**
     * This method handles keypresses
     * SPACE = pause/play
     * j = backwards 5 sec
     * l = forward 5 sec
     * m = main menu
     * @param kc
     */
    @FXML
    public void handleKeyPressed(KeyEvent kc){

        KeyCode key = kc.getCode();

        if(key == KeyCode.SPACE){

            handlePlay();                       // Toogle play/pause button

            if(mp.getStatus() != MediaPlayer.Status.PLAYING && !settings.isVisible()){
                fadeObjects(PLAYBAR_FADE_OPACITY,0,playBar, PLAYBAR_FADE_OUT);
            }
            else if(!settings.isVisible()){
                fadeObjects(0, PLAYBAR_FADE_OPACITY,playBar, PLAYBAR_FADE_IN);
            }

        }

        else if(key == KeyCode.J){

            mp.seek(Duration.seconds(videoProgressSlider.getValue() - 5 ));  // Go backwards in video 5 seconds

            // Show and hide playbar unless settings is enabled
            if(!settings.isVisible() && mp.getStatus() == MediaPlayer.Status.PLAYING){
                fadeObjects(0, PLAYBAR_FADE_OPACITY,playBar, PLAYBAR_FADE_IN);
                fadeObjects(PLAYBAR_FADE_OPACITY,0,playBar, PLAYBAR_FADE_OUT);
            }

        }

        else if(key == KeyCode.L){
            mp.seek(Duration.seconds(videoProgressSlider.getValue() + 5 ));  // Go forward in video 5 seconds

            // Show and hide playbar unless settings is enabled
            if(!settings.isVisible() && mp.getStatus() == MediaPlayer.Status.PLAYING){
                fadeObjects(0, PLAYBAR_FADE_OPACITY,playBar, PLAYBAR_FADE_IN);
                fadeObjects(PLAYBAR_FADE_OPACITY,0,playBar, PLAYBAR_FADE_OUT);
            }

        }

        else if(key == KeyCode.M){
            handleMainMenuButton();   // Go to main menu when pressing "m"
        }

    }

}

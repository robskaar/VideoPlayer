package sample.GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.Database.DB;
import sample.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class playListController extends Main implements Initializable {

    @FXML
    private AnchorPane configureMenu;
    @FXML
    private ScrollPane showToViewPlayListVideos;
    @FXML
    private TilePane playListVideoPane;
    @FXML
    private TilePane videoPane;
    @FXML
    private Text noSearchMatch;
    @FXML
    private TextField searchField;
    @FXML
    private Button SearchButton;
    @FXML
    private Button themechanger;

    public playListController() {
    }

    @FXML
    void searchDB(ActionEvent event) {

    }

    @FXML
    void updateVideoplaylist(ActionEvent event) {

    }

    @FXML
    private Label playlistName;
    @FXML
    private Label instructions;

    private String currentPlaylist = Controller_MainMenu.getPlayListName();
    private String stylesheet = Controller_MainMenu.getStylesheet();
    private final String DARK_MODE = "sample/GUI/resources/Darkmode.css";
    private final String LIGHT_MODE = "sample/GUI/resources/basic.css";


    /***
     * this method is used to initialize the main menu pane with playlists shown
     * @param location - default override
     * @param resources - default override
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showToViewPlayListVideos.toFront();
        updateVideoPlayList();
        playlistName.setText(Controller_MainMenu.getPlayListName());
    }

    /***
     * does the searches for video's, and calls method to add them to pane
     */
    public void searchDbOnConfigureStage() {
        System.out.println("DEBUGGING HITS DB");
        setInstructionstoAdd();
        clearPane(videoPane); // clears videoPane from previous search
        showToViewPlayListVideos.toBack(); // hides play list video's if in front
        videoPane.toFront();
        String searchString = searchField.getText(); // gets the search input
        String[] wordsInSearchString = searchString.split(" "); // splits search input by blank spaces, and inputs seperate words in array
        for (String word : wordsInSearchString) {
            DB.selectSQL("SELECT COUNT(fldFilePath) FROM tblVideo WHERE fldTitle LIKE '%" + word + "%'"); // sql to count videos found
            int amountVideosFound = Integer.parseInt(DB.getData()); //int for videoes found by previous sql query
            DB.selectSQL("SELECT fldFilePath FROM tblVideo WHERE fldTitle LIKE '%" + word + "%'"); // sql query to select filepath from titles like search word
            if (amountVideosFound == 0) { // if no videos found
                noSearchMatch.setOnMouseClicked(event -> { // adds mouse click events to no Match text
                    searchField.clear(); // clears search field
                    searchField.requestFocus(); // sets focus on search field
                });
                noSearchMatch.setVisible(true); // sets the no search match text visible
                videoPane.getChildren().add(noSearchMatch); // adds the text to video pane
            } else { // if videos found
                for (int i = 0; i < amountVideosFound; i++) {
                    addVideosToView(DB.getData(), videoPane); // updateVideoView with videos found
                }
            }
        }
    }

    /***
     * this method is used to add videos to the videoPane and the play list videoPane with video's found from search method
     * @param filepath - filepath of the file we want to add
     * @param tilePane - tile pane passed to add videos to
     */
    private void addVideosToView(String filepath, TilePane tilePane) {
        Button videoInfo = new Button(); // creates button for hover info of video
        videoInfo.setPrefHeight(111);
        videoInfo.setPrefWidth(200);
        videoInfo.setStyle("-fx-background-color: white"); // background to be blurred on hover
        videoInfo.setTextFill(new Color(0, 0, 0, 1)); // text colour
        videoInfo.setOpacity(0.6);// blur
        Media media = new Media(new File(filepath).toURI().toString()); // creates the media from video path
        MediaPlayer video = new MediaPlayer(media); // creates the player from media
        MediaView mediaView = new MediaView(video); // creates mediaview from mediaPlayer
        mediaView.setFitWidth(200);
        mediaView.setFitHeight(200);
        mediaView.setId(filepath);
        mediaView.setOnMouseEntered(event -> {
            videoInfo.toFront();
        });
        videoInfo.setOnMouseExited(event -> {
            videoInfo.toBack();
        });
        videoInfo.setOnMouseClicked(event -> {
            checkIfexists(filepath);
        });


        mediaView.setId(filepath); // sets id to filepath to be used to play videos
        mediaView.setCursor(Cursor.OPEN_HAND); // sets cursor on hover
        Pane videoInfoPane = new Pane(); // info pane created to host mediaview and video info
        videoInfoPane.setId(filepath);
        videoInfoPane.getChildren().add(videoInfo); //adds video info to pane
        videoInfoPane.getChildren().add(mediaView); //adds mediaview to pane
        tilePane.getChildren().add(videoInfoPane); //adds video infoPane to tile pane
    }

    /***
     * method to clear tile panes as i clear panes a lot
     * @param tilePane - the passed tile pane to be cleared
     */
    private void clearPane(TilePane tilePane) {
        tilePane.getChildren().clear();
    }


    /***
     * used to update the playlist pane with current playlists in DB after a new is created
     */

    /***
     * adds videos to the playlistvideopane from a playlist in DB
     */
    public void updateVideoPlayList() {
        showToViewPlayListVideos.toFront();
        System.out.println(currentPlaylist);
        setInstructionstoRemove();
        clearPane(playListVideoPane); // clear pane to start of fresh
        String currentPlayList = currentPlaylist; // gets the playlist name
        DB.selectSQL("SELECT COUNT(fldPlayListName) FROM tblVideoPlayLists WHERE fldPlayListName = '" + currentPlayList + "'"); // sql query to count amount of videos
        int amountVideosFound = Integer.parseInt(DB.getData()); // amount of playlists found
        DB.selectSQL("SELECT fldFilePath FROM tblVideoPlayLists WHERE fldPlayListName = '" + currentPlayList + "'");//sql query to find filepath of videos in playlist
        for (int i = 0; i < amountVideosFound; i++) { // for each video
            addVideosToView(DB.getData(), playListVideoPane); // adds videos to pane
        }
    }

    private void checkIfexists(String path) {
        DB.selectSQL("SELECT COUNT(fldPlayListName)  FROM tblVideoPlaylists WHERE fldFilePath = '" + path + "' AND fldPlayListName = '" + currentPlaylist + "'");
        int videoNum = Integer.parseInt(DB.getData());
        System.out.println(videoNum);
        if (videoNum == 1) {
            deleteVideoFromPlaylist(path);
        } else {
            addVideoToPlaylist(path);
        }

    }

    private void deleteVideoFromPlaylist(String path) {
        DB.pendingData = false;
        DB.deleteSQL("DELETE FROM tblVideoPlaylists WHERE fldFilePath = '" + path + "' AND fldPlayListName = '" + currentPlaylist + "'");
        for (Node child : showToViewPlayListVideos.getChildrenUnmodifiable()) {
            showToViewPlayListVideos.getChildrenUnmodifiable().removeIf(node -> (child.getId() == path));
        }
        updateVideoPlayList();

    }

    private void addVideoToPlaylist(String path) {
        DB.pendingData = false;
        DB.insertSQL("INSERT INTO tblVideoPlaylists \n" +
                "VALUES ('" + currentPlaylist + "','" + path + "');");
    }


    /**
     * This method changes the scene
     *
     * @param path Input the name of the fxml file you want to change to
     */
    public void changeScene(String path) {

        try {
            Parent mainMenuParent = FXMLLoader.load(getClass().getResource(path));
            Scene mainScene = new Scene(mainMenuParent);
            Stage window = (Stage) configureMenu.getScene().getWindow();
            window.setScene(mainScene);
            window.setFullScreen(true);
            window.show();
            mainScene.getStylesheets().add(stylesheet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     *Goes to the Menu, is public for the application to use it
     */
    public void goToMainMenu() {
        changeScene("mainMenu.fxml");
    }

    /***
     * deletes a playerlist from the Database, and returns to the mainMenu
     */
    public void deletePlaylist() {
        DB.pendingData = false;
        DB.deleteSQL("DELETE FROM tblPlayList WHERE fldPlayListName = '" + currentPlaylist + "'");
        goToMainMenu();
    }

    /***
     * sets darkmode by removing previous sheet and applying a new one
     */
    public void setDarkMode() {
        setTheme(DARK_MODE);
    }

    private void setTheme(String theme) {
        configureMenu.getScene().getStylesheets().remove(stylesheet);
        stylesheet = theme;
        configureMenu.getScene().getStylesheets().add(theme);

    }

    public void setLightMode() {
        setTheme(LIGHT_MODE);
    }
    
    private void setInstructionstoAdd() {
        instructions.setText("Click on the Video to add it");
    }

    private void setInstructionstoRemove() {
        instructions.setText("Click on the Video to remove it");
    }


}

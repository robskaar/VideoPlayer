package sample.GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.Database.DB;
import sample.Main;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;


public class Controller_MainMenu implements Initializable {

    private static String path = "";
    private static String playlistName = "";
    private static String stylesheet = "sample/GUI/resources/basic.css";

    @FXML
    private TextField searchField;
    @FXML
    private Text noSearchMatch;
    @FXML
    private Text activePlayList;
    @FXML
    private TilePane videoPane;
    @FXML
    private TilePane playListPane;
    @FXML
    private TilePane playListVideoPane;
    @FXML
    private ScrollPane showToViewPlayListVideos;
    @FXML
    private AnchorPane mainMenuPane;



    /***
     * this method is used to initialize the main menu pane with playlists shown
     * @param location - default override
     * @param resources - default override
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updatePlayListPane(); //updates the list of playlist

    }

    /***
     * does the searches for video's, and calls method to add them to pane
     */
    public void searchDB() {
        clearPane(videoPane); // clears videoPane from previous search
        showToViewPlayListVideos.toBack(); // hides play list video's if in front
        String searchString = searchField.getText(); // gets the search input
        String[] wordsInSearchString = searchString.split(" "); // splits search input by blank spaces, and inputs seperate words in array
        for (String word : wordsInSearchString) {
            DB.selectSQL("SELECT COUNT(fldFilePath) FROM tblVideo WHERE fldTitle LIKE '%" + word + "%' OR fldCategory LIKE '%" + word + "%' "); // sql to count videos found
            int amountVideosFound = Integer.parseInt(DB.getData()); //int for videoes found by previous sql query
            DB.selectSQL("SELECT fldFilePath FROM tblVideo WHERE fldTitle LIKE '%" + word + "%' OR fldCategory LIKE '%" + word + "%'"); // sql query to select filepath from titles like search word
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
            getMoviePath(mediaView);            // Gets path of clicked video
            setPlaylistName();
            changeScene("player.fxml");    // Changes scene to the player
        });


        mediaView.setId(filepath); // sets id to filepath to be used to play videos
        mediaView.setCursor(Cursor.OPEN_HAND); // sets cursor on hover
        Pane videoInfoPane = new Pane(); // info pane created to host mediaview and video info
        videoInfoPane.getChildren().add(videoInfo); //adds video info to pane
        videoInfoPane.getChildren().add(mediaView); //adds mediaview to pane
        tilePane.getChildren().add(videoInfoPane); //adds video infoPane to tile pane
    }

    static public String getPath() {
        return path;
    }

    static void getMoviePath(MediaView mediaView) {
        path = mediaView.getId();
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
    private void updatePlayListPane() {
        clearPane(playListPane);//clear pane from previous content before adding current

        DB.selectSQL("SELECT COUNT(fldPlayListName) FROM tblPlayList"); // sql query to count amount of playlists
        int amountOfPlaylists = Integer.parseInt(DB.getData()); // int var assigned from sql query
        DB.selectSQL("SELECT fldPlayListName FROM tblPlayList"); // sql query to select playlist name
        for (int i = 0; i < amountOfPlaylists; i++) { // for each playlist
            String playListName = DB.getData();//get the playlistname
            if (playListName.equalsIgnoreCase("")) { // if playlistname = null, a.k.a no playlist
                //do not create a button
            } else {
                Button addPlaylist = new Button(); // create a button
                addPlaylist.getStyleClass().add("playListButton"); //add css stylesheet
                addPlaylist.setText(playListName); // sets text with playlistname
                addPlaylist.setId(playListName); // sets id with playlistname
                addPlaylist.setOnMouseClicked(event -> { // onmouseclick event created
                    showToViewPlayListVideos.toFront(); // bring playlistvideopane forward to see videoes on the playlist
                    activePlayList.setText(playListName); // sets playlist name as text in window
                    showVideosOnPlaylist();// method to show current videoes on playlist
                });

                playListPane.getChildren().add(addPlaylist); // adds the playlist to pane
            }
        }
        newPlayListButton(); // creates a new playlistbutton if necessary
    }

    /***
     * adds videos to the playlistvideopane from a playlist in DB
     */
    private void showVideosOnPlaylist() {
        clearPane(playListVideoPane); // clear pane to start of fresh
        String currentPlayList = activePlayList.getText(); // gets the playlist name
        DB.selectSQL("SELECT COUNT(fldPlayListName) FROM tblVideoPlayLists WHERE fldPlayListName = '" + currentPlayList + "'"); // sql query to count amount of videos
        int amountVideosFound = Integer.parseInt(DB.getData()); // amount of playlists found
        DB.selectSQL("SELECT fldFilePath FROM tblVideoPlayLists WHERE fldPlayListName = '" + currentPlayList + "'");//sql query to find filepath of videos in playlist
        for (int i = 0; i < amountVideosFound; i++) { // for each video
            addVideosToView(DB.getData(), playListVideoPane); // adds videos to pane
        }
    }

    public void setPlaylistName() {

        if (activePlayList.getText() != "") {
            playlistName = activePlayList.getText();
        }

    }

    static public String getPlayListName() {
        return playlistName;
    }

    /***
     * checks if there is need for a new playlist button, and adds if necessary
     */
    private void newPlayListButton() {
        boolean isThereANewPlayListButton = false; //default value

        for (Node child : playListPane.getChildren()) { // for each child in playlistpane

            if (child.getId().equalsIgnoreCase("newPlayList")) { // if there is one
                isThereANewPlayListButton = true; // dont create an extra new playlist button
            }
        }
        if (!isThereANewPlayListButton) { // create the new playlist button
            Button addPlaylist = new Button(); // create new button
            TextField newPlayListName = new TextField(); // new textfield for new playlist button
            newPlayListName.setOnAction(event1 -> { // set on action
                String playListName = newPlayListName.getText(); // gets the name for new playlist
                DB.pendingData = false; // to counter DB.class error
                DB.insertSQL("INSERT INTO tblPlayList (fldPlayListName) VALUES ('" + playListName + "')");// inserts playlistname into DB
                updatePlayListPane(); //update view
                newPlayListButton(); // checks if need for new playlistbutton
            });
            newPlayListName.getStyleClass().add("playlistTextField");//add css stylesheet
            addPlaylist.getStyleClass().add("playListButtonTwo");//add css stylesheet
            addPlaylist.setText("+ New Playlist");//sets text on button
            addPlaylist.setId("newPlayList");//sets id on button
            addPlaylist.setOnMouseClicked(event -> {// sets on click event
                addPlaylist.setText(""); // removes text to allow textfield to show properly
                addPlaylist.setGraphic(newPlayListName); // sets the textfield on button
                newPlayListName.requestFocus();// set focus to textfield
            });
            playListPane.getChildren().add(addPlaylist); // add the playlist button

        }
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
            Stage window = (Stage) mainMenuPane.getScene().getWindow();
            window.setScene(mainScene);
            window.setFullScreen(true);
            window.show();
            mainScene.getStylesheets().add(stylesheet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method makes user chose a file, that will be moved to chosen folder
     * Also adds the info to the database
     */
    public void addVideo() throws IOException {

        String fileName;
        String fileNameForDB;
        String fileExtension;
        String fileCategory = "Unknown";
        String destinationPath;
        String destinationPathForDB;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file(s) to add");

        // This is the target folder, where the file will be moved to
        String destinationFolder = System.getProperty("user.dir") + "\\src" + "\\sample" + "\\Media";

        // File chooser window will open on desktop
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));

        // Only the listed file extensions will be accepted
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Videos", "*mp4"),
                new FileChooser.ExtensionFilter("Pictures", "*jpg", "*png", "*jpeg")
        );

        // Open the window to choose file
        File selectedFile = fileChooser.showOpenDialog(mainMenuPane.getScene().getWindow());

        // If a file has been selected
        if (selectedFile != null) {

            // Get filename
            fileName = selectedFile.getName();
            fileNameForDB = getFileNameWithoutExtension(fileName);

            // Add filename to target folder path
            destinationPath = destinationFolder + "\\" + fileName;
            destinationPathForDB = "src/sample/Media/" + fileName;

            // Get file extension. Used to categorize file in database
            fileExtension = getFileExtension(fileName);

            // Move the file
            Files.move(selectedFile.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);

            // Set the file category
            if (fileExtension.equals("mp4")) {
                fileCategory = "Movie";
            }

            // Inserts file info to database
            DB.pendingData = false;
            DB.insertSQL("INSERT INTO tblVideo VALUES('" + destinationPathForDB + "','" + fileCategory + "', '" + fileNameForDB + "')");

        }

    }

    /**
     * This method returns the extension of a file
     *
     * @param fileName
     * @return
     */
    private static String getFileExtension(String fileName) {

        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * This methods returns a filename, without the extension
     *
     * @param fileName
     * @return
     */
    private static String getFileNameWithoutExtension(String fileName) {

        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(0, fileName.lastIndexOf("."));
        } else {
            return "";
        }
    }

    public void playlistConfigure() {
        setPlaylistName();
        changeScene("configurePlaylist.fxml");
    }

    /***
     * Sets the Theme of the Application to dark, by removing the original stylesheet and adding a new one
     */
    public void darkMode() {
        mainMenuPane.getScene().getStylesheets().remove(stylesheet);
        stylesheet = "sample/GUI/resources/Darkmode.css";
        setTheme(stylesheet);

    }

    private void setTheme(String theme) {
        mainMenuPane.getScene().getStylesheets().add(theme);

    }

    /***
     * Sets the Application to light mode, incase anybody would ever want to do that, by removing the currently used stylesheet and replace it with the light one
     */
    public void lightMode() {
        mainMenuPane.getScene().getStylesheets().remove(stylesheet);
        stylesheet = "sample/GUI/resources/basic.css";
        mainMenuPane.getScene().getStylesheets().add(stylesheet);
    }

    /***
     * gets the stylesheet.
     * @return
     */
    public static String getStylesheet() {
        return stylesheet;
    }
}


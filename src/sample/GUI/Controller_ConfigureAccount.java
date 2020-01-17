package sample.GUI;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.Database.DB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class Controller_ConfigureAccount {

    private String userNameEntered;
    private String passwordEntered;


    static private boolean loggedIn = false;

    @FXML
    private AnchorPane accountPane;
    @FXML
    private ImageView cornerProfileImage;


    @FXML
    private TextField nameField;


    @FXML
    private TextField addressField;

    @FXML
    private Text userNameField;

    @FXML
    private TextField zipCodeField;


    @FXML
    private TextArea bioField;

    @FXML
    private ImageView profileImage;


    @FXML
    private TextField phoneNumberField;


    @FXML
    private TextField emailField;


    @FXML
    private TextField defaultFileFolder;

    @FXML
    private TextField dateOfBirth;


    @FXML
    private TextField userName;

    @FXML
    private TextField password;


    @FXML
    private ToggleButton newGenderFemale;

    @FXML
    private ToggleButton newGenderMale;


    @FXML
    private TextField newPassword;


    @FXML
    private TextField newUserName;


    @FXML
    private TextField newEmailAddress;


    @FXML
    private TextField newPhoneNumber;

    @FXML
    private TextField newZipCode;

    @FXML
    private TextField newAddress;

    @FXML
    private DatePicker newDateOfBirth;

    @FXML
    private TextField newName;

    @FXML
    private AnchorPane logInScreen;
    @FXML
    private Text loginPageLockedText;
    @FXML
    private Text createNewProfileText;
    @FXML
    private VBox logInVBox;
    @FXML
    private VBox registerVBox;
    @FXML
    private Button confirmedButton;

    /***
     * toggles between register and login vbox on login screen
     */
    @FXML
    void toggleVBox() {
        if (logInVBox.isVisible()) {
            logInVBox.setVisible(false);
            registerVBox.setVisible(true);
        } else {
            logInVBox.setVisible(true);
            registerVBox.setVisible(false);
        }
    }

    /***
     * used to create the user and insert gathered + default info into DB, at the end clears the register box
     */
    @FXML
    void createUser() {
        String name = newName.getText();
        java.sql.Date date = java.sql.Date.valueOf(newDateOfBirth.getValue());
        String gender = "";
        if (newGenderMale.isSelected() && newGenderFemale.isSelected()) {
            createNewProfileText.setText("Chose Male or Female - gender IS binary");
        } else if (newGenderFemale.isSelected()) {
            gender = "Female";
        } else if (newGenderMale.isSelected()) {
            {
                gender = "Male";
            }
        }
        String address = newAddress.getText();
        String zipCode = newZipCode.getText();
        String phoneNumber = newPhoneNumber.getText();
        String emailAddress = newEmailAddress.getText();
        String userName = newUserName.getText();
        String password = newPassword.getText();
        User newUser = new User(name, address, zipCode, gender, phoneNumber, emailAddress, "Default File Path", date, userName, password, "Your bio");
        if (name.equalsIgnoreCase("") || date == null || gender.equalsIgnoreCase("") || address.equalsIgnoreCase("") || zipCode.equalsIgnoreCase("") || phoneNumber.equalsIgnoreCase("") || emailAddress.equalsIgnoreCase("") || userName.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {
            createNewProfileText.setText("no blank fields, please fill all fields");
        } else {
            String pathFemale = "src/sample/Profile_Pictures/user-female.png";
            String pathMale = "src/sample/Profile_Pictures/user-male.png";
            if (gender.equalsIgnoreCase("male")) {
                DB.pendingData = false;
                DB.insertSQL("INSERT INTO tblUserAccount (fldName,fldAddress,fldZipCode,fldUserName,fldPassWord,fldPhoneNumber,fldEmailAddress,fldDefaultFilePath,fldDateOfBirth,fldGender,fldProfileImage,fldBioText) VALUES('" + name + "','" + address + "','" + zipCode + "','" + userName + "','" + newUser.getPassword() + "','" + phoneNumber + "','" + emailAddress + "','" + newUser.getDefaultFileFolder() + "','" + date + "','" + gender + "','" + pathMale + "','" + newUser.getBio() + "')");

            } else {
                DB.pendingData = false;
                DB.insertSQL("INSERT INTO tblUserAccount (fldName,fldAddress,fldZipCode,fldUserName,fldPassWord,fldPhoneNumber,fldEmailAddress,fldDefaultFilePath,fldDateOfBirth,fldGender,fldProfileImage,fldBioText) VALUES('" + name + "','" + address + "','" + zipCode + "','" + userName + "','" + newUser.getPassword() + "','" + phoneNumber + "','" + emailAddress + "','" + newUser.getDefaultFileFolder() + "','" + date + "','" + gender + "','" + pathFemale + "','" + newUser.getBio() + "')");
            }
        }
        loggedIn = true;
        getAccountInfo(userName);
        logInScreen.setVisible(false);
        toggleVBox();
        newName.clear();
        newAddress.clear();
        newDateOfBirth.setValue(null);
        newGenderFemale.setSelected(false);
        newGenderMale.setSelected(false);
        newEmailAddress.clear();
        newUserName.clear();
        newPassword.clear();
        newZipCode.clear();
        newPhoneNumber.clear();
    }

    /***
     * checks if the correct username and password is set - ive added salting to password to store more securly on DB
     * also updates personal folder movies at login - so when you go back to main menu personal movies from personal folder
     * is added
     */
    @FXML
    void logIn() {
        userNameEntered = userName.getText();
        passwordEntered = password.getText();
        DB.selectSQL("select fldPassword from tblUserAccount WHERE fldUserName = '" + userNameEntered + "'");
        String passwordStored = DB.getData();
        if ((passwordEntered + User.getSalt()).equals(passwordStored)) {
            loggedIn = true;
            getAccountInfo(userNameEntered);
            cornerProfileImage.setVisible(true);
            try {
                updateVideosFromPersonalFolder();
            } catch (IOException e) {
                e.printStackTrace();
            }
            logInScreen.setVisible(false);
        } else {
            loginPageLockedText.setText("Please try again - credentials didnt match");
        }

    }

    /***
     * saves changes made in the account settings, and fades a green confirm button to let user know succeeded operation
     * if a blank field is updated a thick border shows to let user know where to edit
     */
    @FXML
    void saveChanges() {
        String name = nameField.getText();
        String address = addressField.getText();
        String zipCode = zipCodeField.getText();
        String phoneNumber = phoneNumberField.getText();
        String emailAddress = emailField.getText();
        String personalFilePath = defaultFileFolder.getText();
        String bioText = bioField.getText();
        if (name.equalsIgnoreCase("") || address.equalsIgnoreCase("") || zipCode.equalsIgnoreCase("") || phoneNumber.equalsIgnoreCase("") || emailAddress.equalsIgnoreCase("")) {
            if (name.equalsIgnoreCase("")) {
                nameField.setStyle("-fx-border-width: 4");
            }
            if (address.equalsIgnoreCase("")) {
                addressField.setStyle("-fx-border-width: 4");
            }
            if (zipCode.equalsIgnoreCase("")) {
                zipCodeField.setStyle("-fx-border-width: 4");
            }
            if (phoneNumber.equalsIgnoreCase("")) {
                phoneNumberField.setStyle("-fx-border-width: 4");
            }
            if (emailAddress.equalsIgnoreCase("")) {
                emailField.setStyle("-fx-border-width: 4");
            }
        } else {

            nameField.setStyle("-fx-border-width: 1");
            addressField.setStyle("-fx-border-width: 1");
            zipCodeField.setStyle("-fx-border-width: 1");
            phoneNumberField.setStyle("-fx-border-width: 1");
            emailField.setStyle("-fx-border-width: 1");
            DB.pendingData = false;
            DB.updateSQL("UPDATE tblUserAccount SET fldName = '" + name + "', fldAddress = '" + address + "',fldZipCode = '" + zipCode + "',fldPhoneNumber = '" + phoneNumber + "',fldEmailAddress = '" + emailAddress + "', fldDefaultFilePath = '" + personalFilePath + "', fldBioText = '" + bioText + "' WHERE fldUserName ='" + userNameEntered + "'");
            FadeTransition ft = new FadeTransition(Duration.millis(3000), confirmedButton);
            confirmedButton.setVisible(true);
            ft.setFromValue(0.0);
            ft.setToValue(1);
            ft.setAutoReverse(true);
            ft.setFromValue(1);
            ft.setToValue(0.0);
            ft.play();
            loggedIn = true;
            logInScreen.toBack();
            logInScreen.setVisible(false);

        }
    }

    /***
     * log out
     */
    @FXML
    void logOut() {
        loggedIn = false;
        userName.clear();
        password.clear();
        cornerProfileImage.setVisible(false);
        logInScreen.toFront();
        logInScreen.setVisible(true);

    }

    /***
     * gets account info + picture + bio and updates the account pane before visiting it.
     * @param username - username for the account info to be gathered
     */
    private void getAccountInfo(String username) {
        DB.selectSQL("SELECT fldName,fldAddress,fldZipCode,fldPhoneNumber,fldEmailAddress,fldDefaultFilePath,fldDateOfBirth,fldBioText, fldProfileImage FROM tblUserAccount WHERE fldUserName ='" + username + "'");
        for (int i = 1; i < 9 + 1; i++) {
            if (i == 1) nameField.setText(DB.getData());
            else if (i == 2) addressField.setText(DB.getData());
            else if (i == 3) zipCodeField.setText(DB.getData());
            else if (i == 4) phoneNumberField.setText(DB.getData());
            else if (i == 5) emailField.setText(DB.getData());
            else if (i == 6) defaultFileFolder.setText(DB.getData());
            else if (i == 7) dateOfBirth.setText(DB.getData());
            else if (i == 8) bioField.setText(DB.getData());
            else {
                String path = DB.getData();
                Image profileImageFromDb = new Image(new File(path).toURI().toString());
                cornerProfileImage.setImage(profileImageFromDb);
                profileImage.setImage(profileImageFromDb);
                userNameField.setText("@" + username);
            }

        }


    }

    /**
     * simply sets scene to main menu
     * also removes unpersonal videos going to main menu, so person A's personal movies from folder dont show when
     * Person B is logged in
     */
    public void goToMainMenu() {
        removeUnpersonalVideos();
        changeScene();
    }

    /**
     * This method changes the scene
     */
    private void changeScene() {

        try {
            Parent mainMenuParent = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
            Scene mainScene = new Scene(mainMenuParent);
            Stage window = (Stage) accountPane.getScene().getWindow();
            window.setScene(mainScene);
            window.setFullScreen(true);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /***
     * used to set the default file folder for a user, to have a specific movies folder to load from for more control of content
     * and adds them to DB by reference of path
     * @throws IOException
     */
    @FXML
    void setDefaultFileFolder() throws IOException {
        File defaultDirectory;
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose Default File Path");
        DB.selectSQL("SELECT fldDefaultFilePath FROM tblUserAccount WHERE fldUserName = '" + userNameEntered + "'");
        defaultDirectory = new File(System.getProperty("user.home") + "\\Desktop");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(accountPane.getScene().getWindow());

        // If a file has been selected
        if (selectedDirectory != null) {

            // Get filename and set it in textfield

            defaultFileFolder.setText(String.valueOf(selectedDirectory));
            DB.pendingData = false;
            DB.updateSQL("UPDATE tblUserAccount SET fldDefaultFilePath = '" + selectedDirectory + "' WHERE fldUserName = '" + userNameEntered + "'");

            Files.walk(Paths.get(String.valueOf(selectedDirectory))).filter(Files::isRegularFile).forEach(filePath -> {

                String fileCategory = "Movie";
                Path fileNameForDB = filePath.getFileName();
                // Inserts file info to database
                DB.insertSQL("INSERT INTO tblVideo (fldFilePath, fldCategory, fldTitle) VALUES('" + filePath + "','" + fileCategory + "', '" + fileNameForDB + "')");


            });
        }
    }

    /***
     * this is called to from login to update personal movies upon revisiting the main menu
     * @throws IOException
     */
    public void updateVideosFromPersonalFolder() throws IOException {

        DB.selectSQL("SELECT fldDefaultFilePath FROM tblUserAccount WHERE fldUserName = '" + userNameEntered + "'");
        String selectedDirectory = DB.getData();
        DB.pendingData = false;
        DB.updateSQL("UPDATE tblUserAccount SET fldDefaultFilePath = '" + selectedDirectory + "' WHERE fldUserName = '" + userNameEntered + "'");
        Files.walk(Paths.get(String.valueOf(selectedDirectory))).filter(Files::isRegularFile).forEach(filePath -> {

            String fileCategory = "Movie";
            Path fileNameForDB = filePath.getFileName();
            // Inserts file info to database

            DB.insertSQL("INSERT INTO tblVideo (fldFilePath, fldCategory, fldTitle) VALUES('" + filePath + "','" + fileCategory + "', '" + fileNameForDB + "')");

        });
    }

    /**
     * This method makes user chose a picture, that will be moved to chosen folder - src.sample.Profile_Pictures
     * Also adds the info to the database
     */
    public void setProfileImage() throws IOException {

        String fileName;
        String destinationPath;
        String destinationPathForDB;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose profile picture to add");

        // This is the target folder, where the file will be moved to
        String destinationFolder = System.getProperty("user.dir") + "\\src" + "\\sample" + "\\Profile_Pictures";

        // File chooser window will open on desktop
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));

        // Only the listed file extensions will be accepted
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Pictures", "*jpg", "*png", "*jpeg")
        );

        // Open the window to choose file
        File selectedFile = fileChooser.showOpenDialog(accountPane.getScene().getWindow());

        // If a file has been selected
        if (selectedFile != null) {

            // Add filename to target folder path
            fileName = selectedFile.getName();
            destinationPath = destinationFolder + "\\" + fileName;
            destinationPathForDB = "src/sample/Profile_Pictures/" + fileName;

            // Move the file
            Files.move(selectedFile.toPath(), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);

            // Inserts file info to database
            DB.pendingData = false;
            DB.updateSQL("UPDATE tblUserAccount SET fldProfileImage = '" + destinationPathForDB + "' WHERE fldUserName ='" + userNameEntered + "'");

        }


    }

    /***
     * removes videos from other logged in users, so person A dont see person B's personal movie folder content
     */
    private void removeUnpersonalVideos() {
        ArrayList<String> toBeDeleted = new ArrayList<>();

        DB.selectSQL("SELECT fldDefaultFilePath FROM tblUserAccount WHERE fldUserName ='" + userNameEntered + "'");
        String defaultPath = DB.getData();
        DB.pendingData = false;
        DB.selectSQL("SELECT COUNT(fldFilePath) FROM tblVideo WHERE fldFilePath NOT LIKE '%src/sample/Media%' AND fldFilePath NOT LIKE '%" + defaultPath + "\\%'");
        int amountOfVideos = Integer.parseInt(DB.getData());
        DB.pendingData = false;
        DB.selectSQL("SELECT fldFilePath FROM tblVideo WHERE fldFilePath NOT LIKE '%src/sample/Media%' AND fldFilePath NOT LIKE '%" + defaultPath + "\\%'");
        for (int i = 0; i < amountOfVideos; i++) {
            toBeDeleted.add(DB.getData());
        }
        for (String path : toBeDeleted) {
            DB.pendingData = false;
            DB.deleteSQL("DELETE FROM tblVideo WHERE fldFilePath ='" + path + "'");
        }

    }
}


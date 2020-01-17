package sample.GUI;

import java.sql.Date;

public class User {

    private String name;
    private String address;
    private String zipCode;
    private String gender;
    private String phoneNumber;
    private String emailAddress;
    private String defaultFileFolder;
    private Date dateOfBirth;
    static private String salt = "!aMn0TaPaSsW9rD";
    private String userName;
    private String password;
    private String bio;

    public static String getSalt() {
        return salt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDefaultFileFolder() {
        return defaultFileFolder;
    }

    public void setDefaultFileFolder(String defaultFileFolder) {
        this.defaultFileFolder = defaultFileFolder;
    }

    public String getUserName() {
        return userName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User(String name, String address, String zipcode, String gender, String phoneNumber, String emailAddress, String defaultFileFolder, Date dateOfBirth, String userName, String password, String bio){
        this.name = name;
        this.address = address;
        this.zipCode = zipcode;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this. emailAddress = emailAddress;
        this.defaultFileFolder = defaultFileFolder;
        this.dateOfBirth = dateOfBirth;
        this.password = password + salt;
        this.bio =bio;
    }


}

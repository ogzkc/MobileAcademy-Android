package com.kc.Model;

/**
 * Created by Oguz on 12.6.2015.
 */
public class User {


    private String fbID;
    private String firstName;
    private String lastName;
    private String Email;
    private String School;
    private int userId;
    private char Gender;
    private String fbLink;
    private String FullName;
    private String Password;
    private String Nickname;
    private char RegisterType;
    private int userTypeID;
    private int Coin;
    private String GPLink;
    private String GooglePhotoURL;

    public User(){
        this.fbID = "-";
        this.firstName = "-";
        this.lastName = "-";
        this.Email = "-";
        this.School = "-";
        this.Gender = '-';
        this.fbLink = "-";
        this.FullName = "-";
        this.Password = "-";
        this.Nickname = "-";
        this.RegisterType = '-';
        this.userTypeID = '1';
        this.Coin = 0;
        this.GPLink = "-";
        this.userId = 0;
        this.GooglePhotoURL = "-";
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGooglePhotoURL() {
        return GooglePhotoURL;
    }

    public void setGooglePhotoURL(String googlePhotoURL) {
        GooglePhotoURL = googlePhotoURL;
    }

    public String getGPLink() {
        return GPLink;
    }

    public void setGPLink(String GPLink) {
        this.GPLink = GPLink;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public char getRegisterType() {
        return RegisterType;
    }

    public void setRegisterType(char registerType) {
        RegisterType = registerType;
    }

    public int getUserTypeID() {
        return userTypeID;
    }

    public void setUserTypeID(int userTypeID) {
        this.userTypeID = userTypeID;
    }

    public int getCoin() {
        return Coin;
    }

    public void setCoin(int coin) {
        Coin = coin;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getFbID() {
        return fbID;
    }

    public void setFbID(String fbID) {
        this.fbID = fbID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String school) {
        School = school;
    }

    public char getGender() {
        return Gender;
    }

    public void setGender(char gender) {
        Gender = gender;
    }

    public String getFbLink() {
        return fbLink;
    }

    public void setFbLink(String fbLink) {
        this.fbLink = fbLink;
    }

}

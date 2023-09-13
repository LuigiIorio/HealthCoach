package com.example.healthcoach.models;


public class UserProfile {
    private String mail;
    private String password;
    private String fullName;
    private String gender;
    private String uid;
    private int[] birth = new int[3];
    private int weight;
    private int height;
    private int dailySteps;
    private int dailyWater;
    private int dailyKcal;
    private String image;

    // Costruttore con tutti i dati
    public UserProfile(String mail, String password, String fullName, String gender, String uid, int[] birth,
                       int weight, int height, int dailySteps, int dailyWater, int dailyKcal, String image) {
        this.mail = mail;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
        this.uid = uid;
        this.birth = birth;
        this.weight = weight;
        this.height = height;
        this.dailySteps = dailySteps;
        this.dailyWater = dailyWater;
        this.dailyKcal = dailyKcal;
        this.image = image;
    }

    // Costruttore vuoto
    public UserProfile() {}

    // Getter e Setter per ogni variabile

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int[] getBirth() {
        return birth;
    }

    public void setBirth(int[] birth) {
        this.birth = birth;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDailySteps() {
        return dailySteps;
    }

    public void setDailySteps(int dailySteps) {
        this.dailySteps = dailySteps;
    }

    public int getDailyWater() {
        return dailyWater;
    }

    public void setDailyWater(int dailyWater) {
        this.dailyWater = dailyWater;
    }

    public int getDailyKcal() {
        return dailyKcal;
    }

    public void setDailyKcal(int dailyKcal) {
        this.dailyKcal = dailyKcal;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}


package com.example.healthcoach.models;


public class UserProfile {
    private String mail;
    private String password;
    private String fullName;
    private String gender;
    private int[] birth = new int[3];
    private int weight;
    private int height;
    private int dailySteps;
    private int dailyWater;
    private int dailyKcal;

    // Costruttore con tutti i dati
    public UserProfile(String mail, String password, String fullName, String gender, int[] birth,
                       int weight, int height, int dailySteps, int dailyWater, int dailyKcal) {
        this.mail = mail;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
        this.birth = birth;
        this.weight = weight;
        this.height = height;
        this.dailySteps = dailySteps;
        this.dailyWater = dailyWater;
        this.dailyKcal = dailyKcal;
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
}


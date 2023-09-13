package com.example.healthcoach.models;


import android.os.Bundle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UserProfile implements Serializable {
    private String mail;
    private String password;
    private String fullName;
    private String gender;
    private String uid;
    private int day, month, year;
    private int weight;
    private int height;
    private int dailySteps;
    private int dailyWater;
    private int dailyKcal;
    private String image;

    // Costruttore con tutti i dati
    public UserProfile(String mail, String password, String fullName, String gender, String uid, int day, int month, int year,
                       int weight, int height, int dailySteps, int dailyWater, int dailyKcal, String image) {
        this.mail = mail;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
        this.uid = uid;
        this.day = day;
        this.month = month;
        this.year = year;
        this.weight = weight;
        this.height = height;
        this.dailySteps = dailySteps;
        this.dailyWater = dailyWater;
        this.dailyKcal = dailyKcal;
        this.image = image;
    }

    // Costruttore vuoto
    public UserProfile() {}

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            byte[] userData = bos.toByteArray();
            bundle.putByteArray("userProfile", userData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return bundle;
    }

    public static UserProfile getUserProfile(Bundle bundle) {

        byte[] userData = bundle.getByteArray("userProfile");
        UserProfile userProfile = null;

        if (userData != null) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(userData);
                ObjectInputStream in = new ObjectInputStream(bis);
                userProfile = (UserProfile) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return userProfile;

    }

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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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


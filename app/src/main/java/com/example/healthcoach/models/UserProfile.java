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


    /**
     * Constructs a new UserProfile object with all the provided attributes.
     *
     * @param mail       The email of the user.
     * @param password   The password of the user.
     * @param fullName   The full name of the user.
     * @param gender     The gender of the user.
     * @param uid        The unique identifier of the user.
     * @param day        The birth day of the user.
     * @param month      The birth month of the user.
     * @param year       The birth year of the user.
     * @param weight     The weight of the user in kilograms.
     * @param height     The height of the user in centimeters.
     * @param dailySteps The daily step goal of the user.
     * @param dailyWater The daily water intake goal of the user.
     * @param dailyKcal  The daily calorie goal of the user.
     * @param image      The URL or path of the user's profile image.
     */


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


    public UserProfile() {}

    /**
     * Converts the UserProfile object to a Bundle object, allowing it to be passed between activities.
     *
     * @return A Bundle object containing the serialized UserProfile.
     */


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



    /**
     * Retrieves a UserProfile object from a given Bundle object.
     *
     * @param bundle A Bundle object containing a byte array representation of a UserProfile object.
     * @return       The UserProfile object extracted from the byte array, or null if extraction fails.
     */


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


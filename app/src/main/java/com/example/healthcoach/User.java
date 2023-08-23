package com.example.healthcoach;

public class User {

    private int height; //cm
    private boolean male; //T se male, F se female
    private double weight; //kg
    private Date birthDate;
    private String email;
    private String password;

    public User(int height, boolean male, double weight, Date birthDate, String email, String password) {
        this.height = height;
        this.male = male;
        this.weight = weight;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
    }

    public User(int height, boolean male, double weight, int day, int month, int year, String email, String password) {

        this(height, male, weight, new Date(day,month,year), email, password);

    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

package com.example.healthcoach;

public class User {
    private String email;
    private String age;
    private String gender;
    private String birthdate;
    private String weight;
    private String height;

    public User(String email, String age, String gender, String birthdate, String weight, String height) {
        this.email = email;
        this.age = age;
        this.gender = gender;
        this.birthdate = birthdate;
        this.weight = weight;
        this.height = height;
    }

    public String getEmail() {
        return email;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getWeight() {
        return weight;
    }

    public String getHeight() {
        return height;
    }
}

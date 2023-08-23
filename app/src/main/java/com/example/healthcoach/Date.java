package com.example.healthcoach;

import java.time.LocalDateTime;

public class Date {

    private int day;
    private int month;
    private int year;


    //da gestire se gli passo valori errati (uno dei set da false)
    public Date(int day, int month, int year) {



        if (setYear(year) && setMonth(month) && setDay(day));


    }

    public static boolean isLeapYear(int year){

        if (year % 4 == 0 && year % 400 != 0)
            return true;

        return false;
    }

    public int getDay() {
        return day;
    }

    public boolean setDay(int day) {

        if (day <= 0 || day > 31)
            return false;

        switch (month){

            case 4:
            case 6:
            case 9:
            case 11:
                if (day == 31)
                    return false;
            case 2:
                if (isLeapYear(year) && day > 29)
                    return false;
                else if (day > 28)
                    return false;
            default:
                this.day = day;
                return true;


        }

    }

    public int getMonth() {
        return month;
    }

    public boolean setMonth(int month) {

        if (month <= 0 || month > 12)
            return false;

        this.month = month;
        return true;


    }

    public int getYear() {
        return year;
    }

    public boolean setYear(int year) {
        if (year > LocalDateTime.now().getYear() || year < 1900)
            return false;

        this.year = year;
        return true;

    }


}

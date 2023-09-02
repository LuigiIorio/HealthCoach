package com.example.healthcoach;

public class ActivityTypes {

    public static final int TYPE_ACTIVITY_RUNNING = 1;
    public static final int TYPE_ACTIVITY_WALKING = 2;
    public static final int TYPE_ACTIVITY_CYCLING = 3;

    public static String getActivityName(int activityType) {
        switch (activityType) {
            case TYPE_ACTIVITY_RUNNING:
                return "Running";
            case TYPE_ACTIVITY_WALKING:
                return "Walking";
            case TYPE_ACTIVITY_CYCLING:
                return "Cycling";
            default:
                return "Unknown";
        }
    }
}
package com.example.healthcoach;

import com.google.android.gms.common.api.Scope;

public class GoogleFitScopes {
    public static final Scope FITNESS_ACTIVITY_READ = new Scope("https://www.googleapis.com/auth/fitness.activity.read");
    public static final Scope FITNESS_ACTIVITY_WRITE = new Scope("https://www.googleapis.com/auth/fitness.activity.write");
    public static final Scope FITNESS_LOCATION_READ = new Scope("https://www.googleapis.com/auth/fitness.location.read");
    public static final Scope FITNESS_LOCATION_WRITE = new Scope("https://www.googleapis.com/auth/fitness.location.write");
    public static final Scope FITNESS_BODY_READ = new Scope("https://www.googleapis.com/auth/fitness.body.read");
    public static final Scope FITNESS_BODY_WRITE = new Scope("https://www.googleapis.com/auth/fitness.body.write");
    public static final Scope FITNESS_NUTRITION_READ = new Scope("https://www.googleapis.com/auth/fitness.nutrition.read");
    public static final Scope FITNESS_NUTRITION_WRITE = new Scope("https://www.googleapis.com/auth/fitness.nutrition.write");
    public static final Scope FITNESS_BLOOD_PRESSURE_READ = new Scope("https://www.googleapis.com/auth/fitness.blood_pressure.read");
    public static final Scope FITNESS_BLOOD_PRESSURE_WRITE = new Scope("https://www.googleapis.com/auth/fitness.blood_pressure.write");
    public static final Scope FITNESS_BLOOD_GLUCOSE_READ = new Scope("https://www.googleapis.com/auth/fitness.blood_glucose.read");
    public static final Scope FITNESS_BLOOD_GLUCOSE_WRITE = new Scope("https://www.googleapis.com/auth/fitness.blood_glucose.write");
    public static final Scope FITNESS_OXYGEN_SATURATION_READ = new Scope("https://www.googleapis.com/auth/fitness.oxygen_saturation.read");
    public static final Scope FITNESS_OXYGEN_SATURATION_WRITE = new Scope("https://www.googleapis.com/auth/fitness.oxygen_saturation.write");
    public static final Scope FITNESS_BODY_TEMPERATURE_READ = new Scope("https://www.googleapis.com/auth/fitness.body_temperature.read");
    public static final Scope FITNESS_BODY_TEMPERATURE_WRITE = new Scope("https://www.googleapis.com/auth/fitness.body_temperature.write");
    public static final Scope FITNESS_REPRODUCTIVE_HEALTH_READ = new Scope("https://www.googleapis.com/auth/fitness.reproductive_health.read");
    public static final Scope FITNESS_REPRODUCTIVE_HEALTH_WRITE = new Scope("https://www.googleapis.com/auth/fitness.reproductive_health.write");



    public static Scope[] getAllScopes() {
        return new Scope[]{
                FITNESS_ACTIVITY_READ,
                FITNESS_ACTIVITY_WRITE,
                FITNESS_LOCATION_READ,
                FITNESS_LOCATION_WRITE,
                FITNESS_BODY_READ,
                FITNESS_BODY_WRITE,
                FITNESS_NUTRITION_READ,
                FITNESS_NUTRITION_WRITE,
                FITNESS_BLOOD_PRESSURE_READ,
                FITNESS_BLOOD_PRESSURE_WRITE,
                FITNESS_BLOOD_GLUCOSE_READ,
                FITNESS_BLOOD_GLUCOSE_WRITE,
                FITNESS_OXYGEN_SATURATION_READ,
                FITNESS_OXYGEN_SATURATION_WRITE,
                FITNESS_BODY_TEMPERATURE_READ,
                FITNESS_BODY_TEMPERATURE_WRITE,
                FITNESS_REPRODUCTIVE_HEALTH_READ,
                FITNESS_REPRODUCTIVE_HEALTH_WRITE
        };
    }
}

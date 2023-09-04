package com.example.healthcoach.viewmodels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthcoach.models.UserProfile;

public class UserProfileViewModel extends ViewModel {
    private final MutableLiveData<String> gender = new MutableLiveData<>();
    private final MutableLiveData<Integer> age = new MutableLiveData<>();

    public UserProfileViewModel() {
        // Initialize with default values if needed.
    }

    public MutableLiveData<String> getGender() {
        return gender;
    }

    public void setGender(String genderValue) {
        gender.setValue(genderValue);
    }


    public MutableLiveData<Integer> getAge() {
        return age;
    }

    public void setAge(int ageValue) {
        age.setValue(ageValue);
    }
}

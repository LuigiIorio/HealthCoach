package com.example.healthcoach.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Screen3ViewModel extends ViewModel {
    private final MutableLiveData<String> screenText = new MutableLiveData<>();
    private final MutableLiveData<Long> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<String> selectedDataType = new MutableLiveData<>();

    public Screen3ViewModel() {
        screenText.setValue("Welcome to Screen 3");
    }

    public LiveData<String> getScreenText() {
        return screenText;
    }

    public LiveData<Long> getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Long date) {
        selectedDate.setValue(date);
    }

    public LiveData<String> getSelectedDataType() {
        return selectedDataType;
    }

    public void setSelectedDataType(String type) {
        selectedDataType.setValue(type);
    }
}

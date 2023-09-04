package com.example.healthcoach.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Screen3ViewModel extends ViewModel {

    private final MutableLiveData<String> screenText = new MutableLiveData<>();

    public Screen3ViewModel() {
        // For the sake of demonstration, we are setting an initial value here.
        // In real-world scenarios, this might come from a database, API, or other data sources.
        screenText.setValue("Welcome to Screen 3");
    }

    public LiveData<String> getScreenText() {
        return screenText;
    }

    // If you ever need to update the text from somewhere (for example, in response to user input or a database change),
    // you can expose a method to do so:
    // public void setScreenText(String newText) {
    //     screenText.setValue(newText);
    // }
}

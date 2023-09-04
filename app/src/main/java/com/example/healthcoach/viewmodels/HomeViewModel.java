package com.example.healthcoach.viewmodels;



import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> welcomeMessage = new MutableLiveData<>();

    public HomeViewModel() {
        welcomeMessage.setValue("Welcome to HealthCoach!");  // initializing with default value
    }

    // This method can be expanded upon later, for instance if you want to fetch the welcome message from a repository
    public LiveData<String> getWelcomeMessage() {
        return welcomeMessage;
    }

    // If you want to change the welcome message dynamically
    public void setWelcomeMessage(String message) {
        welcomeMessage.setValue(message);
    }

    // Add more methods as needed for your business logic
}

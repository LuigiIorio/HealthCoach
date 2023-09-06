package com.example.healthcoach.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;



public class Screen2ViewModel extends ViewModel {

    private final MutableLiveData<String> journalText = new MutableLiveData<>();

    public LiveData<String> getJournalText() {
        return journalText;
    }

    public void setJournalText(String text) {
        journalText.setValue(text);
    }
}

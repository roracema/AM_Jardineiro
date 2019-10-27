package com.fiap.am_jardineiro.ui.track_live;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrackLiveViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TrackLiveViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
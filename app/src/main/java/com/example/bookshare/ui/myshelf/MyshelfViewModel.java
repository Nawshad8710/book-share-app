package com.example.bookshare.ui.myshelf;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyshelfViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyshelfViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("My Shelf");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
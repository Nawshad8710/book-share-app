package com.example.bookshare.ui.home;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        //mText = new MutableLiveData<>();
        //mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void flipperImages(int image, ViewFlipper v_flipper, Context context){
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(image);

        v_flipper.addView(imageView);
        v_flipper.setFlipInterval(3000);
        v_flipper.setInAnimation(context, android.R.anim.slide_in_left);
        v_flipper.setOutAnimation(context, android.R.anim.slide_out_right);
        v_flipper.setAutoStart(true);
    }
}
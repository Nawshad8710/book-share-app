package com.example.bookshare;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.bookshare.model.GlobalData;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvUsername, tvEarningAmount, tvEmail, tvPhone, tvLatitude, tvLongitude;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolBarLayout.setTitle("");
        //toolbar.setTitle("");

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(i);
                finish();
            }
        });


        tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(GlobalData.getInstance().userFullName);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvUsername.setText("@"+GlobalData.getInstance().userName);

        tvEarningAmount = (TextView) findViewById(R.id.tvEarningAmount);
        tvEarningAmount.setText(GlobalData.getInstance().totalEarning+"");

        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvEmail.setText(GlobalData.getInstance().userEmail);

        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvPhone.setText(GlobalData.getInstance().userPhone);

        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLatitude.setText(GlobalData.getInstance().userLatitude+"");

        tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        tvLongitude.setText(GlobalData.getInstance().userLongitude+"");

        //action back
        backBtn = (ImageView) findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goHome = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(goHome);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent goHome = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(goHome);
        finish();
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        }
        else return false;
    }
}
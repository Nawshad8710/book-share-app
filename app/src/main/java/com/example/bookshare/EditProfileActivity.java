package com.example.bookshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.model.GlobalData;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private TextView etFirstName, etLastName, etUserName, etPhone, etEmail, etLatitude, etLongitude;
    private Button btnUpdate;
    private ImageButton btnCloseScreen;
    private String firstName, lastName, userName, phone, email, latitude, longitude;

    private SharedPreferences preferences;
    private static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        preferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);

        etFirstName = (TextView) findViewById(R.id.first_name);
        etFirstName.setText(GlobalData.getInstance().userFirstName);

        etLastName = (TextView) findViewById(R.id.last_name);
        etLastName.setText(GlobalData.getInstance().userLastName);

        etUserName = (TextView) findViewById(R.id.username);
        etUserName.setText(GlobalData.getInstance().userName);

        etPhone = (TextView) findViewById(R.id.phone);
        etPhone.setText(GlobalData.getInstance().userPhone);

        etEmail = (TextView) findViewById(R.id.email);
        etEmail.setText(GlobalData.getInstance().userEmail);

        etLatitude = (TextView) findViewById(R.id.latitude);
        etLatitude.setText(GlobalData.getInstance().userLatitude+"");

        etLongitude = (TextView) findViewById(R.id.longitude);
        etLongitude.setText(GlobalData.getInstance().userLongitude+"");

        btnCloseScreen = (ImageButton) findViewById(R.id.btn_close_screen);
        btnCloseScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(back);
                finish();
            }
        });

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = formValidation();
                if(flag==1){
                    updateUser();
                }
            }
        });
    }


    private int formValidation(){
        getFormValues();
        //pbSignup.setVisibility(View.GONE);

//        if(TextUtils.isEmpty(firstName)){
//            etFirstName.setError("Please enter first name");
//            return 0;
//        }
//
//        if(TextUtils.isEmpty(lastName)){
//            etLastName.setError("Please enter last name");
//            return 0;
//        }

        if(TextUtils.isEmpty(userName)){
            etUserName.setError("Please enter username");
            return 0;
        }

        if(TextUtils.isEmpty(phone)){
            etPhone.setError("Please enter phone number");
            return 0;
        }

        if(TextUtils.isEmpty(latitude)){
            etLatitude.setError("Please enter latitude");
            return 0;
        }

        if(TextUtils.isEmpty(longitude)){
            etLongitude.setError("Please enter longitude");
            return 0;
        }

        return 1;
    }

    private void getFormValues(){
        firstName = etFirstName.getText().toString();
        lastName = etLastName.getText().toString();
        userName = etUserName.getText().toString();
        phone = etPhone.getText().toString();
        email = etEmail.getText().toString();
        latitude = etLatitude.getText().toString();
        longitude = etLongitude.getText().toString();
    }

    public void updateUser(){
        //pbSignup.setVisibility(View.VISIBLE);
        apiName = "api_signup.php";
        action = action+"editProfile";
        url = url+apiName+action;
        //System.out.println("----------URL= "+url);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.toString().equals("yes")){
                    //Intent i = new Intent(getApplicationContext(),OrderActivity.class);
                    //startActivity(i);
                    Toast.makeText(getApplicationContext(),"Profile updated successfully",Toast.LENGTH_LONG).show();
                    //pb.setVisibility(View.GONE);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("username", userName);
                    editor.putString("userFullName", firstName+" "+lastName);
                    editor.putString("userPhone", phone);
                    editor.putString("userEmail", email);
                    editor.putString("userLatitude", latitude+"");
                    editor.putString("userLongitude", longitude+"");
                    editor.putString("userFirstName", firstName);
                    editor.putString("userLastName", lastName);
                    editor.commit();

                    GlobalData.getInstance().userName = userName;
                    GlobalData.getInstance().userFullName = firstName+" "+lastName;
                    GlobalData.getInstance().userPhone = phone;
                    GlobalData.getInstance().userEmail = email;
                    GlobalData.getInstance().userLatitude = Double.parseDouble(latitude);
                    GlobalData.getInstance().userLongitude = Double.parseDouble(longitude);
                    GlobalData.getInstance().userFirstName = firstName;
                    GlobalData.getInstance().userLastName = lastName;

                    Intent back = new Intent(getApplicationContext(),ProfileActivity.class);
                    startActivity(back);
                    finish();

                } else if(response.toString().equals("tokenFail")){
                    Toast.makeText(getApplicationContext(),"Wrong token, try again",Toast.LENGTH_SHORT).show();
                    //pb.setVisibility(View.GONE);
                    Intent reload = new Intent(getApplicationContext(),EditProfileActivity.class);
                    startActivity(reload);
                    finish();
                } else{
                    //msg.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Server error, try again",Toast.LENGTH_LONG).show();
                    Intent reload = new Intent(getApplicationContext(),EditProfileActivity.class);
                    startActivity(reload);
                    finish();
                    //System.out.println("$$$"+response+"$$$");
                    //pb.setVisibility(View.GONE);
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>"+response);
                //pbSignup.setVisibility(View.GONE);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error occured!",Toast.LENGTH_SHORT).show();
                //pb.setVisibility(View.GONE);
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<"+"ERRoR");
                //pbSignup.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");
                params.put("firstName",firstName);
                params.put("lastName",lastName);
                params.put("username",userName);
                params.put("phone", phone);
                params.put("email",email);
                params.put("latitude", latitude);
                params.put("longitude", longitude);

                return params;
            }
        };


        //System.out.println("edUsername= "+GlobalData.getInstance().username);
        //System.out.println(GlobalData.getInstance());
        GlobalData.getInstance().addToRequestQueue(request);
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
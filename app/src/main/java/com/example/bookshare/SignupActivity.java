package com.example.bookshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.model.GlobalData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private LinearLayout txtGotoLogin;
    private EditText edUsername, edPhone, edPassword, edRePassword, edLatitude, edLongitude;
    private RadioGroup rgGender;
    private Button btnSignup;
    private ProgressBar pbSignup;
    private TextView txtGenderError;

    private int genderCheckId, gender=0;
    private String username, phone, password, rePassword, latitude, longitude;
    private SharedPreferences preferences;
    private static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private FusedLocationProviderClient mFusedLocationClient;
    private int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        init();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // method to get the location
        getLastLocation();

        txtGotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoLogin = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(gotoLogin);
                finish();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = formValidation();
                if(flag==1){
                    registerUser();
                }
                //pbSignup.setVisibility(View.VISIBLE);
                //registerUser();
            }
        });
    }

    private void init(){
        txtGotoLogin = (LinearLayout) findViewById(R.id.txtGotoLogin);
        edUsername = (EditText) findViewById(R.id.username_signup);
        edPhone = (EditText) findViewById(R.id.phone_signup);
        edPassword = (EditText) findViewById(R.id.password_signup);
        edRePassword = (EditText) findViewById(R.id.rePassword_signup);
        rgGender = (RadioGroup) findViewById(R.id.genderSelect_signup);
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.maleSelect_signup:
                        gender = 1;
                        break;

                    case R.id.femaleSelect_signup:
                        gender = 2;
                        break;
                }
            }
        });
        btnSignup = (Button) findViewById(R.id.btn_signup);
        pbSignup = (ProgressBar) findViewById(R.id.pb_signup);
        pbSignup.setVisibility(View.GONE);
        txtGenderError = (TextView) findViewById(R.id.txtGenderError_signup);
        txtGenderError.setVisibility(View.GONE);

        edLatitude = (EditText) findViewById(R.id.latitude_signup);
        edLongitude = (EditText) findViewById(R.id.longitude_signup);
    }

    private void getFormValues(){
        username = edUsername.getText().toString();
        phone = edPhone.getText().toString();
        password = edPassword.getText().toString();
        rePassword = edRePassword.getText().toString();
        genderCheckId = rgGender.getCheckedRadioButtonId();
        latitude = edLatitude.getText().toString();
        longitude = edLongitude.getText().toString();

        System.out.println("-----------------Gender ID="+genderCheckId);
    }

    private int formValidation(){
        getFormValues();
        txtGenderError.setVisibility(View.GONE);
        pbSignup.setVisibility(View.GONE);

        if(TextUtils.isEmpty(username)){
            edUsername.setError("Please enter username");
            return 0;
        }

        if(TextUtils.isEmpty(phone)){
            edPhone.setError("Please enter phone number");
            return 0;
        }

        if(TextUtils.isEmpty(password)){
            edPassword.setError("Please enter password");
            return 0;
        }else if(password.length()<6){
            edPassword.setError("Password should be atleast 6 digit");
            return 0;
        }

        if(!rePassword.equals(password)){
            edRePassword.setError("Password don't match");
            return 0;
        }

        if(!rePassword.equals(password)){
            edRePassword.setError("Password don't match");
            return 0;
        }

        if(rgGender.getCheckedRadioButtonId()==-1){
            txtGenderError.setVisibility(View.VISIBLE);
            return 0;
        }

        if(TextUtils.isEmpty(latitude)){
            edLatitude.setError("Please enter latitude");
            return 0;
        }

        if(TextUtils.isEmpty(longitude)){
            edLongitude.setError("Please enter longitude");
            return 0;
        }

        return 1;
    }

    public void registerUser(){
        pbSignup.setVisibility(View.VISIBLE);
        apiName = "api_signup.php";
        action = action+"signup";
        url = url+apiName+action;
        //System.out.println("----------URL= "+url);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.toString().equals("yes")){
                    //Intent i = new Intent(getApplicationContext(),OrderActivity.class);
                    //startActivity(i);
                    Toast.makeText(getApplicationContext(),"You've registered successfully",Toast.LENGTH_LONG).show();
                    //pb.setVisibility(View.GONE);
                    Intent goLogin = new Intent(SignupActivity.this,LoginActivity.class);
                    startActivity(goLogin);
                    finish();

                } else if(response.toString().equals("tokenFail")){
                    Toast.makeText(getApplicationContext(),"Wrong token, try again",Toast.LENGTH_SHORT).show();
                    //pb.setVisibility(View.GONE);
                    Intent reload = new Intent(getApplicationContext(),SignupActivity.class);
                    startActivity(reload);
                    finish();
                } else{
                    //msg.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Server error, try again",Toast.LENGTH_LONG).show();
                    Intent reload = new Intent(getApplicationContext(),SignupActivity.class);
                    startActivity(reload);
                    finish();
                    //System.out.println("$$$"+response+"$$$");
                    //pb.setVisibility(View.GONE);
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>"+response);
                pbSignup.setVisibility(View.GONE);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error occured!",Toast.LENGTH_SHORT).show();
                //pb.setVisibility(View.GONE);
                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<"+"ERRoR");
                pbSignup.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("username",username);
                params.put("password",password);
                params.put("phone",phone);
                params.put("gender",gender+"");
                params.put("latitude",latitude+"");
                params.put("longitude",longitude+"");

                return params;
            }
        };


        //System.out.println("edUsername= "+GlobalData.getInstance().username);
        //System.out.println(GlobalData.getInstance());
        GlobalData.getInstance().addToRequestQueue(request);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            edLatitude.setText(location.getLatitude() + "");
                            edLongitude.setText(location.getLongitude() + "");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            edLatitude.setText(mLastLocation.getLatitude() + "");
            edLongitude.setText(mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
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

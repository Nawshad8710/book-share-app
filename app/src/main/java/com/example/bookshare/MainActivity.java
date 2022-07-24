package com.example.bookshare;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.model.Category;
import com.example.bookshare.model.GlobalData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private ActionBarDrawerToggle mToggle;
    private ImageView btnSearch, navImage, btnMessages;
    private ImageButton btnProfileOption;
    private BadgeDrawable badgeDrawable;
    private TextView navName, navPhone;
    SharedPreferences preferences;
    public static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private ArrayList<Category> categoryList = new ArrayList<Category>();

    TextView textCartItemCount;
    int mCartItemCount = 0;
    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";

    //private BottomNavigationView bnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        if(GlobalData.getInstance().conversationCount>=0) {
            mCartItemCount = GlobalData.getInstance().conversationCount;
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setBackgroundColor(getResources().getColor(R.color.green));
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        //toolbar.setNavigationIcon(R.drawable.ic_chrome_reader_mode_black_24dp);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.logo_text);
        setTitle("");

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        BottomNavigationView BottomNavView = findViewById(R.id.bottom_nav_view);
        // AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(BottomNavView, navController);
//        badgeDrawable = BottomNavView.getOrCreateBadge(R.id.navigation_mycart);
//        badgeDrawable.setBackgroundColor(Color.RED);
//        badgeDrawable.setBadgeTextColor(Color.WHITE);
//        badgeDrawable.setNumber(0);
//        badgeDrawable.setVisible(false);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        preferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);


        //changing navigation drawer toggle button icon

        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_grey_24dp, getApplication().getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });


        View headerview = navigationView.getHeaderView(0);


        navName = (TextView) headerview.findViewById(R.id.navName);
        if(!GlobalData.getInstance().userFullName.isEmpty() && !GlobalData.getInstance().userFullName.equals("") && !GlobalData.getInstance().userFullName.equals(" ") && !GlobalData.getInstance().userFullName.substring(0,1).equals("@")){
            navName.setText(GlobalData.getInstance().userFullName);
        }else{
            navName.setText(GlobalData.getInstance().userName);
        }

        navPhone = (TextView) headerview.findViewById(R.id.navPhone);
        navPhone.setText(GlobalData.getInstance().userPhone);

        createNotificationChannel();
        getToken();

    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                //If task is failed then
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Failed to get the Token");
                }

                //Token
                String token = task.getResult();
                Log.d(TAG, "onComplete: " + token);
                System.out.println("*************************Token: "+token+"***");

                if(!preferences.getString("tokenSaved","").equals("yes")) {
                    saveToken(token);
                }
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "firebaseNotifChannel";
            String description = "Receive Firebase notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            long [] VIBRATE_PATTERN = { 1000 , 1000 , 1000 , 1000 , 1000 } ;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setLightColor(R.color.colorPrimaryDark ) ;
            channel.setVibrationPattern(VIBRATE_PATTERN) ;
            channel.enableVibration( true ) ;
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_right_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_cart);

        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);

        setupBadge();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_cart: {
                //GlobalData.getInstance().conversationCount = 0;
                Intent i = new Intent(getApplicationContext(), MessageConversationActivity.class);
                i.putExtra("Flag", GlobalData.getInstance().NOT_FROM_NOTIFICATION);
                startActivity(i);
                finish();
                return true;
            }
            case R.id.action_search: {
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(i);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {

        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if(id == R.id.nav_myprofile){
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        if(id == R.id.nav_myrequests){
            Intent intent = new Intent(MainActivity.this,MyRequestsActivity.class);
            startActivity(intent);
            finish();
        }

        if(id == R.id.nav_myearnings){
            Intent intent = new Intent(MainActivity.this,MyEarningsActivity.class);
            startActivity(intent);
            finish();
        }

        if(id == R.id.nav_myorders){
            Intent intent = new Intent(MainActivity.this,MyOrdersActivity.class);
            startActivity(intent);
            finish();
        }

        if(id == R.id.nav_refresh){
            Intent intent = new Intent(MainActivity.this,SplashActivity.class);
            startActivity(intent);
            finish();
        }

        if(id == R.id.nav_Logout){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", "");
            editor.putString("password", "");
            editor.putInt("userId", 0);
            editor.putString("loggedin","no");
            editor.putString("tokenSaved","no");
            editor.commit();
            Toast.makeText(getApplicationContext(), "You've logged out successfully!", Toast.LENGTH_LONG).show();
            Intent goLogin = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(goLogin);
            finish();
        }
        return true;
    }

    private void showAlertDialog() {
        //init alert dialog
        final AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to leave?");
        //set listeners for dialog buttons
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish all the activities

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog gone
                dialog.dismiss();
            }
        });

        //create the alert dialog and show it
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        showAlertDialog();
    }

    public void showProfileOptionMenu(){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, btnProfileOption);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
//        if(preferences.getString("loggedin","").equals("yes")){
//            popupMenu.getMenu().removeItem(R.id.rented_books);
//        }else{
//            popupMenu.getMenu().removeItem(R.id.profile_logout);
//        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.my_all_books:
                        break;
                    case R.id.my_books_for_rent:
                        break;
                    case R.id.my_books_for_sell:
                        break;
                    case R.id.inactive_books:
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
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

    public void saveToken(String token){
        apiName = "api_insertData.php";
        action = action+"saveToken";
        url = url+apiName+action;
        //System.out.println("----------URL= "+url);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.toString().equals("yes")){
                    //Intent i = new Intent(getApplicationContext(),OrderActivity.class);
                    //startActivity(i);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("tokenSaved","yes");
                    editor.commit();

                    //Toast.makeText(getApplicationContext(),"Token saved successfully",Toast.LENGTH_LONG).show();
                    //pb.setVisibility(View.GONE);

                } else if(response.toString().equals("tokenFail")){
                    //Toast.makeText(getApplicationContext(),"Wrong token, try again",Toast.LENGTH_SHORT).show();
                    //pb.setVisibility(View.GONE);
                }else if(response.toString().equals("used")){
                    //Toast.makeText(getApplicationContext(),"Token already saved!",Toast.LENGTH_SHORT).show();
                    //pb.setVisibility(View.GONE);
                } else{
                    //msg.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(),"Server error, try again",Toast.LENGTH_LONG).show();
                    //System.out.println("$$$"+response+"$$$");
                    //pb.setVisibility(View.GONE);
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>"+response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),"Error occurred while saving token!",Toast.LENGTH_SHORT).show();
                //pb.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");
                params.put("token",token);

                return params;
            }
        };


        //System.out.println("edUsername= "+GlobalData.getInstance().username);
        //System.out.println(GlobalData.getInstance());
        GlobalData.getInstance().addToRequestQueue(request);
    }
}

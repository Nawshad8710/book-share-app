package com.example.bookshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.Conversation;
import com.example.bookshare.model.Order;
import com.example.bookshare.model.Request;
import com.example.bookshare.model.Category;
import com.example.bookshare.model.GlobalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    private TextView errorMessage;
    //private Button tryAgainBtn;
    private  Button tryAgainBtn;
    private ImageView errorIcon;

    private ProgressBar pb;

    SharedPreferences preferences;
    public static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Create fullscreen mode
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tryAgainBtn = findViewById(R.id.btn_tryAgain);
        errorMessage = findViewById(R.id.noConnectionText);
        errorIcon = findViewById(R.id.noConnectionIcon);
        pb = findViewById(R.id.progressBar);

        if(!isConnected(getApplicationContext())){
            tryAgainBtn.setVisibility(View.VISIBLE);
            errorMessage.setVisibility(View.VISIBLE);
            errorIcon.setVisibility(View.VISIBLE);
            tryAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
                    startActivity(loadAgain);
                }
            });

        }
        else {
            pb.setVisibility(View.VISIBLE);
            tryAgainBtn.setVisibility(View.GONE);
            errorMessage.setVisibility(View.GONE);
            errorIcon.setVisibility(View.GONE);
            new WaitTask().execute();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
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

    class WaitTask extends AsyncTask<Void, Void, Void> {

        //ArrayList<Category> ac = new ArrayList<Category>();
       // ArrayList<Content> contentList = new ArrayList<Content>();
        Boolean serverError = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            preferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
            clearValues();
        }

        protected void clearValues(){

            GlobalData.getInstance().shelfBooksListed = false;
            GlobalData.getInstance().topRecentBooksListed = false;
            GlobalData.getInstance().topSellingBooksListed = false;
            GlobalData.getInstance().topRentedBooksListed = false;

            GlobalData.getInstance().totalCartPrice = 0.00;
            GlobalData.getInstance().tempTotalCartPrice = 0.00;
            GlobalData.getInstance().totalEarning = 0.00;
            GlobalData.getInstance().conversationCount = 0;

            GlobalData.getInstance().requestList.clear();
            GlobalData.getInstance().myRequestList.clear();
            GlobalData.getInstance().categoryList.clear();
            GlobalData.getInstance().recentBooks.clear();
            GlobalData.getInstance().myShelfBooks.clear();
            GlobalData.getInstance().orderList.clear();
            GlobalData.getInstance().myOrderList.clear();
            GlobalData.getInstance().conversationList.clear();
            GlobalData.getInstance().topRecentBookList.clear();
            GlobalData.getInstance().topSellingBookList.clear();
            GlobalData.getInstance().topRentedBookList.clear();

            fetchPrefData();
        }

        protected void fetchPrefData(){
            if(preferences.getString("loggedin","").equals("yes")){
                //System.out.println("@@@@@@@@@@@@@@ user id set done");
                GlobalData.getInstance().userId = preferences.getInt("userId",0);
                GlobalData.getInstance().userName = preferences.getString("username","");
                GlobalData.getInstance().userPassword = preferences.getString("password","");
                GlobalData.getInstance().userFullName = preferences.getString("userFullName","");
                GlobalData.getInstance().userPhone = preferences.getString("userPhone","");
                GlobalData.getInstance().userEmail = preferences.getString("userEmail","");

                String userLat = preferences.getString("userLatitude","");
                String userlLong = preferences.getString("userLongitude","");

                if(userLat!="" && !userLat.isEmpty()){
                    GlobalData.getInstance().userLatitude = Double.parseDouble(userLat);
                }else{
                    GlobalData.getInstance().userLatitude = 0.00;
                }

                if(userlLong!="" && !userlLong.isEmpty()){
                    GlobalData.getInstance().userLongitude = Double.parseDouble(userlLong);
                }else{
                    GlobalData.getInstance().userLongitude = 0.00;
                }

                GlobalData.getInstance().userFirstName= preferences.getString("userFirstName","");
                GlobalData.getInstance().userLastName = preferences.getString("userLastName","");

                //checkCartValue();
                loadCategoryData();
            }else{
                Intent goLogin = new Intent(SplashActivity.this,LoginActivity.class);
                startActivity(goLogin);
                finish();
            }
        }
    }



    protected void loadCategoryData(){
        GlobalData.getInstance().categoryList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getCategories";
        String urlN = url+apiName+actionN;
        System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.GET, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Category c = new Category();
                        c.categoryId = Integer.parseInt(object.getString("idCategory"));
                        c.categoryName = object.getString("categoryName");

                        GlobalData.getInstance().categoryList.add(c);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading category data",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //GlobalData.getInstance().cartValue = GlobalData.getInstance().categoryList.size();

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList.size());

                //Intent goHome = new Intent(SplashActivity.this,MainActivity.class);
                //startActivity(goHome);
                loadMyRequestData();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading category data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }



    protected void loadMyRequestData(){
        //GlobalData.getInstance().totalCartPrice = 0.00;
        GlobalData.getInstance().myRequestList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getMyRequestItems";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Request c = new Request();
                        c.cartId = Integer.parseInt(object.getString("idCart"));
                        c.requesterId = Integer.parseInt(object.getString("idUser"));
                        c.ownerId = Integer.parseInt(object.getString("idOwner"));
                        c.bookId = Integer.parseInt(object.getString("idBook"));
                        c.bookTitle = object.getString("bookTitle");
                        c.bookPrice = Double.parseDouble(object.getString("bookPrice"));
                        c.purpose = Integer.parseInt(object.getString("purpose"));
                        c.status = Integer.parseInt(object.getString("status"));
                        String [] startDateTime = object.getString("dateAdded").split(" ");
                        c.dateAdded = startDateTime[0];
                        c.bookPicture = object.getString("bookPicture");
                        c.quantity = Integer.parseInt(object.getString("quantity"));
                        c.totalPrice = Double.parseDouble(object.getString("totalPrice"));
                        c.bookCategoryName = object.getString("categoryName");
                        c.bookAuthorName = object.getString("authorName");
                        c.ownerName = object.getString("ownerName");
                        if(object.getString("ownerFirstName").isEmpty() || object.getString("ownerFirstName").equals("null")){
                            c.ownerFirstName = "@";
                        }else{
                            c.ownerFirstName = object.getString("ownerFirstName");
                        }
                        if(object.getString("ownerLastName").isEmpty() || object.getString("ownerLastName").equals("null")){
                            c.ownerLastName = "@";
                        }else{
                            c.ownerLastName = object.getString("ownerLastName");
                        }
                        if(object.getString("ownerFirstName").isEmpty() || object.getString("ownerFirstName").equals("null") || object.getString("ownerLastName").isEmpty() || object.getString("ownerLastName").equals("null")){
                            c.ownerFullName = c.ownerName;
                        }else{
                            c.ownerFullName = object.getString("ownerFullName");
                        }
                        c.orderId = Integer.parseInt(object.getString("idOrder"));
                        c.bookConditionId = Integer.parseInt(object.getString("bookConditionId"));
                        //GlobalData.getInstance().totalCartPrice += c.totalPrice;

                        GlobalData.getInstance().myRequestList.add(c);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading request data",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Cart size="+GlobalData.getInstance().myRequestList.size());
                //GlobalData.getInstance().cartValue = GlobalData.getInstance().myRequestList.size();

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);

                loadRequestData();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading request data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }


    protected void loadRequestData(){
        GlobalData.getInstance().totalCartPrice = 0.00;
        GlobalData.getInstance().requestList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getRequestItems";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Request c = new Request();
                        c.cartId = Integer.parseInt(object.getString("idCart"));
                        c.requesterId = Integer.parseInt(object.getString("idUser"));
                        c.ownerId = Integer.parseInt(object.getString("idOwner"));
                        c.bookId = Integer.parseInt(object.getString("idBook"));
                        c.bookTitle = object.getString("bookTitle");
                        c.bookPrice = Double.parseDouble(object.getString("bookPrice"));
                        c.purpose = Integer.parseInt(object.getString("purpose"));
                        c.status = Integer.parseInt(object.getString("status"));
                        String [] startDateTime = object.getString("dateAdded").split(" ");
                        c.dateAdded = startDateTime[0];
                        c.bookPicture = object.getString("bookPicture");
                        c.quantity = Integer.parseInt(object.getString("quantity"));
                        c.totalPrice = Double.parseDouble(object.getString("totalPrice"));
                        c.bookCategoryName = object.getString("categoryName");
                        c.bookAuthorName = object.getString("authorName");
                        c.requesterName = object.getString("requesterName");

                        if(object.getString("requesterFirstName").isEmpty() || object.getString("requesterFirstName").equals("null")){
                            c.requesterFirstName = "@";
                        }else{
                            c.requesterFirstName = object.getString("requesterFirstName");
                        }
                        if(object.getString("requesterLastName").isEmpty() || object.getString("requesterLastName").equals("null")){
                            c.requesterLastName = "@";
                        }else{
                            c.requesterLastName = object.getString("requesterLastName");
                        }
                        if(object.getString("requesterFirstName").isEmpty() || object.getString("requesterFirstName").equals("null") || object.getString("requesterLastName").isEmpty() || object.getString("requesterLastName").equals("null")){
                            c.requesterFullName = c.requesterName;
                        }else{
                            c.requesterFullName = object.getString("requesterFullName");
                        }
                        c.orderId = Integer.parseInt(object.getString("idOrder"));
                        c.bookConditionId = Integer.parseInt(object.getString("bookConditionId"));
                        GlobalData.getInstance().totalCartPrice += c.totalPrice;

                        GlobalData.getInstance().requestList.add(c);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading request data",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Cart size="+GlobalData.getInstance().requestList.size());
                GlobalData.getInstance().cartValue = GlobalData.getInstance().requestList.size();

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);

                loadMyOrders();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading request data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }


    protected void loadMyOrders(){
        GlobalData.getInstance().myOrderList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getMyOrders";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Order o = new Order();
                        o.orderId = Integer.parseInt(object.getString("idOrder"));
                        o.userId = Integer.parseInt(object.getString("idUser"));
                        o.ownerId = Integer.parseInt(object.getString("idOwner"));
                        o.bookId = Integer.parseInt(object.getString("idBook"));
                        o.orderType = Integer.parseInt(object.getString("orderTypeId"));
                        o.dayCount = Integer.parseInt(object.getString("dayCount"));
                        o.bookTitle = object.getString("bookTitle");
                        o.bookPicture = object.getString("bookPicture");
                        o.totalBill = Double.parseDouble(object.getString("totalBill"));
                        o.orderStatusId = Integer.parseInt(object.getString("idOrderStatus"));
                        String [] startDateTime = object.getString("orderStartDate").split(" ");
                        o.orderStartDate= startDateTime[0];
                        String [] endDateTime = object.getString("orderEndDate").split(" ");
                        o.orderEndDate= endDateTime[0];
                        o.userName = object.getString("userName");
                        if(object.getString("userFirstName").isEmpty() || object.getString("userFirstName").equals("null")){
                            o.userFirstName = "@";
                        }else{
                            o.userFirstName = object.getString("userFirstName");
                        }
                        if(object.getString("userLastName").isEmpty() || object.getString("userLastName").equals("null")){
                            o.userLastName = "@";
                        }else{
                            o.userLastName = object.getString("userLastName");
                        }
                        if(object.getString("userFirstName").isEmpty() || object.getString("userFirstName").equals("null") || object.getString("userLastName").isEmpty() || object.getString("userLastName").equals("null")){
                            o.userFullName = o.userName;
                        }else{
                            o.userFullName = object.getString("userFullName");
                        }

                        GlobalData.getInstance().myOrderList.add(o);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading order data",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Orders size="+GlobalData.getInstance().requestList.size());

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);

                loadOrders();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading order data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }



    protected void loadOrders(){
        GlobalData.getInstance().totalEarning = 0.00;
        GlobalData.getInstance().orderList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getOrders";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Order o = new Order();
                        o.orderId = Integer.parseInt(object.getString("idOrder"));
                        o.userId = Integer.parseInt(object.getString("idUser"));
                        o.ownerId = Integer.parseInt(object.getString("idOwner"));
                        o.bookId = Integer.parseInt(object.getString("idBook"));
                        o.orderType = Integer.parseInt(object.getString("orderTypeId"));
                        o.dayCount = Integer.parseInt(object.getString("dayCount"));
                        o.bookTitle = object.getString("bookTitle");
                        o.bookPicture = object.getString("bookPicture");
                        o.totalBill = Double.parseDouble(object.getString("totalBill"));
                        o.orderStatusId = Integer.parseInt(object.getString("idOrderStatus"));
                        String [] startDateTime = object.getString("orderStartDate").split(" ");
                        o.orderStartDate= startDateTime[0];
                        String [] endDateTime = object.getString("orderEndDate").split(" ");
                        o.orderEndDate= endDateTime[0];
                        o.userName = object.getString("userName");
                        if(object.getString("userFirstName").isEmpty() || object.getString("userFirstName").equals("null")){
                            o.userFirstName = "@";
                        }else{
                            o.userFirstName = object.getString("userFirstName");
                        }
                        if(object.getString("userLastName").isEmpty() || object.getString("userLastName").equals("null")){
                            o.userLastName = "@";
                        }else{
                            o.userLastName = object.getString("userLastName");
                        }
                        if(object.getString("userFirstName").isEmpty() || object.getString("userFirstName").equals("null") || object.getString("userLastName").isEmpty() || object.getString("userLastName").equals("null")){
                            o.userFullName = o.userName;
                        }else{
                            o.userFullName = object.getString("userFullName");
                        }

                        GlobalData.getInstance().totalEarning += o.totalBill;
                        GlobalData.getInstance().orderList.add(o);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading order data",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Orders size="+GlobalData.getInstance().requestList.size());

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);

                getConversations();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading order data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }


    protected void getConversations(){
        GlobalData.getInstance().conversationCount = 0;
        GlobalData.getInstance().conversationList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getConversations";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Conversation c = new Conversation();

                        int fromId = Integer.parseInt(object.getString("idFrom"));
                        int toId = Integer.parseInt(object.getString("idTo"));

                        if(fromId==GlobalData.getInstance().userId){
                            c.messageCount = 0;
                            c.userId = toId;
                            c.userName = object.getString("toUsername");
                            if(object.getString("toFullName").isEmpty() || object.getString("toFullName").equals("") || object.getString("toFullName").substring(0,1).equals("@")){
                                c.userFullName = c.userName;
                            }else{
                                c.userFullName = object.getString("toFullName");
                            }
                            c.chatImageText = c.userFullName.substring(0,1).toUpperCase();
                        }else if(toId==GlobalData.getInstance().userId){
                            System.out.println("yes");
                            int total = Integer.parseInt(object.getString("total"));
                            if(total>0){
                                GlobalData.getInstance().conversationCount++;
                            }
                            c.messageCount = total;
                            c.userId = fromId;
                            c.userName = object.getString("fromUsername");
                            if(object.getString("fromFullName").isEmpty() || object.getString("fromFullName").equals("") || object.getString("fromFullName").substring(0,1).equals("@")){
                                c.userFullName = c.userName;
                            }else{
                                c.userFullName = object.getString("fromFullName");
                            }
                            c.chatImageText = c.userFullName.substring(0,1).toUpperCase();
                        }

                        System.out.println("------------------------From:"+c.userName+"; count:"+c.messageCount);

                        int flag = 0;
                        for(Conversation cnv : GlobalData.getInstance().conversationList){
                            if(cnv.userId==c.userId){
                                flag = 1;
                                if(cnv.messageCount<c.messageCount){
                                    cnv.messageCount = c.messageCount;
                                }
                                System.out.println("------------------------got it-----From:"+cnv.userName+"; count:"+cnv.messageCount);
                            }
                        }

                        if(flag==0){
                            GlobalData.getInstance().conversationList.add(c);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading message data",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Orders size="+GlobalData.getInstance().requestList.size());

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);

                loadRecentBooks();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading message data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }


    protected void loadRecentBooks(){
        GlobalData.getInstance().recentBooks.clear();
        apiName = "api_getData.php";
        String actionN = action+"getRecentBooks";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Book b = new Book();
                        b.id = Integer.parseInt(object.getString("idBook"));
                        b.price = Double.parseDouble(object.getString("bookPrice"));
                        b.minimumQuantity = Integer.parseInt(object.getString("minimumQuantity"));
                        b.totalPage = Integer.parseInt(object.getString("totalPage"));
                        b.title = object.getString("bookTitle");
                        b.picture = object.getString("bookPicture");
                        b.link = object.getString("bookLink");
                        b.edition = object.getString("edition");
                        b.isbn = object.getString("isbn");
                        b.publisher = object.getString("publisher");
                        b.country = object.getString("country");
                        b.language = object.getString("language");
                        b.description = object.getString("bookDescription");
                        b.category = object.getString("categoryName");
                        b.authorName = object.getString("authorName");
                        b.idUser = Integer.parseInt(object.getString("idUser"));
                        b.idOwner = Integer.parseInt(object.getString("idOwner"));
                        b.ownerFullName = object.getString("userFullName");
                        b.ownerUsername = object.getString("userUserName");

                        if(object.getString("userFirstName").isEmpty() || object.getString("userFirstName").equals("null")){
                            b.ownerFirstName = "@";
                        }else{
                            b.ownerFirstName = object.getString("userFirstName");
                        }

                        if(object.getString("userLastName").isEmpty() || object.getString("userLastName").equals("null")){
                            b.ownerLastName = "@";
                        }else{
                            b.ownerLastName = object.getString("userLastName");
                        }

                        if(object.getString("userFirstName").isEmpty() || object.getString("userFirstName").equals("null") || object.getString("userLastName").isEmpty() || object.getString("userLastName").equals("null")){
                            b.ownerFullName = b.ownerUsername;
                        }else{
                            b.ownerFullName = object.getString("userFullName");
                        }

                        b.purpose = Integer.parseInt(object.getString("purpose"));
                        b.idCondition = Integer.parseInt(object.getString("bookConditionId"));
                        if(b.idCondition==0 || b.idCondition==1){
                            b.conditionName = "New";
                        }else if(b.idCondition==2){
                            b.conditionName = "Used";
                        }

                        for(Request c : GlobalData.getInstance().myRequestList){
                            if(b.id == c.bookId){
                                b.cartValue = c.quantity;
                            }

                            //System.out.println("Book id="+c.bookId+","+b.id+" ; quantity="+c.quantity+","+b.cartValue);
                        }
                        System.out.println("Book id="+b.id+" ; purpose="+b.purpose);

                        GlobalData.getInstance().recentBooks.add(b);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading recent books",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Recent books size="+GlobalData.getInstance().recentBooks.size());

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);

                Intent goHome = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(goHome);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading recent books",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }



    private void checkCartValue() {
        apiName = "api_getData.php";
        action = action+"getCartValue";
        url = url+apiName+action;
        //System.out.println("----------URL= "+url);

        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String [] s= response.split("-");
                if(s[0].equals("yes")){
                    GlobalData.getInstance().cartValue = Integer.parseInt(s[1]);
                    System.out.println("-+-+-+-+-+-+-+-+-cart value="+GlobalData.getInstance().cartValue+";");
                    Intent goHome = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(goHome);
                } else{
                    GlobalData.getInstance().cartValue = 0;
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>"+response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error occured!",Toast.LENGTH_SHORT).show();
                //pb.setVisibility(View.GONE);
                //System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<"+"ERRoR");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");
                //params.put("password",password);

                return params;
            }
        };


        //System.out.println("edUsername= "+GlobalData.getInstance().username);
        //System.out.println(GlobalData.getInstance());
        GlobalData.getInstance().addToRequestQueue(request);
    }
}

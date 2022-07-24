package com.example.bookshare.model;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class GlobalData extends Application {
    public static final String TAG = GlobalData.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    private static GlobalData mInstance;

    public static final String url = "http://app.al-hilaal.net/bookshare/api/";

    public String userName = "";
    public String userFirstName = "";
    public String userLastName = "";
    public String userFullName = "";
    public int userId = 0;
    public String userPassword = "";
    public String userEmail = "";
    public String userPhone = "";
    public double userLatitude = 0.0000;
    public double userLongitude = 0.0000;
    public int cartValue = 0;
    public int lastCartId;
    public double totalCartPrice = 0.00;
    public double tempTotalCartPrice = 0.00;
    public double totalEarning = 0.00;
    public int conversationCount = 0;

    public boolean shelfBooksListed = false;
    public boolean topRecentBooksListed = false;
    public boolean topSellingBooksListed = false;
    public boolean topRentedBooksListed = false;

    public List<Request> requestList = new ArrayList<Request>();
    public List<Request> myRequestList = new ArrayList<Request>();
    public List<Category> categoryList = new ArrayList<Category>();
    public List<Book> recentBooks = new ArrayList<Book>();
    public List<Book> myShelfBooks = new ArrayList<Book>();
    public List<Order> orderList = new ArrayList<Order>();
    public List<Order> myOrderList = new ArrayList<Order>();
    public List<Conversation> conversationList = new ArrayList<Conversation>();
    public List<Book> topRecentBookList = new ArrayList<Book>();
    public List<Book> topSellingBookList = new ArrayList<Book>();
    public List<Book> topRentedBookList = new ArrayList<Book>();


    public int TAG_RECENT_BOOKS = 0;
    public int TAG_SELL_BOOKS = 1;
    public int TAG_RENT_BOOKS = 2;

    public int FROM_NOTIFICATION = 1;
    public int NOT_FROM_NOTIFICATION = 0;

    //set default status value 2 which means no book_icon in the process of adding to cart


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized GlobalData getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        //System.out.println("@@@@@@request processing");
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        //System.out.println("@@@@@@request processed");
        return mRequestQueue;
    }



    public <T> void addToRequestQueue(com.android.volley.Request req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(com.android.volley.Request req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}

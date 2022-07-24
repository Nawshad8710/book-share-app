package com.example.bookshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookshare.adapter.ListAdapterCategoryBookList;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreBooksActivity extends AppCompatActivity {

    private String categoryName;
    private int tag;
    private TextView moreBooksTitle, foundBooksQuantity, foundMsg;
    private ImageView backBtn;
    private ProgressBar pbMoreBookList;
    private ListView moreBookList;
    private String[] bookNames = {
            "Book - 1", "Book - 2", "Book - 3", "Book - 4",
            "Book - 5", "Book - 6", "Book - 7", "Book - 8",
            "Book - 9", "Book - 10", "Book - 11", "Book - 12"
    };
    private List<Book> bookList = new ArrayList<Book>();

    SharedPreferences preferences;
    public static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private int startId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_books);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        pbMoreBookList = (ProgressBar) findViewById(R.id.pbMoreBookList);
        pbMoreBookList.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        tag = intent.getIntExtra("tag",0);

        moreBookList = (ListView) findViewById(R.id.more_book_list);
        //foundBooksQuantity = (TextView) findViewById(R.id.foundBooksQuantity);
        //foundMsg = (TextView) findViewById(R.id.foundMsg);
        //System.out.println("Category book_icon list size="+bookList.size());

        //setTitle
        moreBooksTitle = (TextView) findViewById(R.id.book_title);
        if(tag==0){
            moreBooksTitle.setText("Top Recent Books");
            if(GlobalData.getInstance().topRecentBooksListed){
                showBookList(GlobalData.getInstance().topRecentBookList);
            }else{
                //load more books
                loadRecentBooks();
            }
        }else if(tag==1){
            moreBooksTitle.setText("Top Selling Books");
            if(GlobalData.getInstance().topSellingBooksListed){
                showBookList(GlobalData.getInstance().topSellingBookList);
            }else{
                //load more books
                loadRecentBooks();
            }
        }else if(tag==2){
            moreBooksTitle.setText("Top Rented Books");
            if(GlobalData.getInstance().topRentedBooksListed){
                showBookList(GlobalData.getInstance().topRentedBookList);
            }else{
                //load more books
                loadRecentBooks();
            }
        }

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

    protected void loadRecentBooks(){

        bookList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getMoreBooks";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                if(tag==GlobalData.getInstance().TAG_RECENT_BOOKS){
                    GlobalData.getInstance().topRecentBooksListed = true;
                    GlobalData.getInstance().topRecentBookList.clear();
                }else if(tag==GlobalData.getInstance().TAG_SELL_BOOKS){
                    GlobalData.getInstance().topSellingBooksListed = true;
                    GlobalData.getInstance().topSellingBookList.clear();
                }else if(tag==GlobalData.getInstance().TAG_RENT_BOOKS){
                    GlobalData.getInstance().topRentedBooksListed = true;
                    GlobalData.getInstance().topRentedBookList.clear();
                }

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
                            b.ownerFullName =  b.ownerUsername;
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
                        //System.out.println("Book id="+b.id+" ; purpose="+b.purpose);

                        //bookList.add(b);
                        if(tag==GlobalData.getInstance().TAG_RECENT_BOOKS){
                            b.totalSell = Integer.parseInt(object.getString("total_sell"));
                            b.totalRent = Integer.parseInt(object.getString("total_rent"));
                            GlobalData.getInstance().topRecentBookList.add(b);
                        }else if(tag==GlobalData.getInstance().TAG_SELL_BOOKS){
                            b.totalSell = Integer.parseInt(object.getString("total_sell"));
                            b.totalRent = 0;
                            GlobalData.getInstance().topSellingBookList.add(b);
                        }else if(tag==GlobalData.getInstance().TAG_RENT_BOOKS){
                            b.totalSell = 0;
                            b.totalRent = Integer.parseInt(object.getString("total_rent"));
                            GlobalData.getInstance().topRentedBookList.add(b);
                        }

                        for(Request c : GlobalData.getInstance().myRequestList){
                            if(b.id == c.bookId){
                                b.cartValue = c.quantity;
                            }

                            //System.out.println("Book id="+c.bookId+","+b.id+" ; quantity="+c.quantity+","+b.cartValue);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading books",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Recent books size="+GlobalData.getInstance().recentBooks.size());

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);
                if(tag==GlobalData.getInstance().TAG_RECENT_BOOKS){
                    showBookList(GlobalData.getInstance().topRecentBookList);
                }else if(tag==GlobalData.getInstance().TAG_SELL_BOOKS){
                    showBookList(GlobalData.getInstance().topSellingBookList);
                }else if(tag==GlobalData.getInstance().TAG_RENT_BOOKS){
                    showBookList(GlobalData.getInstance().topRentedBookList);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");
                params.put("startId",startId+"");
                params.put("type",tag+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }

    private void showBookList(List<Book> topBookList){
        pbMoreBookList.setVisibility(View.GONE);
        ListAdapterCategoryBookList adapter = new ListAdapterCategoryBookList(topBookList,getApplicationContext());
        moreBookList.setAdapter(adapter);
        moreBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = topBookList.get(position);
                //Toast.makeText(getApplicationContext(),"Position "+position,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),SingleBookActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });
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

    @Override
    public void onBackPressed() {
        Intent goHome = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(goHome);
        finish();
    }
}
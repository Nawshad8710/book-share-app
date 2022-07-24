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
import com.example.bookshare.model.Request;
import com.example.bookshare.model.GlobalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private String categoryName;
    private int categoryId;
    private TextView category_title, foundBooksQuantity, foundMsg;
    private ImageView backBtn;
    private ProgressBar pbCategoryBookList;
    private ListView categoryBookList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        pbCategoryBookList = (ProgressBar) findViewById(R.id.pbMoreBookList);
        pbCategoryBookList.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        categoryName = intent.getStringExtra("title");
        categoryId = intent.getIntExtra("id",1);

        //setTitle
        category_title = (TextView) findViewById(R.id.book_title);
        category_title.setText(categoryName);

        //fetch books of this category
        getThisCategoryBooks();

        categoryBookList = (ListView) findViewById(R.id.more_book_list);
        foundBooksQuantity = (TextView) findViewById(R.id.foundBooksQuantity);
        foundMsg = (TextView) findViewById(R.id.foundMsg);
        System.out.println("Category book_icon list size="+bookList.size());

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


    private void getThisCategoryBooks(){
        bookList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getBooksByCategory";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("------------------@@@@@@response");

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
                        b.authorName = object.getString("author");
                        b.idUser = Integer.parseInt(object.getString("idUser"));
                        b.idOwner = Integer.parseInt(object.getString("idOwner"));
                        b.ownerUsername = object.getString("ownerUserName");
                        if(object.getString("ownerFirstName").isEmpty() || object.getString("ownerFirstName").equals("null")){
                            b.ownerFirstName = "@";
                        }else{
                            b.ownerFirstName = object.getString("ownerFirstName");
                        }
                        if(object.getString("ownerLastName").isEmpty() || object.getString("ownerLastName").equals("null")){
                            b.ownerLastName = "@";
                        }else{
                            b.ownerLastName = object.getString("ownerLastName");
                        }
                        if(object.getString("ownerFirstName").isEmpty() || object.getString("ownerFirstName").equals("null") || object.getString("ownerLastName").isEmpty() || object.getString("ownerLastName").equals("null")){
                            b.ownerFullName =  b.ownerUsername;
                        }else{
                            b.ownerFullName = object.getString("ownerFullName");
                        }
                        b.purpose = Integer.parseInt(object.getString("purpose"));

                        for(Request c : GlobalData.getInstance().myRequestList){
                            if(b.id == c.bookId){
                                b.cartValue = c.quantity;
                            }

                            //System.out.println("Book id="+c.bookId+","+b.id+" ; quantity="+c.quantity+","+b.cartValue);
                        }

                        b.idCondition = Integer.parseInt(object.getString("bookConditionId"));
                        if(b.idCondition==2){
                            b.conditionName = "Used";
                        }else{
                            b.conditionName = "New";
                        }
                        b.totalSell = Integer.parseInt(object.getString("totalSell"));
                        b.totalRent = Integer.parseInt(object.getString("totalRent"));

                        bookList.add(b);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading category books",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }

                pbCategoryBookList.setVisibility(View.GONE);
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Category books size="+bookList.size());

                if(bookList.size()>0){
                    foundBooksQuantity.setText(bookList.size()+"");
                    foundMsg.setVisibility(View.VISIBLE);

                    ListAdapterCategoryBookList adapter = new ListAdapterCategoryBookList(bookList,getApplicationContext());
                    categoryBookList.setAdapter(adapter);
                    categoryBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Book book = bookList.get(position);
                            //Toast.makeText(getApplicationContext(),"Position "+position,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),SingleBookActivity.class);
                            intent.putExtra("book", book);
                            startActivity(intent);
                        }
                    });
                }else{
                    foundMsg.setText("No books found for this category!");
                    foundMsg.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading category data",Toast.LENGTH_LONG).show();
                System.out.println("------------------@@@@@@response");
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("categoryId",categoryId+"");
                params.put("userId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
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

    @Override
    public void onBackPressed() {
        Intent goHome = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(goHome);
        finish();
    }
}

package com.example.bookshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class SearchActivity extends AppCompatActivity {

    private String categoryName;
    private EditText searchName;
    private ImageView backBtn;
    private TextView foundBooksQuantity, foundMsg;
    private ListView searchBookList;
    private ProgressBar pbSearchBookList;
    private List<Book> bookList = new ArrayList<Book>();

    SharedPreferences preferences;
    public static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        searchBookList = (ListView) findViewById(R.id.more_book_list);
        ListAdapterCategoryBookList adapter = new ListAdapterCategoryBookList(bookList,getApplicationContext());
        searchBookList.setAdapter(adapter);
        searchBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),"Position "+position,Toast.LENGTH_SHORT).show();
                Book book = bookList.get(position);
                Intent intent = new Intent(getApplicationContext(),SingleBookActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });

        //Search text
        searchName = (EditText) findViewById(R.id.search_name);

        pbSearchBookList = (ProgressBar) findViewById(R.id.pbSearchBookList);
        pbSearchBookList.setVisibility(View.GONE);

        foundBooksQuantity = (TextView) findViewById(R.id.foundBooksQuantity);
        foundMsg = (TextView) findViewById(R.id.foundMsg);
        foundMsg.setText("Empty result!");
        foundMsg.setVisibility(View.VISIBLE);
        foundBooksQuantity.setVisibility(View.GONE);


        searchName.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0)
                    getSearchBooks();
            }
        });

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

    private void getSearchBooks(){
        bookList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getBooksBySearchTerm";
        String urlN = url+apiName+actionN;

        pbSearchBookList.setVisibility(View.VISIBLE);

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
                        if(b.purpose==1){
                            b.totalSell = Integer.parseInt(object.getString("total_sell"));
                            b.totalRent = 0;
                        }else if(b.purpose==2){
                            b.totalRent = Integer.parseInt(object.getString("total_rent"));
                            b.totalSell = 0;
                        }

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

                        bookList.add(b);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading books",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }

                pbSearchBookList.setVisibility(View.GONE);
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Category books size="+bookList.size());

                if(bookList.size()>0){
                    foundBooksQuantity.setText(bookList.size()+"");
                    foundMsg.setVisibility(View.VISIBLE);
                    foundMsg.setText(" book(s) found");
                    foundBooksQuantity.setVisibility(View.VISIBLE);

                    ListAdapterCategoryBookList adapter = new ListAdapterCategoryBookList(bookList,getApplicationContext());
                    searchBookList.setAdapter(adapter);
                    searchBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    //foundMsg.setText("No books found for this category!");
                    foundMsg.setVisibility(View.VISIBLE);
                    foundMsg.setText("No book found for this name!");
                    foundBooksQuantity.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading data",Toast.LENGTH_LONG).show();
                //System.out.println("------------------@@@@@@response");
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("searchTerm",searchName.getText().toString());
                params.put("userId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);
    }
}

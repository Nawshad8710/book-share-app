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
import com.example.bookshare.adapter.ListAdapterUserBookList;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Request;
import com.example.bookshare.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBooks extends AppCompatActivity {

    private String userName;
    private int userId;
    private TextView user_name, foundBooksQuantity, foundMsg;
    private ImageView backBtn;
    private ProgressBar pbUserBookList;
    private ListView userBookList;
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

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_books);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        pbUserBookList = (ProgressBar) findViewById(R.id.pbUserBookList);
        pbUserBookList.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        //userName = intent.getStringExtra("title");
        //userId = intent.getIntExtra("id",1);
        user = (User) intent.getSerializableExtra("user");

        //setTitle
        user_name = (TextView) findViewById(R.id.user_name);
        if(!user.getFirstName().isEmpty() && !user.getFirstName().equals("") && user.getFirstName().substring(0,1).equals("@")){
            user_name.setText(user.getUsername());
        }else{
            if(user.getLastName().substring(0,1).equals("@")){
                user_name.setText(user.getFirstName());
            }else{
                user_name.setText(user.getFirstName()+" "+user.getLastName());
            }
        }

        //fetch books of this category
        getThisUserBooks();

        foundBooksQuantity = (TextView) findViewById(R.id.foundBooksQuantity);
        foundMsg = (TextView) findViewById(R.id.foundMsg);
        System.out.println("Category book_icon list size="+bookList.size());

        //action back
        backBtn = (ImageView) findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userBookList = (ListView) findViewById(R.id.user_book_list);

        //pbUserBookList.setVisibility(View.GONE);
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

    private void getThisUserBooks(){
        bookList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getBooksByUser";
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
                        b.authorName = object.getString("authorName");
                        //b.idUser = Integer.parseInt(object.getString("idUser"));
                        b.idOwner = Integer.parseInt(object.getString("idOwner"));
                        b.ownerFullName = object.getString("userName");
                        b.ownerUsername = object.getString("userUserName");
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
                        Toast.makeText(getApplicationContext(),"Exception on loading books",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }

                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Category books size="+bookList.size());

                if(bookList.size()>0){
                    foundBooksQuantity.setText(bookList.size()+"");
                    foundMsg.setVisibility(View.VISIBLE);

                    ListAdapterUserBookList adapter = new ListAdapterUserBookList(bookList,getApplicationContext());
                    userBookList.setAdapter(adapter);
                    userBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    foundMsg.setText("No books found for this user!");
                    foundMsg.setVisibility(View.VISIBLE);
                }
                pbUserBookList.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading data",Toast.LENGTH_LONG).show();
                System.out.println("------------------@@@@@@response");
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("ownerId",user.idUser+"");
                params.put("myId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);
    }
}
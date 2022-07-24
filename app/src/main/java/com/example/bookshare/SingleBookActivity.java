package com.example.bookshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Request;
import com.example.bookshare.model.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SingleBookActivity extends AppCompatActivity {

    private String bookName;
    private int bookId;
    private TextView book_title, book_price, book_purpose, sell_quantity, author_name, owner_name, txtSent;
    private TextView category, condition;
    private ImageView backBtn, bookDP;
    private TabHost host;
    private Button btnRequest, submit_comment;
    private LinearLayout btnEdit;
    private ListView commentList;
    private ProgressBar pbRequest;

    private Book book;
    //private TabWidget widget;

    private SharedPreferences preferences;
    private static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_book);
        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        Intent intent = getIntent();
        book = (Book) intent.getSerializableExtra("book");

        pbRequest = (ProgressBar) findViewById(R.id.pbRequest);
        pbRequest.setVisibility(View.GONE);

        //setTitle
        book_title = (TextView) findViewById(R.id.book_title);
        book_title.setText(book.getTitle());

        //set selling and rent quantity
        sell_quantity = (TextView) findViewById(R.id.sell_quantity);
        sell_quantity.setText("");

        //set price
        book_price = (TextView) findViewById(R.id.book_price);

        //set username
        owner_name = (TextView) findViewById(R.id.book_owner);
        owner_name.setText("@" + book.getOwnerUsername());
        owner_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("firstname:"+book.getOwnerFirstName()+"; lastname:"+book.getOwnerLastName()+"; username:"+book.getOwnerUsername());
                User u = new User(book.getIdOwner(), book.getOwnerUsername(), book.getOwnerFirstName(), book.getOwnerLastName());
                Intent i = new Intent(getApplicationContext(), UserBooks.class);
                i.putExtra("user", u);
                startActivity(i);
            }
        });

        //action back
        backBtn = (ImageView) findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Book Image set
        bookDP = (ImageView) findViewById(R.id.book_dp);
        if(book.picture.isEmpty() || book.picture.equals("") || book.picture.equals("null")){
            bookDP.setImageResource(R.drawable.single_book);
        }else{
            String url = GlobalData.getInstance().url+book.picture;
            //bookImage.setImageResource(LoadImageFromWebOperations(b.picture).picture);
            Picasso.get().load(url).into(bookDP);
        }

        btnEdit = (LinearLayout) findViewById(R.id.btn_edit);
        btnEdit.setVisibility(View.GONE);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BookUploadActivity.class);
                i.putExtra("for", "edit");
                i.putExtra("book", book);
                startActivity(i);
                finish();
            }
        });

        //configure request button
        btnRequest = (Button) findViewById(R.id.btn_request);

        txtSent = (TextView) findViewById(R.id.txt_sent);
        if(book.cartValue>0){
            btnRequest.setVisibility(View.GONE);
            txtSent.setVisibility(View.VISIBLE);
        }else{
            btnRequest.setVisibility(View.VISIBLE);
            txtSent.setVisibility(View.GONE);
        }

        //set purpose
        book_purpose = (TextView) findViewById(R.id.book_purpose);
        if(book.getPurpose()==1) {
            book_purpose.setText("For Sell");
            book_purpose.setBackgroundColor(getResources().getColor(R.color.holo_red_dark));
            btnRequest.setText("Send Buy Request");
            book_price.setText(book.getPrice() + " Tk.");
        }else if(book.getPurpose()==2) {
            book_purpose.setText("For Rent");
            book_purpose.setBackgroundColor(Color.BLUE);
            btnRequest.setText("Send Rent Request");
            book_price.setText(book.getPrice() + " Tk. / day");
        }

        if(book.getOwnerUsername().equals(GlobalData.getInstance().userName)){
            btnRequest.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
        }

        //TabHost set
        host = (TabHost)findViewById(R.id.tabHost);
        host.setup();


        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        //set the content for the tab as we set content for an activity in the oncreate method
        //you can pass an intent to this as well to load an activity or load a fragment
        //since we have defined our content in the activity_main.xml
        // we will pass the tab1 id to our setcontent method

        spec.setContent(R.id.tab1);
        spec.setIndicator("Details", getResources().getDrawable(R.drawable.tab_selector_one));
        host.addTab(spec);

        category = findViewById(R.id.book_category);
        category.setText(book.getCategory());

        condition = findViewById(R.id.book_condition);
        condition.setText(book.getConditionName());

        if(book.getConditionName().equals("New")){
            condition.setTextColor(getResources().getColor(R.color.green));
        }else if(book.getConditionName().equals("Used")){
            condition.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        author_name = findViewById(R.id.book_author);
        author_name.setText(book.getAuthorName());

        getBookInfo();

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbRequest.setVisibility(View.VISIBLE);
                btnRequest.setVisibility(View.GONE);
                //Add to cart in database table process start
                apiName = "api_insertData.php";
                String actionN = action+"addToCart";
                String urlN = url+apiName+actionN;
                //System.out.println("----------URL= "+urlN);
                boolean status;

                StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //System.out.println("************URL= "+urlN);
                        String [] s= response.split("-");
                        if(s[0].toString().equals("yes")){
                            //holder.pbCart.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Request sent successfully!",Toast.LENGTH_SHORT).show();
                            pbRequest.setVisibility(View.GONE);
                            txtSent.setVisibility(View.VISIBLE);

                            GlobalData.getInstance().lastCartId = Integer.parseInt(s[1]);
                            book.cartValue += 1;
                            for(Book b: GlobalData.getInstance().recentBooks){
                                if(book.id==b.id){
                                    b.cartValue++;
                                }
                            }

                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                            Date date = new Date();

                            String nowDate = formatter.format(date);

                            GlobalData.getInstance().myRequestList.add(new Request(GlobalData.getInstance().lastCartId, book.id, book.title, book.price, book.picture, GlobalData.getInstance().userId, book.idOwner, book.cartValue, book.getTotalPriceForCart(), book.purpose, 0, nowDate, GlobalData.getInstance().userName, book.ownerUsername,  GlobalData.getInstance().userFullName, book.ownerUsername, book.idCondition));
                        } else if(response.toString().equals("tokenFail")){
                            //holder.pbCart.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Wrong token, try again",Toast.LENGTH_SHORT).show();
                            pbRequest.setVisibility(View.GONE);
                            btnRequest.setVisibility(View.VISIBLE);
                        } else{
                            //holder.pbCart.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(),"Failed to send request!",Toast.LENGTH_SHORT).show();
                            pbRequest.setVisibility(View.GONE);
                            btnRequest.setVisibility(View.VISIBLE);
                            //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);
                            //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //holder.pbCart.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Error occurred!",Toast.LENGTH_SHORT).show();
                        pbRequest.setVisibility(View.GONE);
                        btnRequest.setVisibility(View.VISIBLE);
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("myToken", "786");
                        params.put("userId",GlobalData.getInstance().userId+"");
                        params.put("bookId",book.getId()+"");
                        params.put("ownerId",book.getIdOwner()+"");
                        params.put("quantity",1+"");
                        params.put("totalPrice",book.getPrice()+"");
                        params.put("purpose",book.getPurpose()+"");

                        return params;
                    }
                };


                //System.out.println("edUsername= "+GlobalData.getInstance().username);
                //System.out.println(GlobalData.getInstance());
                GlobalData.getInstance().addToRequestQueue(request);
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


    private void getBookInfo(){
        apiName = "api_getData.php";
        String actionN = action+"getBookInfo";
        String urlN = url+apiName+actionN;
        //System.out.println("----------URL= "+urlN);

        StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String [] s= response.split("/");
                if(s[0].equals("yes")){
                    //Intent i = new Intent(getApplicationContext(),OrderActivity.class);
                    //startActivity(i);
                    int total_sell = Integer.parseInt(s[8]);
                    int total_rent = Integer.parseInt(s[9]);

                    if(total_sell>0 && total_rent>0){
                        sell_quantity.setText(total_sell+" sold, "+total_rent+" rented");
                    }else if(total_sell>0 && total_rent<=0){
                        sell_quantity.setText(total_sell+" sold");
                    }else if(total_rent>0 && total_sell<=0){
                        sell_quantity.setText(total_rent+" rented");
                    }

                } else if(response.toString().equals("tokenFail")){
                    Toast.makeText(getApplicationContext(),"Wrong token, try again",Toast.LENGTH_SHORT).show();
                    Intent reload = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(reload);
                    finish();

                } else{
                    Toast.makeText(getApplicationContext(),"Error loading book details!",Toast.LENGTH_LONG).show();
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>"+response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error occurred!",Toast.LENGTH_SHORT).show();
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
                params.put("bookId",book.getId()+"");

                return params;
            }
        };


        //System.out.println("edUsername= "+GlobalData.getInstance().username);
        //System.out.println(GlobalData.getInstance());
        GlobalData.getInstance().addToRequestQueue(request);
    }
}

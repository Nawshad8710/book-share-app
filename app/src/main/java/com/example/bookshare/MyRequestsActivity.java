package com.example.bookshare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

import com.example.bookshare.adapter.ListAdapterCategoryBookList;
import com.example.bookshare.adapter.ListAdapterMyRequestList;
import com.example.bookshare.adapter.ListAdapterRequestBookList;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Request;

import java.util.ArrayList;
import java.util.List;

public class MyRequestsActivity extends AppCompatActivity {

    private TextView foundBooksQuantity, foundMsg;
    private ImageView backBtn;
    private ProgressBar pbMyRequestList;
    private ListView myRequestList;
    private List<Book> mrList = new ArrayList<Book>();
    public static AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requests);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        pbMyRequestList = (ProgressBar) findViewById(R.id.pbMyRequestList);
        pbMyRequestList.setVisibility(View.VISIBLE);

        myRequestList = (ListView) findViewById(R.id.myrequest_list);
        foundBooksQuantity = (TextView) findViewById(R.id.foundBooksQuantity);
        foundMsg = (TextView) findViewById(R.id.foundMsg);

        builder = new AlertDialog.Builder(getApplicationContext());

        if(GlobalData.getInstance().myRequestList.size()>0){
            foundBooksQuantity.setText(GlobalData.getInstance().myRequestList.size()+"");
            foundMsg.setVisibility(View.VISIBLE);

            ListAdapterMyRequestList adapter = new ListAdapterMyRequestList(GlobalData.getInstance().myRequestList,getApplicationContext(),myRequestList, builder);
            myRequestList.setAdapter(adapter);
            pbMyRequestList.setVisibility(View.GONE);
            myRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(getApplicationContext(),"Title: "+ GlobalData.getInstance().myRequestList.get(position).getBookTitle(),Toast.LENGTH_SHORT).show();
                    Request r = GlobalData.getInstance().requestList.get(position);
                    Book book = new Book();
                    book.id = r.bookId;
                    book.idOwner = r.ownerId;
                    book.title = r.bookTitle;
                    book.price = r.bookPrice;
                    book.purpose = r.purpose;
                    book.ownerUsername = r.requesterName;
                    book.category = r.bookCategoryName;
                    book.authorName = r.bookAuthorName;
                    book.picture = r.bookPicture;
                    book.idCondition = r.bookConditionId;
                    if(book.idCondition==0 || book.idCondition==1){
                        book.conditionName = "New";
                    }else if(book.idCondition==2){
                        book.conditionName = "Used";
                    }

                    Intent intent = new Intent(getApplicationContext(), SingleBookActivity.class);
                    intent.putExtra("book", book);
                    startActivity(intent);
                    //startActivity(intent);
                }
            });
        }else{
            foundMsg.setText("No request found!");
            foundMsg.setVisibility(View.VISIBLE);
            pbMyRequestList.setVisibility(View.GONE);
        }

        //System.out.println("Category book_icon list size="+bookList.size());

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

    @Override
    public void onBackPressed() {
        Intent goHome = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(goHome);
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
}
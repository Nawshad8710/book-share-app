package com.example.bookshare.ui.myshelf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookshare.BookUploadActivity;
import com.example.bookshare.LoginActivity;
import com.example.bookshare.R;
import com.example.bookshare.SingleBookActivity;
import com.example.bookshare.adapter.GridAdapterMyshelf;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.GlobalData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyshelfFragment extends Fragment {

    private MyshelfViewModel myshelfViewModel;
    private GridView gridview_myshelf_books;
    private String bookUrl = "";
    private FloatingActionButton fab;
    private LinearLayout btnAllBooks, btnFilter, btnRefresh;

    private TextView titleText, foundBooks;
    private List<Book> bookList = new ArrayList<Book>();
    private ProgressBar pbMyshelf;

    SharedPreferences preferences;
    public static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myshelfViewModel =
                ViewModelProviders.of(this).get(MyshelfViewModel.class);
        View root = inflater.inflate(R.layout.fragment_myshelf, container, false);
        final TextView textView = root.findViewById(R.id.heading_myshelf);
        myshelfViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        titleText = root.findViewById(R.id.title_myshelf);
        //titleText.setText("12 Books in your Bookshelf");
        foundBooks = root.findViewById(R.id.found_books);
        foundBooks.setText("");

        btnAllBooks = (LinearLayout) root.findViewById(R.id.btn_all);
        btnFilter = (LinearLayout) root.findViewById(R.id.btn_filter);
        btnRefresh = (LinearLayout) root.findViewById(R.id.btn_sort);

        //Gridview for Myshelf books
        pbMyshelf = (ProgressBar) root.findViewById(R.id.pb_myshelf);
        pbMyshelf.setVisibility(View.VISIBLE);
        gridview_myshelf_books = (GridView) root.findViewById(R.id.gridview_myshelf_books);
        if(GlobalData.getInstance().shelfBooksListed) {
            showMyShelfBooks(GlobalData.getInstance().myShelfBooks);
            if(GlobalData.getInstance().myShelfBooks.size()>0) {
                titleText.setText(GlobalData.getInstance().myShelfBooks.size() + " Books in your Bookshelf");
            }else{
                titleText.setText("No books in your Bookshelf");
            }
        }else{
            loadMyShelfBooks();
        }

        btnAllBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbMyshelf.setVisibility(View.VISIBLE);
                showMyShelfBooks(GlobalData.getInstance().myShelfBooks);
                foundBooks.setText(GlobalData.getInstance().myShelfBooks.size()+"");
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileOptionMenu();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbMyshelf.setVisibility(View.VISIBLE);
                loadMyShelfBooks();
            }
        });

        fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), BookUploadActivity.class);
                i.putExtra("for", "add");
                startActivity(i);
            }
        });

        return root;
    }

    private int getBoottomNavHeight(View root){
        View view = getActivity().findViewById(R.id.bottom_nav_view);
        return view.getHeight();


//        Resources resources = getContext().getResources();
//        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            return resources.getDimensionPixelSize(resourceId);
//        }
//        return 0;
    }

    public void showProfileOptionMenu(){
        PopupMenu popupMenu = new PopupMenu(getContext(), btnFilter);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.my_all_books:
                        showMyShelfBooks(GlobalData.getInstance().myShelfBooks);
                        foundBooks.setText(GlobalData.getInstance().myShelfBooks.size()+"");
                        break;
                    case R.id.my_books_for_rent:
                        pbMyshelf.setVisibility(View.VISIBLE);
                        List<Book> listRent = new ArrayList<Book>();
                        for(Book b : GlobalData.getInstance().myShelfBooks){
                            if(b.purpose==2 && b.userBookStatus==1){
                                listRent.add(b);
                            }
                        }
                        foundBooks.setText(listRent.size()+"");
                        showMyShelfBooks(listRent);
                        break;
                    case R.id.my_books_for_sell:
                        pbMyshelf.setVisibility(View.VISIBLE);
                        List<Book> listSell = new ArrayList<Book>();
                        for(Book b : GlobalData.getInstance().myShelfBooks){
                            if(b.purpose==1 && b.userBookStatus==1){
                                listSell.add(b);
                            }
                        }
                        foundBooks.setText(listSell.size()+"");
                        showMyShelfBooks(listSell);
                        break;

                    case R.id.inactive_books:
                        pbMyshelf.setVisibility(View.VISIBLE);
                        List<Book> listInactive = new ArrayList<Book>();
                        for(Book b : GlobalData.getInstance().myShelfBooks){
                            if(b.userBookStatus == 0){
                                listInactive.add(b);
                            }
                        }
                        foundBooks.setText(listInactive.size()+"");
                        showMyShelfBooks(listInactive);
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    protected void loadMyShelfBooks(){
        GlobalData.getInstance().myShelfBooks.clear();
        apiName = "api_getData.php";
        String actionN = action+"getMyShelfBooks";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
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
                        b.idCategory = Integer.parseInt(object.getString("idCategory"));
                        b.idCondition = Integer.parseInt(object.getString("bookConditionId"));
                        b.authorName = object.getString("authorName");
                        b.idUser = Integer.parseInt(object.getString("idUser"));
                        b.ownerFullName = object.getString("userName");
                        b.purpose = Integer.parseInt(object.getString("purpose"));
                        b.userBookType = Integer.parseInt(object.getString("userBookType"));
                        b.userBookStatus = Integer.parseInt(object.getString("userBookStatus"));
                        b.idCondition = Integer.parseInt(object.getString("bookConditionId"));
                        if(b.idCondition==0 || b.idCondition==1){
                            b.conditionName = "New";
                        }else if(b.idCondition==2){
                            b.conditionName = "Used";
                        }

                        bookList.add(b);
                        //System.out.println("------------picture:"+b.picture+";");
                        GlobalData.getInstance().myShelfBooks.add(b);

                    } catch (JSONException e) {
                        Toast.makeText(getContext(),"Exception on loading shelf books",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                if(response.length()>0) {
                    GlobalData.getInstance().shelfBooksListed = true;
                    titleText.setText(GlobalData.getInstance().myShelfBooks.size() + " Books in your Bookshelf");
                } else{
                    titleText.setText("No books in your Bookshelf");
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Recent books size="+GlobalData.getInstance().recentBooks.size());

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);
                showMyShelfBooks(GlobalData.getInstance().myShelfBooks);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error loading shelf data",Toast.LENGTH_LONG).show();
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

    private void showMyShelfBooks(final List<Book> list){
        if(list.size()>0) {
            foundBooks.setText(list.size() + "");
        }else{
            foundBooks.setText("");
        }

        pbMyshelf.setVisibility(View.GONE);
        GridAdapterMyshelf adapterMyshelf = new GridAdapterMyshelf(list,getContext());
        gridview_myshelf_books.setAdapter(adapterMyshelf);
        gridview_myshelf_books.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(),"This is "+list.get(position).title,Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(getContext(), SingleBookActivity.class);
//                intent.putExtra("book_title", bookNames[position]);
//                intent.putExtra("book_id",position);

                Book book = list.get(position);
                book.idOwner = GlobalData.getInstance().userId;
                book.ownerUsername = GlobalData.getInstance().userName;
                book.ownerFirstName = GlobalData.getInstance().userFirstName;
                book.ownerLastName = GlobalData.getInstance().userLastName;
                //Toast.makeText(getApplicationContext(),"Position "+position,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), SingleBookActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });
    }
}
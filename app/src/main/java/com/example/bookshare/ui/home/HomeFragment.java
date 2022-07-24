package com.example.bookshare.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookshare.CategoryActivity;
import com.example.bookshare.MoreBooksActivity;
import com.example.bookshare.R;
import com.example.bookshare.SingleBookActivity;
import com.example.bookshare.adapter.CustomAdapter;
import com.example.bookshare.adapter.GridAdapterCat;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.Request;
import com.example.bookshare.model.Category;
import com.example.bookshare.model.GlobalData;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ViewFlipper v_flipper;
    private GridView gridView_categories, gridview_top10_per_week, gridview_top10_per_month, gridview_top10_per_year;
    private TextView viewAllRecent, viewAllSell, viewAllRent;
    private ArrayList<Integer> images = new ArrayList<Integer>();
    private ProgressBar pbCategories, pbRecentList, pbSellList, pbRentList;
    private LinearLayout homeBottomPart;
    private CustomAdapter.RecyclerViewClickListener rListener;
    private BadgeDrawable badgeDrawable;

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private ArrayList<Category> categoryList = new ArrayList<Category>();


    private RecyclerView listRecent,mList2, listSell, listRent;
    private List<Book> bookListRecent, bookListSell, bookListRent;

    private LinearLayoutManager managerRecent;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        v_flipper = root.findViewById(R.id.viewFlipper);

        images.add(R.drawable.slider1);
        images.add(R.drawable.slider2);
        images.add(R.drawable.slider3);
        //images.add(R.drawable.slider4);

        //set badge over cart menu in bottom
        BottomNavigationView BottomNavView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_nav_view);
        badgeDrawable = BottomNavView.getOrCreateBadge(R.id.navigation_requests);
        badgeDrawable.setBackgroundColor(getResources().getColor(R.color.holo_red_dark));
        badgeDrawable.setBadgeTextColor(Color.WHITE);
        badgeDrawable.setNumber(GlobalData.getInstance().cartValue);
        if(GlobalData.getInstance().cartValue==0){
            badgeDrawable.setVisible(false);
        }else if(GlobalData.getInstance().cartValue>0){
            badgeDrawable.setVisible(true);
        }


        for(Integer image:images){
            homeViewModel.flipperImages(image,v_flipper,getContext());
        }

//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                //textView.setText(s);
//            }
//        });

        //set dynamic margin on bottom
        homeBottomPart = (LinearLayout) root.findViewById(R.id.homeBottomPart);
        homeBottomPart.setMinimumHeight(getBoottomNavHeight(root));


        pbCategories = (ProgressBar) root.findViewById(R.id.pb_categories);
        pbRecentList = (ProgressBar) root.findViewById(R.id.pb_recent);
        pbSellList = (ProgressBar) root.findViewById(R.id.pb_sell);
        pbRentList = (ProgressBar) root.findViewById(R.id.pb_rent);

        //Gridview for book_icon Categories
        gridView_categories = (GridView) root.findViewById(R.id.gridview_categories);
        //get categories
        //loadCategoryData();
        showCategories();



        GlobalData.getInstance().totalCartPrice = 0;
        GlobalData.getInstance().tempTotalCartPrice = 0;
        for(Request c : GlobalData.getInstance().requestList){
            GlobalData.getInstance().totalCartPrice += c.totalPrice;
        }
        GlobalData.getInstance().tempTotalCartPrice = GlobalData.getInstance().totalCartPrice;

        bookListSell = new ArrayList<Book>();
        bookListRent = new ArrayList<Book>();
        for(Book b : GlobalData.getInstance().recentBooks){
            if(b.purpose == 1){
                bookListSell.add(b);
            }else if(b.purpose == 2){
                bookListRent.add(b);
            }
        }

        listRecent = root.findViewById(R.id.list_recent);
        listSell = root.findViewById(R.id.list_sell);
        listRent = root.findViewById(R.id.list_rent);

        //set recyclerview custom listener for recent books

        //Recent books list
        bookListRecent = new ArrayList<Book>();
        setRecentOnClickListener(GlobalData.getInstance().recentBooks);

        //All recent books
        managerRecent = new LinearLayoutManager(getContext());
        managerRecent.setOrientation(LinearLayoutManager.HORIZONTAL);
        listRecent.setLayoutManager(managerRecent);
        CustomAdapter adapterRecent = new CustomAdapter(getContext(),GlobalData.getInstance().recentBooks, rListener, badgeDrawable);
        listRecent.setAdapter(adapterRecent);
        pbRecentList.setVisibility(View.GONE);

        viewAllRecent = root.findViewById(R.id.viewAllRecent);
        viewAllRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MoreBooksActivity.class);
                intent.putExtra("tag", GlobalData.getInstance().TAG_RECENT_BOOKS);
                startActivity(intent);
            }
        });


        //Top selling book list
        if(GlobalData.getInstance().topSellingBooksListed){
            showTopSellingBookList(GlobalData.getInstance().topSellingBookList, root);
        }else{
            loadTopSellingBooks(root);
        }


        //Top rent book list
        if(GlobalData.getInstance().topRentedBooksListed){
            showTopRentedBookList(GlobalData.getInstance().topRentedBookList, root);
        }else{
            loadTopRentedBooks(root);
        }

        return root;
    }

    private void showCategories(){
        GridAdapterCat adapter_cat = new GridAdapterCat(getContext(), GlobalData.getInstance().categoryList);
        gridView_categories.setAdapter(adapter_cat);
        pbCategories.setVisibility(View.GONE);
        gridView_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(),"This is "+GlobalData.getInstance().categoryList.get(position).categoryName,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), CategoryActivity.class);
                intent.putExtra("title", GlobalData.getInstance().categoryList.get(position).categoryName);
                intent.putExtra("id", GlobalData.getInstance().categoryList.get(position).categoryId);
                startActivity(intent);
                getActivity().finish();
            }
        });
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

    protected void loadTopSellingBooks(View root){
        GlobalData.getInstance().topSellingBookList.clear();

        //bookList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getMoreBooks";
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
                        b.totalSell = Integer.parseInt(object.getString("total_sell"));
                        b.totalRent = 0;
                        GlobalData.getInstance().topSellingBookList.add(b);

                        for(Request c : GlobalData.getInstance().myRequestList){
                            if(b.id == c.bookId){
                                b.cartValue = c.quantity;
                            }

                            //System.out.println("Book id="+c.bookId+","+b.id+" ; quantity="+c.quantity+","+b.cartValue);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getContext(),"Exception on loading books",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Recent books size="+GlobalData.getInstance().recentBooks.size());

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);
                GlobalData.getInstance().topSellingBooksListed = true;
                showTopSellingBookList(GlobalData.getInstance().topSellingBookList, root);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error loading data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");
                params.put("startId",0+"");
                params.put("type",1+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }

    protected void loadTopRentedBooks(View root){
        GlobalData.getInstance().topRentedBookList.clear();

        //bookList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getMoreBooks";
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
                        b.totalSell = 0;
                        b.totalRent = Integer.parseInt(object.getString("total_rent"));
                        GlobalData.getInstance().topRentedBookList.add(b);

                        for(Request c : GlobalData.getInstance().myRequestList){
                            if(b.id == c.bookId){
                                b.cartValue = c.quantity;
                            }

                            //System.out.println("Book id="+c.bookId+","+b.id+" ; quantity="+c.quantity+","+b.cartValue);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getContext(),"Exception on loading books",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Recent books size="+GlobalData.getInstance().recentBooks.size());

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);
                GlobalData.getInstance().topRentedBooksListed = true;
                showTopRentedBookList(GlobalData.getInstance().topRentedBookList, root);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Error loading data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("userId",GlobalData.getInstance().userId+"");
                params.put("startId",0+"");
                params.put("type",2+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }

    protected void showTopSellingBookList(List<Book> topBookList, View root){
        //set recyclerview custom listener for recent books
        setRecentOnClickListener(topBookList);

        managerRecent = new LinearLayoutManager(getContext());
        managerRecent.setOrientation(LinearLayoutManager.HORIZONTAL);
        listSell.setLayoutManager(managerRecent);
        CustomAdapter adapterSell = new CustomAdapter(getContext(),topBookList, rListener, badgeDrawable);
        listSell.setAdapter(adapterSell);
        pbSellList.setVisibility(View.GONE);

        viewAllSell = root.findViewById(R.id.viewAllSell);
        viewAllSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MoreBooksActivity.class);
                intent.putExtra("tag", GlobalData.getInstance().TAG_SELL_BOOKS);
                startActivity(intent);
            }
        });
    }

    protected void showTopRentedBookList(List<Book> topBookList, View root){
        //set recyclerview custom listener for recent books
        setRecentOnClickListener(topBookList);

        managerRecent = new LinearLayoutManager(getContext());
        managerRecent.setOrientation(LinearLayoutManager.HORIZONTAL);
        listRent.setLayoutManager(managerRecent);
        CustomAdapter adapterRent = new CustomAdapter(getContext(),topBookList, rListener, badgeDrawable);
        listRent.setAdapter(adapterRent);
        pbRentList.setVisibility(View.GONE);

        viewAllRent = root.findViewById(R.id.viewAllRent);
        viewAllRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MoreBooksActivity.class);
                intent.putExtra("tag", GlobalData.getInstance().TAG_RENT_BOOKS);
                startActivity(intent);
            }
        });
    }

    private void setRecentOnClickListener(final List<Book> bookList) {
        rListener = new CustomAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View v, int position) {
                Toast.makeText(getContext(), bookList.get(position).getTitle(),Toast.LENGTH_SHORT).show();
                Book book = bookList.get(position);
                //Toast.makeText(getApplicationContext(),"Position "+position,Toast.LENGTH_SHORT).show();
                //System.out.println("firstname:"+book.getOwnerFirstName()+"; lastname:"+book.getOwnerLastName()+"; username:"+book.getOwnerUsername());
                Intent intent = new Intent(getContext(), SingleBookActivity.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        };
    }
}
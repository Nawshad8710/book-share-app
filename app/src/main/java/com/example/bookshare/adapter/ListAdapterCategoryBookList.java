package com.example.bookshare.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.R;
import com.example.bookshare.UserBooks;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Request;
import com.example.bookshare.model.User;
import com.google.android.material.badge.BadgeDrawable;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAdapterCategoryBookList extends BaseAdapter {

    private int bookImages[];
    private String[] bookTitles;
    private Context context;
    private LayoutInflater inflater;
    private List<Book> bookList;
    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private BadgeDrawable badgeDrawable;

    public ListAdapterCategoryBookList(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Book book = bookList.get(position);
        //For Top 10 books per week gridview
        View listViewCategoryBooks = convertView;
        if(convertView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listViewCategoryBooks = inflater.inflate(R.layout.row_item_category_book, null);
        }

        //set badge over cart menu in bottom
//        BottomNavigationView BottomNavView = (BottomNavigationView) listViewCategoryBooks.findViewById(R.id.bottom_nav_view);
//        badgeDrawable = BottomNavView.getOrCreateBadge(R.id.navigation_mycart);
//        badgeDrawable.setBackgroundColor(Color.RED);
//        badgeDrawable.setBadgeTextColor(Color.WHITE);
//        badgeDrawable.setNumber(GlobalData.getInstance().cartValue);
//        if(GlobalData.getInstance().cartValue==0){
//            badgeDrawable.setVisible(false);
//        }else if(GlobalData.getInstance().cartValue>0){
//            badgeDrawable.setVisible(true);
//        }

        ImageView bookImage = (ImageView) listViewCategoryBooks.findViewById(R.id.book_image);
        TextView bookTitle = (TextView) listViewCategoryBooks.findViewById(R.id.book_title);
        TextView bookPrice = (TextView) listViewCategoryBooks.findViewById(R.id.book_price);
        TextView bookOwner = (TextView) listViewCategoryBooks.findViewById(R.id.book_owner);
        final TextView btnAddToCart = (TextView) listViewCategoryBooks.findViewById(R.id.btnSendRequest);
        final TextView txtSentRequest = (TextView) listViewCategoryBooks.findViewById(R.id.requestSentText);
        TextView bookLabel = (TextView) listViewCategoryBooks.findViewById(R.id.user_book_label);
        TextView bookLabel2 = (TextView) listViewCategoryBooks.findViewById(R.id.user_book_label2);
        TextView totalOrdered = (TextView) listViewCategoryBooks.findViewById(R.id.totalOrdered);

        final LinearLayout addToCartSection = (LinearLayout) listViewCategoryBooks.findViewById(R.id.addToCart_section);
        final LinearLayout afterCartSection = (LinearLayout) listViewCategoryBooks.findViewById(R.id.afterCart_section);
        final LinearLayout totalSection = (LinearLayout) listViewCategoryBooks.findViewById(R.id.totalSection);
        final ProgressBar pbCart = (ProgressBar) listViewCategoryBooks.findViewById(R.id.pbRequest);

        bookTitle.setText(book.getTitle());
        bookOwner.setText("@"+book.ownerUsername);
        bookOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = new User(book.getIdOwner(), book.getOwnerUsername(), book.getOwnerFirstName(), book.getOwnerLastName());
                Intent i = new Intent(context, UserBooks.class);
                i.putExtra("user", u);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        if(book.purpose==1){
            totalOrdered.setText(book.totalSell+" sold");
        }else if(book.purpose==2){
            totalOrdered.setText(book.totalRent+" rented");
        }

        //set book picture
        if(book.picture.isEmpty() || book.picture.equals("") || book.picture.equals("null")){
            bookImage.setImageResource(R.drawable.single_book);
        }else{
            String url = GlobalData.getInstance().url+book.picture;
            //bookImage.setImageResource(LoadImageFromWebOperations(b.picture).picture);
            Picasso.get().load(url).into(bookImage);
        }

        if(book.purpose==1){
            bookPrice.setText(book.getPrice()+"");
            bookLabel.setText("For Sell");
            bookLabel.setBackgroundColor(context.getResources().getColor(R.color.holo_red_dark));
        }else if(book.purpose==2){
            bookPrice.setText(book.getPrice()+" / day");
            bookLabel.setText("For Rent");
            bookLabel.setBackgroundColor(Color.BLUE);
        }

        if(book.idCondition==1 || book.idCondition==0){
            bookLabel2.setText("New");
            bookLabel2.setBackgroundColor(context.getResources().getColor(R.color.green));
        }else if(book.idCondition==2){
            bookLabel2.setText("Used");
            bookLabel2.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }

        if(book.cartValue>0){
            btnAddToCart.setVisibility(View.GONE);
            txtSentRequest.setVisibility(View.VISIBLE);
        }else{
            btnAddToCart.setVisibility(View.VISIBLE);
            txtSentRequest.setVisibility(View.GONE);
        }

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbCart.setVisibility(View.VISIBLE);
                btnAddToCart.setVisibility(View.GONE);

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
                            pbCart.setVisibility(View.GONE);
                            Toast.makeText(context,"Request sent successfully!",Toast.LENGTH_SHORT).show();
                            btnAddToCart.setVisibility(View.GONE);
                            //holder.afterCart_section.setVisibility(View.VISIBLE);
                            txtSentRequest.setVisibility(View.VISIBLE);
                            GlobalData.getInstance().lastCartId = Integer.parseInt(s[1]);
                            book.cartValue=book.minimumQuantity;
                            for(int i=0; i<GlobalData.getInstance().recentBooks.size();i++){
                                if(book.getId()==GlobalData.getInstance().recentBooks.get(i).getId()){
                                    GlobalData.getInstance().recentBooks.get(i).cartValue = book.minimumQuantity;
                                }
                            }

                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                            Date date = new Date();

                            String nowDate = formatter.format(date);

                            GlobalData.getInstance().myRequestList.add(new Request(GlobalData.getInstance().lastCartId, book.id, book.title, book.price, book.picture, GlobalData.getInstance().userId, book.idOwner, book.cartValue, book.getTotalPriceForCart(), book.purpose, 0, nowDate, GlobalData.getInstance().userName, book.ownerUsername,  GlobalData.getInstance().userFullName, book.ownerFullName, book.idCondition));
                        } else if(response.toString().equals("tokenFail")){
                            pbCart.setVisibility(View.GONE);
                            btnAddToCart.setVisibility(View.VISIBLE);
                            Toast.makeText(context,"Wrong token, try again",Toast.LENGTH_SHORT).show();
                        } else{
                            pbCart.setVisibility(View.GONE);
                            btnAddToCart.setVisibility(View.VISIBLE);
                            Toast.makeText(context,"Failed to send request!",Toast.LENGTH_SHORT).show();
                            //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);
                            //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pbCart.setVisibility(View.GONE);
                        btnAddToCart.setVisibility(View.VISIBLE);
                        Toast.makeText(context,"Error occurred!",Toast.LENGTH_SHORT).show();
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
                        params.put("quantity",book.getMinimumQuantity()+"");
                        params.put("totalPrice",book.getTotalPriceForCart()+"");
                        params.put("purpose",book.getPurpose()+"");

                        return params;
                    }
                };


                //System.out.println("edUsername= "+GlobalData.getInstance().username);
                //System.out.println(GlobalData.getInstance());
                GlobalData.getInstance().addToRequestQueue(request);
            }
        });

        return listViewCategoryBooks;
    }
}

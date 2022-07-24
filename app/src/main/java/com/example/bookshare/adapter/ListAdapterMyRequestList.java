package com.example.bookshare.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.R;
import com.example.bookshare.SingleBookActivity;
import com.example.bookshare.UserBooks;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Request;
import com.example.bookshare.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAdapterMyRequestList extends BaseAdapter {

    private double cartListTotalPrice=0.00;
    private int bookImages[];
    private String[] bookTitles;
    private Context context;
    private LayoutInflater inflater;
    private List<Request> requestList = new ArrayList<Request>();
    private ListView mrList;
    AlertDialog.Builder builder;

    LinearLayout btnCancel, btnEnd;

    private Double tempTotal = 0.00;

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";


    public ListAdapterMyRequestList(List<Request> requestList, Context context, ListView mrList, AlertDialog.Builder builder) {
        this.requestList = requestList;
        this.context = context;
        this.mrList = mrList;
        this.builder = builder;
    }

    @Override
    public int getCount() {
        return requestList.size();
    }

    @Override
    public Object getItem(int position) {
        return requestList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Request r = requestList.get(position);
        tempTotal += r.totalPrice;
        //For Top 10 books per week gridview
        View listViewCartBooks = convertView;
        if(convertView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listViewCartBooks = inflater.inflate(R.layout.row_item_my_request_book, null);
        }

        //set badge over r menu in bottom
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

        ImageView bookImage = (ImageView) listViewCartBooks.findViewById(R.id.book_image);
        TextView bookTitle = (TextView) listViewCartBooks.findViewById(R.id.book_title);
        TextView bookPrice = (TextView) listViewCartBooks.findViewById(R.id.book_price);
        TextView bookLabel = (TextView) listViewCartBooks.findViewById(R.id.request_book_label);
        TextView bookLabel2 = (TextView) listViewCartBooks.findViewById(R.id.request_book_label2);
        final TextView bookOwner = (TextView) listViewCartBooks.findViewById(R.id.book_owner);
        final LinearLayout afterCartSection = (LinearLayout) listViewCartBooks.findViewById(R.id.afterCart_section);
        final ProgressBar pbCart = (ProgressBar) listViewCartBooks.findViewById(R.id.pbRequest);
        //final TextView mCartQuantity = (TextView) listViewCartBooks.findViewById(R.id.txtCartQuantity);
        btnCancel = (LinearLayout) listViewCartBooks.findViewById(R.id.btn_cancel);
        final ProgressBar pbRequest = (ProgressBar) listViewCartBooks.findViewById(R.id.pb_request);

        LinearLayout afterRentSection = (LinearLayout)  listViewCartBooks.findViewById(R.id.after_rent);
        LinearLayout beforeRentSection = (LinearLayout)  listViewCartBooks.findViewById(R.id.before_rent);
        btnEnd = (LinearLayout) listViewCartBooks.findViewById(R.id.btn_end);
        TextView rentStartDate = (TextView) listViewCartBooks.findViewById(R.id.rent_start_date);

        //final AlertDialog.Builder builder =  new AlertDialog.Builder(context);

        pbRequest.setVisibility(View.GONE);

        bookTitle.setText(r.getBookTitle());
        //bookPrice.setText(r.getBookPrice()+"");
        bookOwner.setText("@"+r.getOwnerName());
        rentStartDate.setText(r.dateAdded);
        //mCartQuantity.setText(r.getQuantity()+"");

        bookOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = new User(r.getOwnerId(), r.getOwnerName(), r.getOwnerFirstName(), r.getOwnerLastName());
                Intent i = new Intent(context, UserBooks.class);
                i.putExtra("user", u);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        //set book picture
        if(r.bookPicture.isEmpty() || r.bookPicture.equals("") || r.bookPicture.equals("null")){
            bookImage.setImageResource(R.drawable.single_book);
        }else{
            String url = GlobalData.getInstance().url+r.bookPicture;
            //bookImage.setImageResource(LoadImageFromWebOperations(b.picture).picture);
            Picasso.get().load(url).into(bookImage);
        }

        if(r.status==1){
            afterRentSection.setVisibility(View.VISIBLE);
            beforeRentSection.setVisibility(View.GONE);

            bookLabel.setText("Rent Running");
            bookLabel.setBackgroundColor(Color.MAGENTA);
            bookPrice.setText(r.getBookPrice()+" / day");
        }else if(r.status==0){
            afterRentSection.setVisibility(View.GONE);
            beforeRentSection.setVisibility(View.VISIBLE);

            if(r.purpose==1){
                bookLabel.setText("For Sell");
                bookLabel.setBackgroundColor(context.getResources().getColor(R.color.holo_red_dark));
                bookPrice.setText(r.getBookPrice()+"");
            }else if(r.purpose==2){
                bookLabel.setText("For Rent");
                bookLabel.setBackgroundColor(Color.BLUE);
                bookPrice.setText(r.getBookPrice()+" / day");
            }
        }

        if(r.bookConditionId==1 || r.bookConditionId==0){
            bookLabel2.setText("New");
            bookLabel2.setBackgroundColor(context.getResources().getColor(R.color.green));
        }else if(r.bookConditionId==2){
            bookLabel2.setText("Used");
            bookLabel2.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //book_icon.cartValue++;
                //holder.pbCart.setVisibility(View.VISIBLE);
                //increase r value in database table process start
                //init alert dialog

                pbRequest.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);

                apiName = "api_deleteData.php";
                String actionN = action+"deleteCartItem";
                String urlN = url+apiName+actionN;

                //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);

                StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.toString().equals("yes")){
                            GlobalData.getInstance().myRequestList.remove(position);
                            //requestList.remove(position);
                            //cartList.remove(position);

                            for(Book b : GlobalData.getInstance().recentBooks){
                                if(b.id == r.bookId){
                                    b.cartValue = 0;
                                }
                            }

                            ListAdapterMyRequestList adapter = new ListAdapterMyRequestList(GlobalData.getInstance().myRequestList,context, mrList, builder);
                            mrList.setAdapter(adapter);
                            mrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Request request = requestList.get(position);
                                    Toast.makeText(context,"Title: "+ requestList.get(position).getBookTitle(),Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, SingleBookActivity.class);
                                    intent.putExtra("book_title", request.getBookTitle());
                                    intent.putExtra("book_id", request.getBookId());
                                    //pbCartItemRemove.setVisibility(View.GONE);
                                    context.startActivity(intent);
                                }
                            });
                            //Toast.makeText(context,"Book-"+book_icon.id+" r quantity "+book_icon.cartValue,Toast.LENGTH_SHORT).show();
                        } else if(response.toString().equals("tokenFail")){
                            Toast.makeText(context,"Wrong token, try again",Toast.LENGTH_SHORT).show();
                            pbRequest.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.VISIBLE);
                        } else{
                            Toast.makeText(context,"Failed to delete cancel request!",Toast.LENGTH_SHORT).show();
                            pbRequest.setVisibility(View.GONE);
                            btnCancel.setVisibility(View.VISIBLE);
                            //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                            //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);
                            //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,"Error occurred!",Toast.LENGTH_SHORT).show();
                        pbRequest.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.VISIBLE);
                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("myToken", "786");
                        params.put("cartId",r.getCartId()+"");

                        return params;
                    }
                };

                GlobalData.getInstance().addToRequestQueue(request);
            }
        });


        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(r.purpose==2){
                    pbRequest.setVisibility(View.VISIBLE);
                    btnEnd.setVisibility(View.GONE);
                    //book_icon.cartValue++;
                    //holder.pbCart.setVisibility(View.VISIBLE);
                    //increase r value in database table process start
                    apiName = "api_placeOrder.php";
                    String actionN = action+"endRent";
                    String urlN = url+apiName+actionN;

                    //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);

                    StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String [] s= response.split("/");
                            if(s[0].equals("yes")){
                                GlobalData.getInstance().myRequestList.remove(position);
                                pbRequest.setVisibility(View.GONE);
                                //requestList.remove(position);
                                //cartList.remove(position);

                                for(Book b : GlobalData.getInstance().recentBooks){
                                    if(b.id == r.bookId){
                                        b.cartValue = 0;
                                    }
                                }

                                ListAdapterMyRequestList adapter = new ListAdapterMyRequestList(GlobalData.getInstance().myRequestList,context, mrList, builder);
                                mrList.setAdapter(adapter);
                                mrList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Request request = requestList.get(position);
                                        Toast.makeText(context,"Title: "+ requestList.get(position).getBookTitle(),Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, SingleBookActivity.class);
                                        intent.putExtra("book_title", request.getBookTitle());
                                        intent.putExtra("book_id", request.getBookId());
                                        //pbCartItemRemove.setVisibility(View.GONE);
                                        context.startActivity(intent);
                                    }
                                });
                                //Toast.makeText(context,"Book-"+book_icon.id+" r quantity "+book_icon.cartValue,Toast.LENGTH_SHORT).show();
                            } else if(response.toString().equals("tokenFail")){
                                Toast.makeText(context,"Wrong token, try again",Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(context,"Error occurred",Toast.LENGTH_SHORT).show();
                                //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                                //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);
                                //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,"Error occurred!",Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("myToken", "786");
                            params.put("userId",GlobalData.getInstance().userId+"");
                            params.put("ownerId",r.getOrderId()+"");
                            params.put("bookId",r.getBookId()+"");
                            params.put("orderId",r.getOrderId()+"");
                            params.put("bookPrice",r.getBookPrice()+"");
                            params.put("startDate",r.getDateAdded()+"");

                            return params;
                        }
                    };

                    GlobalData.getInstance().addToRequestQueue(request);
                }

            }
        });



        //gridImageTop10PerWeek.setImageResource(R.drawable.ic_list_black_48dp);
        //gridTextTop10PerWeek.setText(bookTitles[position]);

        return listViewCartBooks;
    }

    private void showAlertDialog() {

    }
}

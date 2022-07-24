package com.example.bookshare.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.R;
import com.example.bookshare.SingleBookActivity;
import com.example.bookshare.UserBooks;
import com.example.bookshare.model.Order;
import com.example.bookshare.model.Request;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.User;
import com.google.android.material.badge.BadgeDrawable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAdapterRequestBookList extends BaseAdapter {

    private final TextView tvTotalCartPrice;
    private double cartListTotalPrice=0.00;
    private BadgeDrawable badgeDrawable;
    private int bookImages[];
    private String[] bookTitles;
    private Context context;
    private LayoutInflater inflater;
    private List<Request> requestList = new ArrayList<Request>();
    private ListView requestBookList;

    private Double tempTotal = 0.00;

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private static AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btnMsg;
    private EditText etMessage;
    private int requesterId=0;
    private String requesterUsername="", requesterFullName="";
    private ProgressBar pbMsg;

    public ListAdapterRequestBookList(List<Request> requestList, Context context, ListView requestBookList, BadgeDrawable badgeDrawable, TextView tvTotalCartPrice, double cartListTotalPrice) {
        this.requestList = requestList;
        this.context = context;
        this.requestBookList = requestBookList;
        this.badgeDrawable = badgeDrawable;
        this.tvTotalCartPrice = tvTotalCartPrice;
        this.cartListTotalPrice = cartListTotalPrice;
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
        requesterId = r.getRequesterId();
        requesterUsername = r.getRequesterName();
        requesterFullName = r.getRequesterFullName();
        tempTotal += r.totalPrice;
        tvTotalCartPrice.setText(GlobalData.getInstance().tempTotalCartPrice+"");
        //For Top 10 books per week gridview
        View listViewCartBooks = convertView;
        if(convertView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listViewCartBooks = inflater.inflate(R.layout.row_item_request_book, null);
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
        final TextView bookLabel = (TextView) listViewCartBooks.findViewById(R.id.request_book_label);
        final TextView bookLabel2 = (TextView) listViewCartBooks.findViewById(R.id.request_book_label2);
        final TextView bookRequester = (TextView) listViewCartBooks.findViewById(R.id.book_requester);
        final LinearLayout afterCartSection = (LinearLayout) listViewCartBooks.findViewById(R.id.afterCart_section);
        final ProgressBar pbCart = (ProgressBar) listViewCartBooks.findViewById(R.id.pbRequest);
        //final TextView mCartQuantity = (TextView) listViewCartBooks.findViewById(R.id.txtCartQuantity);
        final LinearLayout btnSendMessage = (LinearLayout) listViewCartBooks.findViewById(R.id.btn_send_message);
        final LinearLayout btnAccept = (LinearLayout) listViewCartBooks.findViewById(R.id.btn_accept);
        LinearLayout btnReject = (LinearLayout) listViewCartBooks.findViewById(R.id.btn_reject);
        final ProgressBar pbRequest = (ProgressBar) listViewCartBooks.findViewById(R.id.pb_request);

        final LinearLayout afterRentSection = (LinearLayout)  listViewCartBooks.findViewById(R.id.after_rent);
        final LinearLayout beforeRentSection = (LinearLayout)  listViewCartBooks.findViewById(R.id.before_rent);
        final LinearLayout btnEnd = (LinearLayout) listViewCartBooks.findViewById(R.id.btn_end);
        TextView rentStartDate = (TextView) listViewCartBooks.findViewById(R.id.rent_start_date);

        pbRequest.setVisibility(View.GONE);

        bookTitle.setText(r.getBookTitle());
        //bookPrice.setText(r.getBookPrice()+"");
        bookRequester.setText("@"+r.getRequesterName());
        rentStartDate.setText(r.getDateAdded());
        //mCartQuantity.setText(r.getQuantity()+"");
        //System.out.println("cartId:"+r.getCartId()+"; orderId:"+r.getOrderId());

        bookRequester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = new User(r.getRequesterId(), r.getRequesterName(), r.getRequesterFirstName(), r.getRequesterLastName());
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

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessageDialog(r);
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //book_icon.cartValue++;
                //holder.pbCart.setVisibility(View.VISIBLE);
                //increase r value in database table process start
                pbRequest.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.GONE);

                apiName = "api_deleteData.php";
                String actionN = action+"deleteCartItem";
                String urlN = url+apiName+actionN;

                //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);

                StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.toString().equals("yes")){
                            GlobalData.getInstance().requestList.remove(position);
                            GlobalData.getInstance().cartValue--;
                            badgeDrawable.setNumber(GlobalData.getInstance().cartValue);


                            ListAdapterRequestBookList adapter = new ListAdapterRequestBookList(GlobalData.getInstance().requestList,context, requestBookList,badgeDrawable, tvTotalCartPrice,cartListTotalPrice);
                            requestBookList.setAdapter(adapter);
                            requestBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                            Toast.makeText(context,"Wrong token, try again!",Toast.LENGTH_SHORT).show();
                            pbRequest.setVisibility(View.GONE);
                            btnReject.setVisibility(View.VISIBLE);
                        } else{
                            Toast.makeText(context,"Failed to reject request!",Toast.LENGTH_SHORT).show();
                            pbRequest.setVisibility(View.GONE);
                            btnReject.setVisibility(View.VISIBLE);
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
                        btnReject.setVisibility(View.VISIBLE);
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


        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbRequest.setVisibility(View.VISIBLE);
                btnAccept.setVisibility(View.GONE);
                if(r.purpose==1){
                    //book_icon.cartValue++;
                    //holder.pbCart.setVisibility(View.VISIBLE);
                    //increase r value in database table process start
                    apiName = "api_placeOrder.php";
                    String actionN = action+"bookSell";
                    String urlN = url+apiName+actionN;

                    //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);

                    StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String [] s= response.split("-");
                            if(s[0].equals("yes")){
                                GlobalData.getInstance().requestList.remove(position);
                                GlobalData.getInstance().cartValue--;

                                badgeDrawable.setNumber(GlobalData.getInstance().cartValue);
                                //cartList.remove(position);



                                ListAdapterRequestBookList adapter = new ListAdapterRequestBookList(GlobalData.getInstance().requestList,context, requestBookList,badgeDrawable, tvTotalCartPrice,cartListTotalPrice);
                                requestBookList.setAdapter(adapter);
                                requestBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                pbRequest.setVisibility(View.GONE);

                                GlobalData.getInstance().totalEarning += r.getTotalPrice();
                                GlobalData.getInstance().orderList.add(new Order(Integer.parseInt(s[1]),r.getRequesterId(), GlobalData.getInstance().userId, r.getBookId(), r.getPurpose(), 0, r.getTotalPrice(), r.getDateAdded(), r.getDateAdded(), 3, r.getBookTitle(), r.getRequesterName(), r.getRequesterName(), r.getBookPicture()));
                                //Toast.makeText(context,"Book-"+book_icon.id+" r quantity "+book_icon.cartValue,Toast.LENGTH_SHORT).show();
                            } else if(response.toString().equals("tokenFail")){
                                Toast.makeText(context,"Wrong token, try again!",Toast.LENGTH_SHORT).show();
                                pbRequest.setVisibility(View.GONE);
                                btnAccept.setVisibility(View.VISIBLE);
                            } else{
                                Toast.makeText(context,"Failed to accept request!",Toast.LENGTH_SHORT).show();
                                pbRequest.setVisibility(View.GONE);
                                btnAccept.setVisibility(View.VISIBLE);
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
                            btnAccept.setVisibility(View.VISIBLE);
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("myToken", "786");
                            params.put("userId",r.getRequesterId()+"");
                            params.put("ownerId",GlobalData.getInstance().userId+"");
                            params.put("bookId",r.getBookId()+"");
                            params.put("purpose",r.getPurpose()+"");
                            params.put("totalPrice",r.getTotalPrice()+"");

                            return params;
                        }
                    };

                    GlobalData.getInstance().addToRequestQueue(request);

                }else if(r.purpose==2){
                    //book_icon.cartValue++;
                    //holder.pbCart.setVisibility(View.VISIBLE);
                    //increase r value in database table process start
                    apiName = "api_placeOrder.php";
                    String actionN = action+"bookRent";
                    String urlN = url+apiName+actionN;

                    //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);

                    StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            String [] s= response.split("-");
                            if(s[0].equals("yes")){

                                //Toast.makeText(context,"order Id:"+s[1],Toast.LENGTH_SHORT).show();

                                for(Request rr : GlobalData.getInstance().requestList){
                                    if(rr.cartId == r.cartId){
                                        rr.status = 1;
                                        rr.orderId = Integer.parseInt(s[1]);
                                    }
                                }

                                afterRentSection.setVisibility(View.VISIBLE);
                                beforeRentSection.setVisibility(View.GONE);

                                bookLabel.setText("Rent Running");
                                bookLabel.setBackgroundColor(Color.MAGENTA);

                                ListAdapterRequestBookList adapter = new ListAdapterRequestBookList(GlobalData.getInstance().requestList,context, requestBookList,badgeDrawable, tvTotalCartPrice,cartListTotalPrice);
                                requestBookList.setAdapter(adapter);
                                requestBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                pbRequest.setVisibility(View.GONE);
                                //Toast.makeText(context,"Book-"+book_icon.id+" r quantity "+book_icon.cartValue,Toast.LENGTH_SHORT).show();
                            } else if(response.toString().equals("tokenFail")){
                                Toast.makeText(context,"Wrong token, try again!",Toast.LENGTH_SHORT).show();
                                pbRequest.setVisibility(View.GONE);
                                btnAccept.setVisibility(View.VISIBLE);
                            } else{
                                Toast.makeText(context,"Failed to accept request!",Toast.LENGTH_SHORT).show();
                                pbRequest.setVisibility(View.GONE);
                                btnAccept.setVisibility(View.VISIBLE);
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
                            btnAccept.setVisibility(View.VISIBLE);
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("myToken", "786");
                            params.put("userId",r.getRequesterId()+"");
                            params.put("ownerId",GlobalData.getInstance().userId+"");
                            params.put("bookId",r.getBookId()+"");
                            params.put("purpose",r.getPurpose()+"");
                            params.put("totalPrice",r.getTotalPrice()+"");

                            return params;
                        }
                    };

                    GlobalData.getInstance().addToRequestQueue(request);

                }

            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbRequest.setVisibility(View.VISIBLE);
                btnEnd.setVisibility(View.GONE);

                if(r.purpose==2){
                    Toast.makeText(context,"order Id:"+r.getOrderId(),Toast.LENGTH_SHORT).show();
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
                                GlobalData.getInstance().requestList.remove(position);
                                //requestList.remove(position);
                                //cartList.remove(position);

                                ListAdapterRequestBookList adapter = new ListAdapterRequestBookList(GlobalData.getInstance().requestList,context, requestBookList,badgeDrawable, tvTotalCartPrice,cartListTotalPrice);
                                requestBookList.setAdapter(adapter);
                                requestBookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                                pbRequest.setVisibility(View.GONE);
                                GlobalData.getInstance().totalEarning += Double.parseDouble(s[1]);
                                GlobalData.getInstance().orderList.add(new Order(r.getOrderId(),r.getRequesterId(), GlobalData.getInstance().userId, r.getBookId(), r.getPurpose(), Integer.parseInt(s[2]), Double.parseDouble(s[1]), r.getDateAdded(), s[3], 3, r.getBookTitle(), r.getRequesterName(), r.getRequesterName(), r.getBookPicture()));
                                //Toast.makeText(context,"Book-"+book_icon.id+" r quantity "+book_icon.cartValue,Toast.LENGTH_SHORT).show();
                            } else if(response.toString().equals("tokenFail")){
                                Toast.makeText(context,"Wrong token, try again!",Toast.LENGTH_SHORT).show();
                                pbRequest.setVisibility(View.GONE);
                                btnEnd.setVisibility(View.VISIBLE);
                            } else{
                                Toast.makeText(context,"Failed to end rent!",Toast.LENGTH_SHORT).show();
                                pbRequest.setVisibility(View.GONE);
                                btnEnd.setVisibility(View.VISIBLE);
                                //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                                //System.out.println(response);
                                //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,"Error occurred!",Toast.LENGTH_SHORT).show();
                            pbRequest.setVisibility(View.GONE);
                            btnEnd.setVisibility(View.VISIBLE);
                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("myToken","786");
                            params.put("userId",r.getRequesterId()+"");
                            params.put("ownerId",GlobalData.getInstance().userId+"");
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

    public void createMessageDialog(final Request req){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        dialogBuilder = new AlertDialog.Builder(context);
        final View messagePopupView = inflater.inflate(R.layout.send_message_popup, null);
        dialogBuilder.setView(messagePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        pbMsg = (ProgressBar) messagePopupView.findViewById(R.id.pbMsg);
        pbMsg.setVisibility(View.GONE);

        final LinearLayout msg_body_section = (LinearLayout) messagePopupView.findViewById(R.id.msg_body_section);
        msg_body_section.setVisibility(View.VISIBLE);

        etMessage = (EditText) messagePopupView.findViewById(R.id.etMessage);
        btnMsg = (Button) messagePopupView.findViewById(R.id.btnMsg);
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setCancelable(false);
                pbMsg.setVisibility(View.VISIBLE);
                msg_body_section.setVisibility(View.GONE);
                //increase r value in database table process start
                apiName = "api_insertData.php";
                String actionN = action+"sendMessage";
                String urlN = url+apiName+actionN;

                //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);

                StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("yes")){
                            Toast.makeText(context,"Message sent successfully!",Toast.LENGTH_SHORT).show();
                            pbMsg.setVisibility(View.GONE);
                            dialog.dismiss();
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
                        params.put("fromId",GlobalData.getInstance().userId+"");
                        params.put("fromUsername",GlobalData.getInstance().userName);
                        if(GlobalData.getInstance().userFirstName.equals("@") || GlobalData.getInstance().userLastName.equals("@")){
                            params.put("fromFullName",GlobalData.getInstance().userName);
                        }else {
                            params.put("fromFullName",GlobalData.getInstance().userFullName);
                        }
                        params.put("toId",req.getRequesterId()+"");
                        params.put("toUsername",req.getRequesterName());
                        if(req.getRequesterFullName().equals("") || req.getRequesterFullName().substring(0,1).equals("@")){
                            params.put("toFullName",req.getRequesterName());
                        }else {
                            params.put("toFullName",req.getRequesterFullName());
                        }
                        params.put("message",etMessage.getText().toString());

                        return params;
                    }
                };

                GlobalData.getInstance().addToRequestQueue(request);
            }
        });
    }


}

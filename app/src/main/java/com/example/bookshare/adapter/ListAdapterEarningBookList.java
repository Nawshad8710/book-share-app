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

import com.example.bookshare.R;
import com.example.bookshare.UserBooks;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Order;
import com.example.bookshare.model.User;
import com.google.android.material.badge.BadgeDrawable;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListAdapterEarningBookList extends BaseAdapter {

    private int bookImages[];
    private String[] bookTitles;
    private Context context;
    private LayoutInflater inflater;
    private List<Order> orderList;
    private TextView listTxtSentRequest;

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private BadgeDrawable badgeDrawable;

    public ListAdapterEarningBookList(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Order order = orderList.get(position);
        //For Top 10 books per week gridview
        View listViewOrderedBooks = convertView;
        if(convertView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listViewOrderedBooks = inflater.inflate(R.layout.row_item_order_book, null);
        }

        //set badge over cart menu in bottom
//        BottomNavigationView BottomNavView = (BottomNavigationView) listViewOrderedBooks.findViewById(R.id.bottom_nav_view);
//        badgeDrawable = BottomNavView.getOrCreateBadge(R.id.navigation_mycart);
//        badgeDrawable.setBackgroundColor(Color.RED);
//        badgeDrawable.setBadgeTextColor(Color.WHITE);
//        badgeDrawable.setNumber(GlobalData.getInstance().cartValue);
//        if(GlobalData.getInstance().cartValue==0){
//            badgeDrawable.setVisible(false);
//        }else if(GlobalData.getInstance().cartValue>0){
//            badgeDrawable.setVisible(true);
//        }

        ImageView bookImage = (ImageView) listViewOrderedBooks.findViewById(R.id.book_image);
        TextView bookTitle = (TextView) listViewOrderedBooks.findViewById(R.id.book_title);
        TextView totalBill = (TextView) listViewOrderedBooks.findViewById(R.id.total_bill);
        TextView startDate = (TextView) listViewOrderedBooks.findViewById(R.id.start_date);
        TextView endDate = (TextView) listViewOrderedBooks.findViewById(R.id.end_date);
        TextView userName = (TextView) listViewOrderedBooks.findViewById(R.id.user_name);
        final TextView bookLabel = (TextView) listViewOrderedBooks.findViewById(R.id.order_book_label);

        final LinearLayout rentDateSection = (LinearLayout) listViewOrderedBooks.findViewById(R.id.rentDateSection);
        rentDateSection.setVisibility(View.GONE);

        bookTitle.setText(order.getBookTitle());
        totalBill.setText(order.getTotalBill()+"");
        startDate.setText(order.getOrderStartDate());
        userName.setText("@"+order.getUserName());

        //set book picture
        if(order.bookPicture.isEmpty() || order.bookPicture.equals("") || order.bookPicture.equals("null")){
            bookImage.setImageResource(R.drawable.single_book);
        }else{
            String url = GlobalData.getInstance().url+order.bookPicture;
            //bookImage.setImageResource(LoadImageFromWebOperations(b.picture).picture);
            Picasso.get().load(url).into(bookImage);
        }

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = new User(order.getUserId(), order.getUserName(), order.getUserFirstName(), order.getUserLastName());
                Intent i = new Intent(context, UserBooks.class);
                i.putExtra("user", u);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        });

        if(order.getOrderType()==2){
            rentDateSection.setVisibility(View.VISIBLE);
            endDate.setText(order.getOrderEndDate());
            bookLabel.setText("Rented");
            bookLabel.setBackgroundColor(Color.BLUE);
        }else if(order.getOrderType()==1){
            rentDateSection.setVisibility(View.GONE);
            bookLabel.setText("Sold");
            bookLabel.setBackgroundColor(context.getResources().getColor(R.color.holo_red_dark));
        }



        return listViewOrderedBooks;
    }
}

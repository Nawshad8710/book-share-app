package com.example.bookshare.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bookshare.BookUploadActivity;
import com.example.bookshare.R;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.GlobalData;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class GridAdapterMyshelf extends BaseAdapter {

    private int[] bookImages;
    private String[] bookNames;
    private Context context;
    private LayoutInflater inflater;
    private List<Book> bookList;
    private ImageView bookImage;
    private TextView bookTitle, bookPrice, bookLabel, bookLabel2;
    private LinearLayout btnEdit;

    public GridAdapterMyshelf(String[] bookNames, int[] bookImages, Context context) {
        this.bookNames = bookNames;
        this.bookImages = bookImages;
        this.context = context;
    }

    public GridAdapterMyshelf(List<Book> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public Object getItem(int position) {
        return bookList.get(position).getTitle();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Book b = bookList.get(position);

        //For Top 10 books per week gridview
        View gridview_myshelf_books = convertView;
        if(convertView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridview_myshelf_books = inflater.inflate(R.layout.row_item_book_shelf, null);
        }

        bookImage = (ImageView) gridview_myshelf_books.findViewById(R.id.grid_image);
        bookTitle = (TextView) gridview_myshelf_books.findViewById(R.id.grid_bookName);
        bookPrice = (TextView) gridview_myshelf_books.findViewById(R.id.grid_bookPrice);
        bookLabel = (TextView) gridview_myshelf_books.findViewById(R.id.grid_label);
        bookLabel2 = (TextView) gridview_myshelf_books.findViewById(R.id.grid_label2);
        btnEdit = (LinearLayout) gridview_myshelf_books.findViewById(R.id.btn_edit);

        //gridImageMyshelfBook.setImageResource(bookImages[position]);
        if(b.picture.isEmpty() || b.picture.equals("") || b.picture.equals("null")){
            bookImage.setImageResource(R.drawable.single_book);
        }else{
            String url = GlobalData.getInstance().url+b.picture;
            //bookImage.setImageResource(LoadImageFromWebOperations(b.picture).picture);
            Picasso.get().load(url).into(bookImage);
        }
        bookTitle.setText(b.title);
        bookPrice.setText("৳ "+b.price);

        if (b.userBookStatus == 0){
            bookLabel.setText("Inactive");
            bookLabel.setBackgroundColor(Color.DKGRAY);
        }else if (b.userBookStatus == 1){
            if(b.purpose==1){
                bookLabel.setText("For Sell");
                bookLabel.setBackgroundColor(context.getResources().getColor(R.color.holo_red_dark));
            }else if(b.purpose==2){
                bookLabel.setText("For Rent");
                bookLabel.setBackgroundColor(Color.BLUE);
                bookPrice.setText("৳ "+b.price+" / day");
            }
        }

        if(b.idCondition==1 || b.idCondition==0){
            bookLabel2.setText("New");
            bookLabel2.setBackgroundColor(context.getResources().getColor(R.color.green));
        }else if(b.idCondition==2){
            bookLabel2.setText("Used");
            bookLabel2.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BookUploadActivity.class);
                i.putExtra("for", "edit");
                i.putExtra("book", b);
                context.startActivity(i);
            }
        });

        return gridview_myshelf_books;
    }

    public static Drawable LoadImageFromWebOperations(String pictureUrl) {
        try {
            String url = GlobalData.getInstance().url+pictureUrl;
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, pictureUrl);
            return d;
        } catch (Exception e) {
            return null;
        }
    }
}

package com.example.bookshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookshare.R;

public class GridAdapterPerWeek extends BaseAdapter {

    private int[] bookImages;
    private String[] bookNames;
    private Context context;
    private LayoutInflater inflater;

    public GridAdapterPerWeek(String[] bookNames, int[] bookImages, Context context) {
        this.bookNames = bookNames;
        this.bookImages = bookImages;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return bookNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //For Top 10 books per week gridview
        View gridview_top10_per_week = convertView;
        if(convertView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridview_top10_per_week = inflater.inflate(R.layout.row_item_book, null);
        }

        ImageView gridImageTop10PerWeek = (ImageView) gridview_top10_per_week.findViewById(R.id.grid_image);
        TextView gridTextTop10PerWeek = (TextView) gridview_top10_per_week.findViewById(R.id.grid_bookName);

        gridImageTop10PerWeek.setImageResource(bookImages[position]);
        gridTextTop10PerWeek.setText(bookNames[position]);

        return gridview_top10_per_week;
    }
}

package com.example.bookshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookshare.R;
import com.example.bookshare.model.Category;

import java.util.ArrayList;
import java.util.List;

public class GridAdapterCat extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private List<Category> categoryList = new ArrayList<Category>();

    public GridAdapterCat(Context context, List<Category> categoryList) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position).categoryName;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //For category gridview
        View gridview_cat = convertView;
        if(convertView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridview_cat = inflater.inflate(R.layout.row_item_category, null);
        }

        //ImageView gridImageCat = (ImageView) gridview_cat.findViewById(R.id.grid_image);
        TextView gridTextCat = (TextView) gridview_cat.findViewById(R.id.grid_bookName);

        //System.out.println(".............Category Image of "+position+" = "+categoryList.get(position).categoryName);
        gridTextCat.setText(categoryList.get(position).categoryName);

        return gridview_cat;
    }
}

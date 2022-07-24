package com.example.bookshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bookshare.R;

public class ListAdapterComments extends BaseAdapter {

    private int bookImages[];
    private String[] usernames, descriptions;
    private String dp_name, username, commentDate, comment;
    private Context context;
    private LayoutInflater inflater;

    public ListAdapterComments(String[] usernames, String[] descriptions, Context context) {
        this.usernames = usernames;
        this.descriptions = descriptions;
        this.context = context;
    }

    @Override
    public int getCount() {
        return usernames.length;
    }

    @Override
    public Object getItem(int position) {
        return usernames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //For Top 10 books per week gridview
        View listViewComments = convertView;
        if(convertView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listViewComments = inflater.inflate(R.layout.row_item_comment, null);
        }

        //ImageView gridImageTop10PerWeek = (ImageView) listViewComments.findViewById(R.id.book_image);
        TextView dp_name = (TextView) listViewComments.findViewById(R.id.dp_name);
        TextView username = (TextView) listViewComments.findViewById(R.id.user_name);
        TextView commentDate = (TextView) listViewComments.findViewById(R.id.comment_date);
        TextView comment = (TextView) listViewComments.findViewById(R.id.comment);

        //gridImageTop10PerWeek.setImageResource(R.drawable.ic_list_black_48dp);
        //gridTextTop10PerWeek.setText(usernames[position]);
        username.setText(usernames[position]);
        comment.setText(descriptions[position]);

        return listViewComments;
    }
}

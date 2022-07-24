package com.example.bookshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.bookshare.R;
import com.example.bookshare.model.Conversation;
import com.example.bookshare.model.GlobalData;
import com.google.android.material.badge.BadgeDrawable;

import java.util.List;

public class ListAdapterMessageConversationList extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Conversation> conversationList;
    private TextView chatImage, chatImageLabel, chatUsername;

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private BadgeDrawable badgeDrawable;

    public ListAdapterMessageConversationList(List<Conversation> conversationList, Context context) {
        this.conversationList = conversationList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public Object getItem(int position) {
        return conversationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Conversation conversation = conversationList.get(position);
        //For Top 10 books per week gridview
        View listViewCategoryBooks = convertView;
        if(convertView==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listViewCategoryBooks = inflater.inflate(R.layout.row_item_message_conversation, null);
        }

        chatImage = (TextView) listViewCategoryBooks.findViewById(R.id.chat_image);
        chatImageLabel = (TextView) listViewCategoryBooks.findViewById(R.id.chat_image_label);
        chatUsername = (TextView) listViewCategoryBooks.findViewById(R.id.chat_username);

        if(conversation.getChatImageText().isEmpty() || conversation.getChatImageText().equals("") || conversation.getChatImageText().equals(" ") || conversation.getChatImageText().equals("@")){
            chatImage.setText(conversation.getUserName().substring(0,1).toUpperCase());
            chatUsername.setText(conversation.getUserName());
        }else{
            chatImage.setText(conversation.getChatImageText());
            chatUsername.setText(conversation.getUserFullName());
        }

        if(conversation.getMessageCount()>0){
            chatImageLabel.setText(conversation.getMessageCount()+"");
        }else{
            chatImageLabel.setVisibility(View.GONE);
        }

        System.out.println(">>>>>>>>>>>>> Name: "+conversation.getUserName()+"; count: "+conversation.getMessageCount());


        return listViewCategoryBooks;
    }
}

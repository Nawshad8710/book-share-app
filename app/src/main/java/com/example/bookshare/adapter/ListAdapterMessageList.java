package com.example.bookshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.R;
import com.example.bookshare.model.Conversation;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Message;
import com.google.android.material.badge.BadgeDrawable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListAdapterMessageList extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Message> messageList;
    private TextView messageDate, messageTime, message, chatImage;

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private BadgeDrawable badgeDrawable;
    private Conversation conversation;

    public ListAdapterMessageList(List<Message> messageList, Context context, Conversation conversation) {
        this.messageList = messageList;
        this.context = context;
        this.conversation = conversation;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Message msg = messageList.get(position);
        //For Top 10 books per week gridview
        View listViewMessage = convertView;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(msg.fromId == GlobalData.getInstance().userId){

            listViewMessage = inflater.inflate(R.layout.row_item_message_from, null);
            //chatImage = (TextView) listViewMessage.findViewById(R.id.chat_image);
            message = (TextView) listViewMessage.findViewById(R.id.chat_message);
            messageDate = (TextView) listViewMessage.findViewById(R.id.message_date);
            messageTime = (TextView) listViewMessage.findViewById(R.id.message_time);

            message.setText(msg.getMessage());
            if(!msg.getDateAdded().isEmpty() && !msg.getDateAdded().equals("") && !msg.getTimeAdded().isEmpty() && !msg.getTimeAdded().equals("")){
                messageDate.setText(msg.getDateAdded()+", ");
                messageTime.setText(msg.getTimeAdded());
            }else{
                messageDate.setVisibility(View.GONE);
                messageTime.setVisibility(View.GONE);
            }
        }else{
            listViewMessage = inflater.inflate(R.layout.row_item_message_to, null);

            chatImage = (TextView) listViewMessage.findViewById(R.id.chat_image);
            message = (TextView) listViewMessage.findViewById(R.id.chat_message);
            messageDate = (TextView) listViewMessage.findViewById(R.id.message_date);
            messageTime = (TextView) listViewMessage.findViewById(R.id.message_time);

            //chatImage.setText("U");
            if(conversation.getChatImageText().isEmpty() || conversation.getChatImageText().equals("") || conversation.getChatImageText().equals("null") || conversation.getChatImageText().equals("@")){
                chatImage.setText(conversation.getUserName().substring(0,1).toUpperCase());
            }else{
                chatImage.setText(conversation.getUserFullName().substring(0,1).toUpperCase());
            }
            message.setText(msg.getMessage());
            messageDate.setText(msg.getDateAdded()+", ");
            messageTime.setText(msg.getTimeAdded());
        }

        return listViewMessage;
    }
}

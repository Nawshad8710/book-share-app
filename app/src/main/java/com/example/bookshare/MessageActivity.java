package com.example.bookshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bookshare.adapter.ListAdapterMessageConversationList;
import com.example.bookshare.adapter.ListAdapterMessageList;
import com.example.bookshare.model.Conversation;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {

    private ImageView backBtn, ivRefresh;
    private TextView senderName;
    private ProgressBar pbMessages, pbSendMessage;
    private ListView messageList;
    private EditText etMessage;
    private ImageButton btnMsg;
    private LinearLayout btnRefresh, pbRefresh;

    SharedPreferences preferences;
    public static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private List<Message> messagesListData = new ArrayList<Message>();
    private Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        Intent intent = getIntent();
        conversation = (Conversation) intent.getSerializableExtra("conversation");

        senderName = (TextView) findViewById(R.id.sender_name);
        senderName.setText(conversation.getUserFullName());
        if(conversation.getChatImageText().isEmpty() || conversation.getChatImageText().equals("") || conversation.getChatImageText().equals("null") || conversation.getChatImageText().equals("@")){
            senderName.setText(conversation.getUserName());
        }else{
            senderName.setText(conversation.getUserFullName());
        }

        pbMessages = (ProgressBar) findViewById(R.id.pbMessages);
        pbMessages.setVisibility(View.VISIBLE);

        pbSendMessage = (ProgressBar) findViewById(R.id.pbSendMessage);
        pbSendMessage.setVisibility(View.GONE);

        messageList = (ListView) findViewById(R.id.messages_list);

        backBtn = (ImageView) findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MessageConversationActivity.class);
                i.putExtra("Flag", GlobalData.getInstance().NOT_FROM_NOTIFICATION);
                startActivity(i);
                finish();
            }
        });

        ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
        ivRefresh.setVisibility(View.VISIBLE);

        pbRefresh = (LinearLayout) findViewById(R.id.pb_refresh);
        pbRefresh.setVisibility(View.GONE);

        getMessages();

        etMessage = (EditText) findViewById(R.id.et_message);
        btnMsg = (ImageButton) findViewById(R.id.btn_send);
        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etMessage.getText().toString().isEmpty() && !etMessage.getText().toString().equals("") && !etMessage.getText().toString().equals(" ") && etMessage.getText().toString()!="") {
                    pbSendMessage.setVisibility(View.VISIBLE);
                    btnMsg.setVisibility(View.GONE);
                    //increase r value in database table process start
                    apiName = "api_insertData.php";
                    String actionN = action + "sendMessage";
                    String urlN = url + apiName + actionN;

                    //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);

                    StringRequest request = new StringRequest(com.android.volley.Request.Method.POST, urlN, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (response.equals("yes")) {
                                Toast.makeText(getApplicationContext(), "Message sent successfully!", Toast.LENGTH_SHORT).show();
                                pbSendMessage.setVisibility(View.GONE);
                                btnMsg.setVisibility(View.VISIBLE);
                                messagesListData.add(new Message(GlobalData.getInstance().userId, conversation.userId, "", "", etMessage.getText().toString(), GlobalData.getInstance().userName));
                                etMessage.setText("");
                                messageList.setSelection(messagesListData.size()-1);

                                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(etMessage.getApplicationWindowToken(),0);
                            } else if (response.toString().equals("tokenFail")) {
                                Toast.makeText(getApplicationContext(), "Wrong token, try again", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                                //System.out.println(">>>>>>>>>>>>>>>>>>>URL= "+urlN);
                                //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/json");
                            params.put("myToken", "786");
                            params.put("fromId",GlobalData.getInstance().userId+"");
                            params.put("fromUsername",GlobalData.getInstance().userName);
                            params.put("fromFullName",GlobalData.getInstance().userFullName);
                            params.put("toId",conversation.getUserId()+"");
                            params.put("toUsername",conversation.getUserName());
                            params.put("toFullName",conversation.getUserFullName());
                            params.put("message",etMessage.getText().toString());

                            return params;
                        }
                    };

                    GlobalData.getInstance().addToRequestQueue(request);
                }else{
                    Toast.makeText(getApplicationContext(), "Type some text first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRefresh = (LinearLayout) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivRefresh.setVisibility(View.GONE);
                pbRefresh.setVisibility(View.VISIBLE);
                pbMessages.setVisibility(View.VISIBLE);
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(etMessage.getApplicationWindowToken(),0);
                getMessages();
            }
        });
    }

    protected void getMessages(){
        messagesListData.clear();
        apiName = "api_getData.php";
        String actionN = action+"getMessages";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Message m = new Message();

                        m.fromId = Integer.parseInt(object.getString("idFrom"));
                        m.toId = Integer.parseInt(object.getString("idTo"));
                        m.dateAdded = object.getString("dateAdded");
                        m.timeAdded = object.getString("timeAdded");
                        m.message = object.getString("message");
                        if(conversation.getChatImageText().isEmpty() || conversation.getChatImageText().equals("")){
                            m.senderName = conversation.getUserName();
                        }else{
                            m.senderName = conversation.getUserFullName();
                        }

                        messagesListData.add(m);

                        ListAdapterMessageList adapter = new ListAdapterMessageList(messagesListData,getApplicationContext(),conversation);
                        messageList.setAdapter(adapter);
                        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Message msg = messagesListData.get(position);
                                //Toast.makeText(getApplicationContext(),"Position "+position,Toast.LENGTH_SHORT).show();
                                //startActivity(intent);
                            }
                        });

                        messageList.setSelection(messagesListData.size()-1);
                        pbMessages.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading data",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }

                ivRefresh.setVisibility(View.VISIBLE);
                pbRefresh.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error loading data",Toast.LENGTH_LONG).show();
                //serverError = true;
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("myToken", "786");
                params.put("myId",GlobalData.getInstance().userId+"");
                params.put("anotherId",conversation.getUserId()+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MessageConversationActivity.class);
        i.putExtra("Flag", GlobalData.getInstance().NOT_FROM_NOTIFICATION);
        startActivity(i);
        finish();
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        }
        else return false;
    }
}
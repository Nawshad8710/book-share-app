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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.bookshare.adapter.ListAdapterCategoryBookList;
import com.example.bookshare.adapter.ListAdapterMessageConversationList;
import com.example.bookshare.model.Book;
import com.example.bookshare.model.Conversation;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.model.Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageConversationActivity extends AppCompatActivity {

    private ImageView backBtn, ivRefresh;

    private ProgressBar pbMessages;
    private ListView conversationList;
    private LinearLayout btnRefresh, pbRefresh;

    SharedPreferences preferences;
    public static final String FILE_NAME="preferenceFile";

    private String url = GlobalData.url;
    private String action = "?action=";
    private String apiName = "";

    private List<Conversation> conversationListData = new ArrayList<Conversation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_conversations);

        if(!isConnected(getApplicationContext())){
            Intent loadAgain = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(loadAgain);
        }

        pbMessages = (ProgressBar) findViewById(R.id.pbMessages);
        pbMessages.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        int flag = i.getIntExtra("Flag",1);
        if(flag==1){
            pbMessages.setVisibility(View.VISIBLE);
            System.out.println("-----------From notification");
            getConversations();
        }else if(flag==0){
            System.out.println("-----------Not From notification");
        }

        conversationList = (ListView) findViewById(R.id.conversation_list);

        //action back
        backBtn = (ImageView) findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goHome = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(goHome);
                finish();
            }
        });

        ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
        ivRefresh.setVisibility(View.VISIBLE);

        pbRefresh = (LinearLayout) findViewById(R.id.pb_refresh);
        pbRefresh.setVisibility(View.GONE);

        ListAdapterMessageConversationList adapter = new ListAdapterMessageConversationList(GlobalData.getInstance().conversationList,getApplicationContext());
        conversationList.setAdapter(adapter);
        conversationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conversation conversation = GlobalData.getInstance().conversationList.get(position);
                //Toast.makeText(getApplicationContext(),"Position "+position,Toast.LENGTH_SHORT).show();
                if(GlobalData.getInstance().conversationList.get(position).messageCount > 0){
                    GlobalData.getInstance().conversationCount--;
                }
                GlobalData.getInstance().conversationList.get(position).messageCount = 0;
                Intent intent = new Intent(getApplicationContext(),MessageActivity.class);
                intent.putExtra("conversation", conversation);
                startActivity(intent);
            }
        });
        pbMessages.setVisibility(View.GONE);

        btnRefresh = (LinearLayout) findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivRefresh.setVisibility(View.GONE);
                pbRefresh.setVisibility(View.VISIBLE);
                pbMessages.setVisibility(View.VISIBLE);

                getConversations();
            }
        });
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

    @Override
    public void onBackPressed() {
        Intent goHome = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(goHome);
        finish();
    }

    protected void getConversations(){
        GlobalData.getInstance().conversationCount = 0;
        GlobalData.getInstance().conversationList.clear();
        apiName = "api_getData.php";
        String actionN = action+"getConversations";
        String urlN = url+apiName+actionN;

        //System.out.println("--------------------------------------url:"+urlN+"#");
        JsonArrayRequest request = new JsonArrayRequest(com.android.volley.Request.Method.POST, urlN, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //System.out.println("------------------@@@@@@response");

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Conversation c = new Conversation();

                        int fromId = Integer.parseInt(object.getString("idFrom"));
                        int toId = Integer.parseInt(object.getString("idTo"));

                        System.out.println("From: "+fromId+"; To: "+toId+"; count: "+object.getString("total"));

                        if(fromId==GlobalData.getInstance().userId){
                            c.messageCount = 0;
                            c.userId = toId;
                            c.userName = object.getString("toUsername");
                            if(object.getString("toFullName").isEmpty() || object.getString("toFullName").equals("") || object.getString("toFullName").substring(0,1).equals("@")){
                                c.userFullName = c.userName;
                            }else{
                                c.userFullName = object.getString("toFullName");
                            }
                            c.chatImageText = c.userFullName.substring(0,1).toUpperCase();
                        }else if(toId==GlobalData.getInstance().userId){
                            System.out.println("yes");
                            int total = Integer.parseInt(object.getString("total"));
                            if(total>0){
                                GlobalData.getInstance().conversationCount++;
                            }
                            c.messageCount = total;
                            c.userId = fromId;
                            c.userName = object.getString("fromUsername");
                            if(object.getString("fromFullName").isEmpty() || object.getString("fromFullName").equals("") || object.getString("fromFullName").substring(0,1).equals("@")){
                                c.userFullName = c.userName;
                            }else{
                                c.userFullName = object.getString("fromFullName");
                            }
                            c.chatImageText = c.userFullName.substring(0,1).toUpperCase();
                        }

                        int flag = 0;
                        for(Conversation cnv : GlobalData.getInstance().conversationList){
                            if(cnv.userId==c.userId){
                                flag = 1;
                                cnv.messageCount = c.messageCount;
                            }
                        }

                        if(flag==0){
                            GlobalData.getInstance().conversationList.add(c);
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Exception on loading data",Toast.LENGTH_LONG).show();
                        //serverError = true;
                        e.printStackTrace();
                    }
                }
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>Orders size="+GlobalData.getInstance().requestList.size());

                //showCategories();
                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>Response length = "+response.length());
                //System.out.println("----------------------total categories = "+GlobalData.getInstance().categoryList);

                ListAdapterMessageConversationList adapter = new ListAdapterMessageConversationList(GlobalData.getInstance().conversationList,getApplicationContext());
                conversationList.setAdapter(adapter);
                conversationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Conversation conversation = GlobalData.getInstance().conversationList.get(position);
                        Toast.makeText(getApplicationContext(),"Position "+position,Toast.LENGTH_SHORT).show();
                        if(GlobalData.getInstance().conversationList.get(position).messageCount > 0){
                            GlobalData.getInstance().conversationCount--;
                        }
                        GlobalData.getInstance().conversationList.get(position).messageCount = 0;
                        Intent intent = new Intent(getApplicationContext(),MessageActivity.class);
                        intent.putExtra("conversation", conversation);
                        startActivity(intent);
                        finish();
                    }
                });
                pbMessages.setVisibility(View.GONE);
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
                params.put("userId",GlobalData.getInstance().userId+"");

                return params;
            }
        };
        GlobalData.getInstance().addToRequestQueue(request);

    }
}
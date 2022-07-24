package com.example.bookshare.notification;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bookshare.MainActivity;
import com.example.bookshare.MessageConversationActivity;
import com.example.bookshare.R;
import com.example.bookshare.SplashActivity;
import com.example.bookshare.model.GlobalData;
import com.example.bookshare.ui.home.HomeFragment;
import com.example.bookshare.ui.requests.RequestsFragment;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseServices extends FirebaseMessagingService {

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID ="101" ;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    private void showNotification(String title,String message){
        Intent intent;
        if(title.equals("Message")){
            intent = new Intent(this, MessageConversationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("Flag", GlobalData.getInstance().FROM_NOTIFICATION);
        }else if(title.equals("Book Request")){
            intent = new Intent(this, RequestsFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //intent.putExtra("Flag", GlobalData.getInstance().FROM_NOTIFICATION);
        }else if(title.equals("Buy Request Accepted")){
            intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //intent.putExtra("Flag", GlobalData.getInstance().FROM_NOTIFICATION);
        }else if(title.equals("Rent Request Accepted")){
            intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //intent.putExtra("Flag", GlobalData.getInstance().FROM_NOTIFICATION);
        }else if(title.equals("Rent Ended")){
            intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //intent.putExtra("Flag", GlobalData.getInstance().FROM_NOTIFICATION);
        }else if(title.equals("Buy Request Accepted")){
            intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //intent.putExtra("Flag", GlobalData.getInstance().FROM_NOTIFICATION);
        }else{
            intent = new Intent(this, RequestsFragment.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        //message = message+"; Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin feugiat maximus augue";

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Uri alarmSound = RingtoneManager. getDefaultUri (RingtoneManager. TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_icon)
                .setVibrate( new long []{ 1000 , 1000 , 1000 , 1000 , 1000 })
                .setSound(alarmSound)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
}

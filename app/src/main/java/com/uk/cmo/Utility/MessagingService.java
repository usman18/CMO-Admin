package com.uk.cmo.Utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.uk.cmo.Activities.MainActivity;
import com.uk.cmo.Activities.MainScreenActivity;
import com.uk.cmo.R;

/**
 * Created by usman on 29-05-2018.
 */

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title="";
        String body="";

        try {

//            title=remoteMessage.getNotification().getTitle();
//            body=remoteMessage.getNotification().getBody();

              title=remoteMessage.getData().get("title");
              body=remoteMessage.getData().get("text");

        }catch (NullPointerException e){
            e.printStackTrace();
        }
        Log.d(TAG,"Notification Title : "+title);
        Log.d(TAG,"Notification Body : "+body);

        sendNotification(title,body);
//        buildNotification(title,body);
    }

    private void buildNotification(String title,String body){

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,new Intent(this, MainScreenActivity.class),0);


        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_action_email)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();


    }
    private void sendNotification(String notificationTitle, String notificationBody) {
        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String CHANNEL_ID = "my_channel_01";

        NotificationCompat.Builder notificationBuilder =  new NotificationCompat.Builder(this)
                .setAutoCancel(true)   //Automatically delete the notification
                .setSmallIcon(R.mipmap.ic_launcher) //Notification icon
                .setContentIntent(pendingIntent)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody);



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

}

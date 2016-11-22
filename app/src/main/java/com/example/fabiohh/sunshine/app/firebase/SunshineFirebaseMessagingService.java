package com.example.fabiohh.sunshine.app.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.fabiohh.sunshine.app.MainActivity;
import com.example.fabiohh.sunshine.app.R;
import com.example.fabiohh.sunshine.app.Utility;
import com.example.fabiohh.sunshine.app.data.Weather;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by fabiohh on 11/19/16.
 */
public class SunshineFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "SunshineFMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " +
                remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());

        if (!remoteMessage.getData().isEmpty()) {
            if (getString(R.string.gcm_defaultSenderId).equals(remoteMessage.getFrom())) {
                Weather weatherEntry = Weather.fromRemoteData(remoteMessage);

                sendNotification(weatherEntry);
            }
        }
    }

    /*
    {
	"to": "f-sb3ilp-OY:APA91bELWOMMIzqIlpE75aGkkQ8lzM4hzQazi8ZXQfKnuXmNRlSg3OGmWunP--5q_7uHNKwCdPA14Za8Z0LXfOdmR98lg_IKZXoeqcQ7N6Y8K5aChemKGwLQ0JiXFpPn2qVQx7xHMPYv",
    "data": {
        "desc": "Today's",
        "high": "15",
        "low": "2",
        "weather_id": "761",
        "icon_id": "2130837592"
    }
}
     */
    private void sendNotification(Weather weatherEntry) {
        Context context = getApplicationContext();
        String title = context.getString(R.string.app_name);

        // Define the text of the forecast.
        String contentText = String.format(context.getString(R.string.format_notification),
                weatherEntry.getDesc(),
                Utility.formatTemperature(context, weatherEntry.getHigh(), weatherEntry.isMetric()),
                Utility.formatTemperature(context, weatherEntry.getLow(), weatherEntry.isMetric()));

        //build your notification here.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(new Intent(context, MainActivity.class));

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(weatherEntry.getIconId())
                .setContentTitle(title)
                .setContentText(contentText)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }
}
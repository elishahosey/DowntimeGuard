package com.example.downtimeguard.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;

public class ActivityRecognitionHelper {

    public static void requestActivityUpdates(Context context) {
        ActivityRecognitionClient client = ActivityRecognition.getClient(context);
        Intent intent = new Intent(context, ActivityReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        client.requestActivityUpdates(10000, pi);
    }

    public static void removeActivityUpdates(Context context) {
        ActivityRecognitionClient client = ActivityRecognition.getClient(context);
        Intent intent = new Intent(context, ActivityReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        client.removeActivityUpdates(pi);
    }
}

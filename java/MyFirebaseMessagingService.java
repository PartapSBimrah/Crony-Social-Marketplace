package com.abhigam.www.foodspot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by sourabhzalke on 07/03/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String NOTIFICATION_BADGE = "com.abhigam.www.foodspot.pref";

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //this intent should be used only once FLAG_ONE_SHOT
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,i,PendingIntent.FLAG_ONE_SHOT);


        final String url = "http://13.233.234.79/uploads/profile_pic/"+
                remoteMessage.getNotification().getTitle()+"_profile.jpg";

        // Get a handler that can be used to post to the main thread
        Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Glide
                        .with(getApplicationContext())
                        .load(url)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)      //Not caching the image because of image filter problem
                        .skipMemoryCache(true)
                        .centerCrop()
                        .into(new SimpleTarget<Bitmap>(100, 100) { //width and height
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {

                                Uri sound = Uri.parse("android.resource://" + getPackageName()
                                        + "/" + R.raw.notification_sound);

                                NotificationCompat.Builder nb
                                        = new NotificationCompat.Builder(getApplicationContext(),"M_CH_ID");
                                nb.setContentTitle("Crony");
                                nb.setContentText(remoteMessage.getNotification().getBody());
                                nb.setAutoCancel(true);
                                nb.setSmallIcon(R.mipmap.ic_launcher);
                                nb.setLargeIcon(resource);
                                nb.setContentIntent(pendingIntent);
                                nb.setSound(sound);
                                nb.setTicker(remoteMessage.getNotification().getBody());

                                SharedPreferences.Editor editor = PreferenceManager
                                        .getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putString(NOTIFICATION_BADGE,"1");
                                editor.commit();
                        /*
                        //big notification
                        android.support.v7.app.NotificationCompat.BigPictureStyle bps
                        = new android.support.v7.app.NotificationCompat.BigPictureStyle().bigPicture(resource);
                        bps.setBigContentTitle("Crony");
                        bps.setSummaryText(remoteMessage.getNotification().getBody());
                        nb.setStyle(bps);
                        */

                                nb.setPriority(Notification.PRIORITY_MAX);
                                nb.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                nb.setSound(alarmSound, AudioManager.STREAM_MUSIC);

                                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                                String find_follow = remoteMessage.getNotification().getBody();
                                if(find_follow.toLowerCase().contains("follow")){
                                    notificationManager.notify(0,nb.build());
                                }else if(find_follow.toLowerCase().contains("commented")){
                                    notificationManager.notify(1,nb.build());
                                }else{
                                    notificationManager.notify(2,nb.build());
                                }

                            }
                        });
            } // This is your code
        };
        mainHandler.post(myRunnable);



    }
}

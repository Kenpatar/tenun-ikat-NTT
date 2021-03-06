package ken.tenunikatntt.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import ken.tenunikatntt.R;

/**
 * Created by Emilken18 on 10/15/2018.
 */

public class NotificationHelper extends ContextWrapper {

    private static final String TENUN_IKAT_ID = "ken.tenunikatntt.TenunApp";
    private static final String TENUN_IKAT_NAME = "Tenun Ikat NTT";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O); //Berlaku hanya untuk API 26 keatas
        createChannel();

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel tenunChannel= new NotificationChannel(TENUN_IKAT_ID,
        TENUN_IKAT_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        tenunChannel.enableLights(false);
        tenunChannel.enableVibration(true);
        tenunChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(tenunChannel);
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getTenunChannelNotification(String title, String body, PendingIntent contentIntent,
                                                            Uri soundUri)
    {
        return new Notification.Builder(getApplicationContext(),TENUN_IKAT_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(false);

    }
}

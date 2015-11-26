package att.attendanceapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by rujoota on 25-11-2015.
 */
public class AlarmRcvr extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i("AttendanceHelper", "called receiver method");
        try
        {
            generateNotification(context);
        }
        catch(Exception e)
        {
            Log.i("AttendanceHelper", e.toString());
        }

    }
    public static void generateNotification(Context context){

        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,new Intent(context,MainActivity.class),0);
        try
        {
            NotificationCompat.Builder builder=new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.icon_default_present)
                    .setContentTitle("AttendanceApp")
                    .setContentText("Time to take attendance for this class")
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setAutoCancel(true);
            NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1,builder.build());
        }
        catch (Exception e)
        {
            Log.i("AttendanceHelper", e.toString());
        }

    }
}

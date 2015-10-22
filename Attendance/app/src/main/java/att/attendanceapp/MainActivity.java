package att.attendanceapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import Helper.FileUtils;
import Helper.HelperMethods;

public class MainActivity extends ActivityBaseClass
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListView();
        HelperMethods.putSharedPref(this, getString(R.string.isLoggedIn_sharedPref_string), "yes");
        HelperMethods.putSharedPref(this, getString(R.string.loggedInUser_sharedPref_string), "rujoota.shah@gmail.com");
    }
    void setListView()
    {
        String[] arr=getResources().getStringArray(R.array.listview_main_facilitator);

        ListAdapter adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arr);

        ListView listView=(ListView)findViewById(R.id.listViewMain);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        String itemName = String.valueOf(parent.getItemAtPosition(position));
                        changeIntent(position, itemName);
                    }
                }
        );
    }
    private void setRecurringAlarm(Context context)
    {
        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getDefault());
        updateTime.set(Calendar.HOUR_OF_DAY, 16);
        updateTime.set(Calendar.MINUTE, 20);

        Intent downloader = new Intent(context, Helper.AlarmReceiver.class);
        downloader.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // should be AlarmManager.INTERVAL_DAY (but changed to 15min for testing)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }


    void changeIntent(int pos,String itemName)
    {
        if(pos==0)
        {
            //FileUtils.showFileChooser(this,this);
        }
        if(pos==1)
        {
            //setRecurringAlarm(this);
        }
        if(pos==2)
        {
            Intent newIntent=new Intent(this,ManageCourses.class);
            startActivity(newIntent);
        }
        else if(pos==4)
        {
            Intent newIntent=new Intent(this,ManageHolidays.class);
            startActivity(newIntent);
        }
        else if(pos==3)
        {
            Intent newIntent=new Intent(this,MyTimetable.class);
            startActivity(newIntent);
        }
        else if(pos==5)
        {
            Intent newIntent=new Intent(this,ManageMySchedule.class);
            startActivity(newIntent);
        }
    }
}

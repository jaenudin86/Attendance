package att.attendanceapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import DBHelper.TimetableSlot;
import Helper.FileUtils;
import Helper.HelperMethods;

public class MainActivity extends ActivityBaseClass
{
    TimetableSlot timetableForDay;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListView();
        /*HelperMethods.putSharedPref(this, getString(R.string.isLoggedIn_sharedPref_string), "yes");
        HelperMethods.putSharedPref(this, getString(R.string.loggedInUser_sharedPref_string), "rujoota.shah@gmail.com");*/
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
            new GetTimetableForNow().execute();
        }
        if(pos==1)
        {
            //setRecurringAlarm(this);
            Intent newIntent=new Intent(this,FacultyReport.class);
            startActivity(newIntent);
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
    class GetTimetableForNow extends AsyncTask<String, Void, String>
    {
        InputStream is = null;
        String response = "";
        String returnString="";

        @Override
        protected String doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/getTimetableForDayTime.php";

            try
            {
                URL url = new URL(url_select);
                String keys[]={"user_id","date","time"};

                Calendar dateCalendar = Calendar.getInstance();
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String date =sdf.format(dateCalendar.getTime());

                String format = "HH:mm";
                SimpleDateFormat sdfTime = new SimpleDateFormat(format, Locale.US);
                String time = sdfTime.format(dateCalendar.getTime());

                String values[]={HelperMethods.getCurrentLoggedinUser(MainActivity.this),date,time};
                response=HelperMethods.getResponse(url_select,keys,values);

                if(response.equals("null") || response==null)
                {
                    returnString="no data";
                    response="";
                }
                else
                {
                    Gson gson = new Gson();
                    timetableForDay = gson.fromJson(response, TimetableSlot.class);
                    returnString = "ok";
                }
            }
            catch (Exception ex)
            {
                returnString="Exception:"+ex.toString();
            }
            return returnString;
        }

        protected void onPostExecute(String v)
        {
            super.onPostExecute(v);
            // no exception found on previous call
            if(!v.toLowerCase().contains("exception"))
            {
                // if no data found then
                if(response.isEmpty() || response.equals("null"))
                {
                    Toast.makeText(MainActivity.this,"No courses found for today",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent=new Intent(MainActivity.this,FillAttendanceByFaculty.class);
                    intent.putExtra(context.getString(R.string.bundleKeyCourseCode),timetableForDay.getCourseCode());

                    String timingStart= HelperMethods.convertToStandardTime(timetableForDay.getStartTime());
                    String timingEnd= HelperMethods.convertToStandardTime(timetableForDay.getEndTime());
                    String time=timingStart + "-" + timingEnd;
                    String date=HelperMethods.convertDateFromSQLToUS(timetableForDay.getDate());

                    intent.putExtra(context.getString(R.string.bundleKeyAttendanceId),timetableForDay.getId());
                    intent.putExtra(context.getString(R.string.bundleKeyDate),date);
                    intent.putExtra(context.getString(R.string.bundleKeyTimings),time);
                    startActivity(intent);
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),v,Toast.LENGTH_SHORT).show();
                //relativeLayout.setVisibility(View.INVISIBLE);
            }
        }
    }
}

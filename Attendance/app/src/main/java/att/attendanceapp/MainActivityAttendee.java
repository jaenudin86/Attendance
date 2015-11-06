package att.attendanceapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import DBHelper.TimetableSlot;
import Helper.HelperMethods;

public class MainActivityAttendee extends ActivityBaseClass
{
    TimetableSlot timetableForDay;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_attendee);
        setListView();
    }
    void setListView()
    {
        String[] arr=getResources().getStringArray(R.array.listview_main_attendee);

        ListAdapter adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arr);

        ListView listView=(ListView)findViewById(R.id.listViewMainAttendee);
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
    void changeIntent(int pos,String itemName)
    {
        switch(pos)
        {
            case 0:
                new GetTimetableForNow().execute();
                break;
            case 1:
                break;

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
            String url_select = getString(R.string.serviceURL)+"/getTimetableAttendee.php";

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

                String values[]={"mithumahek@gmail.com",date,time};
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
                    Toast.makeText(MainActivityAttendee.this, "No courses found for today", Toast.LENGTH_LONG).show();
                }
                else
                {
                    final Dialog dialog = new Dialog(context);
                    dialog.setTitle("Enter code:");
                    LinearLayout inflatedView = (LinearLayout) View.inflate(MainActivityAttendee.this, R.layout.fill_code_popup, null);
                    dialog.setContentView(inflatedView);
                    final EditText code=(EditText)inflatedView.findViewById(R.id.etFillCode);
                    Button btnOk=(Button)inflatedView.findViewById(R.id.btnFillCodeOk);
                    btnOk.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            new SubmitAttendance().execute("mithumahek@gmail.com",timetableForDay.getId(),code.getText().toString());
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),v,Toast.LENGTH_SHORT).show();
                //relativeLayout.setVisibility(View.INVISIBLE);
            }
        }
    }
    class SubmitAttendance extends AsyncTask<String, String, Void>
    {
        //private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String response="";

        @Override
        protected Void doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL) + "/submitAttendanceAttendee.php";
            String keys[]={"attendee_id","attendance_id","code"};
            String values[]={params[0],params[1],params[2]};
            response=HelperMethods.getResponse(url_select,keys,values);
            return null;
        }
        protected void onPostExecute(Void v)
        {
            if(!response.isEmpty())
            {
                if (response.contains("Exception"))
                {
                    Toast.makeText(MainActivityAttendee.this,"Attendance not fully submitted. Please try later."+response,Toast.LENGTH_LONG).show();
                    //Log.e("Attendance", response);
                }
                else
                    Toast.makeText(MainActivityAttendee.this,"Attendance submitted",Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }
}

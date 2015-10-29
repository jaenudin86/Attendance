package att.attendanceapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import DBHelper.Attendee;
import DBHelper.Holiday;
import DBHelper.Timetable;
import Helper.AdapterInterface;
import Helper.HelperMethods;

public class FillAttendanceByFaculty extends ActivityBaseClass
{
    private RecyclerView attendeesView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    Context context=this;
    ArrayList<Attendee> attendeeList=new ArrayList<Attendee>();
    String courseCode="";
    String attendanceId="";
    FillAttendanceAdapter adapter;
    CheckBox allPresent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_attendance_by_faculty);

        Bundle data = getIntent().getExtras();
        if (data != null)
        {
            courseCode = data.getString(getString(R.string.bundleKeyCourseCode));
            attendanceId=data.getString(getString(R.string.bundleKeyAttendanceId));
        }
        attendeesView = (RecyclerView) findViewById(R.id.rviewFillAttendance);
        attendeesView.setHasFixedSize(true);
        attendeesView.setItemAnimator(new DefaultItemAnimator());
        recyclerLayoutManager = new LinearLayoutManager(this);
        attendeesView.setLayoutManager(recyclerLayoutManager);
        allPresent=(CheckBox)findViewById(R.id.chkFillAttendanceAllPresent);

        new GetAttendeesForThisCourse().execute(courseCode);
    }
    public void onSaveClicked(View view)
    {

    }
    public void setRadioStatus(RadioButton[] radioButtons)
    {
        for(RadioButton btn:radioButtons)
        {
            btn.setChecked(true);
        }
    }
    public void onSaveAttendanceClick(View view)
    {
        String rollnos="";
        for(Attendee att:adapter.absentees)
        {
            rollnos=att.getEmailId()+",";
        }
        if(!rollnos.isEmpty())
            rollnos=rollnos.substring(0,rollnos.length()-1);
        new SubmitAttendance().execute(rollnos);
    }

    class GetAttendeesForThisCourse extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progressDialog;
        InputStream is = null;
        String response = "";
        String returnString="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(FillAttendanceByFaculty.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/getAttendeesForCourse.php";
            try
            {
                String keys[] = {"user_id", "course_code"};
                String values[] = {HelperMethods.getCurrentLoggedinUser(FillAttendanceByFaculty.this), params[0]};
                response = HelperMethods.getResponse(url_select, keys, values);
                if (!response.isEmpty() && !response.equals("null"))
                {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Attendee>>()
                    {
                    }.getType();
                    attendeeList = gson.fromJson(response, type);
                }
                else
                    response = "";
                returnString="ok";
            }
            catch(Exception ex)
            {
                returnString="Exception:"+ex.toString();
            }
            return returnString;
        }

        protected void onPostExecute(String v)
        {
            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            super.onPostExecute(v);
            // no exception found on previous call
            if(!v.toLowerCase().contains("exception"))
            {
                // if no data found then
                if(response.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        adapter=new FillAttendanceAdapter(attendeeList, context, courseCode);
                        attendeesView.setAdapter(adapter);
                        allPresent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                        {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                            {
                                adapter = new FillAttendanceAdapter(attendeeList, context, courseCode, isChecked);
                                attendeesView.setAdapter(adapter);
                            }
                        });

                    }
                    catch (Exception ex)
                    {
                        Log.e("Attendance", "Problems in Manage Attendees:" + ex.getMessage());
                    }
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),v,Toast.LENGTH_SHORT).show();
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
            String url_select = getString(R.string.serviceURL) + "/submitAttendance.php";
            String keys[]={"attendance_id","absent_ids"};
            String values[]={attendanceId,params[0]};
            response=HelperMethods.getResponse(url_select,keys,values);
            return null;
        }
        protected void onPostExecute(Void v)
        {
            if(!response.isEmpty())
            {
                if (response.contains("Exception"))
                {
                    Toast.makeText(FillAttendanceByFaculty.this,"Attendance not fully submitted. Please try later."+response,Toast.LENGTH_LONG).show();
                    //Log.e("Attendance", response);
                }
                else if (response != "null")
                {
                    Toast.makeText(FillAttendanceByFaculty.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

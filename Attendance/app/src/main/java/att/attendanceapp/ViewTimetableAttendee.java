package att.attendanceapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
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
import DBHelper.TimetableSlot;
import Helper.HelperMethods;

public class ViewTimetableAttendee extends ActivityBaseClass
{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    ArrayList<TimetableSlot> timetableForDay;
    TextView noData;
    String attendee;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_timetable_attendee);
        noData=(TextView)findViewById(R.id.tvViewTimetableAttendeeNoDataFound);
        recyclerView = (RecyclerView) findViewById(R.id.rviewMyTimetableAttendee);
        recyclerView.setHasFixedSize(true);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        attendee= HelperMethods.getCurrentLoggedinUser(this);

        new GetAttendeesForThisCourse().execute("2015-10-07");
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
            progressDialog=new ProgressDialog(ViewTimetableAttendee.this);
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
                String values[] = {HelperMethods.getCurrentLoggedinUser(ViewTimetableAttendee.this), params[0]};
                response = HelperMethods.getResponse(url_select, keys, values);
                if (!response.isEmpty() && !response.equals("null"))
                {

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


                    }
                    catch (Exception ex)
                    {
                        Log.e("Attendance", "Problems in Attendees:" + ex.getMessage());
                    }
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),v,Toast.LENGTH_SHORT).show();
            }

        }
    }
}

package att.attendanceapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

import DBHelper.TimetableSlot;
import Helper.HelperMethods;

public class ViewTimetable extends ActivityBaseClass
{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    ArrayList<TimetableSlot> timetableForDay;
    TextView noData;
    String facilitator;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_timetable);
        noData=(TextView)findViewById(R.id.tvViewTimetableNoDataFound);
        recyclerView = (RecyclerView) findViewById(R.id.rviewMyTimetable);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        facilitator= HelperMethods.getCurrentLoggedinUser(this);

        Bundle data=getIntent().getExtras();
        String date="";
        if(data!=null)
        {
            date=data.getString("date");
        }
        new GetTimetable().execute(date);
    }

    class GetTimetable extends AsyncTask<String, Void, String>
    {

        InputStream is = null;
        String response = "";
        String returnString="";

        @Override
        protected String doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/getTimetableForDay.php";
            try
            {
                URL url = new URL(url_select);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(facilitator, "UTF-8")+"&"+
                                URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                is = httpUrlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"));
                String line;
                while ((line = bufferedReader.readLine())!=null)
                {
                    response+= line;
                }
                if(response.equals("null") || response==null)
                {
                    returnString="no data";
                    response="";
                }
                else
                {
                    Gson gson = new Gson();
                    Type typeTimetable = new TypeToken<ArrayList<TimetableSlot>>()
                    {
                    }.getType();
                    timetableForDay = gson.fromJson(response, typeTimetable);
                    returnString = "ok";
                }
                bufferedReader.close();
                is.close();
                httpUrlConnection.disconnect();
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
                if(response.isEmpty())
                {
                    recyclerView.setVisibility(View.INVISIBLE);
                    noData.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(), "No courses scheduled for this day", Toast.LENGTH_SHORT).show();
                    //finish();
                }
                else
                {
                    noData.setVisibility(View.INVISIBLE);
                    try
                    {
                        recyclerAdapter = new RecyclerTimetableAdapter(timetableForDay,ViewTimetable.this);
                        recyclerView.setAdapter(recyclerAdapter);
                    }
                    catch (Exception ex)
                    {
                        Log.e("Attendance", "Problems in ManageCourses:" + ex.getMessage());
                    }
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

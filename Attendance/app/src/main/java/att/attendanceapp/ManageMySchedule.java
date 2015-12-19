package att.attendanceapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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

import DBHelper.Timetable;
import Helper.HelperMethods;

public class ManageMySchedule extends ActivityBaseClass
{
    private RecyclerView recyclerView;
    private MyScheduleRecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    ArrayList<Timetable> mySchedule;
    TextView noData;
    String facilitator;
    private static final int EDIT_CODE = 2;
    private static final int ADD_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_my_schedule);
        noData=(TextView)findViewById(R.id.tvManageScheduleNoDataFound);
        recyclerView = (RecyclerView) findViewById(R.id.rviewSchedule);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        facilitator= HelperMethods.getCurrentLoggedinUser(this);
        new GetTimetable().execute();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CODE && resultCode == Activity.RESULT_OK)
        {
            if(mySchedule==null)
            {
                mySchedule=new ArrayList<>();
                Timetable obj=(Timetable) data.getSerializableExtra("newTimetable");
                mySchedule.add(obj);
                noData.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerAdapter = new MyScheduleRecyclerAdapter(mySchedule,ManageMySchedule.this);
                recyclerView.setAdapter(recyclerAdapter);
            }
            else
            {
                Timetable obj = (Timetable) data.getSerializableExtra("newTimetable");
                //mySchedule.add(obj);
                recyclerView.smoothScrollToPosition(mySchedule.size());


                recyclerAdapter.addNewItem(obj, mySchedule.size());
            }
        }
        else if(requestCode == EDIT_CODE && resultCode == Activity.RESULT_OK)
        {

        }
    }
    public void onAddScheduleClick(View view)
    {
        Intent intent=new Intent(this,AddTimetable.class);
        startActivityForResult(intent,ADD_CODE);
    }
    class GetTimetable extends AsyncTask<String, Void, String>
    {
        InputStream is = null;
        String response = "";
        String returnString="";

        @Override
        protected String doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/getSchedule.php";
            try
            {
                URL url = new URL(url_select);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(facilitator, "UTF-8");
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
                    Type typeTimetable = new TypeToken<ArrayList<Timetable>>()
                    {
                    }.getType();
                    mySchedule = gson.fromJson(response, typeTimetable);
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
                }
                else
                {
                    noData.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    try
                    {
                        recyclerAdapter = new MyScheduleRecyclerAdapter(mySchedule,ManageMySchedule.this);

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
                Toast.makeText(getApplicationContext(), v, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

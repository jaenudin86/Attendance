package att.attendanceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.protocol.ResponseServer;

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
import java.util.Calendar;
import java.util.Locale;

import DBHelper.Holiday;
import DBHelper.Timetable;
import DBHelper.TimetableSlot;
import Helper.HelperMethods;

public class MyTimetable extends ActivityBaseClass
{
    private static final int EDIT_CODE = 2;
    private static final int ADD_CODE = 1;
    private GridView calendarView, calendarDaysHeader;
    private CalendarAdapter adapter;
    Calendar calendar;
    int month,year;
    TextView currentMonth;
    private static final String dateTemplate = "MMMM yyyy";
    String daysOfWeek[]={"MON","TUE","WED","THU","FRI","SAT","SUN"};
    ArrayList<Holiday> holidayArrayList=new ArrayList<Holiday>();
    ArrayList<TimetableSlot> timetableSlots=new ArrayList<TimetableSlot>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_timetable);
        currentMonth=(TextView)findViewById(R.id.tvCurrentMonthDisplay);
        calendarView = (GridView) this.findViewById(R.id.calendarDaysGrid);
        calendarDaysHeader = (GridView) this.findViewById(R.id.calendarDaysHeaderGrid);
        setupHeader();
        setupCalendar();
    }


    public void calendarButtonClick(View view)
    {
        Button btnClicked=(Button)view;
        Intent intent=new Intent(this,ViewTimetable.class);
        intent.putExtra("date",btnClicked.getTag().toString());
        startActivity(intent);
    }
    public void prevMonthClicked(View view)
    {
        if (month <= 1)
        {
            month = 12;
            year--;
        }
        else
        {
            month--;
        }
        setGridCellAdapterToDate(month, year);
    }
    public void nextMonthClicked(View view)
    {
        if (month > 11)
        {
            month = 1;
            year++;
        }
        else
        {
            month++;
        }

        setGridCellAdapterToDate(month, year);
    }
    private void setGridCellAdapterToDate(int month, int year)
    {
        adapter = new CalendarAdapter(this, month, year,holidayArrayList,timetableSlots);
        calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));
        adapter.notifyDataSetChanged(); // update the adapter after month and year change
        currentMonth.setText(HelperMethods.convertDateToFormat(calendar.getTime(), dateTemplate));
        calendarView.setAdapter(adapter);
    }
    void setupHeader()// to setup week header
    {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.week_adapter,daysOfWeek);
        calendarDaysHeader.setAdapter(arrayAdapter);
    }
    void setupCalendar() // setting the whole calendar with adapter
    {
        try
        {
            calendar = Calendar.getInstance(Locale.getDefault());
            month = calendar.get(Calendar.MONTH) + 1;
            year = calendar.get(Calendar.YEAR);

            String formattedDate = HelperMethods.convertDateToFormat(calendar.getTime(), dateTemplate);
            currentMonth.setText(formattedDate);
            new GetHolidays().execute(HelperMethods.getCurrentLoggedinUser(this));
            // Initialised

        }
        catch (Exception ex)
        {
            Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show();
        }
    }
    public void onAddTimetableClick(View view)
    {
        Intent intent=new Intent(this,AddTimetable.class);
        startActivityForResult(intent, ADD_CODE);
    }
    class GetHolidays extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progressDialog;
        InputStream is = null;
        String response = "";
        String returnString="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(MyTimetable.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/getHolidays.php";
            response=getData(url_select,params[0]);
            if(!response.equals("null"))
            {
                Gson gsonHoliday = new Gson();
                Type typeHoliday = new TypeToken<ArrayList<Holiday>>()
                {
                }.getType();
                holidayArrayList = gsonHoliday.fromJson(response, typeHoliday);
            }
            response="";
            url_select = getString(R.string.serviceURL)+"/getTimetable.php";
            response=getData(url_select,params[0]);
            if(!response.equals("null"))
            {
                Gson gsonTimetable = new Gson();
                Type typeTimetable = new TypeToken<ArrayList<TimetableSlot>>()
                {
                }.getType();
                timetableSlots = gsonTimetable.fromJson(response, typeTimetable);
            }
            return returnString;
        }
        private String getData(String url_select,String facilitator)
        {
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
                bufferedReader.close();
                is.close();
                httpUrlConnection.disconnect();
                returnString="ok";
            }
            catch (Exception ex)
            {
                returnString="Exception:"+ex.toString();
            }
            return response;
        }
        protected void onPostExecute(String v)
        {
            super.onPostExecute(v);
            // no exception found on previous call
            if(!v.toLowerCase().contains("exception"))
            {
                adapter = new CalendarAdapter(MyTimetable.this, month, year,holidayArrayList,timetableSlots);
                adapter.notifyDataSetChanged();
                calendarView.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(MyTimetable.this,v,Toast.LENGTH_SHORT).show();
            }
            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}

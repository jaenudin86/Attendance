package att.attendanceapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import DBHelper.Holiday;
import Helper.Helper;

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
        adapter = new CalendarAdapter(this, month, year,holidayArrayList);
        calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));
        adapter.notifyDataSetChanged(); // update the adapter after month and year change
        currentMonth.setText(Helper.convertDateToFormat(calendar.getTime(), dateTemplate));
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

            String formattedDate = Helper.convertDateToFormat(calendar.getTime(), dateTemplate);
            currentMonth.setText(formattedDate);
            new GetHolidays().execute(Helper.getCurrentLoggedinUser(this));
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
            try
            {
                URL url = new URL(url_select);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
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
                Gson gson = new Gson();
                Type typeCourse = new TypeToken<ArrayList<Holiday>>(){}.getType();
                holidayArrayList = gson.fromJson(response, typeCourse);
                bufferedReader.close();
                is.close();
                httpUrlConnection.disconnect();
                returnString="ok";
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
                adapter = new CalendarAdapter(MyTimetable.this, month, year,holidayArrayList);
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

package att.attendanceapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.*;

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
import java.util.Date;
import java.util.List;

import DBHelper.Course;
import DBHelper.DatabaseHandler;
import DBHelper.Holiday;
import Helper.Helper;

public class ManageHolidays extends ActivityBaseClass
{
    ListView holidayList;
    Context context=this;
    ArrayList<Holiday> holidays;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_holidays);
        holidayList=(ListView)findViewById(R.id.lvManageHolidays);
        setupListView();
    }
    void setupListView()
    {
        new GetHolidays().execute("rujoota.shah@gmail.com");
    }

    void changeIntent(int pos)
    {
        HolidayListAdapter adap=(HolidayListAdapter)holidayList.getAdapter();
        Holiday selectedCourse=(Holiday)adap.getItem(pos);
        //Toast.makeText(this,selectedCourse.getCourseCode(),Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this,EditHoliday.class);
        intent.putExtra("selectedHolidayName", selectedCourse.getHolidayName());
        intent.putExtra("selectedHolidayFrom", Helper.convertDate(selectedCourse.getFromDate()));
        intent.putExtra("selectedHolidayTo", Helper.convertDate(selectedCourse.getToDate()));
        intent.putExtra("selectedHoliday", selectedCourse.getId());
        startActivity(intent);
    }
    public void onAddHolidayClick(View view)
    {
        Intent intent=new Intent(this,AddHoliday.class);
        startActivity(intent);
    }
    class GetHolidays extends AsyncTask<String, Void, String>
    {
        InputStream is = null;
        String response = "";
        String returnString="";
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
                holidays = gson.fromJson(response, typeCourse);
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
                // if no data found then
                if(response.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "No holidays found", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        holidayList.setAdapter(new HolidayListAdapter(context,holidays));
                        holidayList.setOnItemClickListener(
                                new AdapterView.OnItemClickListener()
                                {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                                    {
                                        changeIntent(position);
                                    }
                                }
                        );
                    }
                    catch (Exception ex)
                    {
                        //Log.e("Attendance","Problems in ManageCourses:"+ex.getMessage());
                    }
                }
            }
            else
            {
                Toast.makeText(context,v,Toast.LENGTH_SHORT).show();
                //relativeLayout.setVisibility(View.INVISIBLE);
            }
        }
    }
}

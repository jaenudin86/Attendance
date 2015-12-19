package att.attendanceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import DBHelper.Course;
import DBHelper.Holiday;
import Helper.HelperMethods;

public class ManageHolidays extends ActivityBaseClass
{
    ListView holidayList;
    Context context=this;
    ArrayList<Holiday> holidayArrayList;
    HolidayListAdapter holidayListAdapter;
    private static final int EDIT_CODE = 2;
    private static final int ADD_CODE = 1;
    int position;
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
        new GetHolidays().execute(HelperMethods.getCurrentLoggedinUser(this));
    }

    void changeIntent(int pos)
    {
        position=pos;
        //HolidayListAdapter adap=(HolidayListAdapter)holidayList.getAdapter();
        Holiday selectedHoliday=(Holiday)holidayListAdapter.getItem(pos);
        //Toast.makeText(this,selectedCourse.getCourseCode(),Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this,EditHoliday.class);
        intent.putExtra("selectedHolidayName", selectedHoliday.getHolidayName());
        intent.putExtra("selectedHolidayFrom", HelperMethods.convertDateFromSQLToUS(selectedHoliday.getFromDate()));
        intent.putExtra("selectedHolidayTo", HelperMethods.convertDateFromSQLToUS(selectedHoliday.getToDate()));
        intent.putExtra("selectedHoliday", selectedHoliday.getId());
        startActivityForResult(intent, EDIT_CODE);
    }
    public void onAddHolidayClick(View view)
    {
        Intent intent=new Intent(this,AddHoliday.class);
        startActivityForResult(intent, ADD_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CODE && resultCode == Activity.RESULT_OK)
        {
            if(holidayArrayList==null) // if its first being added
            {
                holidayArrayList = new ArrayList<Holiday>();
                holidayArrayList.add((Holiday) data.getSerializableExtra("newHoliday"));
                holidayList.setAdapter(new HolidayListAdapter(context, holidayArrayList));
            }
            else
            {
                holidayArrayList.add((Holiday) data.getSerializableExtra("newHoliday"));
                HolidayListAdapter adapter = (HolidayListAdapter) holidayList.getAdapter();
                adapter.notifyDataSetChanged();
            }
            holidayList.smoothScrollToPosition(holidayArrayList.size()-1);

        }
        else if(requestCode == EDIT_CODE && resultCode == Activity.RESULT_OK)
        {
            holidayArrayList.remove(position);
            holidayArrayList.add(position,(Holiday) data.getSerializableExtra("editHoliday"));
            HolidayListAdapter adapter=(HolidayListAdapter)holidayList.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }
    void setListAdapter()
    {
        holidayListAdapter=new HolidayListAdapter(context, holidayArrayList);
        holidayList.setAdapter(holidayListAdapter);
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
                // if no data found then
                if(response.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "No holidayArrayList found", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        setListAdapter();

                    }
                    catch (Exception ex)
                    {
                        Log.e("Attendance", "Problems in Manage holidays:" + ex.getMessage());
                    }
                }
            }
            else
            {
                Toast.makeText(context,v,Toast.LENGTH_SHORT).show();
            }
        }
    }
}

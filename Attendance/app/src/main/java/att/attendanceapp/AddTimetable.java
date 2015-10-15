package att.attendanceapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Locale;

import DBHelper.Course;
import DBHelper.Holiday;
import Helper.Helper;

public class AddTimetable extends ActivityBaseClass
{
    private int hour;
    private int minute;
    EditText tvFromTime,tvToTime;
    Calendar fromTimeCalendar = Calendar.getInstance();
    Calendar toTimeCalendar = Calendar.getInstance();
    EditText fromDate, toDate;
    String facilitator;
    ArrayList<Course> courseArrayList;
    Spinner courseDropDown;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_timetable);
        tvFromTime=(EditText)findViewById(R.id.tvAddtimetableFromTime);
        tvToTime=(EditText)findViewById(R.id.tvAddtimetableToTime);
        updateEditText(tvFromTime,fromTimeCalendar);
        updateEditText(tvToTime,toTimeCalendar);
        fromDate = (EditText) findViewById(R.id.etAddTimetableFromDate);
        toDate = (EditText) findViewById(R.id.etAddTimetableToDate);
        courseDropDown=(Spinner)findViewById(R.id.spnAddTimetableCourse);
        facilitator= Helper.getCurrentLoggedinUser(this);
        new GetCourses().execute(facilitator);
    }
    public void oneTimeOnlyClicked(View view)
    {

    }
    public void repeatOccuranceClicked(View view)
    {

    }
    public void onFromTimeClicked(View view)
    {
        new TimePickerDialog(this, fromTimePickerListener, fromTimeCalendar.get(Calendar.HOUR_OF_DAY), fromTimeCalendar.get(Calendar.MINUTE), false).show();
    }
    public void onToTimeClicked(View view)
    {
        new TimePickerDialog(this, toTimePickerListener, toTimeCalendar.get(Calendar.HOUR_OF_DAY), toTimeCalendar.get(Calendar.MINUTE), false).show();
    }
    private TimePickerDialog.OnTimeSetListener fromTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            fromTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            fromTimeCalendar.set(Calendar.MINUTE, minutes);
            updateEditText(tvFromTime,fromTimeCalendar);
        }

    };
    private TimePickerDialog.OnTimeSetListener toTimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            toTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            toTimeCalendar.set(Calendar.MINUTE, minutes);
            updateEditText(tvToTime,toTimeCalendar);
        }

    };
    private void updateEditText(TextView txt,Calendar calendar)
    {
        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt.setText(sdf.format(calendar.getTime()));
    }
    Calendar fromDateCalendar = Calendar.getInstance();
    Calendar toDateCalendar = Calendar.getInstance();
    public void setFromDate(View view)
    {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        new DatePickerDialog(this, fromDateDialog, fromDateCalendar.get(Calendar.YEAR), fromDateCalendar.get(Calendar.MONTH),
                fromDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setToDate(View view)
    {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        new DatePickerDialog(this, toDateDialog, toDateCalendar.get(Calendar.YEAR), toDateCalendar.get(Calendar.MONTH),
                toDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateEditTextDate(EditText txt,Calendar calendar)
    {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt.setText(sdf.format(calendar.getTime()));
    }

    DatePickerDialog.OnDateSetListener fromDateDialog = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            fromDateCalendar.set(Calendar.YEAR, year);
            fromDateCalendar.set(Calendar.MONTH, monthOfYear);
            fromDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEditTextDate(fromDate, fromDateCalendar);
        }
    };
    DatePickerDialog.OnDateSetListener toDateDialog = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            toDateCalendar.set(Calendar.YEAR, year);
            toDateCalendar.set(Calendar.MONTH, monthOfYear);
            toDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEditTextDate(toDate, toDateCalendar);
        }
    };
    class GetCourses extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progressDialog;
        InputStream is = null;
        String response = "";
        String returnString="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(AddTimetable.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/getCourses.php";
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
                Type typeCourse = new TypeToken<ArrayList<Course>>(){}.getType();
                courseArrayList = gson.fromJson(response, typeCourse);
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

                        String[] displayCourseCodes = new String[courseArrayList.size()];
                        for (int i=0;i<courseArrayList.size();i++)
                        {
                            displayCourseCodes[i]=courseArrayList.get(i).getCourseCode();
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddTimetable.this, android.R.layout.simple_spinner_item, displayCourseCodes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        courseDropDown.setAdapter(adapter);
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

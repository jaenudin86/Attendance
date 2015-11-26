package att.attendanceapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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
import java.util.GregorianCalendar;
import java.util.Locale;

import DBHelper.Course;
import DBHelper.Timetable;
import Helper.HelperMethods;

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
    RadioButton radioOneTime,radioOccur;
    CheckBox daysCheckboxes[]=new CheckBox[7];
    Timetable timetable;
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
        facilitator= HelperMethods.getCurrentLoggedinUser(this);
        radioOneTime=(RadioButton)findViewById(R.id.rbtnAddTimetableOneTime);
        radioOccur=(RadioButton)findViewById(R.id.rbtnAddTimetableOccuresEvery);
        setupDaysArray();
        new GetCourses().execute(facilitator);
    }
    void setupDaysArray()
    {
        daysCheckboxes[0]=(CheckBox)findViewById(R.id.chkAddTimetableMon);
        daysCheckboxes[1]=(CheckBox)findViewById(R.id.chkAddTimetableTue);
        daysCheckboxes[2]=(CheckBox)findViewById(R.id.chkAddTimetableWed);
        daysCheckboxes[3]=(CheckBox)findViewById(R.id.chkAddTimetableThu);
        daysCheckboxes[4]=(CheckBox)findViewById(R.id.chkAddTimetableFri);
        daysCheckboxes[5]=(CheckBox)findViewById(R.id.chkAddTimetableSat);
        daysCheckboxes[6]=(CheckBox)findViewById(R.id.chkAddTimetableSun);
    }
    public void oneTimeOnlyClicked(View view)
    {
        for(int i=0;i<7;i++)
        {
            daysCheckboxes[i].setEnabled(false);
        }
    }
    public void repeatOccuranceClicked(View view)
    {
        for(int i=0;i<7;i++)
        {
            daysCheckboxes[i].setEnabled(true);
        }
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
        String myFormat = "HH:mm";
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
    public void onOkClick(View view)
    {

        String days="";
        for(int i=0;i<daysCheckboxes.length;i++)
        {
            if(daysCheckboxes[i].isChecked())
            {
                days += daysCheckboxes[i].getTag() + ",";
            }
        }
        if(days.length()>0)
            days=days.substring(0,days.length()-1);
        String isrecurring=radioOccur.isChecked()==true?"yes":"no";
        timetable=new Timetable();
        timetable.setCourseCode(courseDropDown.getSelectedItem().toString());
        timetable.setFacilitatorId(facilitator);
        timetable.setStartTime(tvFromTime.getText().toString());
        timetable.setEndTime(tvToTime.getText().toString());
        timetable.setStartDate(HelperMethods.convertDateFromUSToSQL(fromDate.getText().toString()));
        timetable.setEndDate(HelperMethods.convertDateFromUSToSQL(toDate.getText().toString()));
        timetable.setIsRecurring(isrecurring);
        timetable.setRecurringDays(days);
        new AddToTimetable().execute(timetable);
    }


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
    class AddToTimetable extends AsyncTask<Timetable, Void, String>
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
        protected String doInBackground(Timetable... params)
        {
            String url_select = getString(R.string.serviceURL)+"/addTimetable.php";
            try
            {
                URL url = new URL(url_select);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("facilitator_id", "UTF-8") + "=" + URLEncoder.encode(facilitator, "UTF-8")+"&"+
                        URLEncoder.encode("course_code", "UTF-8") + "=" + URLEncoder.encode(params[0].getCourseCode(), "UTF-8")+"&"+
                        URLEncoder.encode("is_recurring", "UTF-8") + "=" + URLEncoder.encode(params[0].getIsRecurring(), "UTF-8")+"&"+
                        URLEncoder.encode("recurring_days", "UTF-8") + "=" + URLEncoder.encode(params[0].getRecurringDays(), "UTF-8")+"&"+
                        URLEncoder.encode("start_time", "UTF-8") + "=" + URLEncoder.encode(params[0].getStartTime(), "UTF-8")+"&"+
                        URLEncoder.encode("end_time", "UTF-8") + "=" + URLEncoder.encode(params[0].getEndTime(), "UTF-8")+"&"+
                        URLEncoder.encode("start_date", "UTF-8") + "=" + URLEncoder.encode(params[0].getStartDate(), "UTF-8")+"&"+
                        URLEncoder.encode("end_date", "UTF-8") + "=" + URLEncoder.encode(params[0].getEndDate(), "UTF-8");
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
            return returnString;
        }

        protected void onPostExecute(String v)
        {
            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if(v.contains("Exception"))
            {
                Toast.makeText(AddTimetable.this,v,Toast.LENGTH_LONG).show();
            }
            else
            {
                //Toast.makeText(AddTimetable.this,response,Toast.LENGTH_LONG).show();
            }
            timetable.setId(response);
            Intent intent = getIntent(); //gets the intent that called this intent
            intent.putExtra("newTimetable", timetable);
            setResult(Activity.RESULT_OK, intent);
            //setAlarm();
            //cancelAlarm();
            finish();
        }
    }
    // for future use, to set notification when time arrives
    public void setAlarm()
    {
        long alerttime=new GregorianCalendar().getTimeInMillis()+5*1000;
        Intent alertIntent=new Intent(this,AlarmRcvr.class);
        AlarmManager mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Calendar alarmCal=Calendar.getInstance();
        // sets alarm every wed, at 2.24am
        alarmCal.set(Calendar.DAY_OF_WEEK, 4);
        alarmCal.set(Calendar.HOUR_OF_DAY, 2);
        alarmCal.set(Calendar.MINUTE,24);
        alarmCal.set(Calendar.SECOND, 0);
        alarmCal.set(Calendar.MILLISECOND, 0);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP,
                alarmCal.getTimeInMillis(), 1 * 60 * 60 * 1000, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }
    public void cancelAlarm()
    {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent alertIntent=new Intent(this,AlarmRcvr.class);
        PendingIntent pendingUpdateIntent = PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel alarms
        try {
            alarmManager.cancel(pendingUpdateIntent);
            Toast.makeText(this,"alarm cancelled",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.d("AttendanceHelper",e.toString());
        }

    }
}

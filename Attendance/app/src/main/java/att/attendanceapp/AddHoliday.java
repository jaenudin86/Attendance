package att.attendanceapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import Helper.Helper;

public class AddHoliday extends ActivityBaseClass
{

    EditText holidayFromDate, holidayToDate;
    EditText holidayName;
    String facilitator;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_holiday);
        holidayFromDate = (EditText) findViewById(R.id.etHolidayFromDate);
        holidayToDate = (EditText) findViewById(R.id.etHolidayToDate);
        holidayName =(EditText)findViewById(R.id.etHolidayName);
        facilitator= Helper.getCurrentLoggedinUser(this);
    }
    Calendar fromDateCalendar = Calendar.getInstance();
    Calendar toDateCalendar = Calendar.getInstance();
    public void setFromDate(View view)
    {
        new DatePickerDialog(this, fromDateDialog, fromDateCalendar.get(Calendar.YEAR), fromDateCalendar.get(Calendar.MONTH),
                fromDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setToDate(View view)
    {
        new DatePickerDialog(this, toDateDialog, toDateCalendar.get(Calendar.YEAR), toDateCalendar.get(Calendar.MONTH),
                toDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateEditText(EditText txt,Calendar calendar)
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
            updateEditText(holidayFromDate,fromDateCalendar);
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
            updateEditText(holidayToDate,toDateCalendar);
        }
    };
    public void onOkClick(View view)
    {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String fromDate =sdf.format(fromDateCalendar.getTime());
        String toDate =sdf.format(toDateCalendar.getTime());
        new HolidayTask().execute(holidayName.getText().toString(),fromDate,toDate);
    }
    class HolidayTask extends AsyncTask<String, String, Void>
    {
        //private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        InputStream is = null;
        String result = "";
        String response = "";
        @Override
        protected Void doInBackground(String... params) {
            String url_select = getString(R.string.serviceURL)+"/addHoliday.php";
            try
            {
                URL url = new URL(url_select);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("holiday_name", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")+"&"+
                        URLEncoder.encode("from_date", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8")+"&"+
                        URLEncoder.encode("to_date", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8")+"&"+
                        URLEncoder.encode("facilitator_id", "UTF-8") + "=" + URLEncoder.encode(facilitator, "UTF-8");
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
            }
            catch (Exception ex){}
            return null;
        }

        protected void onPostExecute(Void v) {
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
            finish();
            Intent intent=new Intent(getApplicationContext(),ManageHolidays.class);
            startActivity(intent);
        }
    }
}

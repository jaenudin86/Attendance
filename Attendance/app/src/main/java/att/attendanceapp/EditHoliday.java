package att.attendanceapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

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

import DBHelper.Holiday;
import Helper.HelperMethods;

public class EditHoliday extends ActivityBaseClass
{
    String facilitator;
    Context context=this;
    EditText name,from,to;
    String id;
    Holiday holiday;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_holiday);
        facilitator= HelperMethods.getCurrentLoggedinUser(this);
        name=(EditText)findViewById(R.id.etEditHolidayName);
        from=(EditText)findViewById(R.id.etEditHolidayFromDate);
        to=(EditText)findViewById(R.id.etEditHolidayToDate);
        Bundle holidayData=getIntent().getExtras();
        if(holidayData!=null)
        {
            name.setText(holidayData.getString("selectedHolidayName"));
            from.setText(holidayData.getString("selectedHolidayFrom"));
            to.setText(holidayData.getString("selectedHolidayTo"));
            id=holidayData.getString("selectedHoliday");
        }
    }
    Calendar fromDateCalendar = Calendar.getInstance();
    Calendar toDateCalendar = Calendar.getInstance();
    public void setFromDate(View view)
    {
        String dt=from.getText().toString();
        int yr=Integer.parseInt(dt.split("/")[2]);
        int month=Integer.parseInt(dt.split("/")[0])-1; //month start from 0
        int day=Integer.parseInt(dt.split("/")[1]);
        fromDateCalendar.set(yr,month,day);

        new DatePickerDialog(this, fromDateDialog, fromDateCalendar.get(Calendar.YEAR), fromDateCalendar.get(Calendar.MONTH),
                fromDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setToDate(View view)
    {
        String dt=to.getText().toString();
        int yr=Integer.parseInt(dt.split("/")[2]);
        int month=Integer.parseInt(dt.split("/")[0])-1;
        int day=Integer.parseInt(dt.split("/")[1]);
        toDateCalendar.set(yr,month,day);
        new DatePickerDialog(this, toDateDialog, toDateCalendar.get(Calendar.YEAR), toDateCalendar.get(Calendar.MONTH),
                toDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    DatePickerDialog.OnDateSetListener fromDateDialog = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            fromDateCalendar.set(Calendar.YEAR, year);
            fromDateCalendar.set(Calendar.MONTH, monthOfYear);
            fromDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateEditText(from,fromDateCalendar);
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
            updateEditText(to,toDateCalendar);
        }
    };
    private void updateEditText(EditText txt,Calendar calendar)
    {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt.setText(sdf.format(calendar.getTime()));
    }
    public void onOkClick(View view)
    {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String fromDate =sdf.format(fromDateCalendar.getTime());
        String toDate =sdf.format(toDateCalendar.getTime());
        holiday=new Holiday(name.getText().toString(), fromDate, toDate,facilitator,id);
        new EditHolidayTask().execute(holiday);
    }
    public void onCancelClick(View view)
    {
        finish();
    }
    class EditHolidayTask extends AsyncTask<Holiday, String, Void>
    {
        //private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String serviceURL;
        InputStream is = null;
        String result = "";
        String response = "";
        @Override
        protected Void doInBackground(Holiday... params) {

            try
            {
                serviceURL=getString(R.string.serviceURL)+"/editHoliday.php";
                URL url = new URL(serviceURL);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("holiday_name", "UTF-8") + "=" + URLEncoder.encode(params[0].getHolidayName(), "UTF-8")+"&"+
                        URLEncoder.encode("holiday_from", "UTF-8") + "=" + URLEncoder.encode(params[0].getFromDate(), "UTF-8")+"&"+
                        URLEncoder.encode("holiday_to", "UTF-8") + "=" + URLEncoder.encode(params[0].getToDate(), "UTF-8")+"&"+
                        URLEncoder.encode("facilitator_id", "UTF-8") + "=" + URLEncoder.encode(params[0].getFacilitatorId(), "UTF-8")+"&"+
                        URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(params[0].getId(), "UTF-8");;
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
            Intent intent = getIntent(); //gets the intent that called this intent
            intent.putExtra("editHoliday", holiday);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}

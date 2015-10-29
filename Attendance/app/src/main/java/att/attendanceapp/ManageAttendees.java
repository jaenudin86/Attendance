package att.attendanceapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import DBHelper.Attendee;
import DBHelper.Course;
import DBHelper.Holiday;
import Helper.FileUtils;
import Helper.HelperMethods;

public class ManageAttendees extends ActivityBaseClass
{
    private static final int FILE_SELECT_CODE = 0;
    String courseCode="";

    ArrayList<Attendee> attendeeList=new ArrayList<Attendee>();
    private RecyclerView attendeesView;
    private MyScheduleRecyclerAdapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_manage_attendees);
            Bundle data = getIntent().getExtras();
            if (data != null)
                courseCode = data.getString(getString(R.string.bundleKeyCourseCode));

            attendeesView = (RecyclerView) findViewById(R.id.rviewManageAttendees);
            attendeesView.setHasFixedSize(true);
            attendeesView.setItemAnimator(new DefaultItemAnimator());
            recyclerLayoutManager = new LinearLayoutManager(this);
            attendeesView.setLayoutManager(recyclerLayoutManager);
            new GetAttendeesForThisCourse().execute(courseCode);
        }
        catch (Exception ex)
        {
            Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void importClick(View view)
    {
        FileUtils.showFileChooser(this, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK)
                {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    try
                    {
                        String path = FileUtils.getPath(this, uri);
                            // making comma separated attendees and courses list
                        if(path==null || path.isEmpty())
                            Toast.makeText(this, "Unable retrieve file.Make sure your txt file is not currputed", Toast.LENGTH_LONG).show();
                        else
                            new AddAttendee().execute(path);
                    }
                    catch(Exception ex)
                    {
                        Toast.makeText(this, "Unable retrieve file.", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    class AddAttendee extends AsyncTask<String, String, String>
    {
        private ProgressDialog progressDialog;
        InputStream is = null;
        String response = "";
        String returnString="";
        String attendees="",courseCodes="";
        private void readFile(String path)
        {
            String fileContent=FileUtils.readFile(path, "txt");
            if(fileContent.contains("extension"))
                returnString="Wrong file extension";
            else if(fileContent.contains("Exception") || fileContent.isEmpty())
            {
                returnString=fileContent;
            }
            else
            {
                String[] attendeeCourses = fileContent.split("\n");
                for (String item : attendeeCourses)
                {
                    attendees += item.split(",")[0] + ",";
                    courseCodes += courseCode + ",";
                }
                attendees = attendees.substring(0, attendees.length() - 1);
                courseCodes = courseCodes.substring(0, courseCodes.length() - 1);
            }
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(ManageAttendees.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String url_select = getString(R.string.serviceURL)+"/importAttendee.php";
            try
            {
                readFile(params[0]);
                if(attendees.isEmpty() || courseCodes.isEmpty())
                {

                }
                else
                {
                    URL url = new URL(url_select);
                    HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
                    httpUrlConnection.setRequestMethod("POST");
                    httpUrlConnection.setDoOutput(true);
                    OutputStream outputStream = httpUrlConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String data = URLEncoder.encode("attendees", "UTF-8") + "=" + URLEncoder.encode(attendees, "UTF-8") + "&" +
                            URLEncoder.encode("course_codes", "UTF-8") + "=" + URLEncoder.encode(courseCodes, "UTF-8");
                    ;
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    is = httpUrlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null)
                    {
                        response += line;
                    }
                    bufferedReader.close();
                    is.close();
                    httpUrlConnection.disconnect();
                    returnString = response;
                }
            }
            catch (Exception ex){
                returnString="Exception:"+ex.toString();
            }
            return returnString;
        }

        protected void onPostExecute(String v) {
            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            super.onPostExecute(v);
            if(v.contains("extension"))
                Toast.makeText(ManageAttendees.this,"This extension is not supported. Please upload only txt file.",Toast.LENGTH_LONG).show();
            else if(v.contains("Exception"))
                Toast.makeText(ManageAttendees.this,"Error while importing data. Please try again",Toast.LENGTH_LONG).show();
            else if(v.contains("Error"))
                Toast.makeText(ManageAttendees.this,"Your data is imported but all attendees may not have been uploaded",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(ManageAttendees.this,"Successfully imported data",Toast.LENGTH_LONG).show();
            new GetAttendeesForThisCourse().execute(courseCode);
        }
    }
    class GetAttendeesForThisCourse extends AsyncTask<String, Void, String>
    {
        private ProgressDialog progressDialog;
        InputStream is = null;
        String response = "";
        String returnString="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(ManageAttendees.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/getAttendeesForCourse.php";
            try
            {
                URL url = new URL(url_select);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data =URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(HelperMethods.getCurrentLoggedinUser(ManageAttendees.this), "UTF-8")+"&"+
                        URLEncoder.encode("course_code", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
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
                if(!response.isEmpty() && !response.equals("null"))
                {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Attendee>>()
                    {
                    }.getType();
                    attendeeList = gson.fromJson(response, type);
                }
                else
                    response="";
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
                        attendeesView.setAdapter(new AttendeeRecyclerAdapter(attendeeList, context, courseCode));
                    }
                    catch (Exception ex)
                    {
                        Log.e("Attendance", "Problems in Manage Attendees:" + ex.getMessage());
                    }
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),v,Toast.LENGTH_SHORT).show();
            }
        }
    }
}

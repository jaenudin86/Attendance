package att.attendanceapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import DBHelper.Course;

import Helper.Helper;

public class AddCourse extends ActivityBaseClass
{
    EditText courseCode,courseName,description;
    Context context=this;
    String facilitator;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        courseCode=(EditText)findViewById(R.id.etAddCourseCourseCode);
        courseName=(EditText)findViewById(R.id.etAddCourseCourseName);
        description=(EditText)findViewById(R.id.etAddCourseDescription);
        facilitator=Helper.getCurrentLoggedinUser(this);

    }
    public void onOkClick(View view)
    {
        Course course=new Course(courseCode.getText().toString(),courseName.getText().toString(),description.getText().toString(),facilitator);
        course.shouldAnimateOnAdd =true;
        new CourseTask().execute(course);
    }
    class CourseTask extends AsyncTask<Course, String, Void>
    {
        //private ProgressDialog progressDialog = new ProgressDialog(AddCourse.this);
        String serviceURL;
        InputStream is = null;
        String result = "";
        String response = "";
        Course course;
        @Override
        protected Void doInBackground(Course... params) {

            try
            {
                course=(Course)params[0];
                serviceURL=getString(R.string.serviceURL)+"/addCourse.php";
                URL url = new URL(serviceURL);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("course_code", "UTF-8") + "=" + URLEncoder.encode(course.getCourseCode(), "UTF-8")+"&"+
                        URLEncoder.encode("course_name", "UTF-8") + "=" + URLEncoder.encode(course.getCoursename(), "UTF-8")+"&"+
                        URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(course.getCourseDescription(), "UTF-8")+"&"+
                        URLEncoder.encode("facilitator_id", "UTF-8") + "=" + URLEncoder.encode(course.getFacilitatorId(), "UTF-8");
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
            intent.putExtra("newCourse", course);
            setResult(Activity.RESULT_OK, intent);
            finish();
            //Intent intent=new Intent(context,ManageCourses.class);
            //startActivity(intent);
        }
    }
}

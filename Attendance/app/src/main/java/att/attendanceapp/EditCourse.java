package att.attendanceapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

import Helper.Helper;

public class EditCourse extends ActivityBaseClass
{
    String facilitator,courseCode;
    Context context=this;
    TextView code;
    EditText courseName,description;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        facilitator= Helper.getCurrentLoggedinUser(this);
        code=(TextView)findViewById(R.id.tvEditCourseCode);
        courseName=(EditText)findViewById(R.id.etEditCourseCourseName);
        description=(EditText)findViewById(R.id.etEditCourseDescription);
        Bundle courseData=getIntent().getExtras();
        if(courseData!=null)
        {
            code.setText(courseData.getString("selectedCourseCode"));
            courseName.setText(courseData.getString("selectedCourseName"));
            description.setText(courseData.getString("selectedCourseDescription"));
        }


    }
    public void onOkClick(View view)
    {
        new CourseTask().execute(code.getText().toString(),courseName.getText().toString(),description.getText().toString());
    }
    class CourseTask extends AsyncTask<String, String, Void>
    {
        //private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String serviceURL;
        InputStream is = null;
        String result = "";
        String response = "";
        @Override
        protected Void doInBackground(String... params) {

            try
            {
                serviceURL=getString(R.string.serviceURL)+"/editCourse.php";
                URL url = new URL(serviceURL);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("course_code", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")+"&"+
                        URLEncoder.encode("course_name", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8")+"&"+
                        URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8")+"&"+
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
            Intent intent=new Intent(context,ManageCourses.class);
            startActivity(intent);
        }
    }

}

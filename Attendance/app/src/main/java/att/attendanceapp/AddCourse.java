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

public class AddCourse extends ActivityBaseClass
{
    EditText courseCode,courseName,description;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        courseCode=(EditText)findViewById(R.id.etAddCourseCourseCode);
        courseName=(EditText)findViewById(R.id.etAddCourseCourseName);
        description=(EditText)findViewById(R.id.etAddCourseDescription);
    }
    public void onOkClick(View view)
    {
        new CourseTask().execute(courseCode.getText().toString(),courseName.getText().toString(),description.getText().toString());
    }
    class CourseTask extends AsyncTask<String, String, Void>
    {
        //private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        InputStream is = null;
        String result = "";
        String response = "";
        @Override
        protected Void doInBackground(String... params) {
            String url_select = getString(R.string.serviceURL)+"/addCourse.php";
            try
            {
                URL url = new URL(url_select);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("course_code", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")+"&"+
                        URLEncoder.encode("course_name", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8")+"&"+
                        URLEncoder.encode("description", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8");
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

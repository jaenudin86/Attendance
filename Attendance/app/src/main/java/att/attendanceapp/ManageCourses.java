package att.attendanceapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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
import Helper.HelperMethods;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
public class ManageCourses extends ActivityBaseClass
{
    ListView courseList;
    Context context=this;
    ArrayList<Course> courseArrayList;
    private static final int EDIT_CODE = 2;
    private static final int ADD_CODE = 1;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_courses);
        courseList=(ListView)findViewById(R.id.lvManageCourses);
        setupListView();
    }
    void setupListView()
    {
        new GetCourses().execute(HelperMethods.getCurrentLoggedinUser(this));
    }
    public void onAddCourseClick(View view)
    {
        Intent intent =new Intent(this,AddCourse.class);
        startActivityForResult(intent, ADD_CODE);
        //finish();
    }
    void changeIntent(int pos)
    {
        position=pos;
        CourseListAdapter adap=(CourseListAdapter)courseList.getAdapter();
        Course selectedCourse=(Course)adap.getItem(pos);
        //Toast.makeText(this,selectedCourse.getCourseCode(),Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this,EditCourse.class);
        intent.putExtra("selectedCourseCode", selectedCourse.getCourseCode());
        intent.putExtra("selectedCourseName", selectedCourse.getCoursename());
        intent.putExtra("selectedCourseDescription", selectedCourse.getCourseDescription());
        startActivityForResult(intent, EDIT_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CODE && resultCode == Activity.RESULT_OK)
        {
            if(courseArrayList==null) // if its first course being added
            {
                courseArrayList = new ArrayList<Course>();
                courseArrayList.add((Course) data.getSerializableExtra("newCourse"));
                courseList.setAdapter(new CourseListAdapter(context, courseArrayList));
            }
            else
            {
                courseArrayList.add((Course) data.getSerializableExtra("newCourse"));
                CourseListAdapter adapter = (CourseListAdapter) courseList.getAdapter();
                adapter.notifyDataSetChanged();
            }
            courseList.smoothScrollToPosition(courseArrayList.size() - 1);
        }
        else if(requestCode == EDIT_CODE && resultCode == Activity.RESULT_OK)
        {
            courseArrayList.remove(position);
            courseArrayList.add(position,(Course) data.getSerializableExtra("editCourse"));
            CourseListAdapter adapter=(CourseListAdapter)courseList.getAdapter();
            adapter.notifyDataSetChanged();
        }
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
            progressDialog=new ProgressDialog(ManageCourses.this);
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
                    Toast.makeText(getApplicationContext(),"No courseArrayList found",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        courseList.setAdapter(new CourseListAdapter(context, courseArrayList));
                        courseList.setOnItemClickListener(
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
                        Log.e("Attendance","Problems in ManageCourses:"+ex.getMessage());
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

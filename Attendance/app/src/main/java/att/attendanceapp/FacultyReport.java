package att.attendanceapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import DBHelper.AttendanceReport;
import DBHelper.Attendee;
import Helper.FileUtils;
import Helper.HelperMethods;

public class FacultyReport extends ActivityBaseClass
{
    private RecyclerView recyclerView;
    Context context=this;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    ArrayList<AttendanceReport> reportItems;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_report);
        recyclerView = (RecyclerView) findViewById(R.id.rviewFacultyReport);
        recyclerView.setHasFixedSize(true);
        recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        new GetAttendanceReport().execute();
    }
    public void onExportClick(View view)
    {
        String fileContent="";
        for(AttendanceReport item:reportItems)
        {
            fileContent+=item.getEmailId()+","+item.getAttendeeNumber()+","+item.getName()+","+item.getCourseCode()+","+item.getAbsentOn();
            fileContent+= "\r\n";
        }
        String response=FileUtils.writeFile(fileContent,this);
        if(!response.contains("Exception"))
        {
            Toast.makeText(this, "File was created at:" + response, Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.setAction(android.content.Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(response)), "text/plain");
            startActivity(i);
        }
        else
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
    }
    class GetAttendanceReport extends AsyncTask<String, Void, Void>
    {
        private ProgressDialog progressDialog;
        InputStream is = null;
        String response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(FacultyReport.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/getAttendanceReport.php";
            try
            {
                String keys[] = {"user_id"};
                String values[] = {HelperMethods.getCurrentLoggedinUser(FacultyReport.this)};
                response = HelperMethods.getResponse(url_select, keys, values);
                if (!response.isEmpty() && !response.equals("null"))
                {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<AttendanceReport>>()
                    {
                    }.getType();
                    reportItems = gson.fromJson(response, type);
                }
                else
                    response = "";
            }
            catch(Exception ex)
            {
                response="Exception:"+ex.toString();
            }
            return null;
        }

        protected void onPostExecute(Void v)
        {
            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            super.onPostExecute(v);
            // no exception found on previous call
            if(!response.contains("exception"))
            {
                // if no data found then
                if(response.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        FacultyReportAdapter adapter=new FacultyReportAdapter(reportItems, context);
                        recyclerView.setAdapter(adapter);
                    }
                    catch (Exception ex)
                    {
                        Log.e("Attendance", "Problems in Report:" + ex.getMessage());
                    }
                }
            }
            else
            {
                Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
            }

        }
    }
}

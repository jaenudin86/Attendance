package att.attendanceapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import DBHelper.Attendee;
import DBHelper.Course;
import DBHelper.Holiday;
import DBHelper.Timetable;
import Helper.AdapterInterface;
import Helper.DialogUtils;
import Helper.HelperMethods;
import Helper.NFCUtils;

public class FillAttendanceByFaculty extends ActivityBaseClass
{
    private RecyclerView attendeesView;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    Context context=this;
    ArrayList<Attendee> attendeeList=new ArrayList<Attendee>();
    String courseCode="";
    String attendanceId="";
    NfcAdapter nfcAdapter;
    FillAttendanceAdapter adapter;
    CheckBox allPresent;
    TextView tvCourseCode,tvCourseTimings,tvCourseDate;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_attendance_by_faculty);
        tvCourseCode=(TextView)findViewById(R.id.tvFillAttendanceCourseCode);
        tvCourseTimings=(TextView)findViewById(R.id.tvFillAttendanceTimings);
        tvCourseDate=(TextView)findViewById(R.id.tvFillAttendanceDate);
        Bundle data = getIntent().getExtras();
        if (data != null)
        {
            courseCode = data.getString(getString(R.string.bundleKeyCourseCode));
            tvCourseCode.setText(courseCode);
            attendanceId=data.getString(getString(R.string.bundleKeyAttendanceId));
            String timings=data.getString(getString(R.string.bundleKeyTimings));
            tvCourseTimings.setText(timings);
            String date=data.getString(getString(R.string.bundleKeyDate));
            tvCourseDate.setText(date);
        }
        attendeesView = (RecyclerView) findViewById(R.id.rviewFillAttendance);
        attendeesView.setHasFixedSize(true);
        attendeesView.setItemAnimator(new DefaultItemAnimator());
        recyclerLayoutManager = new LinearLayoutManager(this);
        attendeesView.setLayoutManager(recyclerLayoutManager);
        allPresent=(CheckBox)findViewById(R.id.chkFillAttendanceAllPresent);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        new GetAttendeesForThisCourse().execute(courseCode);
        DialogUtils.displayInfoDialog(this, "NFC tag scan", "Please scan the NFC tag");
    }
    @Override
    protected void onResume()
    {
        Intent intent=new Intent(this,FillAttendanceByFaculty.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent=PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter[] intentFilter=new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        nfcAdapter.disableForegroundDispatch(this);
        super.onPause();
    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
        {
            Toast.makeText(this, "NFC intent received", Toast.LENGTH_SHORT).show();
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            int randomNumber=HelperMethods.generateRandom(1000,9999);
            new UpdateRandomCode().execute(attendanceId, String.valueOf(randomNumber));
            new UpdateAttendance().execute(attendanceId);
            String msg=NFCUtils.write("attendanceId:"+attendanceId+",number:"+randomNumber, tag,this);
            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
            DialogUtils.cancelDialog();
        }
        super.onNewIntent(intent);
    }
    public void refreshClick(View view)
    {
        new RefreshAttendance().execute(attendanceId);
    }
    public void generateCodeClick(View view)
    {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        dialog.setTitle("Your random code is:");
        RelativeLayout inflatedView = (RelativeLayout) View.inflate(this, R.layout.random_code_popup, null);
        dialog.setContentView(inflatedView);
        int randomNumber=HelperMethods.generateRandom(1000,9999);

        final TextView tvRandom=(TextView)inflatedView.findViewById(R.id.tvRandomCode);
        tvRandom.setText(String.valueOf(randomNumber));
        new UpdateRandomCode().execute(attendanceId, String.valueOf(randomNumber));
        new UpdateAttendance().execute(attendanceId);
        Button btnOk=(Button)inflatedView.findViewById(R.id.btnRandomCodeOk);
        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        ImageButton btnRefresh=(ImageButton)inflatedView.findViewById(R.id.ibtnRandomCodeRefresh);
        btnRefresh.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int randomNumber=HelperMethods.generateRandom(1000,9999);
                tvRandom.setText(String.valueOf(randomNumber));
                new UpdateRandomCode().execute(attendanceId,String.valueOf(randomNumber));
            }
        });

        dialog.show();
    }
    public void setRadioStatus(RadioButton[] radioButtons)
    {
        for(RadioButton btn:radioButtons)
        {
            btn.setChecked(true);
        }
    }
    public void onSaveAttendanceClick(View view)
    {
        String rollnos="";
        for(Attendee att:adapter.absentees)
        {
            rollnos=att.getEmailId()+",";
        }
        if(!rollnos.isEmpty())
            rollnos=rollnos.substring(0,rollnos.length()-1);
        new SubmitAttendance().execute(rollnos);
    }

    class UpdateRandomCode extends AsyncTask<String, Void, String>
    {
        InputStream is = null;
        String response = "";
        String returnString="";

        @Override
        protected String doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/generateCode.php";
            try
            {
                String keys[] = {"attendance_id", "generated_code"};
                String values[] = {params[0], params[1]};
                response = HelperMethods.getResponse(url_select, keys, values);
                if (!response.isEmpty() && !response.equals("null"))
                {

                }
                else
                    response = "";
                returnString="ok";
            }
            catch(Exception ex)
            {
                returnString="Exception:"+ex.toString();
            }
            return returnString;
        }

        protected void onPostExecute(String v)
        {
            // no exception found on previous call
            if(!v.toLowerCase().contains("exception"))
            {

            }
            else
            {
                Toast.makeText(getApplicationContext(),v,Toast.LENGTH_SHORT).show();
            }
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
            progressDialog=new ProgressDialog(FillAttendanceByFaculty.this);
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
                String keys[] = {"user_id", "course_code"};
                String values[] = {HelperMethods.getCurrentLoggedinUser(FillAttendanceByFaculty.this), params[0]};
                response = HelperMethods.getResponse(url_select, keys, values);
                if (!response.isEmpty() && !response.equals("null"))
                {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Attendee>>()
                    {
                    }.getType();
                    attendeeList = gson.fromJson(response, type);
                }
                else
                    response = "";
                returnString="ok";
            }
            catch(Exception ex)
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
                        adapter=new FillAttendanceAdapter(attendeeList, context, courseCode);
                        attendeesView.setAdapter(adapter);
                        allPresent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                        {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                            {
                                adapter = new FillAttendanceAdapter(attendeeList, context, courseCode, isChecked);
                                attendeesView.setAdapter(adapter);
                            }
                        });

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

    class SubmitAttendance extends AsyncTask<String, String, Void>
    {
        //private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String response="";

        @Override
        protected Void doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL) + "/submitAttendance.php";
            String keys[]={"attendance_id","absent_ids"};
            String values[]={attendanceId,params[0]};
            response=HelperMethods.getResponse(url_select,keys,values);
            return null;
        }
        protected void onPostExecute(Void v)
        {
            if(!response.isEmpty())
            {
                if (response.contains("Exception"))
                {
                    Toast.makeText(FillAttendanceByFaculty.this,"Attendance not fully submitted. Please try later."+response,Toast.LENGTH_LONG).show();
                    //Log.e("Attendance", response);
                }
                else if (response != "null")
                {
                    Toast.makeText(FillAttendanceByFaculty.this, response, Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }
    // this class is used for updating temporary table
    class UpdateAttendance extends AsyncTask<String, String, Void>
    {
        //private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String response="";
       // private ProgressDialog progressDialog;
        InputStream is = null;

        @Override
        protected Void doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL) + "/updateTempAttendance.php";
            String keys[]={"attendance_id"};
            String values[]={params[0]};
            response=HelperMethods.getResponse(url_select,keys,values);

            return null;
        }
        protected void onPostExecute(Void v)
        {
            /*if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }*/
            if(!response.isEmpty() && !response.equals("null"))
            {
                if (response.contains("Exception"))
                {
                    Toast.makeText(FillAttendanceByFaculty.this,"Attendance not fully submitted. Please try later."+response,Toast.LENGTH_LONG).show();
                    //Log.e("Attendance", response);
                }
                else
                    Toast.makeText(FillAttendanceByFaculty.this,response,Toast.LENGTH_LONG).show();
            }
        }
    }

    class RefreshAttendance extends AsyncTask<String, String, Void>
    {
        //private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        String response="";
        // private ProgressDialog progressDialog;
        InputStream is = null;

        @Override
        protected Void doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL) + "/refreshAttendance.php";
            String keys[]={"attendance_id"};
            String values[]={params[0]};
            response=HelperMethods.getResponse(url_select,keys,values);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Attendee>>()
            {
            }.getType();
            attendeeList = gson.fromJson(response, type);
            return null;
        }
        protected void onPostExecute(Void v)
        {
            if(!response.isEmpty() && !response.equals("null"))
            {
                if (response.contains("Exception"))
                {
                    Toast.makeText(FillAttendanceByFaculty.this,"Please try later."+response,Toast.LENGTH_LONG).show();
                    //Log.e("Attendance", response);
                }
                else
                {
                    // reset adapter
                    adapter = new FillAttendanceAdapter(attendeeList, context, courseCode,false);
                    attendeesView.setAdapter(adapter);
                }
            }
        }
    }
}

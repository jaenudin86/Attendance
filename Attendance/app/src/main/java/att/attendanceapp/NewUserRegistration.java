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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import Helper.HelperMethods;

public class NewUserRegistration extends ActivityBaseClass
{
    EditText name,email,pwd,confirmPwd,number;
    Context context=this;
    RadioGroup rgrp;
    RadioButton rFacilitator,rAttendee;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_registration);
        name=(EditText)findViewById(R.id.txtNameRegister);
        email=(EditText)findViewById(R.id.txtEmailRegister);
        number=(EditText)findViewById(R.id.txtNumberRegister);
        pwd=(EditText)findViewById(R.id.txtPasswordRegister);
        confirmPwd=(EditText)findViewById(R.id.txtConfirmPwdRegister);
        rgrp=(RadioGroup)findViewById(R.id.rgrpRegistration);
        rFacilitator=(RadioButton)findViewById(R.id.rbtnRegistrationFacilitator);
        rAttendee=(RadioButton)findViewById(R.id.rbtnRegistrationAttendee);
        rgrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(checkedId==rAttendee.getId())
                    number.setEnabled(true);
                else
                    number.setEnabled(false);
            }
        });
    }

    public void onOkClick(View view)
    {
        if(isValid()) {
            new NewUserAdd().execute(name.getText().toString(), email.getText().toString(), pwd.getText().toString(), rFacilitator.isChecked() ? "true" : "false",number.getText().toString());
        }
    }
    private boolean isValid()
    {
        boolean error=false;
        if(HelperMethods.isEmpty(name))
        {
            name.setError("Please enter valid name");
            error=true;
        }
        if(HelperMethods.isEmpty(email))
        {
            email.setError("Please enter valid email");
            error=true;
        }
        else if(!HelperMethods.isValidEmail(email.getText()))
        {
            email.setError("Please enter valid email");
            error=true;
        }
        if(HelperMethods.isEmpty(pwd))
        {
            pwd.setError("Please enter valid password");
            error=true;
        }
        else if(pwd.getText().length()<6)
        {
            pwd.setError("Password should be at least 6 characters");
            error=true;
        }
        if(HelperMethods.isEmpty(confirmPwd))
        {
            confirmPwd.setError("Please enter valid password");
            error=true;
        }
        else if(!confirmPwd.getText().toString().equals(pwd.getText().toString()))
        {
            confirmPwd.setError("Passwords do not match");
            error=true;
        }
        else if(!rFacilitator.isChecked() && !rAttendee.isChecked())
        {
            rAttendee.setError("Please select one");
            error=true;
        }
        return !error;
    }
    public void onCancelClick(View view)
    {
        this.finish();
    }
    class NewUserAdd extends AsyncTask<String, String, Void>
    {
        String serviceURL;
        String response = "";
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(NewUserRegistration.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            try
            {
                serviceURL=getString(R.string.serviceURL)+"/addUser.php";
                String[] keys={"user_name","user_email","password","is_facilitator","attendee_number"};
                String[] values={params[0],params[1],params[2],params[3],params[4]};
                response=HelperMethods.getResponse(serviceURL,keys,values);
            }
            catch (Exception ex){
                response="Exception:"+ex.toString();
            }
            return null;
        }
        protected void onPostExecute(Void v) {
            if (progressDialog!=null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if(response.contains("Exception"))
                Toast.makeText(NewUserRegistration.this,response,Toast.LENGTH_SHORT).show();
            else
            {
                Intent intent = new Intent(NewUserRegistration.this, LoginUser.class);
                startActivity(intent);
            }
        }
    }
}

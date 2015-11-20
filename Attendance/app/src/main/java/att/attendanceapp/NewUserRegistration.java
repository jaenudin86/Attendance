package att.attendanceapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import Helper.HelperMethods;

public class NewUserRegistration extends ActivityBaseClass
{
    EditText name,email,pwd,confirmPwd;
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
        pwd=(EditText)findViewById(R.id.txtPasswordRegister);
        confirmPwd=(EditText)findViewById(R.id.txtConfirmPwdRegister);
        rgrp=(RadioGroup)findViewById(R.id.rgrpRegistration);
        rFacilitator=(RadioButton)findViewById(R.id.rbtnRegistrationFacilitator);
        rAttendee=(RadioButton)findViewById(R.id.rbtnRegistrationAttendee);
    }
    public void onOkClick(View view)
    {
        if(isValid()) {
            Toast.makeText(this,"Valid",Toast.LENGTH_SHORT).show();
            //new NewUserAdd().execute(name.getText().toString(), email.getText().toString(), pwd.getText().toString(),String.valueOf(Helper.generateRandom(1000, 9999)));
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
}

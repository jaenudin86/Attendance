package att.attendanceapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import Helper.HelperMethods;

public class LoginUser extends ActivityBaseClass
{
    EditText email, password;
    TextView err;
    Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user);

        if(!HelperMethods.getCurrentLoggedinUser(this).isEmpty())
        {
            String userType=HelperMethods.getCurrentLoggedinUserType(this).toLowerCase();
            Intent intent=null;
            if(userType.equals("facilitator"))
            {
                intent=new Intent(LoginUser.this,MainActivity.class);

                /*BroadcastReceiver detachReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
                        {

                        }
                    }
                };*/
                /*IntentFilter filter = new IntentFilter();
                filter.addAction("android.nfc.action.NDEF_DISCOVERED");
                filter.addCategory("android.intent.category.DEFAULT");
                NFCBroadcastReciever reciever = new NFCBroadcastReciever(this);

                this.registerReceiver(reciever, filter);*/
                finish();
                startActivity(intent);
            }
            else if(userType.equals("attendee"))
            {
                intent=new Intent(LoginUser.this,MainActivityAttendee.class);
                startActivity(intent);
                finish();
            }
        }
        email = (EditText) findViewById(R.id.txtEmailLogin);
        password = (EditText) findViewById(R.id.txtPasswordLogin);
        err = (TextView) findViewById(R.id.lblErrorLogin);
    }
    public void onOkClick(View view)
    {

        new LoginCheck().execute(email.getText().toString(), password.getText().toString());
    }

    public void onCancelClick(View view)
    {
        this.finish();
    }
    public void onRegisterClick(View view)
    {
        Intent intent = new Intent(this, NewUserRegistration.class);
        startActivityForResult(intent, 1);
    }
    class LoginCheck extends AsyncTask<String, Void, Void>
    {
        private ProgressDialog progressDialog;
        InputStream is = null;
        String response = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(LoginUser.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Loading...");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(String... params)
        {
            String url_select = getString(R.string.serviceURL)+"/login.php";
            try
            {
                String keys[] = {"user_id","password"};
                String values[] = {params[0],params[1]};
                response = HelperMethods.getResponse(url_select, keys, values);
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
                    Toast.makeText(getApplicationContext(), "Some error occurred, please try again", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    response=response.toLowerCase();
                    if (response.equals("facilitator") || response.equals("attendee")) //ok
                    {
                        err.setVisibility(View.INVISIBLE);
                        HelperMethods.putSharedPref(context, getString(R.string.isLoggedIn_sharedPref_string), "yes");
                        HelperMethods.putSharedPref(context, getString(R.string.loggedInUser_sharedPref_string), email.getText().toString());
                        HelperMethods.putSharedPref(context, getString(R.string.userType_sharedPref_string),response );
                        Intent intent=null;
                        if(response.equals("facilitator"))
                        {
                            intent=new Intent(LoginUser.this,MainActivity.class);

                        }
                        else if(response.equals("attendee"))
                        {
                            intent=new Intent(LoginUser.this,MainActivityAttendee.class);
                        }
                        startActivity(intent);
                    }
                    else if (response.equals("false")) //means userid/password do not match
                    {
                        err.setText("Username or password is invalid");
                        err.setVisibility(View.VISIBLE);
                    }
                }
            }
            else
            {
                Toast.makeText(context,"Some error occurred, please try again",Toast.LENGTH_SHORT).show();
                Log.i(HelperMethods.TAG, "error in Login:" + response);
            }
        }
    }
}

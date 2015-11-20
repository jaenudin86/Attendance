package att.attendanceapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import Helper.NFCUtils;

public class FillAttendanceFacultyNFC extends ActivityBaseClass
{
    NfcAdapter nfcAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_attendance_faculty_nfc);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

    }
    @Override
    protected void onResume()
    {
        Intent intent=new Intent(this,FillAttendanceFacultyNFC.class);
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
        //This method gets called, when a new Intent gets associated with the current activity instance.
        //Instead of creating a new activity, onNewIntent will be called.
        // In our case this method gets called, when the user attaches a Tag to the device.
        //if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED))
        if(intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
        {
            Toast.makeText(this, "NFC intent received", Toast.LENGTH_SHORT).show();
            //handleNFC(intent);
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if(NFCUtils.checkNFCAvailable(this,nfcAdapter))
            {
                String msg=NFCUtils.write("This is hello", tag,this);
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
                /*msg=NFCUtils.read(intent);
                Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();*/
            }
        }
        super.onNewIntent(intent);
    }
}

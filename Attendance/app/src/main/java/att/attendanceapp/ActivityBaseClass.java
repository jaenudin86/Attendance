package att.attendanceapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Base class for all common functionalities of activities
 * Created by rujoota on 24-09-2015.
 */
public class ActivityBaseClass extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home)
        {
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.action_share)
        {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.shareSubject));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.shareBody));
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }
        else if(id==R.id.action_about)
        {
            Intent intent=new Intent(this,About.class);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.action_help)
        {
            Intent intent=new Intent(this,Help.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

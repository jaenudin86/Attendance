package att.attendanceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.*;

import java.util.Date;
import java.util.List;

import DBHelper.DatabaseHandler;
import DBHelper.Holiday;

public class ManageHolidays extends ActivityBaseClass
{
    String TAG="Attendance";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_holidays);
        Parse.initialize(this, "74nJUmHmWvcR3YQNJMDFd14TqmaYzezuLQ22w8Z9",
                "jdhBBOhpcC19sQFUhdJdKddIu3usx99RxUxWK2W3");

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        storeToParse();
    }
    private void storeToSQL() {
        DatabaseHandler db = new DatabaseHandler(this);
        Holiday christmas = new Holiday("Christmas",new Date(2015,12,25),new Date(2015,12,25));

        db.addHoliday(christmas);

        db.close();
    }
    private void storeToParse() {
        /**
         * Create a new ParseObject and push it to the cloud. The name of the
         * ParseObject "Employee" corresponds to a SQL table and the keys of the
         * various key/value pairs correspond to column names. Parse will
         * automatically add ParseObject instances to the appropriate table.
         */
        ParseObject holiday = new ParseObject("Holiday");
        holiday.put("holidayName", "Test Holiday");
        holiday.saveInBackground();

        holiday = new ParseObject("Holiday");
        holiday.put("holidayName", "Test holiday2");

        holiday.saveInBackground();


    }
    public void onTestClick(View view)
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Holiday");
        query.whereEqualTo("holidayName", "Test Holiday");
        query.findInBackground(new FindCallback<ParseObject>()
        {
            public void done(List<ParseObject> holidays, ParseException e)
            {
                if (e == null)
                {
                    if (holidays.size() > 0)
                    {
                        ParseObject holidayObj = holidays.get(0);
                        Log.d(TAG, "storeToParse: holidayName=" + holidayObj.getString("holidayName"));
                    }
                    else
                    {
                        Log.d(TAG, "storeToParse: No holidayName found!");
                    }
                }
                else
                {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }
}

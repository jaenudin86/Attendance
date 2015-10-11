package att.attendanceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import Helper.Helper;

public class MainActivity extends ActivityBaseClass
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListView();
        Helper.putSharedPref(this, getString(R.string.isLoggedIn_sharedPref_string), "yes");
        Helper.putSharedPref(this,getString(R.string.loggedInUser_sharedPref_string),"rujoota.shah@gmail.com");
    }
    void setListView()
    {
        String[] arr=getResources().getStringArray(R.array.listview_main_facilitator);

        ListAdapter adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,arr);

        ListView listView=(ListView)findViewById(R.id.listViewMain);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        String itemName = String.valueOf(parent.getItemAtPosition(position));
                        changeIntent(position, itemName);
                    }
                }
        );
    }
    void changeIntent(int pos,String itemName)
    {
        if(pos==2)
        {
            Intent newIntent=new Intent(this,ManageCourses.class);
            startActivity(newIntent);
        }
        else if(pos==4)
        {
            Intent newIntent=new Intent(this,ManageHolidays.class);
            startActivity(newIntent);
        }
        else if(pos==3)
        {
            Intent newIntent=new Intent(this,MyTimetable.class);
            startActivity(newIntent);
        }
    }
}

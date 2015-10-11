package att.attendanceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MyTimetable extends ActivityBaseClass
{
    TableLayout tbl;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_timetable);
        tbl=(TableLayout)findViewById(R.id.tblMyTimeTable);
        setupTableView();

    }
    String months[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Aug","Sep","Oct","Nov","Dec"};
    int days[]={31,28,31,30,31,30,31,30,31,30,31};
    void setupTableView()
    {
        int i=0,rowCount=0;
        TableRow row = new TableRow(this);
            for (int j = 1; j <= days[i]; j++)
            {
                Button btn = new Button(this);
                btn.setText(String.valueOf(j));
                row.addView(btn);

                if(j%7==0)
                {
                    tbl.addView(row);
                    row = new TableRow(this);
                    rowCount++;
                }
            }
    }
}

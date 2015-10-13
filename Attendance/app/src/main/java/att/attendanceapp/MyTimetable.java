package att.attendanceapp;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Helper.Helper;

public class MyTimetable extends ActivityBaseClass
{
    TableLayout tbl;
    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private TextView txtDate;
    private GridView calendarView, calendarDaysHeader;
    private CalendarAdapter adapter;
    Calendar calendar;
    int month,year;
    TextView currentMonth;
    private static final String dateTemplate = "MMMM yyyy";
    private final DateFormat dateFormatter = new DateFormat();
    String daysOfWeek[]={"MON","TUE","WED","THU","FRI","SAT","SUN"};
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_timetable);

        btnPrev=(ImageButton)findViewById(R.id.ibtnCalendarPrev);
        btnNext=(ImageButton)findViewById(R.id.ibtnCalendarNext);
        currentMonth=(TextView)findViewById(R.id.tvCurrentMonthDisplay);
        calendarView = (GridView) this.findViewById(R.id.calendarDaysGrid);
        calendarDaysHeader = (GridView) this.findViewById(R.id.calendarDaysHeaderGrid);
        setupHeader();
        setupCalendar();

    }
    public void prevMonthClicked(View view)
    {
        if (month <= 1)
        {
            month = 12;
            year--;
        }
        else
        {
            month--;
        }
        setGridCellAdapterToDate(month, year);
    }
    public void nextMonthClicked(View view)
    {
        if (month > 11)
        {
            month = 1;
            year++;
        }
        else
        {
            month++;
        }

        setGridCellAdapterToDate(month, year);
    }
    private void setGridCellAdapterToDate(int month, int year)
    {
        adapter = new CalendarAdapter(this, month, year);
        calendar.set(year, month - 1, calendar.get(Calendar.DAY_OF_MONTH));
        adapter.notifyDataSetChanged();
        currentMonth.setText(Helper.convertDateToFormat(calendar.getTime(), dateTemplate));
        calendarView.setAdapter(adapter);
    }
    void setupHeader()
    {
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, daysOfWeek);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.week_adapter,daysOfWeek);
        calendarDaysHeader.setAdapter(arrayAdapter);
    }
    void setupCalendar()
    {
        try
        {
            calendar = Calendar.getInstance(Locale.getDefault());
            month = calendar.get(Calendar.MONTH) + 1;
            year = calendar.get(Calendar.YEAR);

            String formattedDate = Helper.convertDateToFormat(calendar.getTime(), dateTemplate);
            currentMonth.setText(formattedDate);

            // Initialised
            adapter = new CalendarAdapter(this, month, year);
            adapter.notifyDataSetChanged();
            calendarView.setAdapter(adapter);

        }
        catch (Exception ex)
        {
            Toast.makeText(this,ex.toString(),Toast.LENGTH_LONG).show();
        }
    }
}

package att.attendanceapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by rujoota on 12-10-2015.
 */
public class CalendarAdapter extends BaseAdapter
{
    Context context;
    int month,year;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private final String[] weekdays = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    ArrayList<String> list = new ArrayList<String>();
    private static final int DAY_OFFSET = 1;
    private int prevMonthDays;
    private int currentDayOfMonth,currentWeekDay;
    private TextView gridcell;
    public CalendarAdapter(Context context,int month,int year)
    {
        //super();
        this.context=context;
        this.month=month;
        this.year = year;
        Calendar calendar = Calendar.getInstance();
        currentDayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
        // Print Month

        printMonth(month, year);
    }

    int daysInMonth;
    private void printMonth(int mm, int yy)
    {
        // The number of days to leave blank at
        // the start of this month.
        int trailingSpaces = 0;
        int leadSpaces = 0;
        int daysInPrevMonth = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;

        int currentMonth = mm - 1;
        daysInMonth = daysOfMonth[currentMonth];
        // Gregorian Calendar : MINUS 1, set to FIRST OF MONTH
        GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);


        if (currentMonth == 11)//december
        {
            prevMonth = currentMonth - 1;
            nextMonth = 0;
            prevYear = yy;
            nextYear = yy + 1;
        }
        else if (currentMonth == 0)//january
        {
            prevMonth = 11;
            prevYear = yy - 1;
            nextYear = yy;

            nextMonth = 1;
        }
        else
        {
            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
        }

        // Compute how much to leave before before the first day of the month.
        // this returns 1 for Sunday.
        int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK);
        if(currentWeekDay==1)
            currentWeekDay=7;
        else
            currentWeekDay=currentWeekDay-1;
        trailingSpaces = currentWeekDay;
        boolean isLeapYear=cal.isLeapYear(cal.get(Calendar.YEAR));
        if(!isLeapYear)
        {
            daysOfMonth[1]=28;
        }
        else
        {
            if(currentMonth==1) // if displaying for feb
                daysInMonth=29;
            daysOfMonth[1]=29;
        }
        daysInPrevMonth = daysOfMonth[prevMonth];

        // Trailing Month days
        for (int i = 0; i < trailingSpaces-1; i++)
        {
            list.add(String.valueOf((daysInPrevMonth - trailingSpaces+2 + i) + "-GREY" + "-" + months[prevMonth] + "-" + prevYear));
        }

        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++)
        {
            if (i == currentDayOfMonth)
            {
                list.add(String.valueOf(i) + "-BLUE" + "-" + months[currentMonth] + "-" + yy);
            }
            else
            {
                list.add(String.valueOf(i) + "-WHITE" + "-" + months[currentMonth] + "-" + yy);
            }
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++)
        {
            list.add(String.valueOf(i + 1) + "-GREY" + "-" + months[nextMonth] + "-" + nextYear);
        }
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        View row = view;
        if (row == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.calendar_days_grid, parent, false);
        }


        // ACCOUNT FOR SPACING


        String[] day_color = list.get(position).split("-");
        String theday = day_color[0];
        String themonth = day_color[2];
        String theyear = day_color[3];

        gridcell= (TextView) row.findViewById(R.id.btnCalendarDay);
        // Set the Day GridCell
        gridcell.setText(theday);
        gridcell.setTag(theday + "-" + themonth + "-" + theyear);


        if(day_color[1].equals("GREY"))
        {
            gridcell.setTextColor(Color.LTGRAY);
        }
        if (day_color[1].equals("WHITE"))
        {
            gridcell.setTextColor(Color.BLACK);
        }
        if (day_color[1].equals("BLUE"))
        {
            gridcell.setTextColor(Color.BLUE);
        }
        return row;
    }
}

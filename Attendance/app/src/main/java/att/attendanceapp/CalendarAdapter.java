package att.attendanceapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import DBHelper.Holiday;
import DBHelper.TimetableSlot;

/**
 * Created by rujoota on 12-10-2015.
 */
public class CalendarAdapter extends BaseAdapter
{
    Context context;
    int month,year;
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    ArrayList<String> daysList = new ArrayList<String>();
    private int currentDayOfMonth,currentWeekDay;
    private Button gridcell;
    String[] day_color;
    ArrayList<Holiday> holidayArrayList=new ArrayList<Holiday>();
    ArrayList<TimetableSlot> timetableSlotList=new ArrayList<TimetableSlot>();
    int position;
    public CalendarAdapter(Context context,int month,int year,ArrayList<Holiday> list,ArrayList<TimetableSlot> slots)
    {
        this.context=context;
        this.month=month;
        this.year = year;
        Calendar calendar = Calendar.getInstance();
        currentDayOfMonth=calendar.get(Calendar.DAY_OF_MONTH);
        currentWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
        this.holidayArrayList=list;
        this.timetableSlotList=slots;
        // printing a day
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

        // Prev Month days
        for (int i = 0; i < trailingSpaces-1; i++)
        {
            daysList.add(String.valueOf((daysInPrevMonth - trailingSpaces + 2 + i) + "-GREY" + "-" + (prevMonth+1) + "-" + prevYear));
        }
        Calendar now = Calendar.getInstance();
        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++)
        {
            if (i == currentDayOfMonth && currentMonth==now.get(Calendar.MONTH))
            {
                daysList.add(String.valueOf(i) + "-YELLOW" + "-" + (currentMonth+1) + "-" + yy);
            }
            else
            {
                daysList.add(String.valueOf(i) + "-WHITE" + "-" + (currentMonth+1) + "-" + yy);
            }
        }
        // Next Month days
        for (int i = 0; i < daysList.size() % 7; i++)
        {
            daysList.add(String.valueOf(i + 1) + "-GREY" + "-" + (nextMonth+1) + "-" + nextYear);
        }
    }

    @Override
    public int getCount()
    {
        return daysList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return daysList.get(position);
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

        this.position=position;
        gridcell= (Button) row.findViewById(R.id.tvCalendarDay);
        day_color = daysList.get(position).split("-");
        String theday = day_color[0];
        String themonth = day_color[2];
        String theyear = day_color[3];
        // Set the Day GridCell
        gridcell.setText(theday);
        gridcell.setTag(theyear + "-" + themonth + "-" + theday);
        if(day_color[1].equals("GREY"))
        {
            gridcell.setBackgroundColor(Color.LTGRAY);
        }
        if (day_color[1].equals("WHITE"))
        {
            gridcell.setBackgroundColor(Color.WHITE);
        }
        if (day_color[1].equals("YELLOW"))
        {
            gridcell.setBackgroundResource(R.color.mainColor);
        }
        for(int i=0;i<timetableSlotList.size();i++)
        {
            Date currentPrintingDate = new Date(Integer.parseInt(theyear), Integer.parseInt(themonth), Integer.parseInt(theday));
            String startdt[]=timetableSlotList.get(i).getDate().split("-");

            Date fromDate=new Date(Integer.parseInt(startdt[0]),Integer.parseInt(startdt[1]),Integer.parseInt(startdt[2]));

            if (currentPrintingDate.equals(fromDate))
            {
                if(timetableSlotList.get(i).getIsSubmitted().equals("0"))
                {
                    gridcell.setBackgroundResource(R.color.attendanceNotFilled);
                    break;
                }
                else
                {
                    gridcell.setBackgroundResource(R.color.attendanceFilled);
                    break;
                }
            }
        }
        for(int i=0;i<holidayArrayList.size();i++)
        {
            Date currentPrintingDate = new Date(Integer.parseInt(theyear), Integer.parseInt(themonth), Integer.parseInt(theday));
            String startdt[]=holidayArrayList.get(i).getFromDate().split("-");
            String enddt[]=holidayArrayList.get(i).getToDate().split("-");
            Date fromDate=new Date(Integer.parseInt(startdt[0]),Integer.parseInt(startdt[1]),Integer.parseInt(startdt[2]));
            Date toDate=new Date(Integer.parseInt(enddt[0]),Integer.parseInt(enddt[1]),Integer.parseInt(enddt[2]));
            if ((currentPrintingDate.equals(fromDate) || currentPrintingDate.after(fromDate)) && (currentPrintingDate.equals(toDate) || currentPrintingDate.before(toDate)))
            {
                //gridcell.setBackgroundResource(R.drawable.square_item);
                gridcell.setBackgroundResource(R.color.holiday);
                gridcell.setEnabled(false);
                break;
            }
        }

        return row;
    }

}

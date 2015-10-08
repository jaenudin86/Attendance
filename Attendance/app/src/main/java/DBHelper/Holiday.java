package DBHelper;

import java.util.Date;

/**
 * Created by rujoota on 01-10-2015.
 */
public class Holiday
{
    private String holidayName;
    private Date fromDate;
    private Date toDate;

    public Holiday(String holidayName,Date fromDate,Date toDate)
    {
        this.setHolidayName(holidayName);
        this.setFromDate(fromDate);
        this.setToDate(toDate);
    }
    public String getHolidayName()
    {
        return holidayName;
    }

    public void setHolidayName(String holidayName)
    {
        this.holidayName = holidayName;
    }

    public Date getFromDate()
    {
        return fromDate;
    }

    public void setFromDate(Date fromDate)
    {
        this.fromDate = fromDate;
    }

    public Date getToDate()
    {
        return toDate;
    }

    public void setToDate(Date toDate)
    {
        this.toDate = toDate;
    }
}

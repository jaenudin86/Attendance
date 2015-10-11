package DBHelper;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rujoota on 01-10-2015.
 */
public class Holiday implements Serializable
{
    private String holidayName;
    private String fromDate;
    private String toDate;
    private String id;
    private String facilitatorId;
    public Holiday(String holidayName,String fromDate,String toDate,String facilitatorId)
    {
        this.setHolidayName(holidayName);
        this.setFromDate(fromDate);
        this.setToDate(toDate);
        this.setFacilitatorId(facilitatorId);
        //this.setId(id);
    }
    public Holiday(String holidayName,String fromDate,String toDate,String facilitatorId,String id)
    {
        this.setHolidayName(holidayName);
        this.setFromDate(fromDate);
        this.setToDate(toDate);
        this.setFacilitatorId(facilitatorId);
        this.setId(id);
    }
    public String getHolidayName()
    {
        return holidayName;
    }

    public void setHolidayName(String holidayName)
    {
        this.holidayName = holidayName;
    }

    public String getFromDate()
    {
        return fromDate;
    }

    public void setFromDate(String fromDate)
    {
        this.fromDate = fromDate;
    }

    public String getToDate()
    {
        return toDate;
    }

    public void setToDate(String toDate)
    {
        this.toDate = toDate;
    }
    public void setFacilitatorId(String facilitatorId)
{
    this.facilitatorId = facilitatorId;
}
    public String getFacilitatorId()
    {
        return this.facilitatorId;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}

package DBHelper;

import java.io.Serializable;

/**
 * Created by rujoota on 19-10-2015.
 */
public class Timetable implements Serializable
{
    private String facilitatorId;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private String isRecurring;
    private String recurringDays;
    private String courseCode;
    private String id;
    public String getFacilitatorId()
    {
        return facilitatorId;
    }

    public void setFacilitatorId(String facilitatorId)
    {
        this.facilitatorId = facilitatorId;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getIsRecurring()
    {
        return isRecurring;
    }

    public void setIsRecurring(String isRecurring)
    {
        this.isRecurring = isRecurring;
    }

    public String getRecurringDays()
    {
        return recurringDays;
    }

    public void setRecurringDays(String recurringDays)
    {
        this.recurringDays = recurringDays;
    }

    public String getCourseCode()
    {
        return courseCode;
    }

    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
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

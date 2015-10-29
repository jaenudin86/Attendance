package DBHelper;

/**
 * Created by rujoota on 19-10-2015.
 */
public class TimetableSlot
{
    private String facilitator;
    private String id;
    private String startTime;
    private String endTime;
    private String date;
    private String courseCode;
    private String timetableId;
    private String isSubmitted;
    public String getIsSubmitted()
    {
        return isSubmitted;
    }

    public void setIsSubmitted(String isSubmitted)
    {
        this.isSubmitted = isSubmitted;
    }
    public String getFacilitator()
    {
        return facilitator;
    }

    public void setFacilitator(String facilitator)
    {
        this.facilitator = facilitator;
    }

    public String getTimetableId()
    {
        return timetableId;
    }

    public void setTimetableId(String id)
    {
        this.timetableId = id;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getCourseCode()
    {
        return courseCode;
    }

    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }
}

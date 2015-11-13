package DBHelper;

/**
 * Created by rujoota on 06-11-2015.
 */
public class AttendanceReport
{
    private String name;
    private String emailId;
    private String attendeeNumber;
    private String courseCode;
    private String absentOn;
    private String courseStartDate;
    private String courseEndDate;
    private String presence;
    public String getPresence()
    {
        return presence;
    }

    public void setPresence(String presence)
    {
        this.presence = presence;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmailId()
    {
        return emailId;
    }

    public void setEmailId(String emailId)
    {
        this.emailId = emailId;
    }

    public String getAttendeeNumber()
    {
        return attendeeNumber;
    }

    public void setAttendeeNumber(String attendeeNumber)
    {
        this.attendeeNumber = attendeeNumber;
    }

    public String getCourseCode()
    {
        return courseCode;
    }

    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }

    public String getAbsentOn()
    {
        return absentOn;
    }

    public void setAbsentOn(String absentOn)
    {
        this.absentOn = absentOn;
    }

    public String getCourseStartDate()
    {
        return courseStartDate;
    }

    public void setCourseStartDate(String courseStartDate)
    {
        this.courseStartDate = courseStartDate;
    }

    public String getCourseEndDate()
    {
        return courseEndDate;
    }

    public void setCourseEndDate(String courseEndDate)
    {
        this.courseEndDate = courseEndDate;
    }
}

package DBHelper;

/**
 * Created by rujoota on 22-10-2015.
 */
public class Attendee
{
    private String name;
    private String emailId;
    private String attendeeNumber;

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
}

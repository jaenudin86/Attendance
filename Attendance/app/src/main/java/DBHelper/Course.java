package DBHelper;

import java.io.Serializable;

/**
 * Created by rujoota on 01-10-2015.
 */
public class Course implements Serializable
{
    private String course_code;
    private String course_name;
    private String course_description;
    private String facilitator_id;
    public boolean shouldAnimateOnAdd =false;
    public boolean isDeleted=false;
    public Course(String course_code,String course_name,String course_description,String facilitator_id)
    {
        this.course_code=course_code;
        this.course_name=course_name;
        this.course_description=course_description;
        this.facilitator_id=facilitator_id;
    }
    public String getCourseCode()
    {
        return course_code;
    }

    public void setCourseCode(String course_code)
    {
        this.course_code = course_code;
    }

    public String getCoursename()
    {
        return course_name;
    }

    public void setCoursename(String course_name)
    {
        this.course_name = course_name;
    }

    public String getCourseDescription()
    {
        return course_description;
    }

    public void setCourseDescription(String courseDescription)
    {
        this.course_description = courseDescription;
    }

    public String getFacilitatorId()
    {
        return facilitator_id;
    }

    public void setFacilitatorId(String facilitator_id)
    {
        this.facilitator_id = facilitator_id;
    }
}

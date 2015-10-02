package DBHelper;

/**
 * Created by rujoota on 01-10-2015.
 */
public class Course
{
    private String course_code;
    private String course_name;
    private String course_description;

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
}

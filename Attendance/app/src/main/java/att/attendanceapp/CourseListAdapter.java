package att.attendanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import DBHelper.Course;

/**
 * Created by rujoota on 01-10-2015.
 */
public class CourseListAdapter  extends BaseAdapter
{
    ArrayList<Course> courses;
    Context context;
    CourseListAdapter(Context context,ArrayList<Course> courses)
    {
        this.context=context;
        this.courses=courses;
    }

    class ViewHolder
    {
        TextView courseCode;
        TextView courseName;
        TextView description;
        ViewHolder(View view)
        {
            courseCode=(TextView)view.findViewById(R.id.tvAdapterCourseCode);
            courseName=(TextView)view.findViewById(R.id.tvAdapterCourseName);
            description=(TextView)view.findViewById(R.id.tvAdapterCourseDescription);
        }
    }
    @Override
    public int getCount()
    {
        return courses.size();
    }

    @Override
    public Object getItem(int position)
    {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View row=convertView;
        ViewHolder holder=null;
        if(row==null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(R.layout.course_view_adapter,parent, false);
            holder=new ViewHolder(row);
            row.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)row.getTag();
        }
        final Course obj = courses.get(position);
        holder.courseCode.setText(obj.getCourseCode());
        holder.courseName.setText(obj.getCoursename());
        holder.description.setText(obj.getCourseDescription());
        return row;
    }
}

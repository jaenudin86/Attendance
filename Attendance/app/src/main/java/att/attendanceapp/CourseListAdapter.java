package att.attendanceapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
        //TextView description;
        ImageButton delete;
        ViewHolder(View view)
        {
            courseCode=(TextView)view.findViewById(R.id.tvAdapterCourseCode);
            courseName=(TextView)view.findViewById(R.id.tvAdapterCourseName);
            //description=(TextView)view.findViewById(R.id.tvAdapterCourseDescription);
            delete=(ImageButton)view.findViewById(R.id.ibtnCourseDelete);
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
        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                courses.remove(position);
                notifyDataSetChanged();
                new DeleteCourse().execute(obj.getFacilitatorId(), obj.getCourseCode());
            }
        });
        return row;
    }

    /*public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // arg2 = the id of the item in our view (List/Grid) that we clicked
        // arg3 = the id of the item that we have clicked
        // if we didn't assign any id for the Object (Book) the arg3 value is 0
        // That means if we comment, aBookDetail.setBookIsbn(i); arg3 value become 0
        Toast.makeText(context, "You clicked on position : " + arg2 + " and id : " + arg3, Toast.LENGTH_LONG).show();
    }*/
    class DeleteCourse extends AsyncTask<String, Void, String>
    {
        InputStream is = null;
        int responseCode = 0;
        String returnString="";
        @Override
        protected String doInBackground(String... params)
        {
            String serviceURL = context.getString(R.string.serviceURL)+"/deleteCourse.php";
            try
            {
                URL url = new URL(serviceURL);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")+"&"+
                        URLEncoder.encode("course_code", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                responseCode = httpUrlConnection.getResponseCode();
                httpUrlConnection.disconnect();
                returnString="ok";
            }
            catch (Exception ex)
            {
                returnString="Exception:"+ex.toString();
            }
            return returnString;
        }

        protected void onPostExecute(String v)
        {
            super.onPostExecute(v);
            // no exception found on previous call
            if(!v.toLowerCase().contains("exception"))
            {
                // if no data found then
                if(responseCode==200) // means all ok
                {
                    Toast.makeText(context,"Item deleted",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(context,"Some error has occurred",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context,v,Toast.LENGTH_LONG).show();
            }
        }
    }
}

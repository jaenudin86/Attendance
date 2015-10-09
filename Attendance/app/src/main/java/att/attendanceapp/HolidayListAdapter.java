package att.attendanceapp;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import DBHelper.Course;
import DBHelper.Holiday;
import Helper.Helper;

/**
 * Created by rujoota on 08-10-2015.
 */
public class HolidayListAdapter  extends BaseAdapter
{
    ArrayList<Holiday> holidays;
    Context context;
    HolidayListAdapter(Context context,ArrayList<Holiday> holidays)
    {
        this.context=context;
        this.holidays=holidays;
    }

    class ViewHolder
    {
        TextView holidayName;
        TextView from,to;
        //TextView description;
        ImageButton delete;
        ViewHolder(View view)
        {
            holidayName=(TextView)view.findViewById(R.id.tvAdapterHolidayName);
            from=(TextView)view.findViewById(R.id.tvAdapterHolidayFrom);
            to=(TextView)view.findViewById(R.id.tvAdapterHolidayTo);
            delete=(ImageButton)view.findViewById(R.id.ibtnHolidayDelete);
        }
    }
    @Override
    public int getCount()
    {
        return holidays.size();
    }

    @Override
    public Object getItem(int position)
    {
        return holidays.get(position);
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
            row=inflater.inflate(R.layout.holiday_view_adapter,parent, false);
            holder=new ViewHolder(row);
            row.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)row.getTag();
        }
        final Holiday obj = holidays.get(position);
        holder.holidayName.setText(obj.getHolidayName());
        holder.from.setText(Helper.convertDate(obj.getFromDate().toString()));
        holder.to.setText(Helper.convertDate(obj.getToDate().toString()));
        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                holidays.remove(position);
                notifyDataSetChanged();
                new DeleteHoliday().execute(obj.getFacilitatorId(), obj.getId());
            }
        });
        return row;
    }


    class DeleteHoliday extends AsyncTask<String, Void, String>
    {
        InputStream is = null;
        int responseCode = 0;
        String returnString="";
        @Override
        protected String doInBackground(String... params)
        {
            String serviceURL = context.getString(R.string.serviceURL)+"/deleteHoliday.php";
            try
            {
                URL url = new URL(serviceURL);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")+"&"+
                        URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");
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
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
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

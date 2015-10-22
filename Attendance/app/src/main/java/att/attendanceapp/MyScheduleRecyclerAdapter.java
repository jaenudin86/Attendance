package att.attendanceapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;

import DBHelper.Timetable;
import Helper.HelperMethods;

/**
 * Created by rujoota on 19-10-2015.
 */
public class MyScheduleRecyclerAdapter  extends RecyclerView.Adapter<MyScheduleRecyclerAdapter.ViewHolder>
{
    private ArrayList<Timetable> mDataset;
    Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView courseCode;
        public TextView startTime,endTime,startDate,endDate,recurringDays;
        ImageButton delete;
        public ViewHolder(View itemView) {
            super(itemView);
            courseCode=(TextView)itemView.findViewById(R.id.tvMyScheduleCourseCode);
            startTime=(TextView)itemView.findViewById(R.id.tvMyScheduleStartDate);
            endTime=(TextView)itemView.findViewById(R.id.tvMyScheduleEndDate);
            startDate=(TextView)itemView.findViewById(R.id.tvMyScheduleStartTime);
            endDate=(TextView)itemView.findViewById(R.id.tvMyScheduleEndTime);
            recurringDays=(TextView)itemView.findViewById(R.id.tvMyScheduleRecurringDays);
            delete=(ImageButton)itemView.findViewById(R.id.ibtnMyScheduleDelete);
        }
    }

    public MyScheduleRecyclerAdapter(ArrayList<Timetable> myDataset,Context context) {
        mDataset = myDataset;
        this.context=context;
    }

    @Override
    public MyScheduleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_schedule_recycler_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Timetable current=mDataset.get(position);
        holder.courseCode.setText(current.getCourseCode());
        String timingStart= HelperMethods.convertToStandardTime(current.getStartTime());
        String timingEnd= HelperMethods.convertToStandardTime(current.getEndTime());
        holder.startTime.setText(timingStart);
        holder.endTime.setText(timingEnd);
        String dateStart= HelperMethods.convertDateFromSQLToUS(current.getStartDate());
        String dateEnd= HelperMethods.convertDateFromSQLToUS(current.getEndDate());
        holder.startDate.setText(dateStart);
        holder.endDate.setText(dateEnd);
        if(current.getIsRecurring().equals("1") || current.getIsRecurring().equals("yes"))
        {
            holder.recurringDays.setText("on every "+current.getRecurringDays());
        }
        else
        {
            holder.recurringDays.setText("one time only");
        }
        holder.delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Delete alert");
                alertDialog.setMessage("Are you sure you want to delete this?");
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        deleteItem(position);
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addNewItem(Timetable item, int position) {
        mDataset.add(position, item);
        notifyItemInserted(position);
    }

    public void deleteItem(int position)
    {
        Timetable itemToRemove=mDataset.get(position);
        mDataset.remove(itemToRemove);

        new DeleteSchedule().execute(itemToRemove.getId());
        notifyItemRemoved(position);
    }
    class DeleteSchedule extends AsyncTask<String, Void, String>
    {
        InputStream is = null;
        int responseCode = 0;
        String returnString="";
        @Override
        protected String doInBackground(String... params)
        {
            String serviceURL = context.getString(R.string.serviceURL)+"/deleteTimetable.php";
            try
            {
                URL url = new URL(serviceURL);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("timetable_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
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

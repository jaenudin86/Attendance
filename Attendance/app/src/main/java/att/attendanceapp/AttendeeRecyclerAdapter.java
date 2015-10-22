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

import DBHelper.Attendee;
import DBHelper.Timetable;


/**
 * Created by rujoota on 22-10-2015.
 */
public class AttendeeRecyclerAdapter extends RecyclerView.Adapter<AttendeeRecyclerAdapter.ViewHolder>
{
    private ArrayList<Attendee> mDataset;
    String courseCode;
    Context context;
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView email,number;
        ImageButton delete;
        public ViewHolder(View itemView) {
            super(itemView);
            email=(TextView)itemView.findViewById(R.id.tvManageAttendeeId);
            number=(TextView)itemView.findViewById(R.id.tvManageAttendeeNumber);

            delete=(ImageButton)itemView.findViewById(R.id.ibtnManageAttendeeDelete);
        }
    }

    public AttendeeRecyclerAdapter(ArrayList<Attendee> myDataset,Context context,String courseCode) {
        mDataset = myDataset;
        this.context=context;
        this.courseCode=courseCode;
    }
    public void deleteItem(int position)
    {
        Attendee itemToRemove=mDataset.get(position);
        mDataset.remove(itemToRemove);
        new DeleteAttendee().execute(itemToRemove.getEmailId(),courseCode);
        notifyItemRemoved(position);
    }
    @Override
    public AttendeeRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendees_recycler_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        Attendee current=mDataset.get(position);
        holder.email.setText(current.getEmailId());
        holder.number.setText(current.getAttendeeNumber());

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
    class DeleteAttendee extends AsyncTask<String, Void, String>
    {
        InputStream is = null;
        int responseCode = 0;
        String returnString="";
        @Override
        protected String doInBackground(String... params)
        {
            String serviceURL = context.getString(R.string.serviceURL)+"/deleteAttendee.php";
            try
            {
                URL url = new URL(serviceURL);
                HttpURLConnection httpUrlConnection=(HttpURLConnection)url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                OutputStream outputStream = httpUrlConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8")+"&"+
                        URLEncoder.encode("course_code", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");;
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

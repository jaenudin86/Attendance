package att.attendanceapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import DBHelper.AttendanceReport;
import DBHelper.Attendee;
import Helper.HelperMethods;

/**
 * Created by rujoota on 12-11-2015.
 */
public class FacultyReportAdapter  extends RecyclerView.Adapter<FacultyReportAdapter.ViewHolder>
{
    private ArrayList<AttendanceReport> mDataset;
    String courseCode;
    Context context;
    boolean isAllPresentChecked=false;
    Boolean presentStates;
    ArrayList<Attendee> absentees;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView email,number,name,date,coursecode;

        public ViewHolder(View itemView) {
            super(itemView);
            email=(TextView)itemView.findViewById(R.id.tvFacultyReportStudentId);
            number=(TextView)itemView.findViewById(R.id.tvFacultyReportStudentNumber);
            name=(TextView)itemView.findViewById(R.id.tvFacultyReportStudentName);
            date=(TextView)itemView.findViewById(R.id.tvFacultyReportAbsentOn);
            coursecode=(TextView)itemView.findViewById(R.id.tvFacultyReportCourseCode);
        }
    }

    public FacultyReportAdapter(ArrayList<AttendanceReport> myDataset,Context context) {
        mDataset = myDataset;
        this.context=context;
    }

    @Override
    public FacultyReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_faculty_recycler_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AttendanceReport current=mDataset.get(position);
        holder.email.setText(current.getEmailId());
        holder.number.setText(current.getAttendeeNumber());
        holder.name.setText(current.getName());
        //holder.date.setText(HelperMethods.convertDateFromSQLToUS(current.getAbsentOn()));
        String presence=current.getPresence();
        if(presence.length()>4)
            presence=presence.substring(0,4);
        holder.date.setText(presence+"%");
        holder.coursecode.setText(current.getCourseCode());
    }
}

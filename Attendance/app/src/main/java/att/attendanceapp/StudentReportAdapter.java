package att.attendanceapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import DBHelper.AttendanceReport;
import DBHelper.Attendee;

/**
 * Created by rujoota on 02-12-2015.
 */
public class StudentReportAdapter extends RecyclerView.Adapter<StudentReportAdapter.ViewHolder>
{

    private ArrayList<AttendanceReport> mDataset;
    String courseCode;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date,coursecode;
        public ViewHolder(View itemView) {
            super(itemView);

            date=(TextView)itemView.findViewById(R.id.tvStudentReportDates);
            coursecode=(TextView)itemView.findViewById(R.id.tvStudentReportCourseCode);
        }
    }

    public StudentReportAdapter(ArrayList<AttendanceReport> myDataset,Context context) {
        mDataset = myDataset;
        this.context=context;
    }

    @Override
    public StudentReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_student_recycler_adapter, parent, false);
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
        holder.coursecode.setText(current.getCourseCode());
        holder.date.setText(current.getAbsentOn());
    }
}

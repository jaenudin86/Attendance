package att.attendanceapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.security.KeyStore;
import java.util.ArrayList;

import DBHelper.Attendee;

/**
 * Created by rujoota on 26-10-2015.
 */
public class FillAttendanceAdapter  extends RecyclerView.Adapter<FillAttendanceAdapter.ViewHolder>
{
    private ArrayList<Attendee> mDataset;
    String courseCode;
    Context context;
    boolean isAllPresentChecked=false;
    Boolean presentStates;
    ArrayList<Attendee> absentees;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView email,number,name;
        RadioButton present,absent;
        RadioGroup radioGroup;
        public ViewHolder(View itemView) {
            super(itemView);
            email=(TextView)itemView.findViewById(R.id.tvFillAttendanceEmailId);
            number=(TextView)itemView.findViewById(R.id.tvFillAttendanceStudentNumber);
            name=(TextView)itemView.findViewById(R.id.tvFillAttendanceStudentName);
            present=(RadioButton)itemView.findViewById(R.id.rbtnFillAttendancePresent);
            absent=(RadioButton)itemView.findViewById(R.id.rbtnFillAttendanceAbsent);
            radioGroup=(RadioGroup)itemView.findViewById(R.id.rgFillAttendance);

        }
    }
    public void setIsAllPresentChecked(Boolean isChecked)
    {
        isAllPresentChecked=isChecked;
    }
    public FillAttendanceAdapter(ArrayList<Attendee> myDataset,Context context,String courseCode) {
        mDataset = myDataset;
        this.context=context;
        this.courseCode=courseCode;
        absentees=new ArrayList<Attendee>();
        //this.presentStates= presentStates;
    }
    public FillAttendanceAdapter(ArrayList<Attendee> myDataset,Context context,String courseCode,Boolean isChecked) {
        mDataset = myDataset;
        this.context=context;
        this.courseCode=courseCode;
        this.isAllPresentChecked=isChecked;
        absentees=new ArrayList<Attendee>();
    }
    @Override
    public FillAttendanceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fill_attendance_recycler_adapter, parent, false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Attendee current=mDataset.get(position);
        holder.email.setText(current.getEmailId());
        holder.number.setText(current.getAttendeeNumber());
        holder.name.setText(current.getName());
        //if(isAllPresentChecked)
        if(isAllPresentChecked)
            holder.present.setChecked(isAllPresentChecked);
        else
        {
            if (current.getIsAbsent().equals("1")) //absent
            {
                holder.absent.setChecked(true);
                absentees.add(mDataset.get(position));
            }
            else if (current.getIsAbsent().equals("0"))
            {
                holder.present.setChecked(true);
            }
        }
        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if(checkedId==R.id.rbtnFillAttendanceAbsent)// absent was changed
                {
                    if(holder.absent.isChecked())
                        absentees.add(mDataset.get(position));
                    else
                    {
                        if (absentees.contains(mDataset.get(position)))
                        {
                            absentees.remove(mDataset.get(position));
                        }
                    }
                }
                else if(checkedId==R.id.rbtnFillAttendancePresent)// absent was changed
                {
                    if(!holder.present.isChecked())
                        absentees.add(mDataset.get(position));
                    else
                    {
                        if (absentees.contains(mDataset.get(position)))
                        {
                            absentees.remove(mDataset.get(position));
                        }
                    }
                }
            }
        });
    }
}

package att.attendanceapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import DBHelper.TimetableSlot;
import Helper.HelperMethods;

/**
 * Created by rujoota on 19-10-2015.
 */
public class RecyclerTimetableAdapter extends RecyclerView.Adapter<RecyclerTimetableAdapter.ViewHolder>
{
    private ArrayList<TimetableSlot> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView courseCode;
        public TextView time;
        public ViewHolder(View itemView) {
            super(itemView);
            courseCode = (TextView)itemView.findViewById(R.id.tvViewTimetableCourseName);
            time=(TextView)itemView.findViewById(R.id.tvViewTimetableCourseTime);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerTimetableAdapter(ArrayList<TimetableSlot> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerTimetableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_recycler_adapter, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.courseCode.setText(mDataset.get(position).getCourseCode());
        String timingStart= HelperMethods.convertToStandardTime(mDataset.get(position).getStartTime());
        String timingEnd= HelperMethods.convertToStandardTime(mDataset.get(position).getEndTime());
        holder.time.setText(timingStart+"-"+timingEnd);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

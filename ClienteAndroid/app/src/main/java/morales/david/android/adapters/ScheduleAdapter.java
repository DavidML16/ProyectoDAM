package morales.david.android.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import morales.david.android.R;
import morales.david.android.models.Course;
import morales.david.android.models.Schedule;
import morales.david.android.utils.Utils;

/**
 * @author David Morales
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private Activity activity;

    private LayoutInflater inflater;

    private List<Schedule> schedulesOriginal;
    private List<Schedule> schedules;

    private FragmentManager fragmentManager;

    public ScheduleAdapter(Activity activity, FragmentManager fragmentManager, List<Schedule> schedules) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.schedules = new ArrayList<>(schedules);
        this.schedulesOriginal = new ArrayList<>(schedules);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {

        Schedule schedule = getSchedule(position);

        if(schedule.getTimeZone() != null)
            holder.dayTextView.setText(schedule.getTimeZone().getDay().getName());

        if(schedule.getTimeZone() != null)
            holder.hourTextView.setText(schedule.getTimeZone().getHour().getName());

        if(schedule.getTeacher() != null)
            holder.teacherTextView.setText(activity.getString(R.string.item_schedule_teacher, schedule.getTeacher().getName()));

        if(schedule.getSubject() != null)
            holder.subjectTextView.setText(activity.getString(R.string.item_schedule_subject, schedule.getSubject().getAbreviation()));

        if(schedule.getSubject() != null)
            holder.cardView.setCardBackgroundColor(Color.parseColor(schedule.getSubject().getColor()));

        if(schedule.getClassroom() != null)
            holder.classroomTextView.setText(activity.getString(R.string.item_schedule_classroom, schedule.getClassroom().getName()));

        if(schedule.getGroup() != null)
            holder.groupTextView.setText(activity.getString(R.string.item_schedule_group, schedule.getGroup().toString()));

    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = new ArrayList<>(schedules);
        this.schedulesOriginal = new ArrayList<>(schedules);
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        return scheduleFilter;
    }

    private Filter scheduleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Schedule> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(schedulesOriginal);
            } else {
                String filterPattern = constraint.toString().trim();

                for (Schedule item : schedulesOriginal) {

                    if(item.getTeacher() != null) {
                        if (Utils.containsIgnoreCase(item.getTeacher().getName(), filterPattern)) {
                            filteredList.add(item);
                            continue;
                        }
                    }

                    if(item.getGroup() != null) {
                        if (Utils.containsIgnoreCase(item.getGroup().toString(), filterPattern)) {
                            filteredList.add(item);
                            continue;
                        }
                    }

                    if(item.getSubject() != null) {
                        if (Utils.containsIgnoreCase(item.getSubject().getAbreviation(), filterPattern) || Utils.containsIgnoreCase(item.getSubject().getName(), filterPattern)) {
                            filteredList.add(item);
                            continue;
                        }
                    }

                    if(item.getClassroom() != null) {
                        if (Utils.containsIgnoreCase(item.getClassroom().getName(), filterPattern)) {
                            filteredList.add(item);
                            continue;
                        }
                    }

                    if(item.getTimeZone() != null) {
                        if (Utils.containsIgnoreCase(item.getTimeZone().getDay().getName(), filterPattern) || Utils.containsIgnoreCase(item.getTimeZone().getHour().getName(), filterPattern)) {
                            filteredList.add(item);
                            continue;
                        }
                    }

                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            schedules.clear();
            schedules.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public Schedule getSchedule(int position) {
        return schedules.get(position);
    }

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView dayTextView, hourTextView, teacherTextView, subjectTextView, classroomTextView, groupTextView;

        public ScheduleViewHolder(@NonNull View itemView) {

            super(itemView);

            dayTextView = itemView.findViewById(R.id.item_schedule_day);
            hourTextView = itemView.findViewById(R.id.item_schedule_hour);
            teacherTextView = itemView.findViewById(R.id.item_schedule_teacher);
            subjectTextView = itemView.findViewById(R.id.item_schedule_subject);
            classroomTextView = itemView.findViewById(R.id.item_schedule_classroom);
            groupTextView = itemView.findViewById(R.id.item_schedule_group);

            cardView = itemView.findViewById(R.id.item_schedule_cardview);

        }

    }

}

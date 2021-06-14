package morales.david.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import morales.david.android.managers.DataManager;
import morales.david.android.models.Course;
import morales.david.android.models.Group;
import morales.david.android.models.Subject;

/**
 * @author David Morales
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.SubjectViewHolder> {

    private Context activity;

    private LayoutInflater inflater;

    private List<Group> groups;
    private List<Group> groupsOriginal;

    private FragmentManager fragmentManager;

    public GroupsAdapter(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.groups = new ArrayList<>(DataManager.getInstance().getGroups().getValue());
        this.groupsOriginal = new ArrayList<>(groups);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_group, parent, false);
        return new SubjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Group group = getGroup(position);
        holder.courseTextView.setText(activity.getString(R.string.item_group_course, group.getCourse().toString()));
        holder.letterTextView.setText(activity.getString(R.string.item_group_letter, group.getLetter()));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void setGroups(List<Group> groups) {
        this.groups = new ArrayList<>(groups);
        this.groupsOriginal = new ArrayList<>(groups);
        notifyDataSetChanged();
    }

    public Group getGroup(int position) {
        return groups.get(position);
    }

    public Filter getFilter() {
        return groupFilter;
    }

    public Filter getCourseFilter() {
        return groupCourseFilter;
    }

    private Filter groupFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Group> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(groupsOriginal);
            } else {
                String filterPattern = constraint.toString().trim();

                for (Group item : groupsOriginal) {
                    if (item.getCourse().toString().contains(filterPattern) || item.getLetter().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            groups.clear();
            groups.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    private Filter groupCourseFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Group> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(groupsOriginal);
            } else {
                String filterPattern = constraint.toString().trim();

                for (Group item : groupsOriginal) {
                    if (item.getCourse().toString().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }

            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            groups.clear();
            groups.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class SubjectViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView courseTextView, letterTextView;

        public SubjectViewHolder(@NonNull View itemView) {

            super(itemView);

            cardView = itemView.findViewById(R.id.item_group_cardview);
            courseTextView = itemView.findViewById(R.id.item_group_course);
            letterTextView = itemView.findViewById(R.id.item_group_letter);

        }

    }

}

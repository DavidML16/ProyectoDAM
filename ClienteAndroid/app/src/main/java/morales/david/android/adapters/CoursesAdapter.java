package morales.david.android.adapters;

import android.app.Activity;
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
import morales.david.android.fragments.CourseOptionsDialogFragment;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Course;

/**
 * @author David Morales
 */
public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CoursesViewHolder> {

    private Activity activity;

    private LayoutInflater inflater;

    private List<Course> courses;
    private List<Course> coursesOriginal;

    private FragmentManager fragmentManager;

    public CoursesAdapter(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.courses = new ArrayList<>(DataManager.getInstance().getCourses().getValue());
        this.coursesOriginal = new ArrayList<>(courses);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public CoursesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_course, parent, false);
        return new CoursesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursesViewHolder holder, int position) {
        Course course = getCourse(position);
        holder.nameTextView.setText(activity.getString(R.string.item_course_name, course.toString()));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void setCourses(List<Course> courses) {
        this.courses = new ArrayList<>(courses);
        this.coursesOriginal = new ArrayList<>(courses);
        notifyDataSetChanged();
    }

    public Course getCourse(int position) {
        return courses.get(position);
    }

    public Filter getFilter() {
        return courseFilter;
    }

    private Filter courseFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Course> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(coursesOriginal);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Course item : coursesOriginal) {
                    if (item.getName().toLowerCase().contains(filterPattern)
                            || item.getLevel().toLowerCase().contains(filterPattern)) {
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
            courses.clear();
            courses.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class CoursesViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView nameTextView;

        public CoursesViewHolder(@NonNull View itemView) {

            super(itemView);

            cardView = itemView.findViewById(R.id.item_course_cardview);
            nameTextView = itemView.findViewById(R.id.item_course_name);

            cardView.setOnClickListener(v -> {
                CourseOptionsDialogFragment dialog =
                        CourseOptionsDialogFragment.newInstance(getCourse(getAdapterPosition()));
                dialog.show(fragmentManager, "OptionBottomSheet");
            });

        }

    }

}

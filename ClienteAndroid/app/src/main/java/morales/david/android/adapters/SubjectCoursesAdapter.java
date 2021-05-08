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
public class SubjectCoursesAdapter extends RecyclerView.Adapter<SubjectCoursesAdapter.CoursesViewHolder> {

    private Activity activity;

    private LayoutInflater inflater;

    private List<Course> courses;

    public SubjectCoursesAdapter(Activity activity, List<Course> courses) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.courses = new ArrayList<>(courses);
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
        notifyDataSetChanged();
    }

    public Course getCourse(int position) {
        return courses.get(position);
    }

    public class CoursesViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView nameTextView;

        public CoursesViewHolder(@NonNull View itemView) {

            super(itemView);

            cardView = itemView.findViewById(R.id.item_course_cardview);
            nameTextView = itemView.findViewById(R.id.item_course_name);

        }

    }

}

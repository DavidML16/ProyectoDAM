package morales.david.android.adapters;

import android.app.Activity;
import android.content.Context;
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
import morales.david.android.fragments.SubjectCoursesDialogFragment;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Course;
import morales.david.android.models.Subject;
import morales.david.android.models.Teacher;

/**
 * @author David Morales
 */
public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {

    private Context activity;

    private LayoutInflater inflater;

    private List<Subject> subjects;
    private List<Subject> subjectsOriginal;

    private FragmentManager fragmentManager;

    public SubjectsAdapter(Activity activity, FragmentManager fragmentManager) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.subjects = new ArrayList<>(DataManager.getInstance().getSubjects().getValue());
        this.subjectsOriginal = new ArrayList<>(subjects);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = getSubject(position);
        holder.nameTextView.setText(subject.getName());
        holder.numberTextView.setText(activity.getString(R.string.item_subject_number, Integer.toString(subject.getNumber())));
        holder.abreviationTextView.setText(activity.getString(R.string.item_subject_abreviation, subject.getAbreviation()));
        holder.coursesTextView.setText(activity.getString(R.string.item_subject_courses, Integer.toString(subject.getCourses().size())));
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = new ArrayList<>(subjects);
        this.subjectsOriginal = new ArrayList<>(subjects);
        notifyDataSetChanged();
    }

    public Subject getSubject(int position) {
        return subjects.get(position);
    }

    public Filter getFilter() {
        return subjectFilter;
    }

    public Filter getCourseFilter() {
        return subjectCourseFilter;
    }

    private Filter subjectFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Subject> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(subjectsOriginal);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Subject item : subjectsOriginal) {
                    if (item.getName().toLowerCase().contains(filterPattern)
                            || Integer.toString(item.getNumber()).toLowerCase().contains(filterPattern)
                            || item.getAbreviation().toLowerCase().contains(filterPattern)) {
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
            subjects.clear();
            subjects.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    private Filter subjectCourseFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Subject> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(subjectsOriginal);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Subject item : subjectsOriginal) {
                    for(Course course : item.getCourses()) {
                        if (course.toString().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                            break;
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
            subjects.clear();
            subjects.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class SubjectViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView nameTextView, numberTextView, abreviationTextView, coursesTextView;

        public SubjectViewHolder(@NonNull View itemView) {

            super(itemView);

            cardView = itemView.findViewById(R.id.item_subject_cardview);
            nameTextView = itemView.findViewById(R.id.item_subject_name);
            numberTextView = itemView.findViewById(R.id.item_subject_number);
            abreviationTextView = itemView.findViewById(R.id.item_subject_abreviation);
            coursesTextView = itemView.findViewById(R.id.item_subject_courses);

            cardView.setOnClickListener(v -> {
                SubjectCoursesDialogFragment dialog =
                        SubjectCoursesDialogFragment.newInstance(getSubject(getAdapterPosition()));
                dialog.show(fragmentManager, "OptionBottomSheet");
            });

        }

    }

}

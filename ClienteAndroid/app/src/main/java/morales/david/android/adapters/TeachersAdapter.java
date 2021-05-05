package morales.david.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import morales.david.android.R;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Teacher;

/**
 * @author David Morales
 */
public class TeachersAdapter extends RecyclerView.Adapter<TeachersAdapter.TeacherViewHolder> {

    private Context activity;

    private LayoutInflater inflater;

    private List<Teacher> teachers;
    private List<Teacher> teachersOriginal;

    public TeachersAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.teachers = new ArrayList<>(DataManager.getInstance().getTeachers().getValue());
        this.teachersOriginal = new ArrayList<>(teachers);
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = getTeacher(position);
        holder.nameTextView.setText(teacher.getName());
        holder.departmentTextView.setText(activity.getString(R.string.item_teacher_department, teacher.getDepartment()));
        holder.abreviationTextView.setText(activity.getString(R.string.item_teacher_abreviation, teacher.getAbreviation()));
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = new ArrayList<>(teachers);
        this.teachersOriginal = new ArrayList<>(teachers);
        notifyDataSetChanged();
    }

    public Teacher getTeacher(int position) {
        return teachers.get(position);
    }

    public Filter getFilter() {
        return teacherFilter;
    }

    public Filter getDepartmentFilter() {
        return teacherDepartmentFilter;
    }

    private Filter teacherFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Teacher> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(teachersOriginal);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Teacher item : teachersOriginal) {
                    if (item.getName().toLowerCase().contains(filterPattern)
                            || item.getDepartment().toLowerCase().contains(filterPattern)
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
            teachers.clear();
            teachers.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    private Filter teacherDepartmentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Teacher> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(teachersOriginal);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Teacher item : teachersOriginal) {
                    if (item.getDepartment().toLowerCase().contains(filterPattern)) {
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
            teachers.clear();
            teachers.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class TeacherViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView nameTextView, departmentTextView, abreviationTextView;

        public TeacherViewHolder(@NonNull View itemView) {

            super(itemView);

            cardView = itemView.findViewById(R.id.item_teacher_cardview);
            nameTextView = itemView.findViewById(R.id.item_teacher_name);
            departmentTextView = itemView.findViewById(R.id.item_teacher_department);
            abreviationTextView = itemView.findViewById(R.id.item_teacher_abreviation);

        }

    }

}

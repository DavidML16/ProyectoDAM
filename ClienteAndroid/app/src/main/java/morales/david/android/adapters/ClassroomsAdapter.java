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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import morales.david.android.R;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Classroom;
import morales.david.android.models.Teacher;

/**
 * @author David Morales
 */
public class ClassroomsAdapter extends RecyclerView.Adapter<ClassroomsAdapter.ClassroomViewHolder> {

    private Context activity;

    private LayoutInflater inflater;

    private List<Classroom> classrooms;
    private List<Classroom> classroomsOriginal;

    public ClassroomsAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.classrooms = new ArrayList<>(DataManager.getInstance().getClassrooms().getValue());
        this.classroomsOriginal = new ArrayList<>(classrooms);
    }

    public ClassroomsAdapter(Activity activity, List<Classroom> classrooms) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.classrooms = new ArrayList<>(classrooms);
        this.classroomsOriginal = new ArrayList<>(classrooms);
        getFilter().filter(null);
    }

    @NonNull
    @Override
    public ClassroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_classroom, parent, false);
        return new ClassroomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassroomViewHolder holder, int position) {
        Classroom classroom = getClassroom(position);
        holder.nameTextView.setText(activity.getString(R.string.item_classroom_name, classroom.getName()));
    }

    @Override
    public int getItemCount() {
        return classrooms.size();
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = new ArrayList<>(classrooms);
        this.classroomsOriginal = new ArrayList<>(classrooms);
        notifyDataSetChanged();
    }

    public Classroom getClassroom(int position) {
        return classrooms.get(position);
    }

    public Filter getFilter() {
        return classroomFilter;
    }

    private Filter classroomFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Classroom> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(classroomsOriginal);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Classroom item : classroomsOriginal) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
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
            classrooms.clear();
            classrooms.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class ClassroomViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView nameTextView;

        public ClassroomViewHolder(@NonNull View itemView) {

            super(itemView);

            cardView = itemView.findViewById(R.id.item_classroom_cardview);
            nameTextView = itemView.findViewById(R.id.item_classroom_name);

        }

    }

}

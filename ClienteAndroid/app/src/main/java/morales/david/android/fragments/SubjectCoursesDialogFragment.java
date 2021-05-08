package morales.david.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import morales.david.android.R;
import morales.david.android.adapters.SubjectCoursesAdapter;
import morales.david.android.models.Subject;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     ScheduleListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class SubjectCoursesDialogFragment extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SubjectCoursesAdapter adapter;

    public static SubjectCoursesDialogFragment newInstance(Subject subject) {
        final SubjectCoursesDialogFragment fragment = new SubjectCoursesDialogFragment();
        final Bundle args = new Bundle();
        args.putSerializable("subject", subject);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subject_courses_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.fragment_subjectcourses_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        Subject subject = (Subject) getArguments().getSerializable("subject");

        TextView subjectNameTextView = view.findViewById(R.id.fragment_subjectcourses_info_line2);
        subjectNameTextView.setText(subject.getName());

        adapter = new SubjectCoursesAdapter(getActivity(), subject.getCourses());

        recyclerView.setAdapter(adapter);

    }

}
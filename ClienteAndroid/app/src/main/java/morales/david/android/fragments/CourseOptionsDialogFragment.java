
package morales.david.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import morales.david.android.R;
import morales.david.android.interfaces.OptionClicked;
import morales.david.android.models.Course;

/**
 * @author David Morales
 */
public class CourseOptionsDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private Course course;

    private OptionClicked callback;

    public CourseOptionsDialogFragment() { }

    public static CourseOptionsDialogFragment newInstance(Course course) {
        CourseOptionsDialogFragment fragment = new CourseOptionsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("course", course);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            course = (Course) getArguments().getSerializable("course");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_course_options_dialog, container, false);

        TextView courseTextView = view.findViewById(R.id.option_course_textview);
        courseTextView.setText(getString(R.string.course_options_name, Integer.toString(course.getLevel()), course.getName()));

        view.findViewById(R.id.option_groups_cardview).setOnClickListener(this);
        view.findViewById(R.id.option_subjects_cardview).setOnClickListener(this);

        return view;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OptionClicked) {
            callback = (OptionClicked) context;
        }
    }

    @Override
    public void onClick(View v) {

        callback.onClick(v);

    }

}
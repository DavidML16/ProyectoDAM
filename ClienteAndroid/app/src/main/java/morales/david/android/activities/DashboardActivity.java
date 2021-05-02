package morales.david.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import morales.david.android.R;
import morales.david.android.managers.DataManager;
import morales.david.android.managers.ScreenManager;

public class DashboardActivity extends AppCompatActivity {

    private TextView teachersTextView, coursesTextView, classroomsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ScreenManager.getInstance().setActivity(this);

        teachersTextView = findViewById(R.id.act_dashboard_textview_teachers);
        coursesTextView = findViewById(R.id.act_dashboard_textview_courses);
        classroomsTextView = findViewById(R.id.act_dashboard_textview_classrooms);

        DataManager.getInstance().getTeachers().observe(this, teachers -> {
            teachersTextView.setText(Integer.toString(teachers.size()));
        });

        DataManager.getInstance().getClassrooms().observe(this, classrooms -> {
            classroomsTextView.setText(Integer.toString(classrooms.size()));
        });

        DataManager.getInstance().getCourses().observe(this, courses -> {
            coursesTextView.setText(Integer.toString(courses.size()));
        });

    }

}
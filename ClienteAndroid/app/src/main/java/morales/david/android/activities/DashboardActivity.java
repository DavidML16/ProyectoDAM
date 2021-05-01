package morales.david.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import morales.david.android.R;
import morales.david.android.managers.DataManager;

public class DashboardActivity extends AppCompatActivity {

    private TextView teachersTextView, credentialsTextView, coursesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        teachersTextView = findViewById(R.id.act_dashboard_textview_teachers);
        credentialsTextView = findViewById(R.id.act_dashboard_textview_credentials);
        coursesTextView = findViewById(R.id.act_dashboard_textview_courses);

        DataManager.getInstance().getTeachers().observe(this, teachers -> {
            teachersTextView.setText("Profesores: " + teachers.size());
        });

        DataManager.getInstance().getCredentials().observe(this, credentials -> {
            credentialsTextView.setText("Credenciales: " + credentials.size());
        });

        DataManager.getInstance().getCourses().observe(this, courses -> {
            coursesTextView.setText("Cursos: " + courses.size());
        });

    }

}
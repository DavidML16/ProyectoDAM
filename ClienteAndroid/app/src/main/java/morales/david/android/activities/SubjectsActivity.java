package morales.david.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import java.util.HashSet;
import java.util.Set;

import morales.david.android.R;
import morales.david.android.adapters.SubjectsAdapter;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Classroom;
import morales.david.android.models.Course;
import morales.david.android.utils.ActionBarUtil;

public class SubjectsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private AutoCompleteTextView courseDropDown;
    private ArrayAdapter<String> coursesAdapter;
    private ImageView unfilterImageView;

    private SubjectsAdapter adapter;

    private Course intentCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects);

        getSupportActionBar().setTitle(getString(R.string.act_subjects_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarUtil.changeStyle(this, getSupportActionBar());

        recyclerView = findViewById(R.id.act_subjects_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
            intentCourse = (Course) extras.getSerializable("course");

        Set<String> courses = getCourses();
        String[] coursesArray = new String[courses.size()];
        courses.toArray(coursesArray);

        coursesAdapter = new ArrayAdapter(this, R.layout.item_dropdown, coursesArray);

        courseDropDown = findViewById(R.id.act_subjects_spinner_course);
        courseDropDown.setOnItemClickListener((parent, view, position, id) -> {
            String selected = coursesAdapter.getItem(position);
            adapter.getCourseFilter().filter(selected);
        });

        courseDropDown.setAdapter(coursesAdapter);

        unfilterImageView = findViewById(R.id.act_subjects_unfilter);
        unfilterImageView.setOnClickListener(v -> {
            adapter.getCourseFilter().filter(null);
            adapter.getFilter().filter(null);
            courseDropDown.setText(null);
            courseDropDown.clearFocus();
        });

        adapter = new SubjectsAdapter(this, getSupportFragmentManager());

        recyclerView.setAdapter(adapter);

        DataManager.getInstance().getSubjects().observe(this, subjects -> {

            adapter.setSubjects(subjects);

            if(intentCourse != null) {
                adapter.getCourseFilter().filter(intentCourse.toString());
                courseDropDown.setText(intentCourse.toString());
            }

        });

    }

    @Override
    protected void onResume() {

        super.onResume();

        Set<String> courses = getCourses();
        String[] coursesArray = new String[courses.size()];
        courses.toArray(coursesArray);

        coursesAdapter = new ArrayAdapter(this, R.layout.item_dropdown, coursesArray);
        courseDropDown.setAdapter(coursesAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                courseDropDown.setText(null);
                courseDropDown.clearFocus();
                return false;
            }
        });
        return true;
    }

    private Set<String> getCourses() {

        Set<String> courses = new HashSet<>();

        if (DataManager.getInstance().getCourses().getValue() == null)
            return courses;

        for (Course course : DataManager.getInstance().getCourses().getValue())
            courses.add(course.toString());

        return courses;

    }

}
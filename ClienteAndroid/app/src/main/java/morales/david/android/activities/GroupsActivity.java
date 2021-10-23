package morales.david.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import java.util.HashSet;
import java.util.Set;

import morales.david.android.R;
import morales.david.android.adapters.GroupsAdapter;
import morales.david.android.adapters.SubjectsAdapter;
import morales.david.android.databinding.ActivityDashboardBinding;
import morales.david.android.databinding.ActivityGroupsBinding;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Course;
import morales.david.android.utils.ActionBarUtil;

public class GroupsActivity extends AppCompatActivity {

    private ActivityGroupsBinding binding;

    private ArrayAdapter<String> coursesAdapter;

    private GroupsAdapter adapter;

    private Course intentCourse;

    private boolean searchOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityGroupsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener((v) -> onBackPressed());

        binding.searchButton.setOnClickListener((v) -> {

            binding.textView2.setVisibility(View.GONE);
            binding.searchButton.setVisibility(View.GONE);

            binding.searchEditText.setVisibility(View.VISIBLE);
            binding.searchEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

            binding.backButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_close_24));

            searchOpen = true;

        });

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                binding.courseSpinner.setText(null);
                binding.courseSpinner.clearFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.textView2.setAlpha(0f);
        binding.textView2.animate().alpha(1f).setDuration(350).setStartDelay(300).start();

        binding.groupsRecyclerView.setHasFixedSize(true);
        binding.groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        if(extras != null)
            intentCourse = (Course) extras.getSerializable("course");

        Set<String> courses = getCourses();
        String[] coursesArray = new String[courses.size()];
        courses.toArray(coursesArray);

        coursesAdapter = new ArrayAdapter(this, R.layout.item_dropdown, coursesArray);

        binding.courseSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selected = coursesAdapter.getItem(position);
            adapter.getCourseFilter().filter(selected);
        });

        binding.courseSpinner.setAdapter(coursesAdapter);

        binding.unfilterButton.setOnClickListener(v -> {
            adapter.getCourseFilter().filter(null);
            adapter.getFilter().filter(null);
            binding.courseSpinner.setText(null);
            binding.courseSpinner.clearFocus();
        });

        adapter = new GroupsAdapter(this, getSupportFragmentManager());

        binding.groupsRecyclerView.setAdapter(adapter);

        DataManager.getInstance().getGroups().observe(this, groups -> {

            adapter.setGroups(groups);

            if(intentCourse != null) {
                adapter.getCourseFilter().filter(intentCourse.toString());
                binding.courseSpinner.setText(intentCourse.toString());
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
        binding.courseSpinner.setAdapter(coursesAdapter);

    }

    @Override
    public void onBackPressed() {

        if(searchOpen) {

            binding.textView2.setVisibility(View.VISIBLE);
            binding.searchButton.setVisibility(View.VISIBLE);
            binding.searchEditText.setVisibility(View.GONE);

            binding.searchEditText.setText("");
            binding.searchEditText.clearFocus();
            adapter.getFilter().filter(null);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.searchEditText.getWindowToken(), 0);

            binding.backButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_chevron_left_24));

            searchOpen = false;

        } else {

            if(intentCourse == null) {

                Intent intent = new Intent(this, DashboardActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.textView2, "title"));
                startActivity(intent, options.toBundle());

            } else {

                super.onBackPressed();

            }

        }

    }

    private Set<String> getCourses() {

        Set<String> courses = new HashSet<>();

        if (DataManager.getInstance().getCourses().getValue() == null)
            return courses;

        for (Course course : DataManager.getInstance().getCourses().getValue())
            courses.add(course.toString().toUpperCase());

        return courses;

    }

}
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

import morales.david.android.R;
import morales.david.android.adapters.CoursesAdapter;
import morales.david.android.databinding.ActivityClassroomsBinding;
import morales.david.android.databinding.ActivityCoursesBinding;
import morales.david.android.interfaces.OptionClicked;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Course;
import morales.david.android.models.Subject;
import morales.david.android.utils.ActionBarUtil;

public class CoursesActivity extends AppCompatActivity implements OptionClicked {

    private ActivityCoursesBinding binding;

    private CoursesAdapter adapter;

    private boolean searchOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityCoursesBinding.inflate(getLayoutInflater());
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
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.textView2.setAlpha(0f);
        binding.textView2.animate().alpha(1f).setDuration(250).setStartDelay(300).start();

        binding.coursesRecyclerView.setHasFixedSize(true);
        binding.coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CoursesAdapter(this, getSupportFragmentManager());

        binding.coursesRecyclerView.setAdapter(adapter);

        DataManager.getInstance().getCourses().observe(this, courses -> {
            adapter.setCourses(courses);
        });

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

            Intent intent = new Intent(this, DashboardActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.textView2, "title"));
            startActivity(intent, options.toBundle());

        }

    }

    @Override
    public void onClick(View item, Object argument) {

        Course course = null;
        if(argument != null)
            course = (Course) argument;

        Intent intent = null;

        switch (item.getId()) {
            case R.id.option_groups_cardview:
                intent = new Intent(this, GroupsActivity.class);
                intent.putExtra("course", course);
                break;
            case R.id.option_subjects_cardview:
                intent = new Intent(this, SubjectsActivity.class);
                intent.putExtra("course", course);
                break;
        }

        startActivity(intent);

    }

}
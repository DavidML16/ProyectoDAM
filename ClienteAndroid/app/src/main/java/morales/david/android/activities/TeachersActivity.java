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
import morales.david.android.adapters.TeachersAdapter;
import morales.david.android.databinding.ActivitySubjectsBinding;
import morales.david.android.databinding.ActivityTeachersBinding;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Teacher;
import morales.david.android.utils.ActionBarUtil;

public class TeachersActivity extends AppCompatActivity {

    private ActivityTeachersBinding binding;

    private ArrayAdapter<String> departmentsAdapter;

    private TeachersAdapter adapter;

    private boolean searchOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityTeachersBinding.inflate(getLayoutInflater());
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
                binding.departmentSpinner.setText(null);
                binding.departmentSpinner.clearFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.textView2.setAlpha(0f);
        binding.textView2.animate().alpha(1f).setDuration(250).setStartDelay(300).start();

        binding.teachersRecyclerView.setHasFixedSize(true);
        binding.teachersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Set<String> departments = getDepartments();
        String[] departmentsArray = new String[departments.size()];
        departments.toArray(departmentsArray);

        departmentsAdapter = new ArrayAdapter(this, R.layout.item_dropdown, departmentsArray);

        binding.departmentSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selected = departmentsAdapter.getItem(position);
            adapter.getDepartmentFilter().filter(selected);
        });

        binding.departmentSpinner.setAdapter(departmentsAdapter);

        binding.unfilter.setOnClickListener(v -> {
            adapter.getDepartmentFilter().filter(null);
            adapter.getFilter().filter(null);
            binding.departmentSpinner.setText(null);
            binding.departmentSpinner.clearFocus();
        });

        adapter = new TeachersAdapter(this);

        binding.teachersRecyclerView.setAdapter(adapter);

        DataManager.getInstance().getTeachers().observe(this, teachers -> {
            adapter.setTeachers(teachers);
        });

    }

    @Override
    protected void onResume() {

        super.onResume();

        Set<String> departments = getDepartments();
        String[] departmentsArray = new String[departments.size()];
        departments.toArray(departmentsArray);

        departmentsAdapter = new ArrayAdapter(this, R.layout.item_dropdown, departmentsArray);
        binding.departmentSpinner.setAdapter(departmentsAdapter);

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

    private Set<String> getDepartments() {

        Set<String> departments = new HashSet<>();

        for(Teacher teacher : DataManager.getInstance().getTeachers().getValue())
            departments.add(teacher.getDepartment());

        return departments;

    }

}
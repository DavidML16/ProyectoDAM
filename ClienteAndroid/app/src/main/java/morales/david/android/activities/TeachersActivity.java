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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import java.util.HashSet;
import java.util.Set;

import morales.david.android.R;
import morales.david.android.adapters.TeachersAdapter;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Teacher;
import morales.david.android.utils.ActionBarUtil;

public class TeachersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private AutoCompleteTextView departmentDropDown;
    private ArrayAdapter<String> departmentsAdapter;
    private ImageView unfilterImageView;

    private TeachersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);

        getSupportActionBar().setTitle(getString(R.string.act_teachers_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarUtil.changeStyle(this, getSupportActionBar());

        recyclerView = findViewById(R.id.act_teachers_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Set<String> departments = getDepartments();
        String[] departmentsArray = new String[departments.size()];
        departments.toArray(departmentsArray);

        departmentsAdapter = new ArrayAdapter(this, R.layout.item_dropdown, departmentsArray);

        departmentDropDown = findViewById(R.id.act_teachers_spinner_department);
        departmentDropDown.setOnItemClickListener((parent, view, position, id) -> {
            String selected = departmentsAdapter.getItem(position);
            adapter.getDepartmentFilter().filter(selected);
        });

        departmentDropDown.setAdapter(departmentsAdapter);

        unfilterImageView = findViewById(R.id.act_teachers_unfilter);
        unfilterImageView.setOnClickListener(v -> {
            adapter.getDepartmentFilter().filter(null);
            adapter.getFilter().filter(null);
            departmentDropDown.setText(null);
            departmentDropDown.clearFocus();
        });

        adapter = new TeachersAdapter(this);

        recyclerView.setAdapter(adapter);

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
        departmentDropDown.setAdapter(departmentsAdapter);

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
                departmentDropDown.setText(null);
                departmentDropDown.clearFocus();
                return false;
            }
        });
        return true;
    }

    private Set<String> getDepartments() {

        Set<String> departments = new HashSet<>();

        for(Teacher teacher : DataManager.getInstance().getTeachers().getValue())
            departments.add(teacher.getDepartment());

        return departments;

    }

}
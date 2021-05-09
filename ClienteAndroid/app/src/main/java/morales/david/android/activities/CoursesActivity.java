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

import morales.david.android.R;
import morales.david.android.adapters.CoursesAdapter;
import morales.david.android.interfaces.OptionClicked;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Course;
import morales.david.android.models.Subject;
import morales.david.android.utils.ActionBarUtil;

public class CoursesActivity extends AppCompatActivity implements OptionClicked {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private CoursesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        getSupportActionBar().setTitle(getString(R.string.act_courses_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarUtil.changeStyle(this, getSupportActionBar());

        recyclerView = findViewById(R.id.act_courses_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CoursesAdapter(this, getSupportFragmentManager());

        recyclerView.setAdapter(adapter);

        DataManager.getInstance().getCourses().observe(this, courses -> {
            adapter.setCourses(courses);
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
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
                return false;
            }
        });
        return true;
    }

    @Override
    public void onClick(View item, Object argument) {

        Course course = null;
        if(argument != null)
            course = (Course) argument;

        switch (item.getId()) {
            case R.id.option_groups_cardview:

                break;
            case R.id.option_subjects_cardview:
                Intent intent = new Intent(this, SubjectsActivity.class);
                intent.putExtra("course", course);
                startActivity(intent);
                break;
        }

    }

}
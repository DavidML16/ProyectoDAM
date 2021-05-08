package morales.david.android.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.Set;

import morales.david.android.R;
import morales.david.android.adapters.ClassroomsAdapter;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Classroom;

public class ClassroomsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private AutoCompleteTextView floorDropDown;
    private ArrayAdapter<String> floorsAdapter;
    private ImageView unfilterImageView;

    private ClassroomsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classrooms);

        getSupportActionBar().setTitle(getString(R.string.act_classrooms_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.act_classrooms_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Set<String> floors = getFloors();
        String[] floorsArray = new String[floors.size()];
        floors.toArray(floorsArray);

        floorsAdapter = new ArrayAdapter(this, R.layout.item_dropdown, floorsArray);

        floorDropDown = findViewById(R.id.act_classrooms_spinner_floor);
        floorDropDown.setOnItemClickListener((parent, view, position, id) -> {
            String selected = floorsAdapter.getItem(position);
            adapter.getFloorFilter().filter(selected);
        });

        floorDropDown.setAdapter(floorsAdapter);

        unfilterImageView = findViewById(R.id.act_classrooms_unfilter);
        unfilterImageView.setOnClickListener(v -> {
            adapter.getFloorFilter().filter(null);
            adapter.getFilter().filter(null);
            floorDropDown.setText(null);
            floorDropDown.clearFocus();
        });

        adapter = new ClassroomsAdapter(this);

        recyclerView.setAdapter(adapter);

        DataManager.getInstance().getClassrooms().observe(this, classrooms -> {
            adapter.setClassrooms(classrooms);
        });

    }

    @Override
    protected void onResume() {

        super.onResume();

        Set<String> floors = getFloors();
        String[] floorsArray = new String[floors.size()];
        floors.toArray(floorsArray);

        floorsAdapter = new ArrayAdapter(this, R.layout.item_dropdown, floorsArray);
        floorDropDown.setAdapter(floorsAdapter);

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
                floorDropDown.setText(null);
                floorDropDown.clearFocus();
                return false;
            }
        });
        return true;
    }

    private Set<String> getFloors() {

        Set<String> floors = new HashSet<>();

        if(DataManager.getInstance().getClassrooms().getValue() == null)
            return floors;

        for(Classroom classroom : DataManager.getInstance().getClassrooms().getValue())
            floors.add(Integer.toString(classroom.getFloor()));

        return floors;

    }

}
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

import java.util.List;

import morales.david.android.R;
import morales.david.android.adapters.CoursesAdapter;
import morales.david.android.adapters.ScheduleAdapter;
import morales.david.android.databinding.ActivityGroupsBinding;
import morales.david.android.databinding.ActivityScheduleBinding;
import morales.david.android.managers.DataManager;
import morales.david.android.models.Schedule;
import morales.david.android.utils.ActionBarUtil;

public class ScheduleActivity extends AppCompatActivity {

    private ActivityScheduleBinding binding;

    private ScheduleAdapter adapter;

    private List<Schedule> schedules;

    private boolean searchOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
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

        binding.schedulesRecyclerView.setHasFixedSize(true);
        binding.schedulesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        schedules = (List<Schedule>) extras.getSerializable("schedules");

        adapter = new ScheduleAdapter(this, getSupportFragmentManager(), schedules);

        binding.schedulesRecyclerView.setAdapter(adapter);

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

            Intent intent = new Intent(this, SearchScheduleActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.textView2, "title"));
            startActivity(intent, options.toBundle());

        }

    }

}
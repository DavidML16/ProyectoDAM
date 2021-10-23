package morales.david.android.activities;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

import morales.david.android.databinding.ActivityClassroomsBinding;
import morales.david.android.netty.ClientManager;
import morales.david.android.R;
import morales.david.android.adapters.ClassroomsAdapter;
import morales.david.android.fragments.EmptyClassroomsDialogFragment;
import morales.david.android.fragments.SearchEmptyClassroomsDialogFragment;
import morales.david.android.interfaces.OptionClicked;
import morales.david.android.managers.DataManager;
import morales.david.android.managers.eventcallbacks.EmptyClassroomsConfirmationListener;
import morales.david.android.managers.eventcallbacks.ErrorEventListener;
import morales.david.android.managers.eventcallbacks.EventManager;
import morales.david.android.models.Classroom;
import morales.david.android.models.TimeZone;
import morales.david.android.models.packets.Packet;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;
import morales.david.android.utils.ActionBarUtil;

public class ClassroomsActivity extends AppCompatActivity implements OptionClicked {

    private ClassroomsAdapter adapter;

    private ActivityClassroomsBinding binding;

    private boolean searchOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityClassroomsBinding.inflate(getLayoutInflater());
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

        binding.classroomsRecyclerView.setHasFixedSize(true);
        binding.classroomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ClassroomsAdapter(this);

        binding.classroomsRecyclerView.setAdapter(adapter);

        DataManager.getInstance().getClassrooms().observe(this, classrooms -> {
            adapter.setClassrooms(classrooms);
        });

        binding.searchClassroomsCardView.setOnClickListener((v) -> {
            SearchEmptyClassroomsDialogFragment dialogFragment = SearchEmptyClassroomsDialogFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "SearchEmptyClassroomsDialogFragment");
        });

    }

    @Override
    public void onBackPressed() {

        if (searchOpen) {

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

        if(!(argument instanceof TimeZone)) return;

        TimeZone timeZone = (TimeZone) argument;

        String uuid = UUID.randomUUID().toString();

        EventManager.getInstance().subscribe(uuid, (eventType, eventListenerType) -> {

            if(eventListenerType instanceof EmptyClassroomsConfirmationListener) {

                List<Classroom> emptyClassrooms = ((EmptyClassroomsConfirmationListener) eventListenerType).getClassroomList();

                EmptyClassroomsDialogFragment dialogFragment = EmptyClassroomsDialogFragment.newInstance(timeZone, emptyClassrooms);
                dialogFragment.show(getSupportFragmentManager(), "EmptyClassroomsDialogFragment");

            } else if (eventListenerType instanceof ErrorEventListener) {

                ErrorEventListener errorListener = (ErrorEventListener) eventListenerType;

                Toast.makeText(this, errorListener.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

        Packet emptyClassroomsRequestPacket = new PacketBuilder()
                .ofType(PacketType.EMPTYCLASSROOMSTIMEZONE.getRequest())
                .addArgument("uuid", uuid)
                .addArgument("timeZone", timeZone)
                .build();

        ClientManager.getInstance().sendPacketIO(emptyClassroomsRequestPacket);


    }

}
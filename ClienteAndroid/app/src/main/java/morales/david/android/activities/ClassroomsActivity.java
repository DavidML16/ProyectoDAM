package morales.david.android.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import morales.david.android.R;
import morales.david.android.adapters.ClassroomsAdapter;
import morales.david.android.fragments.EmptyClassroomsDialogFragment;
import morales.david.android.fragments.SearchEmptyClassroomsDialogFragment;
import morales.david.android.interfaces.OptionClicked;
import morales.david.android.managers.DataManager;
import morales.david.android.managers.SocketManager;
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

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ClassroomsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classrooms);

        getSupportActionBar().setTitle(getString(R.string.act_classrooms_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarUtil.changeStyle(this, getSupportActionBar());

        recyclerView = findViewById(R.id.act_classrooms_recyclerview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ClassroomsAdapter(this);

        recyclerView.setAdapter(adapter);

        DataManager.getInstance().getClassrooms().observe(this, classrooms -> {
            adapter.setClassrooms(classrooms);
        });

        CardView searchButton = findViewById(R.id.act_classrooms_searcheempty_cardview);
        searchButton.setOnClickListener((v) -> {
            SearchEmptyClassroomsDialogFragment dialogFragment = SearchEmptyClassroomsDialogFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "SearchEmptyClassroomsDialogFragment");
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

        SocketManager.getInstance().sendPacketIO(emptyClassroomsRequestPacket);


    }

}
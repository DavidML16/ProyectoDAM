package morales.david.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import morales.david.android.ClientManager;
import morales.david.android.R;
import morales.david.android.managers.DataManager;
import morales.david.android.managers.eventcallbacks.ErrorEventListener;
import morales.david.android.managers.eventcallbacks.EventManager;
import morales.david.android.managers.eventcallbacks.SchedulesConfirmationEventListener;
import morales.david.android.models.Classroom;
import morales.david.android.models.Group;
import morales.david.android.models.Schedule;
import morales.david.android.models.Teacher;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;
import morales.david.android.utils.ActionBarUtil;

public class SearchScheduleActivity extends AppCompatActivity {

    private AutoCompleteTextView teachersDropDown;
    private ArrayAdapter<String> teachersAdapter;
    private CardView teachersButton;
    private Teacher selectedTeacher;

    private AutoCompleteTextView groupsDropDown;
    private ArrayAdapter<String> groupsAdapter;
    private CardView groupsButton;
    private Group selectedGroup;

    private AutoCompleteTextView classroomsDropDown;
    private ArrayAdapter<String> classroomsAdapter;
    private CardView classroomsButton;
    private Classroom selectedClassroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_schedule);

        getSupportActionBar().setTitle(getString(R.string.act_schedules_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarUtil.changeStyle(this, getSupportActionBar());

        {

            teachersAdapter = new ArrayAdapter(this, R.layout.item_dropdown, getTeachers());
            teachersDropDown = findViewById(R.id.act_schedules_teacher);
            teachersDropDown.setAdapter(teachersAdapter);

            teachersDropDown.setOnItemClickListener((parent, v, position, id) -> {
                String selected = teachersAdapter.getItem(position);
                if (DataManager.getInstance().getTeachers().getValue() == null)
                    return;
                for (Teacher teacher : DataManager.getInstance().getTeachers().getValue()) {
                    if (teacher.getName().equalsIgnoreCase(selected)) {
                        selectedTeacher = teacher;
                        break;
                    }
                };
            });

            teachersButton = findViewById(R.id.act_schedules_teacher_button);

            teachersButton.setOnClickListener(v -> {

                if(selectedTeacher == null)
                    return;

                PacketBuilder packetBuilder = new PacketBuilder()
                    .ofType(PacketType.SEARCHSCHEDULE.getRequest())
                    .addArgument("type", "TEACHER")
                    .addArgument("item", selectedTeacher)
                    .addArgument("callback", "SEARCH");

                ClientManager.getInstance().sendPacketIO(packetBuilder.build());

            });

        }

        {

            groupsAdapter = new ArrayAdapter(this, R.layout.item_dropdown, getGroups());
            groupsDropDown = findViewById(R.id.act_schedules_group);
            groupsDropDown.setAdapter(groupsAdapter);

            groupsDropDown.setOnItemClickListener((parent, v, position, id) -> {
                String selected = groupsAdapter.getItem(position);
                if (DataManager.getInstance().getGroups().getValue() == null)
                    return;
                for (Group group : DataManager.getInstance().getGroups().getValue()) {
                    if (group.toString().equalsIgnoreCase(selected)) {
                        selectedGroup = group;
                        break;
                    }
                };
            });

            groupsButton = findViewById(R.id.act_schedules_group_button);

            groupsButton.setOnClickListener(v -> {

                if(selectedGroup == null)
                    return;

                PacketBuilder packetBuilder = new PacketBuilder()
                        .ofType(PacketType.SEARCHSCHEDULE.getRequest())
                        .addArgument("type", "GROUP")
                        .addArgument("item", selectedGroup)
                        .addArgument("callback", "SEARCH");

                ClientManager.getInstance().sendPacketIO(packetBuilder.build());

            });

        }

        {

            classroomsAdapter = new ArrayAdapter(this, R.layout.item_dropdown, getClassrooms());
            classroomsDropDown = findViewById(R.id.act_schedules_classroom);
            classroomsDropDown.setAdapter(classroomsAdapter);

            classroomsDropDown.setOnItemClickListener((parent, v, position, id) -> {
                String selected = classroomsAdapter.getItem(position);
                if (DataManager.getInstance().getClassrooms().getValue() == null)
                    return;
                for (Classroom classroom : DataManager.getInstance().getClassrooms().getValue()) {
                    if (classroom.getName().equalsIgnoreCase(selected)) {
                        selectedClassroom = classroom;
                        break;
                    }
                };
            });

            classroomsButton = findViewById(R.id.act_schedules_classroom_button);

            classroomsButton.setOnClickListener(v -> {

                if(selectedClassroom == null)
                    return;

                PacketBuilder packetBuilder = new PacketBuilder()
                        .ofType(PacketType.SEARCHSCHEDULE.getRequest())
                        .addArgument("type", "CLASSROOM")
                        .addArgument("item", selectedClassroom)
                        .addArgument("callback", "SEARCH");

                ClientManager.getInstance().sendPacketIO(packetBuilder.build());

            });

        }

        EventManager.getInstance().subscribe("searchschedule", (eventType, eventListenerType) -> {

            if(eventListenerType instanceof SchedulesConfirmationEventListener) {

                SchedulesConfirmationEventListener listener = (SchedulesConfirmationEventListener) eventListenerType;

                ArrayList<Schedule> schedules = (ArrayList<Schedule>) listener.getSchedules();

                Intent intent = new Intent(this, ScheduleActivity.class);
                intent.putExtra("schedules", schedules);
                startActivity(intent);

            } else if (eventListenerType instanceof ErrorEventListener) {

                ErrorEventListener errorListener = (ErrorEventListener) eventListenerType;

                Toast.makeText(this, errorListener.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

    private List<String> getTeachers() {
        List<String> list = new ArrayList<>();
        if (DataManager.getInstance().getTeachers().getValue() == null)
            return list;
        for (Teacher teacher : DataManager.getInstance().getTeachers().getValue())
            list.add(teacher.getName());
        return list;
    }

    private List<String> getGroups() {
        List<String> list = new ArrayList<>();
        if (DataManager.getInstance().getGroups().getValue() == null)
            return list;
        for (Group group : DataManager.getInstance().getGroups().getValue())
            list.add(group.toString());
        return list;
    }

    private List<String> getClassrooms() {
        List<String> list = new ArrayList<>();
        if (DataManager.getInstance().getClassrooms().getValue() == null)
            return list;
        for (Classroom classroom : DataManager.getInstance().getClassrooms().getValue())
            list.add(classroom.getName());
        return list;
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

}
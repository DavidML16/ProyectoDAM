package morales.david.android.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import morales.david.android.databinding.ActivityScheduleBinding;
import morales.david.android.databinding.ActivitySearchScheduleBinding;
import morales.david.android.netty.ClientManager;
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

    private ActivitySearchScheduleBinding binding;

    private ArrayAdapter<String> teachersAdapter;
    private Teacher selectedTeacher;

    private ArrayAdapter<String> groupsAdapter;
    private Group selectedGroup;

    private ArrayAdapter<String> classroomsAdapter;
    private Classroom selectedClassroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySearchScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.textView2.setAlpha(0f);
        binding.textView2.animate().alpha(1f).setDuration(250).setStartDelay(300).start();

        binding.backButton.setOnClickListener((v) -> onBackPressed());

        {

            teachersAdapter = new ArrayAdapter(this, R.layout.item_dropdown, getTeachers());
            binding.teacherDropDown.setAdapter(teachersAdapter);

            binding.teacherDropDown.setOnItemClickListener((parent, v, position, id) -> {
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

            binding.teacherSearchButton.setOnClickListener(v -> {

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
            binding.groupDropDown.setAdapter(groupsAdapter);

            binding.groupDropDown.setOnItemClickListener((parent, v, position, id) -> {
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

            binding.groupSearchButton.setOnClickListener(v -> {

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
            binding.classroomDropDown.setAdapter(classroomsAdapter);

            binding.classroomDropDown.setOnItemClickListener((parent, v, position, id) -> {
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

            binding.classroomSearchButton.setOnClickListener(v -> {

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
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.textView2, "title"));
                intent.putExtra("schedules", schedules);
                startActivity(intent, options.toBundle());

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
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.textView2, "title"));
        startActivity(intent, options.toBundle());
    }

}
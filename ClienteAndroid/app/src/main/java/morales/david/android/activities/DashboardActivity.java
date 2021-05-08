package morales.david.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;

import morales.david.android.R;
import morales.david.android.managers.DataManager;
import morales.david.android.managers.ScreenManager;
import morales.david.android.managers.SocketManager;
import morales.david.android.models.Teacher;
import morales.david.android.models.packets.Packet;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;

public class DashboardActivity extends AppCompatActivity {

    private CardView teachersCard, coursesCard, groupsCard, subjectsCard, classroomsCard, schedulesCard, disconnectCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().hide();

        ScreenManager.getInstance().setActivity(this);

        teachersCard = findViewById(R.id.act_dashboard_cardview_teachers);
        coursesCard = findViewById(R.id.act_dashboard_cardview_courses);
        groupsCard = findViewById(R.id.act_dashboard_cardview_groups);
        subjectsCard = findViewById(R.id.act_dashboard_cardview_subjects);
        classroomsCard = findViewById(R.id.act_dashboard_cardview_classrooms);
        schedulesCard = findViewById(R.id.act_dashboard_cardview_schedules);
        disconnectCard = findViewById(R.id.act_dashboard_cardview_disconnect);

        teachersCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, TeachersActivity.class);
            startActivity(intent);
        });

        coursesCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, CoursesActivity.class);
            startActivity(intent);
        });

        subjectsCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, SubjectsActivity.class);
            startActivity(intent);
        });

        classroomsCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, ClassroomsActivity.class);
            startActivity(intent);
        });

        disconnectCard.setOnClickListener((view) -> {
            Packet exitRequestPacket = new PacketBuilder().ofType(PacketType.EXIT.getRequest()).build();
            SocketManager.getInstance().sendPacketIO(exitRequestPacket);
        });

    }

}
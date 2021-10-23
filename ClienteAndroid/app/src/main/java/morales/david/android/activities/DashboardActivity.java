package morales.david.android.activities;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import morales.david.android.databinding.ActivityDashboardBinding;
import morales.david.android.netty.ClientManager;
import morales.david.android.R;
import morales.david.android.managers.ScreenManager;
import morales.david.android.models.packets.Packet;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;
import morales.david.android.utils.Utils;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ScreenManager.getInstance().setActivity(this);

        binding.titleTexView.setAlpha(0f);
        binding.userTextView.setAlpha(0f);
        binding.titleTexView.animate().alpha(1f).setDuration(250).setStartDelay(150).start();
        binding.userTextView.animate().alpha(1f).setDuration(250).setStartDelay(300).start();

        binding.teachersCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, TeachersActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.userTextView, "title"));
            startActivity(intent, options.toBundle());
        });

        binding.coursesCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, CoursesActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.userTextView, "title"));
            startActivity(intent, options.toBundle());
        });

        binding.groupsCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, GroupsActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.userTextView, "title"));
            startActivity(intent, options.toBundle());
        });

        binding.subjectsCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, SubjectsActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.userTextView, "title"));
            startActivity(intent, options.toBundle());
        });

        binding.classroomsCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, ClassroomsActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.userTextView, "title"));
            startActivity(intent, options.toBundle());
        });

        binding.schedulesCard.setOnClickListener((view) -> {
            Intent intent = new Intent(this, SearchScheduleActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, Pair.create(binding.userTextView, "title"));
            startActivity(intent, options.toBundle());
        });

        binding.disconnectCard.setOnClickListener((view) -> {

            Dialog dialog = new Dialog(this);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialog.setContentView(R.layout.disconnect_dialog);

            CardView yesCard = dialog.findViewById(R.id.option_accept);
            CardView noCard = dialog.findViewById(R.id.option_cancel);

            yesCard.setOnClickListener(v2 -> {
                dialog.dismiss();
                Packet exitRequestPacket = new PacketBuilder().ofType(PacketType.EXIT.getRequest()).build();
                ClientManager.getInstance().sendPacketIO(exitRequestPacket);
            });

            noCard.setOnClickListener(v2 -> dialog.dismiss());

            dialog.show();
        });

        if(ClientManager.getInstance().getClientSession() != null && !ClientManager.getInstance().getClientSession().getName().isEmpty())
            binding.userTextView.setText(Utils.capitalizeFirstLetter(ClientManager.getInstance().getClientSession().getName()));

    }

    @Override
    public void onBackPressed() {

        Dialog dialog = new Dialog(this);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.setContentView(R.layout.close_app_dialog);

        CardView yesCard = dialog.findViewById(R.id.option_accept);
        CardView noCard = dialog.findViewById(R.id.option_cancel);

        yesCard.setOnClickListener(v2 -> {
            dialog.dismiss();
            ClientManager.getInstance().close();
            ScreenManager.getInstance().getActivity().finishAffinity();
            System.exit(0);
        });

        noCard.setOnClickListener(v2 -> dialog.dismiss());

        dialog.show();

    }

}
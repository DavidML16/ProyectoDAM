package morales.david.android.activities;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import morales.david.android.R;
import morales.david.android.databinding.ActivitySettingsBinding;
import morales.david.android.netty.ClientManager;
import morales.david.android.utils.Constants;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener((v) -> onBackPressed());

        binding.textView2.setAlpha(0f);
        binding.textView2.animate().alpha(1f).setDuration(250).setStartDelay(300).start();

        binding.ipSettingValueTextView.setText(Constants.SERVER_IP);
        binding.portSettingValueTextView.setText(""+Constants.SERVER_PORT);

        binding.ipSettingCardView.setOnClickListener(v -> {

            Dialog dialog = new Dialog(this);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialog.setContentView(R.layout.ipdir_setting_dialog);

            CardView yesCard = dialog.findViewById(R.id.option_accept);
            CardView noCard = dialog.findViewById(R.id.option_cancel);

            EditText editText = dialog.findViewById(R.id.inputEditText);

            editText.setText(""+Constants.SERVER_IP);

            yesCard.setOnClickListener(v2 -> {

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sgh_preference_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                String msg = editText.getText().toString().trim();

                if(msg.isEmpty())
                    return;

                editor.putString(getString(R.string.sgh_preference_ip), msg);
                editor.commit();

                Constants.SERVER_IP = msg;

                binding.ipSettingValueTextView.setText(Constants.SERVER_IP);
                binding.portSettingValueTextView.setText(""+Constants.SERVER_PORT);

                dialog.dismiss();

            });

            noCard.setOnClickListener(v2 -> dialog.dismiss());

            dialog.show();

        });

        binding.portSettingCardView.setOnClickListener(v -> {

            Dialog dialog = new Dialog(this);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            dialog.setContentView(R.layout.port_setting_dialog);

            CardView yesCard = dialog.findViewById(R.id.option_accept);
            CardView noCard = dialog.findViewById(R.id.option_cancel);

            EditText editText = dialog.findViewById(R.id.inputEditText);

            editText.setText(""+Constants.SERVER_PORT);

            yesCard.setOnClickListener(v2 -> {

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sgh_preference_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                String msg = editText.getText().toString().trim();

                if(msg.isEmpty())
                    return;

                editor.putInt(getString(R.string.sgh_preference_port), Integer.parseInt(msg));
                editor.commit();

                Constants.SERVER_PORT = Integer.parseInt(msg);

                binding.ipSettingValueTextView.setText(Constants.SERVER_IP);
                binding.portSettingValueTextView.setText(""+Constants.SERVER_PORT);

                dialog.dismiss();

            });

            noCard.setOnClickListener(v2 -> dialog.dismiss());

            dialog.show();

        });

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

        ClientManager.getInstance().close();

        Intent intent = new Intent(this, MainActivity.class);

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                Pair.create(binding.textView2, "title"));

        startActivity(intent, options.toBundle());

    }

}
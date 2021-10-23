package morales.david.android.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import morales.david.android.R;
import morales.david.android.activities.DashboardActivity;
import morales.david.android.activities.SettingsActivity;
import morales.david.android.databinding.ActivityLoginBinding;
import morales.david.android.managers.ScreenManager;
import morales.david.android.managers.eventcallbacks.ConfirmationEventListener;
import morales.david.android.managers.eventcallbacks.ErrorEventListener;
import morales.david.android.managers.eventcallbacks.EventManager;
import morales.david.android.models.packets.Packet;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;
import morales.david.android.netty.ClientManager;
import morales.david.android.utils.Constants;
import morales.david.android.utils.HashUtil;

public class MainActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.ClienteAndroid);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ScreenManager screenManager = ScreenManager.getInstance();
        screenManager.setActivity(this);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sgh_preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Constants.SERVER_IP = sharedPref.getString(getString(R.string.sgh_preference_ip), "129.159.252.134");
        Constants.SERVER_PORT = sharedPref.getInt(getString(R.string.sgh_preference_port), 6565);

        if(sharedPref.contains(getString(R.string.sgh_preference_user)) && sharedPref.contains(getString(R.string.sgh_preference_user))) {

            binding.titleTextView.setAlpha(0f);
            binding.titleTextView2.setAlpha(0f);
            binding.usernameCardView.setAlpha(0f);
            binding.passwordCardView.setAlpha(0f);
            binding.settingsButton.setAlpha(0f);
            binding.loginButton.setAlpha(0f);

            String user = sharedPref.getString(getString(R.string.sgh_preference_user), "");
            String pass = sharedPref.getString(getString(R.string.sgh_preference_pass), "");

            login(editor, user, pass, true);

        } else {

            binding.titleTextView.setAlpha(0f);
            binding.titleTextView2.setAlpha(0f);
            binding.titleTextView2.animate().alpha(1f).setDuration(250).setStartDelay(300).start();
            binding.titleTextView.animate().alpha(1f).setDuration(250).setStartDelay(450).start();

            binding.animationViewLoading.setVisibility(View.GONE);

        }

        binding.loginButton.setOnClickListener(v -> {
            login(editor, binding.usernameInput.getText().toString(), HashUtil.sha1(binding.passwordInput.getText().toString()), false);
        });

        binding.settingsButton.setOnClickListener(v -> {

            Intent intent = new Intent(this, SettingsActivity.class);

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                    Pair.create(binding.titleTextView, "title"));

            startActivity(intent, options.toBundle());

        });



    }

    public void login(SharedPreferences.Editor editor, String user, String pass, boolean saved) {

        final String username = user;
        final String password = pass;

        if(username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.act_login_message_error_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        Packet loginRequestPacket = new PacketBuilder()
                .ofType(PacketType.LOGIN.getRequest())
                .addArgument("username", username)
                .addArgument("password", password)
                .build();

        ClientManager clientManager = ClientManager.getInstance();

        if(clientManager.isClosed()) {

            clientManager.addPendingPacket(loginRequestPacket);

            clientManager.open();

            EventManager.getInstance().subscribe("start", (eventType, eventListenerType) -> {

                if (eventListenerType instanceof ErrorEventListener) {

                    ErrorEventListener errorListener = (ErrorEventListener) eventListenerType;

                    binding.loginButtonText.setText(getString(R.string.act_login_button_login));
                    binding.loginButton.setEnabled(true);
                    binding.loginButton.animate().alpha(1f).setDuration(150);

                    Toast.makeText(this, errorListener.getMessage(), Toast.LENGTH_SHORT).show();

                    ClientManager.getInstance().close();

                }

            });

        } else {

            ClientManager.getInstance().sendPacketIO(loginRequestPacket);

        }

        if(!saved) {
            binding.loginButtonText.setText(getString(R.string.act_login_button_login_progress));
            binding.loginButton.setEnabled(false);
            binding.loginButton.animate().alpha(0.75f).setDuration(250);
        }

        EventManager.getInstance().subscribe("login", (eventType, eventListenerType) -> {

            if(eventListenerType instanceof ConfirmationEventListener) {

                editor.putString(getString(R.string.sgh_preference_user), username);
                editor.putString(getString(R.string.sgh_preference_pass), password);
                editor.putString(getString(R.string.sgh_preference_ip), Constants.SERVER_IP);
                editor.putInt(getString(R.string.sgh_preference_port), Constants.SERVER_PORT);
                editor.commit();

                Intent intent = new Intent(this, DashboardActivity.class);

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                        Pair.create(binding.titleTextView, "title"),
                        Pair.create(binding.titleTextView2, "title2"));

                startActivity(intent, options.toBundle());

            } else if (eventListenerType instanceof ErrorEventListener) {

                ErrorEventListener errorListener = (ErrorEventListener) eventListenerType;

                binding.loginButtonText.setText(getString(R.string.act_login_button_login));
                binding.loginButton.setEnabled(true);
                binding.loginButton.animate().alpha(1f).setDuration(150);

                binding.titleTextView.setAlpha(1f);
                binding.titleTextView2.setAlpha(1f);
                binding.usernameCardView.setAlpha(1f);
                binding.passwordCardView.setAlpha(1f);
                binding.settingsButton.setAlpha(1f);
                binding.loginButton.setAlpha(1f);

                binding.animationViewLoading.setVisibility(View.GONE);

                Toast.makeText(this, errorListener.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

}
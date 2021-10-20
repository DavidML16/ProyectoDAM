package morales.david.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import morales.david.android.activities.DashboardActivity;
import morales.david.android.activities.SettingsActivity;
import morales.david.android.managers.ScreenManager;
import morales.david.android.managers.eventcallbacks.ConfirmationEventListener;
import morales.david.android.managers.eventcallbacks.ErrorEventListener;
import morales.david.android.managers.eventcallbacks.EventManager;
import morales.david.android.models.packets.Packet;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;
import morales.david.android.utils.Constants;
import morales.david.android.utils.HashUtil;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private CardView loginButton;
    private ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.ClienteAndroid);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        ScreenManager screenManager = ScreenManager.getInstance();
        screenManager.setActivity(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Constants.SERVER_IP = preferences.getString("preference_ip", "192.168.1.46");
        Constants.SERVER_PORT = Integer.parseInt(preferences.getString("preference_port", "6565"));

        usernameInput = findViewById(R.id.act_login_input_username);
        passwordInput = findViewById(R.id.act_login_input_password);
        loginButton = findViewById(R.id.act_login_button_login);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sgh_preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(sharedPref.contains(getString(R.string.sgh_preference_user)) && sharedPref.contains(getString(R.string.sgh_preference_user))) {

            String user = sharedPref.getString(getString(R.string.sgh_preference_user), "");
            String pass = sharedPref.getString(getString(R.string.sgh_preference_pass), "");

            login(editor, user, pass);

        }

        loginButton.setOnClickListener(v -> {
            login(editor, usernameInput.getText().toString(), HashUtil.sha1(passwordInput.getText().toString()));
        });

        settings = findViewById(R.id.act_login_settings);
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

    }

    public void login(SharedPreferences.Editor editor, String user, String pass) {

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

                    Toast.makeText(this, errorListener.getMessage(), Toast.LENGTH_SHORT).show();

                }

            });

        } else {

            ClientManager.getInstance().sendPacketIO(loginRequestPacket);

        }

        EventManager.getInstance().subscribe("login", (eventType, eventListenerType) -> {

            if(eventListenerType instanceof ConfirmationEventListener) {

                editor.putString(getString(R.string.sgh_preference_user), username);
                editor.putString(getString(R.string.sgh_preference_pass), password);
                editor.commit();

                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);

            } else if (eventListenerType instanceof ErrorEventListener) {

                ErrorEventListener errorListener = (ErrorEventListener) eventListenerType;

                Toast.makeText(this, errorListener.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }

}
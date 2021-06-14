package morales.david.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import morales.david.android.activities.DashboardActivity;
import morales.david.android.managers.ScreenManager;
import morales.david.android.managers.SocketManager;
import morales.david.android.managers.eventcallbacks.ConfirmationEventListener;
import morales.david.android.managers.eventcallbacks.ErrorEventListener;
import morales.david.android.managers.eventcallbacks.EventManager;
import morales.david.android.models.packets.Packet;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;
import morales.david.android.utils.HashUtil;

public class MainActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput;
    private CardView loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.ClienteAndroid);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        ScreenManager screenManager = ScreenManager.getInstance();
        screenManager.setActivity(this);

        SocketManager socketManager = SocketManager.getInstance();

        if(!socketManager.isOpened()) {
            socketManager.setDaemon(true);
            socketManager.start();
        }

        usernameInput = findViewById(R.id.act_login_input_username);
        passwordInput = findViewById(R.id.act_login_input_password);
        loginButton = findViewById(R.id.act_login_button_login);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.sgh_preference_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(sharedPref.contains(getString(R.string.sgh_preference_user)) && sharedPref.contains(getString(R.string.sgh_preference_user))) {

            String user = sharedPref.getString(getString(R.string.sgh_preference_user), "");
            String pass = sharedPref.getString(getString(R.string.sgh_preference_pass), "");

            new Handler().postDelayed(() -> {
                login(editor, user, pass);
            }, 100);

        }

        loginButton.setOnClickListener(v -> {
            login(editor, usernameInput.getText().toString(), HashUtil.sha1(passwordInput.getText().toString()));
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

        SocketManager.getInstance().sendPacketIO(loginRequestPacket);

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
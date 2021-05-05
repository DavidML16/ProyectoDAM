package morales.david.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import morales.david.android.interfaces.MessageListener;
import morales.david.android.managers.DataManager;
import morales.david.android.managers.EventManager;
import morales.david.android.managers.ScreenManager;
import morales.david.android.managers.SocketManager;
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
        setContentView(R.layout.activity_login);
        setTheme(R.style.ClienteAndroid);

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

        loginButton.setOnClickListener(v -> login());

        EventManager.getInstance().subscribe("login", (eventType, message) -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

    }

    public void login() {

        final String username = usernameInput.getText().toString();
        final String password = HashUtil.sha1(passwordInput.getText().toString());

        if(username.isEmpty() || passwordInput.getText().toString().isEmpty()) {

            Toast.makeText(this, getString(R.string.act_login_message_error_empty), Toast.LENGTH_SHORT).show();

            return;

        }

        Packet loginRequestPacket = new PacketBuilder()
                .ofType(PacketType.LOGIN.getRequest())
                .addArgument("username", username)
                .addArgument("password", password)
                .build();

        SocketManager.getInstance().sendPacketIO(loginRequestPacket);

    }

}
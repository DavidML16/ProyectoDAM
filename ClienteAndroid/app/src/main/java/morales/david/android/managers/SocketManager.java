package morales.david.android.managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import morales.david.android.R;
import morales.david.android.activities.DashboardActivity;
import morales.david.android.models.ClientSession;
import morales.david.android.models.packets.Packet;
import morales.david.android.models.packets.PacketBuilder;
import morales.david.android.models.packets.PacketType;
import morales.david.android.utils.Constants;

public class SocketManager extends Thread {

    private static SocketManager INSTANCE = null;

    public static SocketManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new SocketManager();
        return INSTANCE;
    }

    private Activity context;

    private Socket socket;

    private BufferedReader input;
    private BufferedWriter output;

    private Packet receivedPacket;

    private ClientSession clientSession;

    private final boolean[] closed = { false };
    private boolean opened = false;

    public SocketManager() {

        this.clientSession = new ClientSession();

    }

    public void openSocket() {

        try {

            socket = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);

            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            opened = true;

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    private void close() {

        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

        openSocket();

        context.runOnUiThread(() -> {
            Toast.makeText(context, "SOCKET ABIERTO", Toast.LENGTH_SHORT).show();
        });

        while(!closed[0]) {

            try {

                if (input != null && input.ready()) {

                    receivedPacket = readPacketIO();

                    context.runOnUiThread(() -> {

                        PacketType packetType = PacketType.valueOf(PacketType.getIdentifier(receivedPacket.getType()));

                        switch (packetType) {

                            case LOGIN: {

                                if(receivedPacket.getType().equalsIgnoreCase(PacketType.LOGIN.getConfirmation())) {

                                    clientSession.setId(((Double) receivedPacket.getArgument("id")).intValue());
                                    clientSession.setName((String) receivedPacket.getArgument("name"));
                                    clientSession.setRole((String) receivedPacket.getArgument("role"));

                                    Intent intent = new Intent(context, DashboardActivity.class);
                                    context.startActivity(intent);

                                } else if(receivedPacket.getType().equalsIgnoreCase(PacketType.LOGIN.LOGIN.getError())) {

                                    Toast.makeText(context, context.getString(R.string.act_login_message_error_credentials), Toast.LENGTH_SHORT).show();

                                }

                                break;
                            }

                        }

                    });

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public boolean isOpened() {
        return opened;
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public Socket getSocket() { return socket; }

    public ClientSession getClientSession() { return clientSession; }

    public void socketError() {
        closed[0] = true;
        close();
    }

    // Methods to send data in socket I/O's
    public void sendPacketIO(Packet packet) {
        new Thread(() -> {
            try {
                output.write(packet.toString());
                output.newLine();
                output.flush();
            } catch (IOException e) {
                socketError();
            }
        }).start();
    }

    public Packet readPacketIO() {
        try {
            String json = input.readLine();
            return PacketBuilder.GSON.fromJson(json, Packet.class);
        } catch (IOException e) {
            socketError();
        }
        return null;
    }

}

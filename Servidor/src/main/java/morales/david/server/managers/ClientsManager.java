package morales.david.server.managers;

import morales.david.server.Server;
import morales.david.server.clients.ClientRepository;
import morales.david.server.clients.ClientThread;
import morales.david.server.models.packets.Packet;
import morales.david.server.models.packets.PacketBuilder;
import morales.david.server.models.packets.PacketType;
import morales.david.server.utils.Constants;

import java.util.ConcurrentModificationException;

public class ClientsManager extends Thread {

    private Server server;
    private ClientRepository clientRepository;

    public ClientsManager(Server server) {
        this.server = server;
        this.clientRepository = server.getClientRepository();
    }

    @Override
    public void run() {

        while(true) {

            try {
                sleep(Constants.CLIENT_CONNECTION_CHECKING_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Packet pingPacket = new PacketBuilder().ofType(PacketType.PING.getRequest()).build();

            try {
                for(ClientThread clientThread : clientRepository.getClients())
                    clientThread.getClientProtocol().sendPacketIO(pingPacket);
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }

        }

    }

}

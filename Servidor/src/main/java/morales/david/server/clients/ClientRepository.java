package morales.david.server.clients;

import morales.david.server.models.packets.Packet;

import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    private final List<ClientThread> clients;

    public ClientRepository(){
        clients = new ArrayList<>();
    }

    public synchronized void addClient(ClientThread clientThread){
        clients.add(clientThread);
    }

    public synchronized void removeClient(ClientThread clientThread){
        clients.remove(clientThread);
    }

    public synchronized List<ClientThread> getClients() {
        return clients;
    }

    public synchronized boolean isNoClients(){
        return clients.isEmpty();
    }

    public synchronized void broadcast(Packet packet) {
        for(ClientThread clientThread : clients)
            clientThread.getClientProtocol().sendPacketIO(packet);
    }

}

package morales.david.server.clients;

import morales.david.server.models.packets.Packet;

import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    private final List<ClientThread> clients;

    /**
     * Create a new instance of ClientRepository
     */
    public ClientRepository(){
        clients = new ArrayList<>();
    }

    /**
     * Add a client thread to the list
     * @param clientThread
     */
    public synchronized void addClient(ClientThread clientThread){
        clients.add(clientThread);
    }

    /**
     * Remove the client thread from the list
     * @param clientThread
     */
    public synchronized void removeClient(ClientThread clientThread){
        clients.remove(clientThread);
    }

    /**
     * Get the list of clients
     * @return clients
     */
    public synchronized List<ClientThread> getClients() { return clients; }

    /**
     * Get if clients list its empty
     * @return list of clients empty
     */
    public synchronized boolean isNoClients(){
        return clients.isEmpty();
    }

    /**
     * Send the specified packet to all the clients in the list
     * @param packet
     */
    public synchronized void broadcast(Packet packet) {
        for(ClientThread clientThread : clients)
            clientThread.getClientProtocol().sendPacketIO(packet);
    }

}

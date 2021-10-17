package morales.david.server.clients;

import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import morales.david.server.Server;
import morales.david.server.models.packets.Packet;
import morales.david.server.utils.Constants;
import morales.david.server.utils.DBConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRepository {

    private static ClientRepository INSTANCE = null;

    public static ClientRepository getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ClientRepository();
        return INSTANCE;
    }


    private final ChannelGroup channels;

    private final Map<Channel, ClientSession> clientSessions;

    /**
     * Create a new instance of ClientRepository
     */
    public ClientRepository(){

        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        clientSessions = new HashMap<>();

    }

    /**
     * Add a channel to the list
     * @param channel
     */
    public synchronized void addClient(Channel channel){
        channels.add(channel);
        clientSessions.put(channel, new ClientSession());
    }

    /**
     * Remove the channel from the list
     * @param channel
     */
    public synchronized void removeClient(Channel channel){
        channels.remove(channel);
        clientSessions.remove(channel);
    }

    /**
     * Get the list of channels
     * @return channels
     */
    public synchronized ChannelGroup getChannels() { return channels; }

    public synchronized ClientSession getSession(Channel channel) { return clientSessions.getOrDefault(channel, new ClientSession()); }

    /**
     * Send the specified packet to all the clients in the list
     * @param packet
     */
    public synchronized void broadcast(Packet packet) {

        for(Channel channel : getChannels())
            sendPacketIO(channel, packet);

    }

    /**
     * Send packet to client's socket output
     * @param packet
     */
    public synchronized void sendPacketIO(Channel channel, Packet packet) {

        String msg = packet.toString() + "\n";

        final ByteBuf byteBufMsg = channel.alloc().buffer(msg.length());
        byteBufMsg.writeBytes(msg.getBytes());

        channel.writeAndFlush(byteBufMsg);

    }

    /**
     * Read packet from server, client's socket input
     * @return packet object parsed by json input
     */
    public synchronized Packet readPacketIO(String message) {

        return Server.GSON.fromJson(message, Packet.class);

    }

}

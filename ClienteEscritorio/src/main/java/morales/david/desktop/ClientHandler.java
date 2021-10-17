package morales.david.desktop;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.application.Platform;
import morales.david.desktop.models.packets.Packet;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static ClientManager clientManager = ClientManager.getInstance();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {

        Packet packet = clientManager.readPacketIO((String) message);

        clientManager.processPacket(packet);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {

        for(Packet packet : clientManager.getPendingPackets())
            clientManager.sendPacketIO(packet);

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        Platform.exit();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {  }

}

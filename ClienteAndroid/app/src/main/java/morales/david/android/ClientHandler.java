package morales.david.android;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import morales.david.android.managers.ScreenManager;
import morales.david.android.models.packets.Packet;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private static ClientManager clientManager = ClientManager.getInstance();
    private static ScreenManager screenManager = ScreenManager.getInstance();

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

        screenManager.getActivity().finishAffinity();
        System.exit(0);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { }

}

package morales.david.android.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import morales.david.android.managers.ScreenManager;
import morales.david.android.models.packets.Packet;

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

        clientManager.getPendingPackets().clear();

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

        if(!clientManager.isClosedManually()) {

            ScreenManager.getInstance().getActivity().finishAffinity();
            System.exit(0);

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        cause.printStackTrace();

    }

}

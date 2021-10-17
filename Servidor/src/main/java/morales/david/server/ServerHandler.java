package morales.david.server;

import io.netty.channel.*;
import morales.david.server.clients.ClientRepository;
import morales.david.server.clients.PacketProcessor;
import morales.david.server.models.packets.Packet;
import morales.david.server.utils.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {

        System.out.println(Constants.LOG_SERVER_USER_CONNECTED);

        ClientRepository.getInstance().addClient(ctx.channel());

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {

        System.out.println(Constants.LOG_SERVER_USER_DISCONNECTED);

        ctx.channel().closeFuture().addListener((ChannelFutureListener) channelFuture -> {
            ClientRepository.getInstance().removeClient(ctx.channel());
        });

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {

        Channel channel = ctx.channel();

        Packet packet = ClientRepository.getInstance().readPacketIO((String) message);

        executorService.submit(new PacketProcessor(channel, packet));

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { }

}

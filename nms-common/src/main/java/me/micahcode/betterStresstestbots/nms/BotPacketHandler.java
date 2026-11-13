package me.micahcode.betterStresstestbots.nms;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerCommonPacketListener;

/** Emulates keepalive replies and consumes packets for a bot with no network client. */
public final class BotPacketHandler extends ChannelOutboundHandlerAdapter {
    private final Connection connection;

    /** Uses the connection's current listener, which is replaced during player login. */
    public BotPacketHandler(Connection connection) {
        this.connection = connection;
    }

    /** Answers each challenge with its original ID and completes discarded packet writes. */
    @Override
    public void write(ChannelHandlerContext context, Object message, ChannelPromise promise) throws Exception {
        if (!(message instanceof Packet<?>)) {
            // Protocol configuration tasks must still pass through the pipeline.
            super.write(context, message, promise);
            return;
        }

        try {
            if (message instanceof ClientboundKeepAlivePacket keepAlive
                    && connection.getPacketListener() instanceof ServerCommonPacketListener listener) {
                listener.handleKeepAlive(new ServerboundKeepAlivePacket(keepAlive.getId()));
            }
            promise.setSuccess();
        } finally {
            // EmbeddedChannel otherwise retains every outgoing packet indefinitely.
            ReferenceCountUtil.release(message);
        }
    }
}

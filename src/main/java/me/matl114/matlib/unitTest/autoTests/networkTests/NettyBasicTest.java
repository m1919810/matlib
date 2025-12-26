package me.matl114.matlib.unitTest.autoTests.networkTests;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import org.jetbrains.annotations.NotNull;

public class NettyBasicTest implements TestCase {
    @OnlineTest(name = "network-server-client-test")
    public void test_network_transport() throws Throwable {
        Channel server = initServer().sync().channel();
        Channel clientChannel = initClient().sync().channel();
        try {
            Debug.logger(clientChannel);
            AssertNN(client);
            AssertNN(client.channel);
            Assert(clientChannel == client.channel);
            Debug.logger("starting communication");
            clientChannel.writeAndFlush(new Packet(114514, "shit, it is me, client"));
            ExecutorUtils.sleep(10);
            clientChannel.writeAndFlush(new Packet(-1, "disconnect"));
            ExecutorUtils.sleep(10);
            Assert(serverConnections.isEmpty());
        } finally {
            new ArrayList<>(serverConnections).forEach(MessageHandler::disconnect);
            server.close();
        }
    }

    @OnlineTest(name = "network server client injection test")
    public void test_network_inject() throws Throwable {
        Channel server = initServer().sync().channel();

        try {
            FutureTask<?> signal = ExecutorUtils.signal();
            Debug.logger("synced");
            Debug.logger(server.pipeline());
            server.pipeline().addFirst("fuckyou", new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                    Debug.logger("interceptor registered here");
                    Channel channel = (Channel) msg;
                    Debug.logger(channel.pipeline());
                    channel.pipeline().addFirst(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                            Debug.logger("client channel registered at interceptor");
                            ctx.channel()
                                    .pipeline()
                                    .addAfter("decoder", "interceptor", new MessageToMessageDecoder<>() {
                                        @Override
                                        protected void decode(
                                                ChannelHandlerContext channelHandlerContext,
                                                Object object,
                                                List<Object> list)
                                                throws Exception {
                                            Debug.logger("Intercepted", object, list);
                                            list.clear();
                                            signal.run();
                                        }
                                    });
                            ctx.channel().pipeline().remove(this);
                            Debug.logger(ctx.channel().pipeline());
                            super.channelRegistered(ctx);
                        }
                    });
                    super.channelRead(ctx, msg);
                }
            });
            Debug.logger("start init child client");
            Channel clientChannel = initClient().sync().channel();
            Debug.logger(clientChannel);
            ExecutorUtils.sleep(10);
            AssertNN(client);
            AssertNN(client.channel);
            Assert(clientChannel == client.channel);
            Debug.logger("starting communication");
            clientChannel.writeAndFlush(new Packet(114514, "shit, it is me, client"));
            ExecutorUtils.sleep(10);
            signal.get(5, TimeUnit.SECONDS);
            Debug.logger("intercepted accepted");
        } finally {
            new ArrayList<>(serverConnections).forEach(MessageHandler::disconnect);
            server.close();
        }
    }

    private Supplier<EventLoopGroup> SERVER_EVENT_LOOP =
            Suppliers.memoize(() -> (EventLoopGroup) new NioEventLoopGroup());
    private Supplier<EventLoopGroup> CLIENT_EVENT_LOOP =
            Suppliers.memoize(() -> (EventLoopGroup) new NioEventLoopGroup());
    public MessageHandler client;
    public static List<MessageHandler> serverConnections = new ArrayList<>();

    public int testPort = new Random().nextInt(10001, 25565);

    public ChannelFuture initServer() {
        return new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(@NotNull Channel channel) throws Exception {
                        Debug.logger("channel init called");
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("decoder", new PacketCodec());
                        var connection = new MessageHandler(Bound.SERVER);
                        serverConnections.add(connection);
                        pipeline.addLast("handler", connection);
                    }
                })
                .handler(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                        Debug.logger("Server Channel registered");

                        super.channelRegistered(ctx);
                    }

                    @Override
                    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
                        Debug.logger("Server Channel read");
                        super.channelRead(ctx, msg);
                    }
                })
                .group(SERVER_EVENT_LOOP.get())
                .localAddress(new InetSocketAddress("127.0.0.1", testPort))
                .bind();
    }

    public ChannelFuture initClient() {
        return new Bootstrap()
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(@NotNull Channel channel) throws Exception {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("decoder", new PacketCodec());
                        client = new MessageHandler(Bound.CLIENT);
                        pipeline.addLast("handler", client);
                    }
                })
                .group(CLIENT_EVENT_LOOP.get())
                .connect(new InetSocketAddress("127.0.0.1", testPort));
    }

    public static enum Bound {
        SERVER,
        CLIENT;

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    @AllArgsConstructor
    @Data
    public static class Packet {
        int code;
        String message;
    }

    public static class MessageHandler extends SimpleChannelInboundHandler<Packet> {
        Channel channel;
        Bound bound;
        List<Packet> collectedPackets = new ArrayList<>();

        public MessageHandler(Bound bound) {
            this.bound = bound;
        }

        @Override
        public void channelActive(@NotNull ChannelHandlerContext ctx) throws Exception {
            Debug.logger(bound.getName(), "channel active");
            this.channel = ctx.channel();

            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(@NotNull ChannelHandlerContext ctx) throws Exception {
            Debug.logger(bound.getName(), "channel inactive");
            this.channel = null;
            this.disconnect();
            super.channelInactive(ctx);
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
            if (this.channel == null || !this.channel.isOpen()) {
                Debug.logger("Illegal State");
            }
            Debug.logger("read read", packet);
            Debug.logger(bound.getName(), "bound receive", packet);
            if (packet.code == -1) {
                if (bound == Bound.SERVER) {
                    disconnect();
                } else {

                }
            }
            if (packet.code == 114514) {
                this.channel.writeAndFlush(new Packet(1919810, "shit, it is me, server"));
            }
        }

        public void disconnect() {
            this.collectedPackets.clear();
            if (this.channel != null && this.channel.isOpen()) {
                if (bound != Bound.SERVER) {
                    this.channel.writeAndFlush(new Packet(-1, "close disconnect"));
                }
                this.channel.close();
            }
            serverConnections.remove(this);
        }
    }

    public static class PacketCodec extends ByteToMessageCodec<Packet> {

        @Override
        protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf out)
                throws Exception {
            out.writeInt(packet.code);
            byte[] strBuf = packet.message.getBytes(StandardCharsets.UTF_8);
            out.writeInt(strBuf.length);
            out.writeBytes(strBuf);
        }

        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list)
                throws Exception {
            int code = in.readInt();
            int len = in.readInt();
            byte[] strBuf = new byte[len];
            in.readBytes(strBuf);
            list.add(new Packet(code, new String(strBuf, StandardCharsets.UTF_8)));
        }
    }
}

package com.hakai.irc.client;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.hakai.gui.altlogin.AltLogin;
import com.hakai.irc.protocol.codec.IRCPacketCodec;
import com.hakai.irc.protocol.codec.IRCPacketEncryption;
import com.hakai.irc.protocol.codec.IRCPacketSizer;
import com.hakai.irc.protocol.IRCIncomingPacket;
import com.hakai.irc.protocol.IRCOutgoingPacket;
import com.hakai.irc.protocol.IRCProtocol;
import com.hakai.irc.protocol.packets.mojang.IRCPacketMinecraftRequest;
import com.hakai.main.HakaiClient;
import com.hakai.utils.config.YellowSnowConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.apache.logging.log4j.LogManager;

import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.GeneralSecurityException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class IRCClient extends SimpleChannelInboundHandler<IRCIncomingPacket> {

    public static final int VERSION = 1;
    public static final SocketAddress SOCKET_ADDRESS;

    static {
        SocketAddress socketAddress;
        try {
            socketAddress = new InetSocketAddress("yellowsnow.xyz", 1414);
        } catch(Exception e) {
            socketAddress = null;
        }
        SOCKET_ADDRESS = socketAddress;
    }

    private static IRCClient instance = null;

    public static IRCClient get() {
        if(instance == null)
            instance = new IRCClient();
        return instance;
    }

    private final IRCProtocol protocol;
    private final IRCPacketHandler handler;
    private final ScheduledExecutorService executorService;
    private final IRCClientConfig config;

    private Session lastMinecraftSession = null;

    private EventLoopGroup group = null;
    private Channel channel = null;

    private byte failedConnectCount = 0;
    private boolean connectAsync = false;

    private IRCClient() {
        instance = this;

        this.protocol = new IRCProtocol();
        this.handler = new IRCPacketHandler(this);
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.config = new IRCClientConfig();
        YellowSnowConfig.get().registerSubConfig("irc", this.config);
    }

    public boolean isConnected() {
        if(!isPreConnected())
            return false;
        return handler.isConnected();
    }

    public boolean isPreConnected() {
        return channel != null && channel.isActive();
    }

    public boolean isLoggedIn() {
        return handler.isLoggedIn();
    }

    public void sendPacket(IRCOutgoingPacket packet) {
        if(channel == null)
            return;
        channel.writeAndFlush(packet);
        if(HakaiClient.VERSION.startsWith("Pre"))
            LogManager.getLogger().info("IRCClient has send " + packet.getClass());
    }

    private void autoReconnect() {
        disconnect();
        if(failedConnectCount < 3) {
            failedConnectCount++;
            LogManager.getLogger().info("IRCClient is reconnecting...");
            connect();
        } else {
            failedConnectCount = 0;
        }
    }

    public void connect() {
        if(SOCKET_ADDRESS == null) {
            LogManager.getLogger().error("SocketAddress in IRCClient is null");
            return;
        }
        if(connectAsync || channel != null)
            return;
        connectAsync = true;
        executorService.schedule(() -> {
            connect0();
        }, 4, TimeUnit.SECONDS);
    }

    private void connect0() {
        if(group != null || channel != null)
            return;

        this.group = new NioEventLoopGroup();

        final Bootstrap bootstrap = new Bootstrap().channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) {

                channel.config().setOption(ChannelOption.TCP_NODELAY, true);

                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("timeout", new ReadTimeoutHandler(120L, TimeUnit.SECONDS));
                pipeline.addLast("sizer", new IRCPacketSizer());
                pipeline.addLast("codec", new IRCPacketCodec(IRCClient.this));
                pipeline.addLast("client", IRCClient.this);

            }

        }).group(this.group).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        try {
            LogManager.getLogger().info("IRCClient is connecting to " + SOCKET_ADDRESS.toString());
            ChannelFuture future = bootstrap.connect(SOCKET_ADDRESS);
            future.sync();
        } catch (InterruptedException ignored) { } catch (Exception e) {
            LogManager.getLogger().error("IRCClient has throw a exception by connecting", e);
            autoReconnect();
        }
    }

    public void encrypt(SecretKey key) throws GeneralSecurityException {
        if(channel == null || !channel.isActive())
            return;
        if(channel.pipeline().get("encrypt") != null)
            throw new GeneralSecurityException("Encryption already active");
        if(key != null)   channel.pipeline().addBefore("sizer", "encrypt", new IRCPacketEncryption(key));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
        connectAsync = false;
        handler.onConnected();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        autoReconnect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(cause != null) {
            LogManager.getLogger().error("IRCClient has throw a exception: " + cause.getMessage());
        }
        disconnect();
        connect();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IRCIncomingPacket packet) throws Exception {
        packet.handle(handler);
    }

    public void disconnect() {
        handler.onDisconnected();
        if(channel != null && channel.isOpen()) {
            try {
                channel.flush().close().sync();
            } catch (InterruptedException ignored) { }
        }
        if(this.group != null) {
            this.group.shutdownGracefully();
            this.group = null;
        }
        connectAsync = false;
        channel = null;
    }

    public IRCClientConfig getConfig() {
        return config;
    }

    public IRCProtocol getProtocol() {
        return protocol;
    }

    public IRCPacketHandler getHandler() {
        return handler;
    }

    public void updateMinecraftSession(Session session) {
        if(session != lastMinecraftSession) {
            lastMinecraftSession = session;
            if(isConnected())
                updateLastMinecraftSession();
        }
    }

    public void updateLastMinecraftSession() {
        if(lastMinecraftSession == null)
            return;
        if(!AltLogin.isOnlineMode(lastMinecraftSession.getAccessToken())) {
            sendPacket(new IRCPacketMinecraftRequest(null));
        } else {
            Thread thread = new Thread(() -> {
                try {
                    MinecraftClient.getInstance().getSessionService().joinServer(lastMinecraftSession.getProfile(), lastMinecraftSession.getAccessToken(), "59656c6c6f77536e6f7759656c6c6f77536e6f77"); //serverId "YellowSnowYellowSnow"
                    sendPacket(new IRCPacketMinecraftRequest(lastMinecraftSession.getUsername()));
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                    sendPacket(new IRCPacketMinecraftRequest(null));
                }
            });
            thread.start();
        }
    }

}

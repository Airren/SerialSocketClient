package org.ecust.client;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class SocketClient implements Runnable {
    private static Logger logger = Logger.getLogger(SocketClient.class);
    IoSession session = null;
    String msg = "";
    String host = "";
    int port;

    public SocketClient(String msg, String host, int port) {
        this.msg = msg;
        this.host = host;
        this.port = port;
    }

    public Object getReceivedResult() {
        Object msg = null;
        if (session != null) {
            msg = session.read().getMessage();
        }
        return msg;
    }

    public void run() {
        // 创建一个非阻塞的客户端程序
        IoConnector connector = new NioSocketConnector();

        // 设置链接超时时间
        connector.setConnectTimeout(30000);

        TextLineCodecFactory lineCodec = new TextLineCodecFactory(Charset.forName("UTF-8"));
        lineCodec.setDecoderMaxLineLength(1024 * 1024 * 10);
        lineCodec.setEncoderMaxLineLength(1024 * 1024 * 10);
        // 设置过滤器
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(lineCodec));

        IoSessionConfig cfg = connector.getSessionConfig();

        //实现同步的mina客户端必须设置UseReadOperation属性为true
        cfg.setUseReadOperation(true);

        // 添加业务逻辑处理器类
        connector.setHandler(new ClientHandler());


        try {
            ConnectFuture future = connector.connect(new InetSocketAddress(host, port));// 创建连接
            future.awaitUninterruptibly();// 等待连接创建完成
            session = future.getSession();// 获得session
            session.write(msg);// 发送给服务端
            session.getCloseFuture().awaitUninterruptibly();// 等待连接断开
        } catch (Exception e) {
            logger.error("客户端链接异常...", e);
        } finally {
            session.closeNow();
            connector.dispose();
        }
    }
}

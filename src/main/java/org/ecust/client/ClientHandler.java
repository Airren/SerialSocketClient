package org.ecust.client;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class ClientHandler extends IoHandlerAdapter {

    private static Logger logger = Logger.getLogger(ClientHandler.class);

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String msg = message.toString();
        logger.info("客户端接收到的信息为：" + msg);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        logger.info("客户端发送的信息为：" + message);
    }

//    @Override
//    public void sessionOpened(IoSession session) throws Exception {
//        logger.info("客户端与服务端建立连接");
//    }


    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.error("客户端发生异常...", cause);
    }

}

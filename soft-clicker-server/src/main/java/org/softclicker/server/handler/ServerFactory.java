package org.softclicker.server.handler;

import org.softclicker.message.dto.SoftClickBroadcast;
import org.softclicker.server.entity.Question;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.gui.controllers.quiz.AnswerListener;
import org.softclicker.server.handler.impl.TCPServerHandler;
import org.softclicker.server.handler.impl.UDPServerHandler;

public class ServerFactory {
    /**
     * Starts a TCP server thread and listening to answers.
     *
     * @param question
     * @param answerListener
     */
    public static ServerHandler createListeningServer(Question question, AnswerListener answerListener) throws SoftClickerException {
        TCPServerHandler tcpServerHandler = new TCPServerHandler(TransportUtils.TCP_SERVER_PORT_NUM, question, answerListener);
        tcpServerHandler.start();
        return tcpServerHandler;
    }

    /**
     * Starts a UDP Server thread and broadcasting server details for TCP connections.
     */
    public static ServerHandler createBroadcastingServer() throws SoftClickerException {
        SoftClickBroadcast broadcastMsg = new SoftClickBroadcast();
        broadcastMsg.setServerName("SoftClicker_100");
        broadcastMsg.setServerIP(TransportUtils.getLocalHost().getAddress());
        broadcastMsg.setPort(TransportUtils.TCP_SERVER_PORT_NUM);

        UDPServerHandler udpServerHandler = new UDPServerHandler(TransportUtils.UDP_SERVER_PORT_NUM, broadcastMsg);
        udpServerHandler.start();
        return udpServerHandler;
    }
}
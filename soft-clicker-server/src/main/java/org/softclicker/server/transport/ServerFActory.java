package org.softclicker.server.transport;

import org.softclicker.message.dto.SoftClickBroadcast;
import org.softclicker.server.entity.Question;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.gui.controllers.quiz.AnswerListener;
import org.softclicker.server.transport.impl.TCPServer;
import org.softclicker.server.transport.impl.UDPServer;

public class ServerFactory {
    /**
     * Starts a TCP server thread and listening to answers.
     *
     * @param question
     * @param answerListener
     */
    public static Server createListeningServer(Question question, AnswerListener answerListener) throws SoftClickerException {
        TCPServer tcpServer = new TCPServer(TransportUtils.TCP_SERVER_PORT_NUM, question, answerListener);
        tcpServer.start();
        return tcpServer;
    }

    /**
     * Starts a UDP Server thread and broadcasting server details for TCP connections.
     */
    public static Server createBroadcastingServer() throws SoftClickerException {
        SoftClickBroadcast broadcastMsg = new SoftClickBroadcast();
        broadcastMsg.setServerName("SoftClicker_100");
        broadcastMsg.setServerIP(TransportUtils.getLocalHost().getAddress());
        broadcastMsg.setPort(TransportUtils.TCP_SERVER_PORT_NUM);

        UDPServer udpServer = new UDPServer(TransportUtils.UDP_SERVER_PORT_NUM, broadcastMsg);
        udpServer.start();
        return udpServer;
    }
}
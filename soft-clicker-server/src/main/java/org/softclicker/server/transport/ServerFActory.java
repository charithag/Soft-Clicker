package org.softclicker.server.transport;

import org.softclicker.server.entity.Question;
import org.softclicker.server.gui.controllers.quiz.AnswerListener;

public class ServerFactory {
    /**
     * Starts a TCP server thread and listening to answers.
     * @param question
     * @param answerListener
     */
    public static void createTCPServer(Question question, AnswerListener answerListener) {
        TCPServerThread tcpServer = new TCPServerThread(question, answerListener);
        tcpServer.start();
    }

    /**
     * Starts a UDP Server thread and broadcasting server details for TCP connections.
     */
    public static void createUDPServer() {
        UDPServerThread udpServer = new UDPServerThread();
        udpServer.start();
    }
}
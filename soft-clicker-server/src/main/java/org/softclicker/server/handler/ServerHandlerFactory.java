package org.softclicker.server.handler;

import org.softclicker.message.dto.SoftClickBroadcast;
import org.softclicker.server.entity.Question;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.gui.controllers.quiz.AnswerListener;
import org.softclicker.server.handler.impl.ListeningHandler;
import org.softclicker.server.handler.impl.BroadcastingHandler;

public class ServerHandlerFactory {
    /**
     * Starts a TCP server thread and listening to answers.
     *
     * @param question
     * @param answerListener
     */
    public static ServerHandler createListeningHandler(Question question, AnswerListener answerListener) throws SoftClickerException {
        ListeningHandler listeningHandler = new ListeningHandler(HandlerUtils.TCP_SERVER_PORT_NUM, question, answerListener);
        listeningHandler.start();
        return listeningHandler;
    }

    /**
     * Starts a UDP Server thread and broadcasting server details for TCP connections.
     */
    public static ServerHandler createBroadcastingHandler() throws SoftClickerException {
        SoftClickBroadcast broadcastMsg = new SoftClickBroadcast();
        broadcastMsg.setServerName("SoftClicker_100");
        broadcastMsg.setServerIP(HandlerUtils.getLocalHost().getAddress());
        broadcastMsg.setPort(HandlerUtils.TCP_SERVER_PORT_NUM);

        BroadcastingHandler broadcastingHandler = new BroadcastingHandler(HandlerUtils.UDP_SERVER_PORT_NUM, broadcastMsg);
        broadcastingHandler.start();
        return broadcastingHandler;
    }
}
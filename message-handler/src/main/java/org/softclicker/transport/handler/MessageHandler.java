package org.softclicker.transport.handler;

import org.softclicker.message.dao.SoftClickAnswerDAO;
import org.softclicker.message.dao.SoftClickBroadcastDAO;
import org.softclicker.message.dto.SoftClickAnswer;
import org.softclicker.message.dto.SoftClickBroadcast;

import java.net.UnknownHostException;

public class MessageHandler {

    private SoftClickBroadcastDAO softClickBroadcastDAO;
    private SoftClickAnswerDAO softClickAnswerDAO;

    private MessageHandler() {
    }

    public MessageHandler(SoftClickBroadcastDAO softClickBroadcastDAO,
                          SoftClickAnswerDAO softClickAnswerDAO) {
        this.softClickBroadcastDAO = softClickBroadcastDAO;
        this.softClickAnswerDAO = softClickAnswerDAO;
    }

    public byte[] encodeBroadcast(SoftClickBroadcast softClickBroadcast) throws UnknownHostException {
        return softClickBroadcastDAO.encodeMessage(softClickBroadcast);
    }

    public SoftClickBroadcast decodeBroadcast(byte[] message) throws UnknownHostException {
        return softClickBroadcastDAO.decodeMessage(message);
    }

    public byte[] encodeAnswer(SoftClickAnswer softClickAnswer) {
        return softClickAnswerDAO.encodeMessage(softClickAnswer);
    }

    public SoftClickAnswer decodeAnswer(byte[] message) {
        return softClickAnswerDAO.decodeMessage(message);
    }

}
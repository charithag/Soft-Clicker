package org.softclicker.transport;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.softclicker.message.dao.impl.SoftClickAnswerDAOImpl;
import org.softclicker.message.dao.impl.SoftClickBroadcastDAOImpl;
import org.softclicker.message.dto.SoftClickAnswer;
import org.softclicker.message.dto.SoftClickBroadcast;
import org.softclicker.transport.handler.MessageHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

public class TransportTest {

    private static Logger log = Logger.getLogger(TransportTest.class);

    @Test
    public void testBroadcast() throws UnknownHostException {
        MessageHandler messageHandler = new MessageHandler(new SoftClickBroadcastDAOImpl(),
                new SoftClickAnswerDAOImpl());

        SoftClickBroadcast softClickBroadcast = new SoftClickBroadcast();
        softClickBroadcast.setServerName("Test server");
        softClickBroadcast.setServerIP(InetAddress.getLocalHost());
        softClickBroadcast.setPort(8080);

        byte[] message = messageHandler.encodeBroadcast(softClickBroadcast);
        assertEquals("Expected length:", 13 + softClickBroadcast.getServerName().length(), message.length);

        SoftClickBroadcast softClickBroadcastDecoded = messageHandler.decodeBroadcast(message);
        assertEquals("Server name:", softClickBroadcast.getServerName(),
                softClickBroadcastDecoded.getServerName());
        assertEquals("Server IP:", softClickBroadcast.getServerIP(), softClickBroadcastDecoded.getServerIP());
        assertEquals("Server port:", softClickBroadcast.getPort(), softClickBroadcastDecoded.getPort());
    }

    @Test
    public void testAnswer() throws UnknownHostException {
        MessageHandler messageHandler = new MessageHandler(new SoftClickBroadcastDAOImpl(),
                new SoftClickAnswerDAOImpl());

        SoftClickAnswer softClickAnswerEntity = new SoftClickAnswer();
        softClickAnswerEntity.setAnswerOption(SoftClickAnswer.AnswerOption.OPTION_3);
        softClickAnswerEntity.setStudentId("168222C");

        byte[] message = messageHandler.encodeAnswer(softClickAnswerEntity);
        assertEquals("Expected length:", 7 + softClickAnswerEntity.getStudentId().length(), message.length);

        SoftClickAnswer softClickAnswerEntityDecoded = messageHandler.decodeAnswer(message);
        assertEquals("Student ID:", softClickAnswerEntity.getStudentId(),
                softClickAnswerEntityDecoded.getStudentId());
        assertEquals("Answer option:", softClickAnswerEntity.getAnswerOption(),
                softClickAnswerEntityDecoded.getAnswerOption());
    }
}
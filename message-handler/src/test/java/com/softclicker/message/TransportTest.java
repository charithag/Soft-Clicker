package com.softclicker.message;

import com.softclicker.message.dto.SCAnswer;
import com.softclicker.message.dto.SCBroadcast;
import com.softclicker.message.handler.TransportDecoder;
import com.softclicker.message.handler.TransportEncoder;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;

/**
 * Created by charitha on 3/6/16.
 */
public class TransportTest {

    private static Logger log = Logger.getLogger(TransportTest.class);

    @Test
    public void testBroadcast() throws UnknownHostException {
        SCBroadcast scBroadcast = new SCBroadcast();
        scBroadcast.setServerName("Test server");
        scBroadcast.setServerIP(InetAddress.getLocalHost());
        scBroadcast.setPort(8080);

        byte[] message = TransportEncoder.encodeBroadcast(scBroadcast);
        assertEquals("Expected length:", 13 + scBroadcast.getServerName().length(), message.length);

        SCBroadcast scBroadcastDecoded = TransportDecoder.decodeBoradcast(message);
        assertEquals("Server name:", scBroadcast.getServerName(), scBroadcastDecoded.getServerName());
        assertEquals("Server IP:", scBroadcast.getServerIP(), scBroadcastDecoded.getServerIP());
        assertEquals("Server port:", scBroadcast.getPort(), scBroadcastDecoded.getPort());
    }

    @Test
    public void testAnswer() throws UnknownHostException {
        SCAnswer scAnswer = new SCAnswer();
        scAnswer.setAnswerOption(SCAnswer.AnswerOption.OPTION_3);
        scAnswer.setStudentId("168222C");

        byte[] message = TransportEncoder.encodeAnswer(scAnswer);
        assertEquals("Expected length:", 7 + scAnswer.getStudentId().length(), message.length);

        SCAnswer scAnswerDecoded = TransportDecoder.decodeAnswer(message);
        assertEquals("Student ID:", scAnswer.getStudentId(), scAnswerDecoded.getStudentId());
        assertEquals("Answer option:", scAnswer.getAnswerOption(), scAnswerDecoded.getAnswerOption());
    }
}
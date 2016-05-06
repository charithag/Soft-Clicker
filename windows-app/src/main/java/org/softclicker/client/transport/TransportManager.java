package org.softclicker.client.transport;

import org.softclicker.message.dao.impl.SoftClickAnswerDAOImpl;
import org.softclicker.message.dao.impl.SoftClickBroadcastDAOImpl;
import org.softclicker.message.dto.SoftClickAnswer;
import org.softclicker.transport.handler.MessageHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Admin on 5/6/2016.
 */
public class TransportManager {

    private static TransportManager instance;
    private String host = "localhost";

    public static TransportManager getInstance() {

        if (instance == null) {
            instance = new TransportManager();
        }
        return instance;
    }

    private TransportManager() {
    }

    public void sendAnswers(String studentId, SoftClickAnswer.AnswerOption answerOption) {
        MessageHandler messageHandler = new MessageHandler(new SoftClickBroadcastDAOImpl(),
                new SoftClickAnswerDAOImpl());

        SoftClickAnswer softClickAnswerEntity = new SoftClickAnswer();
        softClickAnswerEntity.setAnswerOption(answerOption);
        softClickAnswerEntity.setStudentId(studentId);

        byte[] message = messageHandler.encodeAnswer(softClickAnswerEntity);

        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(host);

            DatagramPacket sendPacket = new DatagramPacket(message, message.length, IPAddress, 8080);
            clientSocket.send(sendPacket);

        } catch (Exception ex) {
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}

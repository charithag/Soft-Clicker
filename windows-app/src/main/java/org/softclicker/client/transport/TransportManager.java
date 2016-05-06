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

    public void sendAnswers(String studentId, SoftClickAnswer.AnswerOption answerOption) {
        MessageHandler messageHandler = new MessageHandler(new SoftClickBroadcastDAOImpl(),
                new SoftClickAnswerDAOImpl());

        SoftClickAnswer softClickAnswerEntity = new SoftClickAnswer();
        softClickAnswerEntity.setAnswerOption(answerOption);
        softClickAnswerEntity.setStudentId(studentId);

        byte[] message = messageHandler.encodeAnswer(softClickAnswerEntity);

        try {


            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");
//            byte[] receiveData = new byte[1024];

            DatagramPacket sendPacket = new DatagramPacket(message, message.length, IPAddress, 8080);
            clientSocket.send(sendPacket);

        } catch (Exception ex) {
        }
    }
}

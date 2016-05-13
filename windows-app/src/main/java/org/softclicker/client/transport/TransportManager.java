package org.softclicker.client.transport;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.client.gui.Constants;
import org.softclicker.message.dao.impl.SoftClickAnswerDAOImpl;
import org.softclicker.message.dao.impl.SoftClickBroadcastDAOImpl;
import org.softclicker.message.dto.SoftClickAnswer;
import org.softclicker.message.dto.SoftClickBroadcast;
import org.softclicker.transport.handler.MessageHandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Admin on 5/6/2016.
 */
public class TransportManager {

    private final static Logger log = LogManager.getLogger(TransportManager.class);

    private static TransportManager instance;
    private String host = "";
    private int port;
    private String name;


    private DatagramSocket socket;
    private MessageHandler messageHandler;

    public static TransportManager getInstance() {

        if (instance == null) {
            instance = new TransportManager();
        }
        return instance;
    }

    private TransportManager() {
        messageHandler = new MessageHandler(new SoftClickBroadcastDAOImpl(), new SoftClickAnswerDAOImpl());
        listenToServerBroadcast();
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

            DatagramPacket sendPacket = new DatagramPacket(message, message.length, IPAddress, port);
            clientSocket.send(sendPacket);

        } catch (Exception ex) {
        }
    }

    private void listenToServerBroadcast() {


        new Thread(new Runnable() {
            public void run() {
                try {
                    byte[] recvBuf = new byte[15000];
                    if (socket == null || socket.isClosed()) {
                        socket = new MulticastSocket(Constants.BORADCAST_PORT);
                        socket.setBroadcast(true);
                    }
                    socket.setSoTimeout(10000);
                    DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);


                    socket.receive(packet);

                    String senderIP = packet.getAddress().getHostAddress();
                    String message = new String(packet.getData()).trim();


                    final SoftClickBroadcast softClickBroadcast = messageHandler.decodeBroadcast(packet.getData());
                    host = softClickBroadcast.getServerIP().getHostAddress();
                    port = softClickBroadcast.getPort();
                    name = softClickBroadcast.getServerName();


                    socket.close();




                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }).start();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}

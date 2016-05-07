package org.softclicker.server.transport;

import org.apache.log4j.LogManager;
import org.softclicker.message.dao.impl.SoftClickAnswerDAOImpl;
import org.softclicker.message.dao.impl.SoftClickBroadcastDAOImpl;
import org.softclicker.message.dto.SoftClickBroadcast;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.exception.SoftClickerRuntimeException;
import org.softclicker.transport.handler.MessageHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPServerThread extends Thread {

    private final static org.apache.log4j.Logger log = LogManager.getLogger(UDPServerThread.class);

    @Override
    public void run() {
        DatagramSocket socket = null;
        InetSocketAddress destination = null;
        try {
            //Keep a socket open to listen to all the UDP traffic that is destined for this port
            socket = new DatagramSocket(TransportUtils.SOFTCLICKER_UDP_PORT_NUM);
            socket.setBroadcast(true);
            destination = new InetSocketAddress(InetAddress.getByName(TransportUtils.getBroadcast().toString().substring(1)), TransportUtils.SOFTCLICKER_UDP_PORT_NUM);
        } catch (IOException | SoftClickerException e) {
            throw new SoftClickerRuntimeException("Cannot create socket for server broadcasting.", e);
        }
        while (true) {
            try {
                MessageHandler messageHandler = new MessageHandler(new SoftClickBroadcastDAOImpl(),
                        new SoftClickAnswerDAOImpl());
                SoftClickBroadcast softClickBroadcast = new SoftClickBroadcast();
                softClickBroadcast.setServerName("Test server");
                softClickBroadcast.setServerIP(InetAddress.getLocalHost());
                softClickBroadcast.setPort(TransportUtils.SOFTCLICKER_TCP_PORT_NUM);
                byte[] message = messageHandler.encodeBroadcast(softClickBroadcast);
                DatagramPacket sendPacket = new DatagramPacket(message, message.length, destination);
                socket.send(sendPacket);
                Thread.sleep(5000);
            } catch (IOException | InterruptedException e) {
                log.error("Error while broadcasting message", e);
            }
        }

    }
}

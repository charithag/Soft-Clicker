package org.softclicker.server.transport.impl;

import org.apache.log4j.LogManager;
import org.softclicker.message.dao.impl.SoftClickAnswerDAOImpl;
import org.softclicker.message.dao.impl.SoftClickBroadcastDAOImpl;
import org.softclicker.message.dto.SoftClickBroadcast;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.exception.SoftClickerRuntimeException;
import org.softclicker.server.transport.Server;
import org.softclicker.server.transport.TransportUtils;
import org.softclicker.transport.handler.MessageHandler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class UDPServer implements Server {

    private final static org.apache.log4j.Logger log = LogManager.getLogger(UDPServer.class);
    private volatile Thread serverThread;

    public UDPServer(int port, SoftClickBroadcast broadcastMsg) {
        this.serverThread = new Thread() {
            @Override
            public void run() {
                DatagramSocket socket;
                InetSocketAddress destination;
                try {
                    //Keep a socket open to listen to all the UDP traffic that is destined for this port
                    socket = new DatagramSocket(port);
                    socket.setBroadcast(true);
                    destination = new InetSocketAddress(InetAddress.getByName(TransportUtils.getBroadcast().toString().substring(1)), port);
                } catch (IOException | SoftClickerException e) {
                    throw new SoftClickerRuntimeException("Cannot create socket for server broadcasting.", e);
                }
                while (true) {
                    try {
                        MessageHandler messageHandler = new MessageHandler(new SoftClickBroadcastDAOImpl(),
                                new SoftClickAnswerDAOImpl());
                        byte[] message = messageHandler.encodeBroadcast(broadcastMsg);
                        DatagramPacket sendPacket = new DatagramPacket(message, message.length, destination);
                        socket.send(sendPacket);
                        Thread.sleep(5000);
                    } catch (IOException e) {
                        log.error("Error while broadcasting message", e);
                    } catch (InterruptedException e) {
                        log.info("UDPServer interrupted.");
                    }
                }
            }
        };
    }

    @Override
    public void start() {
        this.serverThread.start();
    }

    @Override
    public void stop() {
        this.serverThread = null;
    }

    @Override
    public boolean isRunning() {
        return false;
    }
}

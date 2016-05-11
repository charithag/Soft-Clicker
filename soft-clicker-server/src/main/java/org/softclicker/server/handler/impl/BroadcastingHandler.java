package org.softclicker.server.handler.impl;

import org.apache.log4j.LogManager;
import org.softclicker.message.dao.impl.SoftClickAnswerDAOImpl;
import org.softclicker.message.dao.impl.SoftClickBroadcastDAOImpl;
import org.softclicker.message.dto.SoftClickBroadcast;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.handler.ServerHandler;
import org.softclicker.server.handler.HandlerUtils;
import org.softclicker.transport.handler.MessageHandler;

import java.io.IOException;
import java.net.*;

public class BroadcastingHandler implements ServerHandler {

    private final static org.apache.log4j.Logger log = LogManager.getLogger(BroadcastingHandler.class);
    private volatile Thread serverThread;

    public BroadcastingHandler(int port, SoftClickBroadcast broadcastMsg) throws SoftClickerException {
        MulticastSocket socket;
        InetSocketAddress destination;
        try {
            //Keep a socket open to listen to all the UDP traffic that is destined for this port
            socket = new MulticastSocket(port);
            socket.setBroadcast(true);
            destination = new InetSocketAddress(InetAddress.getByName(HandlerUtils.getBroadcast().toString().substring(1)), port);
        } catch (IOException e) {
            throw new SoftClickerException("Cannot create socket for server broadcasting.", e);
        }
        this.serverThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        MessageHandler messageHandler = new MessageHandler(new SoftClickBroadcastDAOImpl(),
                                new SoftClickAnswerDAOImpl());
                        byte[] message = messageHandler.encodeBroadcast(broadcastMsg);
                        DatagramPacket sendPacket = new DatagramPacket(message, message.length, destination);
                        socket.send(sendPacket);
                        log.info(new String(sendPacket.getData()));
                        Thread.sleep(2000);
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
        this.serverThread.interrupt();
        this.serverThread = null;
    }

    @Override
    public boolean isRunning() {
        return this.serverThread.isAlive();
    }
}

package org.softclicker.message.dao.impl;

import org.softclicker.message.dao.MessageUtil;
import org.softclicker.message.dao.SoftClickBroadcastDAO;
import org.softclicker.message.dto.SoftClickBroadcast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;

public class SoftClickBroadcastDAOImpl implements SoftClickBroadcastDAO {

    public byte[] encodeMessage(SoftClickBroadcast softClickBroadcast) throws UnknownHostException {
        byte[] serverIp = softClickBroadcast.getServerIP().getAddress();
        byte[] port = new byte[2];
        port[0] = (byte) (softClickBroadcast.getPort() / 256);
        port[1] = (byte) (softClickBroadcast.getPort() % 256);
        byte[] serverName = softClickBroadcast.getServerName().getBytes();

        int length = 13 + serverName.length;
        byte[] message = new byte[length];
        int index = 0;

        message[index++] = 1; //SOH
        // Server IP
        for (int i = 0; i < 4; i++) {
            message[index++] = serverIp[i];
        }

        message[index++] = 30; //RS
        //Server port
        for (int i = 0; i < 2; i++) {
            message[index++] = port[i];
        }

        message[index++] = 30; //RS
        message[index++] = (byte) serverName.length; //length
        message[index++] = 2; //STX
        //Server name
        for (byte a : serverName) {
            message[index++] = a;
        }
        message[index++] = 3; //ETX
        message[index] = 4; //EOT

        return message;
    }

    public SoftClickBroadcast decodeMessage(byte[] received) throws UnknownHostException {

        byte[] message = MessageUtil.getMessageFromBytes(received);

        SoftClickBroadcast softClickBroadcast = new SoftClickBroadcast();

        int index = 0;
        //Ensure EOH and EOT present in the message.
        if (message[index++] != 1 || message[message.length - 1] != 4) {
            throw new InputMismatchException("Invalid message format.");
        }

        //Set server IP.
        byte[] serverIP = new byte[4];
        for (int i = 0; i < 4; i++) {
            serverIP[i] = message[index++];
        }
        softClickBroadcast.setServerIP(InetAddress.getByAddress(serverIP));

        //Check for RS
        if (message[index++] != 30) {
            throw new InputMismatchException("Invalid message format.");
        }
        //Set server port
        byte[] port = new byte[2];
        for (int i = 0; i < 2; i++) {
            port[i] = message[index++];
        }
        softClickBroadcast.setPort(((port[0] & 0xFF)) * 256 + (port[1] & 0xFF));

        //Check for RS
        if (message[index++] != 30) {
            throw new InputMismatchException("Invalid message format.");
        }
        //Read server name.
        int length = message[index++];
        //Check for STX
        if (message[index++] != 2) {
            throw new InputMismatchException("Invalid message format.");
        }
        byte[] serverName = new byte[length];
        for (int i = 0; i < length; i++) {
            serverName[i] = message[index++];
        }
        softClickBroadcast.setServerName(new String(serverName));
        //Check for ETX
        if (message[index] != 3) {
            throw new InputMismatchException("Invalid message format.");
        }

        return softClickBroadcast;
    }
}
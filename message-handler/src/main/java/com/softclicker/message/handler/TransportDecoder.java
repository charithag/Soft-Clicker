package com.softclicker.message.handler;

import com.softclicker.message.dto.SCAnswer;
import com.softclicker.message.dto.SCBroadcast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;

/**
 * Created by charitha on 3/6/16.
 */
public class TransportDecoder {

    public static SCBroadcast decodeBoradcast(byte[] message) throws UnknownHostException {
        SCBroadcast scBroadcast = new SCBroadcast();

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
        scBroadcast.setServerIP(InetAddress.getByAddress(serverIP));

        //Check for RS
        if (message[index++] != 30) {
            throw new InputMismatchException("Invalid message format.");
        }
        //Set server port
        byte[] port = new byte[2];
        for (int i = 0; i < 2; i++) {
            port[i] = message[index++];
        }
        scBroadcast.setPort(((port[0] & 0xFF)) * 256 + (port[1] & 0xFF));

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
        scBroadcast.setServerName(new String(serverName));
        //Check for ETX
        if (message[index] != 3) {
            throw new InputMismatchException("Invalid message format.");
        }

        return scBroadcast;
    }

    public static SCAnswer decodeAnswer(byte[] message) throws UnknownHostException {
        SCAnswer scAnswer = new SCAnswer();

        int index = 0;
        //Ensure EOH and EOT present in the message.
        if (message[index++] != 1 || message[message.length - 1] != 4) {
            throw new InputMismatchException("Invalid message format.");
        }

        //Read student ID.
        int length = message[index++];
        //Check for STX
        if (message[index++] != 2) {
            throw new InputMismatchException("Invalid message format.");
        }
        byte[] serverName = new byte[length];
        for (int i = 0; i < length; i++) {
            serverName[i] = message[index++];
        }
        scAnswer.setStudentId(new String(serverName));
        //Check for ETX
        if (message[index++] != 3) {
            throw new InputMismatchException("Invalid message format.");
        }

        //Check for RS
        if (message[index++] != 30) {
            throw new InputMismatchException("Invalid message format.");
        }
        scAnswer.setAnswerOption(SCAnswer.AnswerOption.values()[(int) message[index]]);

        return scAnswer;
    }

}

package com.softclicker.message.handler;

import com.softclicker.message.dto.SCAnswer;
import com.softclicker.message.dto.SCBroadcast;

import java.nio.ByteBuffer;

/**
 * Created by charitha on 3/6/16.
 */
public class TransportEncoder {

    public static byte[] encodeBroadcast(SCBroadcast scBroadcast) {

        byte[] serverIp = scBroadcast.getServerIP().getAddress();
        byte[] port = new byte[2];
        port[0] = (byte) (scBroadcast.getPort() / 256);
        port[1] = (byte) (scBroadcast.getPort() % 256);
        byte[] serverName = scBroadcast.getServerName().getBytes();

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

    public static byte[] encodeAnswer(SCAnswer scAnswer) {

        byte[] studentId = scAnswer.getStudentId().getBytes();

        int length = 7 + studentId.length;
        byte[] message = new byte[length];
        int index = 0;

        message[index++] = 1; //SOH
        message[index++] = (byte) studentId.length; //length
        message[index++] = 2; //STX
        //Student Id
        for (byte a : studentId) {
            message[index++] = a;
        }
        message[index++] = 3; //ETX

        message[index++] = 30; //RS
        //Answer
        message[index++] = (byte) scAnswer.getAnswerOption().ordinal();
        message[index] = 4; //EOT

        return message;
    }
}

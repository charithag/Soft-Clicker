package org.softclicker.message.dao.impl;

import org.softclicker.message.dao.MessageUtil;
import org.softclicker.message.dao.SoftClickAnswerDAO;
import org.softclicker.message.dto.SoftClickAnswer;

import java.util.InputMismatchException;

public class SoftClickAnswerDAOImpl implements SoftClickAnswerDAO {

    public byte[] encodeMessage(SoftClickAnswer softClickAnswer) {
        byte[] studentId = softClickAnswer.getStudentId().getBytes();

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
        message[index++] = (byte) softClickAnswer.getAnswerOption().ordinal();
        message[index] = 4; //EOT

        return message;
    }

    public SoftClickAnswer decodeMessage(byte[] received) {

        // No need to remove extra bytes
//        byte[] message = MessageUtil.getMessageFromBytes(received);
        byte[] message = received;

        SoftClickAnswer softClickAnswer = new SoftClickAnswer();

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
        softClickAnswer.setStudentId(new String(serverName));
        //Check for ETX
        if (message[index++] != 3) {
            throw new InputMismatchException("Invalid message format.");
        }

        //Check for RS
        if (message[index++] != 30) {
            throw new InputMismatchException("Invalid message format.");
        }
        softClickAnswer.setAnswerOption(SoftClickAnswer.AnswerOption.values()[(int) message[index]]);

        return softClickAnswer;
    }
}
package org.softclicker.server.transport;

import org.softclicker.server.entity.Answer;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.exception.SoftClickerRuntimeException;
import org.softclicker.server.gui.MainApplication;
import org.softclicker.server.gui.controllers.quiz.AnswerListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class TCPServerThread extends Thread {

    private final Question listeningQuestion;
    private final AnswerListener answerListener;

    public TCPServerThread(Question listeningQuestion, AnswerListener answerListener) {
        this.listeningQuestion = listeningQuestion;
        this.answerListener = answerListener;
    }

    @Override
    public void run() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(TransportUtils.SOFTCLICKER_TCP_PORT_NUM);
        } catch (IOException e) {
            throw new SoftClickerRuntimeException("Cannot create TCP socket.");
        }

        while (true) {
            try {
                Socket connectionSocket = serverSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                String answerText = inFromClient.readLine();
                User loggedUser = MainApplication.getInstance().getLoggedUser();
                Answer answer = new Answer(-1, answerText, listeningQuestion, loggedUser, new Date());
                try {
                    //answer received, trigger the callback
                    answerListener.answerReceived(answer);
                    //Send a success response
                    String sendData = "Answer saved";
                    outToClient.writeBytes(sendData);
                } catch (SoftClickerException e) {
                    //Send an error response
                    String sendData = "Answer saving failed";
                    outToClient.writeBytes(sendData);
                }
            } catch (IOException e) {
                throw new SoftClickerRuntimeException("Cannot listen TCP socket.");
            }
        }
    }
}

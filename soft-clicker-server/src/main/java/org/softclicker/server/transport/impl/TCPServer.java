package org.softclicker.server.transport.impl;

import org.softclicker.server.entity.Answer;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;
import org.softclicker.server.exception.SoftClickerException;
import org.softclicker.server.exception.SoftClickerRuntimeException;
import org.softclicker.server.gui.MainApplication;
import org.softclicker.server.gui.controllers.quiz.AnswerListener;
import org.softclicker.server.transport.Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class TCPServer implements Server {

    private volatile Thread serverThread;

    public TCPServer(int port, Question listeningQuestion, AnswerListener answerListener) throws SoftClickerException {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new SoftClickerException("Cannot create TCP socket.");
        }
        this.serverThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Socket connectionSocket = serverSocket.accept();
                        BufferedReader receivedMsg = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        DataOutputStream sendingMsg = new DataOutputStream(connectionSocket.getOutputStream());
                        String answerText = receivedMsg.readLine();
                        User loggedUser = MainApplication.getInstance().getLoggedUser();
                        Answer answer = new Answer(-1, answerText, listeningQuestion, loggedUser, new Date());
                        try {
                            //answer received, trigger the callback
                            answerListener.answerReceived(answer);
                            //Send a success response
                            String sendData = "Answer saved";
                            sendingMsg.writeBytes(sendData);
                        } catch (SoftClickerException e) {
                            //Send an error response
                            String sendData = "Answer saving failed";
                            sendingMsg.writeBytes(sendData);
                        }
                    } catch (IOException e) {
                        throw new SoftClickerRuntimeException("Cannot listen TCP socket.");
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

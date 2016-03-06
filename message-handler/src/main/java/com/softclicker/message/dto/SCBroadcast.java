package com.softclicker.message.dto;

import java.net.InetAddress;

/**
 * Created by charitha on 3/6/16.
 */
public class SCBroadcast {
    private InetAddress serverIP;
    private int port;
    private String serverName;

    public InetAddress getServerIP() {
        return serverIP;
    }

    public void setServerIP(InetAddress serverIP) {
        this.serverIP = serverIP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}

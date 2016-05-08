package org.softclicker.server.handler;

import org.apache.log4j.LogManager;
import org.softclicker.server.exception.SoftClickerException;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;

public class HandlerUtils {

    public static final int UDP_SERVER_PORT_NUM = 10100;
    public static final int TCP_SERVER_PORT_NUM = 6789;

    public static InterfaceAddress getLocalHost() throws SoftClickerException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
            Enumeration list = NetworkInterface.getNetworkInterfaces();
            while (list.hasMoreElements()) {
                NetworkInterface iface = (NetworkInterface) list.nextElement();
                if (iface == null) continue;
                if (!iface.isLoopback() && iface.isUp()) {
                    Iterator it = iface.getInterfaceAddresses().iterator();
                    while (it.hasNext()) {
                        InterfaceAddress address = (InterfaceAddress) it.next();
                        if (address == null) continue;
                        InetAddress broadcast = address.getBroadcast();
                        if (broadcast != null) {
                            return address;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new SoftClickerException("Cannot read local interfaces.", e);
        }
        throw new SoftClickerException("Cannot not find local interface address.");
    }

    public static InetAddress getBroadcast() throws SoftClickerException {
        return getLocalHost().getBroadcast();
    }
}

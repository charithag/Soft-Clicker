package org.softclicker.message.dao;

import java.util.Arrays;

public class MessageUtil {

    public static byte[] getMessageFromBytes(byte[] received) {
        int length;
        //length should be started from 13 to by pass 0x00 in IP address
        for (length = 13; length < received.length; length++) {
            if (received[length] == 0x00) {
                break;
            }
        }
        return Arrays.copyOf(received, length);
    }
}

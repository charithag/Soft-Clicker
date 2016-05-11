package org.softclicker.message.dao;

import java.util.Arrays;

public class MessageUtil {

    public static byte[] getMessageFromBytes(byte[] received) {
        int length;

        for (length = 0; length < received.length; length++) {
            if (received[length] == 0x00) {
                break;
            }
        }
        return Arrays.copyOf(received, length);
    }
}

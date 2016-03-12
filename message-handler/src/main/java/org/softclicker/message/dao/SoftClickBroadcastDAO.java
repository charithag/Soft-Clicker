package org.softclicker.message.dao;

import org.softclicker.message.dto.SoftClickBroadcast;

import java.net.UnknownHostException;

public interface SoftClickBroadcastDAO {

    byte[] encodeMessage(SoftClickBroadcast softClickBroadcast) throws UnknownHostException;

    SoftClickBroadcast decodeMessage(byte[] message) throws UnknownHostException;
}
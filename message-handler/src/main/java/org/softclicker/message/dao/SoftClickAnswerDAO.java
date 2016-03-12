package org.softclicker.message.dao;

import org.softclicker.message.dto.SoftClickAnswer;

public interface SoftClickAnswerDAO {

    byte[] encodeMessage(SoftClickAnswer answer);

    SoftClickAnswer decodeMessage(byte[] message);
}
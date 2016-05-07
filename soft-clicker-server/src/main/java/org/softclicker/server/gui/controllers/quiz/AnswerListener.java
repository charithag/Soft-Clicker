package org.softclicker.server.gui.controllers.quiz;

import org.softclicker.server.entity.Answer;
import org.softclicker.server.exception.SoftClickerException;

public interface AnswerListener {
    void answerReceived(Answer answer) throws SoftClickerException;
}


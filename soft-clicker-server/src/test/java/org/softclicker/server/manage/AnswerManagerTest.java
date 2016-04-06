package org.softclicker.server.manage;

import org.junit.Test;
import org.softclicker.server.entity.Answer;

public class AnswerManagerTest extends AbstractDatabaseTest {

    @Test
    public void testGetAllUsers() throws Exception {
        AnswerManager answerManager = new AnswerManager(scopingDataSource);
        for (Answer answer : answerManager.getAllAnswers()) {
            System.out.println(answer);
        }
    }
}
package org.softclicker.server.manage;

import org.junit.Assert;
import org.junit.Test;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.Question;
import org.softclicker.server.entity.User;

public class QuestionManagerTest extends AbstractDatabaseTest {

    @Test
    public void testGetAllQuestions() throws Exception {
        QuestionManager questionManager = new QuestionManager(scopingDataSource);
        for (Question question : questionManager.getAllQuestions()) {
            System.out.println(question);
        }
    }
}
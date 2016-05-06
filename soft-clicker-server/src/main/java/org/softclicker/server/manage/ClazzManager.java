package org.softclicker.server.manage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.dao.impl.ClazzDAO;
import org.softclicker.server.dao.impl.QuestionDAO;
import org.softclicker.server.entity.Clazz;
import org.softclicker.server.entity.Question;
import org.softclicker.server.exception.SoftClickerException;

import java.sql.SQLException;
import java.util.List;

public class ClazzManager {

    private static final Logger log = LogManager.getLogger(ClazzManager.class);
    private final ScopingDataSource scopingDataSource;
    private final ClazzDAO clazzDAO;

    public ClazzManager(ScopingDataSource scopingDataSource) {
        this.scopingDataSource = scopingDataSource;
        this.clazzDAO = new ClazzDAO(scopingDataSource);
    }

    public List<Clazz> getAllClazzes() throws SoftClickerException {
        try {
            scopingDataSource.beginConnectionScope();
            return clazzDAO.getAllClazzes();
        } catch (SQLException e) {
            throw new SoftClickerException("Error while retrieving class list", e);
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

    public boolean saveClazz(Clazz clazz) throws SoftClickerException{
        try {
            scopingDataSource.beginConnectionScope();
            return clazzDAO.saveClazz(clazz);
        } catch (SQLException e) {
            throw new SoftClickerException("Error while retrieving class list", e);
        } finally {
            scopingDataSource.endConnectionScope();
        }
    }

}

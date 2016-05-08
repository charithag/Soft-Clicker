package org.softclicker.server.dao.impl;

import org.apache.log4j.Logger;
import org.softclicker.server.dao.DAOUtil;
import org.softclicker.server.dao.ScopingDataSource;
import org.softclicker.server.entity.Clazz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chamika on 5/6/16.
 */
public class ClazzDAO extends AbstractGenericDAO<Clazz> {

    private final static String TABLE_NAME = "CLASS";
    private final static Logger log = Logger.getLogger(ClazzDAO.class);

    public ClazzDAO(ScopingDataSource scopingDataSource) {
        super(scopingDataSource, TABLE_NAME);
    }

    @Override
    public int count() throws SQLException {
        return 0;
    }

    public List<Clazz> getAllClazzes() throws SQLException {
        String sql = "SELECT * FROM `CLASS`";
        List<Clazz> clazzes = new ArrayList<>();
        try (
                Connection conn = scopingDataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
        ) {
            while (rs.next()) {
                Clazz clazz = DAOUtil.loadClass(rs, null);
                clazzes.add(clazz);
            }
            return clazzes;
        }
    }

    public boolean saveClazz(Clazz clazz) {
        String sql = "INSERT INTO " + TABLE_NAME + " (CLASS_YEAR, CLASS_NAME) values(?, ?)";
        try (
                Connection conn = scopingDataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ) {
            int count = 0;
            stmt.setInt(++count,clazz.getYear());
            stmt.setString(++count,clazz.getName());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            int clazzId = rs.getInt(1);
            return true;
        } catch (SQLException e) {
            log.error("Unable to save Class details", e);
            return false;
        }
    }
}

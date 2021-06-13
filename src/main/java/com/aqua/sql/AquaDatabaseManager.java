package com.aqua.sql;

import com.aqua.sql.factory.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AquaDatabaseManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AquaDatabaseManager.class);
    private static final String INSERT_TIMESTAMP = "INSERT INTO feeding (time) VALUES (CURRENT_TIMESTAMP())";
    private static final String SELECT_TODAY_RECORDS = "SELECT COUNT(*) AS total FROM feeding WHERE DATE(time) = CURRENT_DATE";

    public void saveFeeding() throws Exception {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(INSERT_TIMESTAMP)) {
                ps.execute();
            }
        }
    }

    public int selectTotalCountOfFeeding() {
        int total = 0;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement st = con.createStatement();
             ResultSet rset = st.executeQuery(SELECT_TODAY_RECORDS)) {
            if (rset != null) {
                rset.first();
                total = rset.getInt("total");
            }
        } catch (Exception e) {
            LOGGER.warn("Failed select from feeding: {}", e);
        }
        return total;
    }
}

package com.aqua.sql.migrations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aqua.sql.factory.DatabaseFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringJoiner;

public class DatabaseMigrationManager {
    private final Logger LOGGER = LoggerFactory.getLogger(DatabaseMigrationManager.class);

    protected DatabaseMigrationManager()
    {
        init();
    }

    private void init()
    {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement ps = con.createStatement();
             ResultSet rs = ps.executeQuery("SHOW TABLES LIKE \"feeding\""))
        {
            if (!rs.next())
            {
                final StringJoiner sj = new StringJoiner(System.lineSeparator());
                sj.add("CREATE TABLE IF NOT EXISTS `feeding` (");
                sj.add("`id` int(10) unsigned NOT NULL AUTO_INCREMENT,");
                sj.add("`time` timestamp NOT NULL,");
                sj.add("PRIMARY KEY (`id`,`time`)");
                sj.add(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
                ps.execute(sj.toString());
                LOGGER.info("Feeding table created!");
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("Failed to create feeding table", e);
        }
    }

    /**
     * Gets the single instance of {@code DatabaseMigrationManager}.
     * @return single instance of {@code DatabaseMigrationManager}
     */
    public static DatabaseMigrationManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final DatabaseMigrationManager INSTANCE = new DatabaseMigrationManager();
    }
}

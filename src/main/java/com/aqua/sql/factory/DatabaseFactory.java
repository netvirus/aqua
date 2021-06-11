package com.aqua.sql.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class manages the database connections.
 */
public final class DatabaseFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseFactory.class);

    private static DatabaseFactory INSTANCE;

    private DataSource _dataSource;

    private DatabaseFactory() throws Exception {
        init(new DataSourceInitializer());
    }

    public void init(DataSourceInitializer config) throws Exception
    {
        final Driver driver = DriverManager.drivers().filter(Driver::jdbcCompliant).findFirst().orElseThrow(() -> new ExceptionInInitializerError("jdbc driver not found!"));
        _dataSource = config.initDataSource();

        /* Test the connection. */
        _dataSource.getConnection().close();
        LOGGER.info("Initialized {} DB '{}' as user '{}' using '{}' driver 'v{}.{}'.", _dataSource.getClass().getSimpleName(), driver.getClass().getName(), driver.getMajorVersion(), driver.getMinorVersion());
    }

    public static DatabaseFactory getInstance() throws Exception {
        if (INSTANCE == null)
        {
            synchronized (DatabaseFactory.class)
            {
                if (INSTANCE == null)
                {
                    INSTANCE = new DatabaseFactory();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Gets the connection.
     * @return the connection
     */
    public Connection getConnection()
    {
        try
        {
            return _dataSource.getConnection();
        }
        catch (SQLException e)
        {
            throw new LostConnectionException("Couldn't connect to the database!", e);
        }
    }

    private static final class LostConnectionException extends RuntimeException
    {
        LostConnectionException(String message, Throwable cause)
        {
            super(message, cause);
        }
    }
}

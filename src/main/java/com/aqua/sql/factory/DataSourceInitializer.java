package com.aqua.sql.factory;

import com.aqua.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataSourceInitializer
{
    DataSource initDataSource()
    {
        final HikariConfig config = new HikariConfig("config/hikari.properties");
        config.setDriverClassName(Config.DATABASE_DRIVER);
        config.setJdbcUrl(Config.DATABASE_URL);
        config.setUsername(Config.DATABASE_USER);
        config.setPassword(Config.DATABASE_PASSWORD);
        return new HikariDataSource(config);
    }
}

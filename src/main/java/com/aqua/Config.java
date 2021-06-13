package com.aqua;

import com.aqua.configuration.ExProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Config {
    public static Logger _log = LoggerFactory.getLogger(Config.class);
    /**
     * Configuration files
     */
    public static String DB_CONFIG_FILE = "config/database.properties";
    public static String TIME_CONFIG_FILE = "config/time.properties";
    /**
     * DB parameters
     */
    public static String DATABASE_DRIVER;
    public static String DATABASE_URL;
    public static String DATABASE_USER;
    public static String DATABASE_PASSWORD;
    /**
     * Lighting
     */
    public static int LIGHTING_START_HOURS;
    public static int LIGHTING_STOP_HOURS;
    public static int LIGHTING_START_MINUTES;
    /**
     * Oxygen
     */
    public static int OXYGEN_START_HOURS;
    public static int OXYGEN_STOP_HOURS;
    public static int OXYGEN_START_MINUTES;
    /**
     * Food
     */
    public static int FOOD_START_HOURS;
    public static int FOOD_STOP_HOURS;
    public static int FOOD_NUMBER_OF_FEEDINGS;

    public static void loadDatabaseConfig() {
        ExProperties dbSettings = load(DB_CONFIG_FILE);
        DATABASE_DRIVER = dbSettings.getProperty("DatabaseDriver", "org.mariadb.jdbc.Driver");
        DATABASE_URL = dbSettings.getProperty("DatabaseUrl", "jdbc:mysql://localhost/aquarium?allowPublicKeyRetrieval=false&useSSL=false&serverTimezone=Asia/Novosibirsk");
        DATABASE_USER = dbSettings.getProperty("DatabaseUser", "root");
        DATABASE_PASSWORD = dbSettings.getProperty("DatabasePassword", "root");
    }

    public static void loadTimeConfig() {
        ExProperties timeSettings = load(TIME_CONFIG_FILE);
        LIGHTING_START_HOURS = timeSettings.getProperty("LightingStartHours", 9);
        LIGHTING_STOP_HOURS = timeSettings.getProperty("LightingStopHours", 18);
        LIGHTING_START_MINUTES = timeSettings.getProperty("LightingStartMinutes", 0);
        OXYGEN_START_HOURS = timeSettings.getProperty("OxygenStartHours", 9);
        OXYGEN_STOP_HOURS = timeSettings.getProperty("OxygenStopHours", 22);
        OXYGEN_START_MINUTES = timeSettings.getProperty("OxygenStartMinutes", 0);
        FOOD_START_HOURS = timeSettings.getProperty("FeedingStartHours", 10);
        FOOD_STOP_HOURS = timeSettings.getProperty("FeedingStopHours", 22);
        FOOD_NUMBER_OF_FEEDINGS = timeSettings.getProperty("NumberOfFeeding", 1);
    }

    public static void load() {
        loadDatabaseConfig();
        loadTimeConfig();
    }

    public static ExProperties load(String filename)
    {
        return load(new File(filename));
    }

    public static ExProperties load(File file)
    {
        ExProperties result = new ExProperties();
        try
        {
            result.load(file);
        }
        catch (IOException e)
        {
            _log.error("Error loading config : " + file.getName() + "!", e);
        }
        return result;
    }
}

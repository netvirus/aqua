package com.aqua;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.aqua.sql.migrations.DatabaseMigrationManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author netvirus
 */

public class Aqua {
    private static final Logger LOGGER = LoggerFactory.getLogger(Aqua.class);

    private static boolean _light = false;

    private static boolean _oxygen = false;

    private static boolean _food = false;

    public static void main(String[] args) {
        // Загружаем параметры из конфигов
        Config.load();
        // Создаем таблицы в SQL базе, если их там нет
        DatabaseMigrationManager.getInstance();

        while (true) {
            /*
             * Проверяем необходимость включения или выключения освещения
             * */
            // Освещение уже включено и пришло время его выключить
            final boolean _lightingTimer = checkTime(Config.LIGHTING_START_HOURS, Config.LIGHTING_STOP_HOURS, Config.LIGHTING_START_MINUTES);
            if (_light && !_lightingTimer) {
                LOGGER.info("Выключаем освещение. - Споконой ночи!!!");
                // Меняем состояние флга "Освещения" на Выключено
                setLightingState(false);
                // Освещение еще не включено и пришло время его включить
            } else if (!_light && _lightingTimer) {
                LOGGER.info("Включаем освещение. - Доброе утро!!!");
                // Меняем состояние флга "Освещения" на Включено
                setLightingState(true);
                // Меняем состояние флга "Кормление" на Включено
                setFoodState(false); // TODO Нужно хранить и брать состояние из базы, вдруг это повторный запуск сегодня!
            }
            /*
             * Проверяем необходимость включения или выключения подачи кислорода
             * */
            // Подача кислорода уже идет и пришло время его выключить
            final boolean _oxygenTimer = checkTime(Config.OXYGEN_START_HOURS, Config.OXYGEN_STOP_HOURS, Config.OXYGEN_START_MINUTES);
            if (_oxygen && !_oxygenTimer) {
                LOGGER.info("Выключаем подачу кислорода. Надеюсь вы им запаслись))");
                // Меняем состояние флга "Подача кислорода" на Выключено
                setOxygenState(false);
                // Сброс состояния флга "Подача корма" на Выключено
                setFoodState(false);
            } else if (!_oxygen && _oxygenTimer) {
                LOGGER.info("Включаем подачу кислорода. Ура!!!");
                // Меняем состояние флга "Подача кислорода" на Включено
                setOxygenState(true);
            }
            /*
             * Проверяем необходимость включения кормушки
             * */
            // Если False то нужно покормить
            final boolean _foodTimer = checkTime(Config.FOOD_START_HOURS, Config.FOOD_STOP_HOURS, Config.FOOD_START_MINUTES);
            if (!_food && _foodTimer) {
                LOGGER.info("Включаем кормушку. Ом ном ном!!!");
                // Меняем состояние флга "Подача корма" на Включено. Значит покормлено.
                setFoodState(true);
            }
        }
    }

    public static boolean checkTime(int startH, int stopH, int startM) {
        int currentHours = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        int currentMinutes = Integer.parseInt(new SimpleDateFormat("mm").format(new Date()));
        if ((currentHours >= startH) && (currentHours < stopH)) {
            if (currentMinutes >= startM) {
                return true;
            }
        }
        return false;
    }

    public static void setLightingState(boolean state) {
        _light = state;
    }

    public static void setOxygenState(boolean state) {
        _oxygen = state;
    }

    public static void setFoodState(boolean state) {
        _food = state;
    }
}

package com.aqua;

import com.aqua.sql.AquaDatabaseManager;
import com.aqua.sql.migrations.DatabaseMigrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author netvirus
 */

public class Aqua {
    private static final Logger LOGGER = LoggerFactory.getLogger(Aqua.class);

    private static int _feedingFirstHour;
    private static int _feedingSecondHour;
    private static boolean _feedingFirstState = false;
    private static boolean _feedingSecondState = false;
    private static boolean _light = false;
    private static boolean _oxygen = false;
    private static boolean _food = false;
    private static boolean _backupFeeding = false;

    private static final AquaDatabaseManager aquaDatabaseManager = new AquaDatabaseManager();

    public static void main(String[] args) throws Exception {
        // Загружаем параметры из конфигов
        Config.load();
        // Создаем таблицы в SQL базе, если их там нет
        DatabaseMigrationManager.getInstance();
        // Настраиваем все значения - для правильного старта нового цикла
        setupAllParameters();

        while (true) {
            /**
             * Проверяем необходимость включения или выключения освещения
             */
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
            }
            /**
             * Проверяем необходимость включения или выключения подачи кислорода
             */
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
            /**
             * Проверяем необходимость включения кормушки
             */
            // Если False то нужно покормить
            if (!_food) {
                if ((!_feedingFirstState && checkHour(_feedingFirstHour)) || (!_feedingSecondState && checkHour(_feedingSecondHour))) {
                    LOGGER.info("Подаем сигнал на реле с кормушкой");
                    aquaDatabaseManager.saveFeeding();

                    int countOfFeedings = aquaDatabaseManager.selectTotalCountOfFeeding();
                    if (countOfFeedings == Config.FOOD_NUMBER_OF_FEEDINGS) {
                        // Если все попытки совершены то выставляем флаг в True
                        setFoodState(true);
                    } else if (countOfFeedings == 1 && Config.FOOD_NUMBER_OF_FEEDINGS > 1) {
                        // У нас больше чем одно кормление, выставляем флаг первого кормления в True
                        _feedingFirstState = true;
                    }
                }
            } else if (_backupFeeding) {
                LOGGER.info("Подаем РЕЗЕРВНЫЙ сигнал на реле с кормушкой");
                aquaDatabaseManager.saveFeeding();
                setBackupFeedingState(false);
            }
            /**
             * Сброс флага кормления - начало нового дня
             */
            if (checkHour(Config.FOOD_START_HOURS) && _food) {
                if (aquaDatabaseManager.selectTotalCountOfFeeding() == 0)
                    setupAllParameters();
            }
        }
    }

    public static int getSecondHours() {
        return Config.FOOD_START_HOURS + (int) Math.floor((Config.FOOD_STOP_HOURS - Config.FOOD_START_HOURS) / 2) + 1;
    }

    public static boolean checkHour(int hour) {
        int currentHours = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        return (currentHours == hour);
    }

    public static boolean checkTimeForFeeding() {
        int currentHours = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if ((currentHours >= Config.FOOD_START_HOURS) && (currentHours < Config.FOOD_STOP_HOURS)) {
            return true;
        }
        return false;
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

    public static void setBackupFeedingState(boolean state) {
        _backupFeeding = state;
    }

    public static void setupAllParameters() {
        // Проверяем кормили ли мы сегодня поискав в базе
        final int count = aquaDatabaseManager.selectTotalCountOfFeeding();
        // Проверяем время, вдруг система запустилась не разу не кормив и время уже не для кормления
        // Но покормить хотя бы один раз нужно!
        if (checkTimeForFeeding()) {
            if (count == 0) {
                if (Config.FOOD_NUMBER_OF_FEEDINGS == 1) {
                    _feedingFirstHour = Config.FOOD_START_HOURS;
                    _feedingSecondHour = Config.FOOD_STOP_HOURS;
                } else if (Config.FOOD_NUMBER_OF_FEEDINGS == 2) {
                    _feedingFirstHour = Config.FOOD_START_HOURS + 1;
                    _feedingSecondHour = getSecondHours();
                }
            } else if (count == 1) {
                if (Config.FOOD_NUMBER_OF_FEEDINGS == 1) {
                    // Если покормили то ставим флаг в True чтоб больше не кормить
                    setFoodState(true);
                } else if (Config.FOOD_NUMBER_OF_FEEDINGS == 2) {
                    _feedingSecondHour = getSecondHours();
                    // Один раз уже покормили
                    _feedingFirstState = true;
                }
            } else if (count == 2) {
                // Если покормили то ставим флаг в True чтоб больше не кормить
                setFoodState(true);
            }
        } else if (count == 0) {
            // За день не разу не кормлено!!!
            // Отключаем обработку базового модуля кормления
            setFoodState(true);
            // Включаем разовое резервное кормление
            setBackupFeedingState(true);
        }
    }
}

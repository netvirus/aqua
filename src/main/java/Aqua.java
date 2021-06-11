import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author netvirus
 */

public class Aqua {
    private static final Logger LOGGER = LoggerFactory.getLogger(Aqua.class);
    // Lighting
    private static final int LIGHTING_START_HOURS = 9;
    private static final int LIGHTING_STOP_HOURS = 18;
    private static final int LIGHTING_START_MINUTES = 0;
    private static boolean _light = false;
    // Oxygen
    private static final int OXYGEN_START_HOURS = 9;
    private static final int OXYGEN_STOP_HOURS = 22;
    private static final int OXYGEN_START_MINUTES = 0;
    private static boolean _oxygen = false;
    // Food
    private static final int FOOD_START_HOURS = 9;
    private static final int FOOD_STOP_HOURS = OXYGEN_STOP_HOURS;
    private static final int FOOD_START_MINUTES = 0;
    private static boolean _food = false;

    public static void main(String[] args) {
        while (true) {
            /*
             * Проверяем необходимость включения или выключения освещения
             * */
            // Освещение уже включено и пришло время его выключить
            final boolean _lightingTimer = checkTime(LIGHTING_START_HOURS, LIGHTING_STOP_HOURS, LIGHTING_START_MINUTES);
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
            final boolean _oxygenTimer = checkTime(OXYGEN_START_HOURS, OXYGEN_STOP_HOURS, OXYGEN_START_MINUTES);
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
            final boolean _foodTimer = checkTime(FOOD_START_HOURS, FOOD_STOP_HOURS, FOOD_START_MINUTES);
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

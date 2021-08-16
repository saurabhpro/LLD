/**
 *
 */
package io.parkinglot.constants;

import java.util.Map;

/**
 * @author saurabhk
 *
 */
public final class CommandInputMap {
    private static final Map<String, Integer> commandsParameterMap = Map.of(
            Constants.CREATE_PARKING_LOT, 1,
            Constants.PARK, 2,
            Constants.LEAVE, 1,
            Constants.STATUS, 0,
            Constants.REG_NUMBER_FOR_CARS_WITH_COLOR, 1,
            Constants.SLOTS_NUMBER_FOR_CARS_WITH_COLOR, 1,
            Constants.SLOTS_NUMBER_FOR_REG_NUMBER, 1);

    private CommandInputMap() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static Map<String, Integer> getCommandsParameterMap() {
        return commandsParameterMap;
    }
}

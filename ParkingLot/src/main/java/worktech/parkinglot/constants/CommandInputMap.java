/**
 *
 */
package worktech.parkinglot.constants;

import java.util.Map;

/**
 * @author saurabhk
 *
 */
public final class CommandInputMap {
    // command and expected arguments
    private static final Map<String, Integer> commandsParameterMap = Map.of(
            Constants.CREATE_PARKING_LOT, 3,
            Constants.PARK, 3,
            Constants.LEAVE, 1,
            Constants.DISPLAY, 2);

    private CommandInputMap() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static Map<String, Integer> getCommandsParameterMap() {
        return commandsParameterMap;
    }
}

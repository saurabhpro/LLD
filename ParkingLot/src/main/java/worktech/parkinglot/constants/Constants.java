/**
 *
 */
package worktech.parkinglot.constants;

/**
 * @author saurabhk
 *
 */
public final class Constants {
    public static final String CREATE_PARKING_LOT = "create_parking_lot";
    public static final String PARK = "park_vehicle";
    public static final String LEAVE = "unpark_vehicle";
    public static final String DISPLAY = "display";
    public static final String REG_NUMBER_FOR_CARS_WITH_COLOR = "registration_numbers_for_cars_with_colour";
    public static final String SLOTS_NUMBER_FOR_CARS_WITH_COLOR = "slot_numbers_for_cars_with_colour";
    public static final String SLOTS_NUMBER_FOR_REG_NUMBER = "slot_number_for_registration_number";

    public static final int NOT_AVAILABLE = -1;
    public static final int VEHICLE_ALREADY_EXIST = -2;
    public static final int NOT_FOUND = -1;

    private Constants() throws IllegalAccessException {
        throw new IllegalAccessException();
    }
}

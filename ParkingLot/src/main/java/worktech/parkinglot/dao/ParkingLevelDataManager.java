/**
 *
 */
package worktech.parkinglot.dao;

import worktech.parkinglot.model.Vehicle;

import java.util.List;

/**
 * @author saurabhk
 *
 */
public interface ParkingLevelDataManager<T extends Vehicle> {

    int parkCar(T vehicle);

    boolean leaveCar(int slotNumber);

    /**
     * Get status of parked cars at the moment
     * @return list containing parking slot id + registration number + color
     */
    List<String> getStatus();

    List<String> getRegNumberForColor(String color);

    List<Integer> getSlotNumbersFromColor(String colour);

    int getSlotNoFromRegistrationNo(String registrationNo);

    int getAvailableSlotsCount();

    void doCleanUp();
}

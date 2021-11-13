/**
 *
 */
package worktech.parkinglot.service;

import worktech.parkinglot.exception.ParkingException;
import worktech.parkinglot.model.Vehicle;

import java.util.Optional;

/**
 * @author saurabhk
 *
 */
public interface ParkingService extends AbstractService {

    /* ---- Actions ----- */
    boolean createParkingLot(String parkingLotId, int level, int capacity) throws ParkingException;

    Optional<Integer> park(int level, Vehicle vehicle) throws ParkingException;

    String unPark(int level, int slotNumber) throws ParkingException;

    String getStatus(int level) throws ParkingException;

    Optional<Integer> getAvailableSlotsCount(int level) throws ParkingException;

    void doCleanup();
}

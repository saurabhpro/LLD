/**
 *
 */
package io.parkinglot.service;

import io.parkinglot.exception.ParkingException;
import io.parkinglot.model.Vehicle;

import java.util.Optional;

/**
 * @author saurabhk
 *
 */
public interface ParkingService extends AbstractService {

    /* ---- Actions ----- */
    void createParkingLot(int level, int capacity) throws ParkingException;

    Optional<Integer> park(int level, Vehicle vehicle) throws ParkingException;

    void unPark(int level, int slotNumber) throws ParkingException;

    void getStatus(int level) throws ParkingException;

    Optional<Integer> getAvailableSlotsCount(int level) throws ParkingException;

    void getRegNumberForColor(int level, String color) throws ParkingException;

    void getSlotNumbersFromColor(int level, String colour) throws ParkingException;

    int getSlotNoFromRegistrationNo(int level, String registrationNo) throws ParkingException;

    void doCleanup();
}

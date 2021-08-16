/**
 *
 */
package io.parkinglot.service.impl;

import io.parkinglot.constants.Constants;
import io.parkinglot.dao.ParkingDataManager;
import io.parkinglot.dao.impl.MemoryParkingManager;
import io.parkinglot.exception.AlreadyParkedException;
import io.parkinglot.exception.ErrorCode;
import io.parkinglot.exception.ParkingException;
import io.parkinglot.exception.ParkingLotFullException;
import io.parkinglot.model.Vehicle;
import io.parkinglot.model.strategy.NearestFirstParkingStrategy;
import io.parkinglot.service.ParkingService;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static io.parkinglot.exception.ErrorCode.NOT_FOUND;

/**
 *
 * This class has to be made singleton and used as service to be injected in
 * RequestProcessor
 *
 * @author saurabhk
 *
 */
public class ParkingServiceImpl implements ParkingService {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ParkingDataManager<Vehicle> dataManager;

    @Override
    public boolean createParkingLot(int level, int capacity) throws ParkingException {
        if (dataManager != null) {
            throw new ParkingException(ErrorCode.PARKING_ALREADY_EXIST.getMessage());
        }

        var parkingLevels = List.of(level);
        var capacityList = List.of(capacity);

        this.dataManager = MemoryParkingManager.getInstance(parkingLevels, capacityList, new NearestFirstParkingStrategy());
        System.out.println("Created parking lot with " + capacity + " slots");

        return true;
    }

    @Override
    public Optional<Integer> park(int level, Vehicle vehicle) throws ParkingException {
        Optional<Integer> value;
        lock.writeLock().lock();

        try {
            validateParkingLot();

            value = Optional.of(dataManager.parkCar(level, vehicle));
            switch (value.get()) {
                case Constants.NOT_AVAILABLE -> throw new ParkingLotFullException("Sorry, parking lot is full");
                case Constants.VEHICLE_ALREADY_EXIST -> throw new AlreadyParkedException("Sorry, vehicle is already parked.");
                default -> System.out.println("Allocated slot number: " + value.get());
            }
        } catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        } finally {
            lock.writeLock().unlock();
        }

        return value;
    }

    private void validateParkingLot() throws ParkingException {
        if (dataManager == null) {
            throw new ParkingException(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage());
        }
    }

    @Override
    public String unPark(int level, int slotNumber) throws ParkingException {
        lock.writeLock().lock();
        try {
            validateParkingLot();

            if (dataManager.leaveCar(level, slotNumber))
                return "Slot number " + slotNumber + " is free";
            else
                return "Slot number is Empty Already.";
        } catch (Exception e) {
            throw new ParkingException(ErrorCode.INVALID_VALUE.getMessage().replace("{variable}", "slot_number"), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public String getStatus(int level) throws ParkingException {
        lock.readLock().lock();

        try {
            validateParkingLot();

            String result;
            List<String> statusList = dataManager.getStatus(level);

            if (statusList.isEmpty())
                result = """
                        Sorry, parking lot is empty.
                        """;
            else {
                result = "Slot No.\tRegistration No.\tColor\n" + String.join("\n", statusList);
            }

            return result;
        } catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<Integer> getAvailableSlotsCount(int level) throws ParkingException {
        lock.readLock().lock();
        Optional<Integer> value;
        try {
            validateParkingLot();

            value = Optional.of(dataManager.getAvailableSlotsCount(level));
        } catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        } finally {
            lock.readLock().unlock();
        }

        return value;
    }

    @Override
    public String getRegNumberForColor(int level, String color) throws ParkingException {
        lock.readLock().lock();
        try {
            validateParkingLot();

            List<String> registrationList = dataManager.getRegNumberForColor(level, color);
            if (registrationList.isEmpty())
                return NOT_FOUND.getMessage();
            else
                return String.join(",", registrationList);
        } catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String getSlotNumbersFromColor(int level, String color) throws ParkingException {
        lock.readLock().lock();
        try {
            validateParkingLot();

            List<Integer> slotList = dataManager.getSlotNumbersFromColor(level, color);
            if (slotList.isEmpty())
                return NOT_FOUND.getMessage();

            StringJoiner joiner = new StringJoiner(",");
            slotList.stream().map(slot -> slot + "").forEach(joiner::add);

            return joiner.toString();
        } catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int getSlotNoFromRegistrationNo(int level, String registrationNo) throws ParkingException {
        int value;
        lock.readLock().lock();
        try {
            validateParkingLot();

            value = dataManager.getSlotNoFromRegistrationNo(level, registrationNo);
            System.out.println(value != -1 ? value : NOT_FOUND);

        } catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        } finally {
            lock.readLock().unlock();
        }

        return value;
    }

    @Override
    public void doCleanup() {
        if (dataManager != null)
            dataManager.doCleanup();
    }
}

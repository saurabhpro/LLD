/**
 *
 */
package io.parkinglot.service.impl;

import io.parkinglot.constants.Constants;
import io.parkinglot.dao.ParkingDataManager;
import io.parkinglot.dao.impl.MemoryParkingManager;
import io.parkinglot.exception.ErrorCode;
import io.parkinglot.exception.ParkingException;
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
    public void createParkingLot(int level, int capacity) throws ParkingException {
        if (dataManager != null) {
            throw new ParkingException(ErrorCode.PARKING_ALREADY_EXIST.getMessage());
        }

        var parkingLevels = List.of(level);
        var capacityList = List.of(capacity);

        this.dataManager = MemoryParkingManager.getInstance(parkingLevels, capacityList, new NearestFirstParkingStrategy());
        System.out.println("Created parking lot with " + capacity + " slots");
    }

    @Override
    public Optional<Integer> park(int level, Vehicle vehicle) throws ParkingException {
        Optional<Integer> value;
        lock.writeLock().lock();

        try {
            validateParkingLot();

            value = Optional.of(dataManager.parkCar(level, vehicle));
            switch (value.get()) {
                case Constants.NOT_AVAILABLE -> System.out.println("Sorry, parking lot is full");
                case Constants.VEHICLE_ALREADY_EXIST -> System.out.println("Sorry, vehicle is already parked.");
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
    public void unPark(int level, int slotNumber) throws ParkingException {
        lock.writeLock().lock();
        try {
            validateParkingLot();

            if (dataManager.leaveCar(level, slotNumber))
                System.out.println("Slot number " + slotNumber + " is free");
            else
                System.out.println("Slot number is Empty Already.");
        } catch (Exception e) {
            throw new ParkingException(ErrorCode.INVALID_VALUE.getMessage().replace("{variable}", "slot_number"), e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void getStatus(int level) throws ParkingException {
        lock.readLock().lock();

        try {
            validateParkingLot();

            System.out.println("Slot No.\tRegistration No.\tColor");
            List<String> statusList = dataManager.getStatus(level);

            if (statusList.isEmpty())
                System.out.println("Sorry, parking lot is empty.");
            else {
                statusList.forEach(System.out::println);
            }
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
    public void getRegNumberForColor(int level, String color) throws ParkingException {
        lock.readLock().lock();
        try {
            validateParkingLot();

            List<String> registrationList = dataManager.getRegNumberForColor(level, color);
            if (registrationList.isEmpty())
                System.out.println(NOT_FOUND);
            else
                System.out.println(String.join(",", registrationList));
        } catch (Exception e) {
            throw new ParkingException(ErrorCode.PROCESSING_ERROR.getMessage(), e);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void getSlotNumbersFromColor(int level, String color) throws ParkingException {
        lock.readLock().lock();
        try {
            validateParkingLot();

            List<Integer> slotList = dataManager.getSlotNumbersFromColor(level, color);
            if (slotList.isEmpty())
                System.out.println(NOT_FOUND);

            StringJoiner joiner = new StringJoiner(",");
            slotList.stream().map(slot -> slot + "").forEach(joiner::add);

            System.out.println(joiner);

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

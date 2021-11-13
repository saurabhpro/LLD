/**
 *
 */
package worktech.parkinglot.service.impl;

import worktech.parkinglot.constants.Constants;
import worktech.parkinglot.dao.ParkingDataManager;
import worktech.parkinglot.dao.impl.MemoryParkingManager;
import worktech.parkinglot.exception.AlreadyParkedException;
import worktech.parkinglot.exception.ErrorCode;
import worktech.parkinglot.exception.ParkingException;
import worktech.parkinglot.exception.ParkingLotFullException;
import worktech.parkinglot.model.Vehicle;
import worktech.parkinglot.model.strategy.NearestFirstParkingStrategy;
import worktech.parkinglot.service.ParkingService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    public boolean createParkingLot(String parkingLotId, int level, int capacity) throws ParkingException {
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
    public void doCleanup() {
        if (dataManager != null)
            dataManager.doCleanup();
    }
}

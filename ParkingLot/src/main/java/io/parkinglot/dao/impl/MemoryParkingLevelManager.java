package io.parkinglot.dao.impl;

import io.parkinglot.constants.Constants;
import io.parkinglot.dao.ParkingLevelDataManager;
import io.parkinglot.model.Vehicle;
import io.parkinglot.model.strategy.NearestFirstParkingStrategy;
import io.parkinglot.model.strategy.ParkingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is a singleton class to manage the data of parking system
 *
 * @param <T>
 */
public class MemoryParkingLevelManager<T extends Vehicle> implements ParkingLevelDataManager<T> {
    private static MemoryParkingLevelManager instance;

    // For Multilevel Parking lot - 0 -> Ground floor 1 -> First Floor etc
    private AtomicInteger level = new AtomicInteger(0);

    private AtomicInteger capacity = new AtomicInteger();
    private AtomicInteger availability = new AtomicInteger();

    // Allocation Strategy for parking
    private ParkingStrategy parkingStrategy;

    // this is per level - slot - vehicle
    private Map<Integer, Optional<T>> slotVehicleMap;

    private MemoryParkingLevelManager(int level, int capacity, ParkingStrategy parkingStrategy) {
        this.level.set(level);
        this.capacity.set(capacity);
        this.availability.set(capacity);

        if (parkingStrategy == null) {
            parkingStrategy = new NearestFirstParkingStrategy();
        }
        this.parkingStrategy = parkingStrategy;

        slotVehicleMap = new ConcurrentHashMap<>();

        for (int parkingSlotId = 1; parkingSlotId <= capacity; parkingSlotId++) {
            slotVehicleMap.put(parkingSlotId, Optional.empty());
            parkingStrategy.addParkingSlot(parkingSlotId); // adding free slots to parking strategy
        }
    }

    public static synchronized <T extends Vehicle> MemoryParkingLevelManager<T> getInstance(int level,
                                                                                            int capacity,
                                                                                            ParkingStrategy parkingStrategy) {
        if (instance == null) {
            instance = new MemoryParkingLevelManager<T>(level, capacity, parkingStrategy);
        }

        return instance;
    }

    @Override
    public int parkCar(T vehicle) {
        int availableSlot;
        if (availability.get() == 0) {
            return Constants.NOT_AVAILABLE;
        } else {
            if (slotVehicleMap.containsValue(Optional.of(vehicle))) {
                return Constants.VEHICLE_ALREADY_EXIST;
            }

            availableSlot = parkingStrategy.assignSlot();

            slotVehicleMap.put(availableSlot, Optional.of(vehicle));
            availability.decrementAndGet();

            parkingStrategy.removeSlot(availableSlot);
        }

        return availableSlot;
    }

    @Override
    public boolean leaveCar(int slotNumber) {
        if (slotVehicleMap.get(slotNumber).isEmpty()) // Slot already empty
        {
            return false;
        }
        availability.incrementAndGet();

        // add the freed slot back
        parkingStrategy.addParkingSlot(slotNumber);

        // clear the slot state
        slotVehicleMap.put(slotNumber, Optional.empty());

        return true;
    }

    @Override
    public List<String> getStatus() {
        List<String> statusList = new ArrayList<>();

        IntStream.rangeClosed(1, capacity.get())
                .forEach(parkingSlotId -> {
                    Optional<T> vehicle = slotVehicleMap.get(parkingSlotId);
                    vehicle.ifPresent(t -> statusList.add(parkingSlotId + "\t\t" + t.getRegistrationNo() + "\t\t" + t.getColor()));
                });

        return statusList;
    }

    public int getAvailableSlotsCount() {
        return availability.get();
    }

    @Override
    public List<String> getRegNumberForColor(String color) {

        return IntStream.rangeClosed(1, capacity.get())
                .mapToObj(i -> slotVehicleMap.get(i))
                .filter(vehicle -> vehicle.isPresent() && color.equalsIgnoreCase(vehicle.get().getColor()))
                .map(vehicle -> vehicle.get().getRegistrationNo())
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getSlotNumbersFromColor(String colour) {
        List<Integer> slotList = new ArrayList<>();

        IntStream.rangeClosed(1, capacity.get())
                .forEach(i -> {
                    Optional<T> vehicle = slotVehicleMap.get(i);
                    if (vehicle.isPresent() && colour.equalsIgnoreCase(vehicle.get().getColor())) {
                        slotList.add(i);
                    }
                });

        return slotList;
    }

    @Override
    public int getSlotNoFromRegistrationNo(String registrationNo) {
        int result = Constants.NOT_FOUND;

        for (int i = 1; i <= capacity.get(); i++) {
            Optional<T> vehicle = slotVehicleMap.get(i);
            if (vehicle.isPresent() && registrationNo.equalsIgnoreCase(vehicle.get().getRegistrationNo())) {
                result = i;
                break;
            }
        }

        return result;
    }

    @Override
    public void doCleanUp() {
        this.level = new AtomicInteger();
        this.capacity = new AtomicInteger();
        this.availability = new AtomicInteger();
        this.parkingStrategy = null;
        slotVehicleMap = null;
        instance = null;
    }
}

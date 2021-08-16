/**
 *
 */
package io.parkinglot.dao.impl;

import com.google.inject.Singleton;
import io.parkinglot.dao.ParkingDataManager;
import io.parkinglot.dao.ParkingLevelDataManager;
import io.parkinglot.model.Vehicle;
import io.parkinglot.model.strategy.ParkingStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a singleton class to manage the data of parking system
 *
 * @author saurabhk
 * @param <T>
 */
@Singleton
public final class MemoryParkingManager<T extends Vehicle> implements ParkingDataManager<T> {
    private static MemoryParkingManager instance;
    private Map<Integer, ParkingLevelDataManager<T>> levelParkingMap;

    private MemoryParkingManager(List<Integer> parkingLevels,
                                 List<Integer> capacityList,
                                 ParkingStrategy parkingStrategy) {

        levelParkingMap = new HashMap<>();

        for (int i = 0; i < parkingLevels.size(); i++) {
            levelParkingMap.put(
                    parkingLevels.get(i),
                    MemoryParkingLevelManager.getInstance(parkingLevels.get(i), capacityList.get(i), parkingStrategy)
            );

        }
    }

    public static synchronized <T extends Vehicle> MemoryParkingManager<T> getInstance(List<Integer> parkingLevels,
                                                                                       List<Integer> capacityList,
                                                                                       ParkingStrategy parkingStrategies) {
        // Make sure the of the lists are of equal size
        if (instance == null) {
            instance = new MemoryParkingManager<T>(parkingLevels, capacityList, parkingStrategies);
        }
        return instance;
    }

    @Override
    public int parkCar(int level, T vehicle) {
        return levelParkingMap.get(level).parkCar(vehicle);
    }

    @Override
    public boolean leaveCar(int level, int slotNumber) {
        return levelParkingMap.get(level).leaveCar(slotNumber);
    }

    @Override
    public List<String> getStatus(int level) {
        return levelParkingMap.get(level).getStatus();
    }

    public int getAvailableSlotsCount(int level) {
        return levelParkingMap.get(level).getAvailableSlotsCount();
    }

    @Override
    public List<String> getRegNumberForColor(int level, String color) {
        return levelParkingMap.get(level).getRegNumberForColor(color);
    }

    @Override
    public List<Integer> getSlotNumbersFromColor(int level, String color) {
        return levelParkingMap.get(level).getSlotNumbersFromColor(color);
    }

    @Override
    public int getSlotNoFromRegistrationNo(int level, String registrationNo) {
        return levelParkingMap.get(level).getSlotNoFromRegistrationNo(registrationNo);
    }

    @Override
    public void doCleanup() {
        for (ParkingLevelDataManager<T> levelDataManager : levelParkingMap.values()) {
            levelDataManager.doCleanUp();
        }
        levelParkingMap = null;
        instance = null;
    }
}

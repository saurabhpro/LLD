/**
 *
 */
package io.parkinglot.model.strategy;

import java.util.TreeSet;

/**
 * @author saurabhk
 *
 */
public class NearestFirstParkingStrategy implements ParkingStrategy {
    private final TreeSet<Integer> freeSlots;

    public NearestFirstParkingStrategy() {
        freeSlots = new TreeSet<>();
    }

    @Override
    public void addParkingSlot(int id) {
        freeSlots.add(id);
    }

    @Override
    public int assignSlot() {
        return freeSlots.first();
    }

    @Override
    public void removeSlot(int availableSlot) {
        freeSlots.remove(availableSlot);
    }
}

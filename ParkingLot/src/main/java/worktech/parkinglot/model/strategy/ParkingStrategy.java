/**
 *
 */
package worktech.parkinglot.model.strategy;

/**
 * @author saurabhk
 *
 */
public interface ParkingStrategy {
    void addParkingSlot(int id);

    int assignSlot();

    void removeSlot(int slot);
}

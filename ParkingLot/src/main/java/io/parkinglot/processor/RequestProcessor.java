/**
 *
 */
package io.parkinglot.processor;

import io.parkinglot.constants.Constants;
import io.parkinglot.exception.ErrorCode;
import io.parkinglot.exception.ParkingException;
import io.parkinglot.model.Car;
import io.parkinglot.service.ParkingService;

import javax.inject.Inject;

/**
 *
 * @author saurabhk
 */
public class RequestProcessor implements AbstractProcessor {
    private final ParkingService parkingService;

    @Inject
    public RequestProcessor(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @Override
    public void execute(String input) throws ParkingException {
        int level = 1;
        String[] inputs = input.split(" ");

        String key = inputs[0];

        switch (key) {
            case Constants.CREATE_PARKING_LOT:
                try {
                    int capacity = Integer.parseInt(inputs[1]);
                    parkingService.createParkingLot(level, capacity);
                } catch (NumberFormatException e) {
                    throw new ParkingException(ErrorCode.INVALID_VALUE.getMessage().replace("{variable}", "capacity"));
                }
                break;
            case Constants.PARK:
                parkingService.park(level, new Car(inputs[1], inputs[2]));
                break;
            case Constants.LEAVE:
                try {
                    int slotNumber = Integer.parseInt(inputs[1]);
                    parkingService.unPark(level, slotNumber);
                } catch (NumberFormatException e) {
                    throw new ParkingException(
                            ErrorCode.INVALID_VALUE.getMessage().replace("{variable}", "slot_number"));
                }
                break;
            case Constants.STATUS:
                parkingService.getStatus(level);
                break;
            case Constants.REG_NUMBER_FOR_CARS_WITH_COLOR:
                parkingService.getRegNumberForColor(level, inputs[1]);
                break;
            case Constants.SLOTS_NUMBER_FOR_CARS_WITH_COLOR:
                parkingService.getSlotNumbersFromColor(level, inputs[1]);
                break;
            case Constants.SLOTS_NUMBER_FOR_REG_NUMBER:
                parkingService.getSlotNoFromRegistrationNo(level, inputs[1]);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + key);
        }
    }
}

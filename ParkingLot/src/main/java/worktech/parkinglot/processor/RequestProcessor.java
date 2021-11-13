/**
 *
 */
package worktech.parkinglot.processor;

import worktech.parkinglot.constants.Constants;
import worktech.parkinglot.exception.ErrorCode;
import worktech.parkinglot.exception.ParkingException;
import worktech.parkinglot.model.Bike;
import worktech.parkinglot.model.Car;
import worktech.parkinglot.model.Truck;
import worktech.parkinglot.model.VehicleType;
import worktech.parkinglot.service.ParkingService;

/**
 * @author saurabhk
 */
public record RequestProcessor(ParkingService parkingService) implements AbstractProcessor {


    @Override
    public void execute(String input) throws ParkingException {
        String[] inputs = input.split(" ");

        String key = inputs[0];

        switch (key) {
            case Constants.CREATE_PARKING_LOT:
                createParkingLot(inputs);
                break;
            case Constants.PARK:
                parkVehicle(parkingService.l, inputs);
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
            case Constants.DISPLAY:
                parkingService.getStatus(level);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + key);
        }
    }

    private void createParkingLot(String[] inputs) throws ParkingException {
        try {
            String parkingLotId = inputs[1];
            int levels = Integer.parseInt(inputs[2]);
            int capacity = Integer.parseInt(inputs[3]);

            parkingService.createParkingLot(parkingLotId, levels, capacity);
        } catch (NumberFormatException e) {
            throw new ParkingException(ErrorCode.INVALID_VALUE.getMessage().replace("{variable}", "capacity"));
        }
    }

    private void parkVehicle(int level, String[] inputs) throws ParkingException {
        final VehicleType vehicleType = VehicleType.valueOf(inputs[1]);
        switch (vehicleType) {
            case CAR -> parkingService.park(level, new Car(inputs[2], inputs[3]));
            case BIKE -> parkingService.park(level, new Bike(inputs[2], inputs[3]));
            case TRUCK -> parkingService.park(level, new Truck(inputs[2], inputs[3]));
        }
    }
}

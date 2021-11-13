package worktech.parkinglot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import worktech.parkinglot.exception.ErrorCode;
import worktech.parkinglot.exception.ParkingException;
import worktech.parkinglot.guice.BasicModule;
import worktech.parkinglot.processor.AbstractProcessor;
import worktech.parkinglot.processor.RequestProcessor;
import worktech.parkinglot.service.ParkingService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws ParkingException {
        Injector injector = Guice.createInjector(new BasicModule());
        ParkingService parkingService = injector.getInstance(ParkingService.class);

        AbstractProcessor processor = new RequestProcessor(parkingService);

        System.out.println("\n\n\n\n\n");
        System.out.println("===================================================================");
        System.out.println("===================      NEW PARKING LOT     ====================");
        System.out.println("===================================================================");
        printUsage();
        withFileArgument("ParkingLot/src/main/resources/parking_lot_file_inputs.txt", processor);
    }

    static void withFileArgument(String filePath, AbstractProcessor processor) throws ParkingException {
        String input;
        Path path = Paths.get(filePath);
        System.out.println(path.toAbsolutePath());
        File inputFile = new File(filePath);

        try (BufferedReader bufferReader = new BufferedReader(new FileReader(inputFile))) {
            int lineNo = 1;
            while ((input = bufferReader.readLine()) != null) {
                input = input.trim();
                if (processor.validate(input)) {
                    try {
                        processor.execute(input);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("Incorrect Command Found at line: " + lineNo + " ,Input: " + input);
                }
                lineNo++;
            }
        } catch (Exception e) {
            throw new ParkingException(ErrorCode.INVALID_FILE.getMessage(), e);
        }
    }

    private static void printUsage() {

        var buffer = """
                --------------Please Enter one of the below commands. {variable} to be replaced -----------------------
                A) For creating parking lot of size n               ---> create_parking_lot {capacity}
                B) To park a car                                    ---> park <<car_number>> {car_clour}
                C) Remove(Unpark) car from parking                  ---> leave {slot_number}
                D) Print status of parking slot                     ---> status
                E) Get cars registration no for the given car color ---> registration_numbers_for_cars_with_color {car_color}
                F) Get slot numbers for the given car color         ---> slot_numbers_for_cars_with_color {car_color}
                G) Get slot number for the given car number         ---> slot_number_for_registration_number {car_number}
                """;
        System.out.println(buffer);
    }
}

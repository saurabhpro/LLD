package io.parkinglot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.parkinglot.exception.ErrorCode;
import io.parkinglot.exception.ParkingException;
import io.parkinglot.guice.BasicModule;
import io.parkinglot.processor.AbstractProcessor;
import io.parkinglot.processor.RequestProcessor;
import io.parkinglot.service.ParkingService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world!
 */
public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BasicModule());
        ParkingService parkingService = injector.getInstance(ParkingService.class);

        AbstractProcessor processor = new RequestProcessor(parkingService);

        String input;
        try {
            System.out.println("\n\n\n\n\n");
            System.out.println("===================================================================");
            System.out.println("===================      NEW PARKING LOT     ====================");
            System.out.println("===================================================================");
            printUsage();
            switch (args.length) {
                case 0 -> // Interactive: command-line input/output
                        {
                            System.out.println("Please Enter 'exit' to end Execution");
                            System.out.println("Input:");
                            while (true) {
                                try (BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in))) {
                                    input = bufferReader.readLine().trim();
                                    if (input.equalsIgnoreCase("exit")) {
                                        break;
                                    } else {
                                        if (processor.validate(input)) {
                                            try {
                                                processor.execute(input.trim());
                                            } catch (Exception e) {
                                                System.out.println(e.getMessage());
                                            }
                                        } else {
                                            printUsage();
                                        }
                                    }
                                } catch (Exception e) {
                                    throw new ParkingException(ErrorCode.INVALID_REQUEST.getMessage(), e);
                                }
                            }
                        }
                case 1 ->// File input/output
                        withFileArgument(args[0], processor);
                default -> System.out.println("Invalid input. Usage Style: java -jar <jar_file_path> <input_file_path>");
            }
        } catch (ParkingException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
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

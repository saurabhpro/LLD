package io.parkinglot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.parkinglot.exception.ErrorCode;
import io.parkinglot.exception.ParkingException;
import io.parkinglot.guice.BasicModule;
import io.parkinglot.model.Car;
import io.parkinglot.processor.AbstractProcessor;
import io.parkinglot.processor.RequestProcessor;
import io.parkinglot.service.ParkingService;
import io.parkinglot.service.impl.ParkingServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 * https://medium.com/@vaibhav0109/design-problem-parking-lot-2617785a8ef7
 */
public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final int parkingLevel = 1;
    private static final PrintStream standardOut = System.out;

    @BeforeEach
    public void init() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterAll
    public static void cleanUp() {
        System.setOut(standardOut);
    }

    @Test
    public void createParkingLot() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        instance.createParkingLot(parkingLevel, 65);
        assertTrue("createdparkinglotwith65slots".equalsIgnoreCase(outContent.toString().trim().replace(" ", "")));
        instance.doCleanup();
    }

    @Test
    public void alreadyExistParkingLot() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        // created already
        instance.createParkingLot(parkingLevel, 65);
        assertTrue("createdparkinglotwith65slots".equalsIgnoreCase(outContent.toString().trim().replace(" ", "")));

        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.createParkingLot(parkingLevel, 65));
        assertEquals(ErrorCode.PARKING_ALREADY_EXIST.getMessage(), parkingException.getMessage());

        instance.doCleanup();
    }

    @Test
    public void testParkingCapacity() throws Exception {
        ParkingService instance = new ParkingServiceImpl();

        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 11);
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        instance.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));

        assertEquals(8, instance.getAvailableSlotsCount(parkingLevel).orElse(0));

        instance.doCleanup();
    }

    @Test
    public void testEmptyParkingLot() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.getStatus(parkingLevel));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 6);
        instance.getStatus(parkingLevel);
        assertEquals("""
                        Created parking lot with 6 slots
                        Slot No.	Registration No.	Color
                        Sorry, parking lot is empty.
                        """
                , outContent.toString());
        instance.doCleanup();
    }

    @Test
    public void testParkingLotIsFull() throws Exception {
        ParkingService instance = new ParkingServiceImpl();

        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 2);
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        instance.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));
        assertEquals("""
                        Created parking lot with 2 slots
                        Allocated slot number: 1
                        Allocated slot number: 2
                        Sorry, parking lot is full"""
                , outContent.toString().trim());
        instance.doCleanup();
    }

    @Test
    public void testNearestSlotAllotment() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 5);
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
        instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-9999");

        assertTrue("createdparkinglotwith5slots\nAllocatedslotnumber:1\nAllocatedslotnumber:2\n1\n2"
                .equalsIgnoreCase(outContent.toString().trim().replace(" ", "")));

        instance.doCleanup();
    }

    @Test
    public void leave() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.unPark(parkingLevel, 2));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 6);
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        instance.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));
        instance.unPark(parkingLevel, 4);
        assertEquals("""
                Created parking lot with 6 slots
                Allocated slot number: 1
                Allocated slot number: 2
                Allocated slot number: 3
                Slot number is Empty Already.
                """, outContent.toString());
        instance.doCleanup();
    }

    @Test
    public void testWhenVehicleAlreadyPresent() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 3);
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals("""
                        Created parking lot with 3 slots
                        Allocated slot number: 1
                        Sorry, vehicle is already parked.
                        """
                , outContent.toString());
        instance.doCleanup();
    }

    @Test
    public void testWhenVehicleAlreadyPicked() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 99);
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        instance.unPark(parkingLevel, 1);
        instance.unPark(parkingLevel, 1);
        assertEquals("""
                        Created parking lot with 99 slots
                        Allocated slot number: 1
                        Allocated slot number: 2
                        Slot number 1 is free
                        Slot number is Empty Already.
                        """,
                outContent.toString());
        instance.doCleanup();
    }

    @Test
    public void testStatus() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.getStatus(parkingLevel));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 8);
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        instance.getStatus(parkingLevel);
        assertEquals(
                """
                        Created parking lot with 8 slots
                        Allocated slot number: 1
                        Allocated slot number: 2
                        Slot No.	Registration No.	Color
                        1		KA-01-HH-1234		White
                        2		KA-01-HH-9999		White
                        """, outContent.toString());
        instance.doCleanup();

    }

    @Test
    public void testGetSlotsByRegNo() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234"));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 10);
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
        assertEquals("""
                        Created parking lot with 10 slots
                        Allocated slot number: 1
                        Allocated slot number: 2
                        1
                        """,
                outContent.toString());
        instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1235");
        assertEquals("""
                        Created parking lot with 10 slots
                        Allocated slot number: 1
                        Allocated slot number: 2
                        1
                        Not Found
                        """,
                outContent.toString());
        instance.doCleanup();
    }

    @Test
    public void testGetSlotsByColor() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.getRegNumberForColor(parkingLevel, "white"));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        instance.createParkingLot(parkingLevel, 7);
        instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        instance.getStatus(parkingLevel);
        instance.getRegNumberForColor(parkingLevel, "Cyan");
        assertEquals(
                """
                        Created parking lot with 7 slots
                        Allocated slot number: 1
                        Allocated slot number: 2
                        Slot No.\tRegistration No.\tColor
                        1\t\tKA-01-HH-1234\t\tWhite
                        2\t\tKA-01-HH-9999\t\tWhite
                        Not Found
                        """,
                outContent.toString());
        instance.getRegNumberForColor(parkingLevel, "Red");
        assertEquals(
                """
                        Created parking lot with 7 slots
                        Allocated slot number: 1
                        Allocated slot number: 2
                        Slot No.\tRegistration No.\tColor
                        1\t\tKA-01-HH-1234\t\tWhite
                        2\t\tKA-01-HH-9999\t\tWhite
                        Not Found
                        Not Found
                        """,
                outContent.toString());
        instance.doCleanup();
    }
}
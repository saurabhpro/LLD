package io.parkinglot;

import io.parkinglot.constants.Constants;
import io.parkinglot.exception.ErrorCode;
import io.parkinglot.exception.ParkingException;
import io.parkinglot.model.Car;
import io.parkinglot.service.ParkingService;
import io.parkinglot.service.impl.ParkingServiceImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for simple App.
 * https://medium.com/@vaibhav0109/design-problem-parking-lot-2617785a8ef7
 */
public class MainTest {
    //private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final int parkingLevel = 1;
    //private static final PrintStream standardOut = System.out;

    @BeforeEach
    public void init() {
        //System.setOut(new PrintStream(outContent));
    }

    @AfterAll
    public static void cleanUp() {
        //System.setOut(standardOut);
    }

    @Test
    public void createParkingLot() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final boolean parkingLot = instance.createParkingLot(parkingLevel, 65);
        assertTrue(parkingLot);

        instance.doCleanup();
    }

    @Test
    public void alreadyExistParkingLot() throws Exception {
        ParkingService instance = new ParkingServiceImpl();

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 65);
        assertTrue(parkingLot);

        // created already
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

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 6);
        assertTrue(parkingLot);

        final String status = instance.getStatus(parkingLevel);
        assertEquals("Sorry, parking lot is empty.\n", status);

        instance.doCleanup();
    }

    @Test
    public void testParkingLotIsFull() throws Exception {
        ParkingService instance = new ParkingServiceImpl();

        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 2);
        assertTrue(parkingLot);

        final Optional<Integer> a = instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals(Optional.of(1), a);
        final Optional<Integer> b = instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        assertEquals(Optional.of(2), b);

        final ParkingException exception = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-BB-0001", "Black")));
        assertEquals("Sorry, parking lot is full", exception.getCause().getMessage());

        instance.doCleanup();
    }

    @Test
    public void testNearestSlotAllotment() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 5);
        assertTrue(parkingLot);

        final Optional<Integer> a = instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals(Optional.of(1), a);
        final Optional<Integer> b = instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        assertEquals(Optional.of(2), b);

        final int s1 = instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
        assertEquals(1, s1);
        final int s2 = instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-9999");
        assertEquals(2, s2);

        instance.doCleanup();
    }

    @Test
    public void leave() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.unPark(parkingLevel, 2));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 6);
        assertTrue(parkingLot);

        final Optional<Integer> a = instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals(Optional.of(1), a);
        final Optional<Integer> b = instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        assertEquals(Optional.of(2), b);
        final Optional<Integer> c = instance.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));
        assertEquals(Optional.of(3), c);

        final String unPark = instance.unPark(parkingLevel, 4);
        assertEquals("Slot number is Empty Already.", unPark);

        instance.doCleanup();
    }

    @Test
    public void testWhenVehicleAlreadyPresent() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 3);
        assertTrue(parkingLot);

        final Optional<Integer> a = instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals(Optional.of(1), a);

        final ParkingException exception = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));
        assertEquals("Sorry, vehicle is already parked.", exception.getCause().getMessage());

        instance.doCleanup();
    }

    @Test
    public void testWhenVehicleAlreadyPicked() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.park(parkingLevel, new Car("KA-01-HH-1234", "White")));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 99);
        assertTrue(parkingLot);

        final Optional<Integer> a = instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals(Optional.of(1), a);
        final Optional<Integer> b = instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        assertEquals(Optional.of(2), b);

        final String s1 = instance.unPark(parkingLevel, 1);
        assertEquals("Slot number 1 is free", s1);
        final String s2 = instance.unPark(parkingLevel, 1);
        assertEquals("Slot number is Empty Already.", s2);

        instance.doCleanup();
    }

    @Test
    public void testStatus() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.getStatus(parkingLevel));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 8);
        assertTrue(parkingLot);

        final Optional<Integer> a = instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals(Optional.of(1), a);
        final Optional<Integer> b = instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        assertEquals(Optional.of(2), b);

        final String status = instance.getStatus(parkingLevel);
        assertEquals("""
                Slot No.	Registration No.	Color
                1		KA-01-HH-1234		White
                2		KA-01-HH-9999		White""", status);
        instance.doCleanup();

    }

    @Test
    public void testGetSlotsByRegNo() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234"));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 10);
        assertTrue(parkingLot);

        final Optional<Integer> a = instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals(Optional.of(1), a);
        final Optional<Integer> b = instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        assertEquals(Optional.of(2), b);

        final int s1 = instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
        assertEquals(1, s1);

        final int s2 = instance.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1235");
        assertEquals(Constants.NOT_FOUND, s2);

        instance.doCleanup();
    }

    @Test
    public void testGetSlotsByColor() throws Exception {
        ParkingService instance = new ParkingServiceImpl();
        final var parkingException = assertThrows(ParkingException.class,
                () -> instance.getRegNumberForColor(parkingLevel, "white"));

        assertEquals(ErrorCode.PARKING_NOT_EXIST_ERROR.getMessage(), parkingException.getCause().getMessage());

        final boolean parkingLot = instance.createParkingLot(parkingLevel, 7);
        assertTrue(parkingLot);

        final Optional<Integer> a = instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
        assertEquals(Optional.of(1), a);
        final Optional<Integer> b = instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
        assertEquals(Optional.of(2), b);

        final String status = instance.getStatus(parkingLevel);
        assertEquals(
                """
                        Slot No.\tRegistration No.\tColor
                        1\t\tKA-01-HH-1234\t\tWhite
                        2\t\tKA-01-HH-9999\t\tWhite""",
                status);
        final String s1 = instance.getRegNumberForColor(parkingLevel, "Cyan");
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), s1);
        final String s2 = instance.getRegNumberForColor(parkingLevel, "Red");
        assertEquals(ErrorCode.NOT_FOUND.getMessage(), s2);

        instance.doCleanup();
    }
}
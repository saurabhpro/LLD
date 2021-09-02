package elevator;

public class ProcessJobWorker implements Runnable {

    private final Elevator elevator;

    ProcessJobWorker(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public void run() {
        /**
         * start the elevator
         */
        System.out.println("Starting the Elevator");
        elevator.startElevator();
    }

}

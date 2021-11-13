package elevator;

import elevator.models.Direction;
import elevator.models.Request;
import elevator.models.State;

import java.util.TreeSet;

import static elevator.ThreadUtils.sleep;

public class Elevator implements IElevator {
    /**
     * up jobs which cannot be processed now so put in pending queue
     */
    private final TreeSet<Request> upPendingJobs = new TreeSet<>();
    /**
     * down jobs which cannot be processed now so put in pending queue
     */
    private final TreeSet<Request> downPendingJobs = new TreeSet<>();
    private int currentFloor = 0;
    private Direction currentDirection = Direction.UP;
    private State currentState = State.IDLE;
    /**
     * jobs which are being processed
     */
    private TreeSet<Request> currentJobs = new TreeSet<>();

    @Override
    @SuppressWarnings("java:S2189")
    public void startElevator() {
        while (true) {

            if (hasJob()) {

                if (currentDirection == Direction.UP) {
                    Request request = currentJobs.pollFirst();
                    processUpRequest(request);

                    if (currentJobs.isEmpty()) {
                        addPendingDownJobsToCurrentJobs();
                    }
                }

                if (currentDirection == Direction.DOWN) {
                    Request request = currentJobs.pollLast();
                    processDownRequest(request);

                    if (currentJobs.isEmpty()) {
                        addPendingUpJobsToCurrentJobs();
                    }

                }
            }
        }
    }

    public boolean hasJob() {
        return !currentJobs.isEmpty();
    }

    private void processUpRequest(Request request) {
        int startFloor = currentFloor;

        // The elevator is not on the floor where the person has requested it i.e. source floor. So first bring it there.
        if (startFloor < request.externalRequest().sourceFloor()) {
            for (int i = startFloor; i <= request.externalRequest().sourceFloor(); i++) {
                sleep(1000);
                System.out.println("We have reached floor -- " + i);
                currentFloor = i;
            }
        }

        // The elevator is now on the floor where the person has requested it i.e. source floor. User can enter and go to the destination floor.
        System.out.println("Reached Source Floor--opening door");

        startFloor = currentFloor;
        for (int i = startFloor; i <= request.internalRequest().destinationFloor(); i++) {
            sleep(1000);
            System.out.println("We have reached floor -- " + i);
            currentFloor = i;
            if (checkIfNewJobCanBeProcessed(request)) {
                break;
            }
        }
    }

    private void processDownRequest(Request request) {

        int startFloor = currentFloor;
        if (startFloor < request.externalRequest().sourceFloor()) {
            for (int i = startFloor; i <= request.externalRequest().sourceFloor(); i++) {
                sleep(1000);

                System.out.println("We have reached floor -- " + i);
                currentFloor = i;
            }
        }

        System.out.println("Reached Source Floor--opening door");

        startFloor = currentFloor;

        for (int i = startFloor; i >= request.internalRequest().destinationFloor(); i--) {
            sleep(1000);

            System.out.println("We have reached floor -- " + i);
            currentFloor = i;
            if (checkIfNewJobCanBeProcessed(request)) {
                break;
            }
        }
    }

    private boolean checkIfNewJobCanBeProcessed(Request currentRequest) {
        if (hasJob()) {
            if (currentDirection == Direction.UP) {
                Request request = currentJobs.pollFirst();

                if (request.internalRequest().destinationFloor() < currentRequest.internalRequest()
                        .destinationFloor()) {
                    currentJobs.add(request);
                    currentJobs.add(currentRequest);
                    return true;
                }
                currentJobs.add(request);
            }

            if (currentDirection == Direction.DOWN) {
                Request request = currentJobs.pollLast();

                if (request.internalRequest().destinationFloor() > currentRequest.internalRequest()
                        .destinationFloor()) {
                    currentJobs.add(request);
                    currentJobs.add(currentRequest);
                    return true;
                }
                currentJobs.add(request);
            }
        }
        return false;
    }

    private void addPendingDownJobsToCurrentJobs() {
        if (!downPendingJobs.isEmpty()) {
            currentJobs = downPendingJobs;
            currentDirection = Direction.DOWN;
        } else {
            currentState = State.IDLE;
        }
    }

    private void addPendingUpJobsToCurrentJobs() {
        if (!upPendingJobs.isEmpty()) {
            currentJobs = upPendingJobs;
            currentDirection = Direction.UP;
        } else {
            currentState = State.IDLE;
        }
    }

    @Override
    public void addJob(Request request) {
        if (currentState == State.IDLE) {
            currentState = State.MOVING;
            currentDirection = request.externalRequest().directionToGo();
            currentJobs.add(request);
        } else if (currentState == State.MOVING) {

            if (request.externalRequest().directionToGo() != currentDirection) {
                addToPendingJobs(request);
            } else if (request.externalRequest().directionToGo() == currentDirection) {
                if (currentDirection == Direction.UP
                    && request.internalRequest().destinationFloor() < currentFloor) {
                    addToPendingJobs(request);
                } else if (currentDirection == Direction.DOWN
                           && request.internalRequest().destinationFloor() > currentFloor) {
                    addToPendingJobs(request);
                } else {
                    currentJobs.add(request);
                }
            }
        }
    }

    @Override
    public void addToPendingJobs(Request request) {
        if (request.externalRequest().directionToGo() == Direction.UP) {
            System.out.println("Add to pending up jobs");
            upPendingJobs.add(request);
        } else {
            System.out.println("Add to pending down jobs");
            downPendingJobs.add(request);
        }
    }
}

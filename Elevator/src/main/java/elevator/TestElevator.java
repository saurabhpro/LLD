package elevator;

import elevator.models.Direction;
import elevator.models.ExternalRequest;
import elevator.models.InternalRequest;
import elevator.models.Request;

public class TestElevator {
    public static void main(String[] args) {
        Elevator elevator = new Elevator();

        /**
         * Thread for starting the elevator
         */
        ProcessJobWorker processJobWorker = new ProcessJobWorker(elevator);
        Thread t2 = new Thread(processJobWorker);
        t2.start();

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //person wants to go in up direction from source floor 0
        ExternalRequest er = new ExternalRequest(Direction.UP, 0);

        //the destination floor is 5
        InternalRequest ir = new InternalRequest(5);
        Request request1 = new Request(ir, er);

        /**
         * Pass job to the elevator
         */
        new Thread(new AddJobWorker(elevator, request1)).start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
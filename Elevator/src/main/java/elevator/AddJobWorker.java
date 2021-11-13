package elevator;

import elevator.models.Request;

import static elevator.ThreadUtils.sleep;

class AddJobWorker implements Runnable {

    private final Elevator elevator;
    private final Request request;

    AddJobWorker(Elevator elevator, Request request) {
        this.elevator = elevator;
        this.request = request;
    }

    @Override
    public void run() {
        sleep(200);
        elevator.addJob(request);
    }

}
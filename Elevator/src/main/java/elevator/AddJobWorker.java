package elevator;

import elevator.models.Request;

class AddJobWorker implements Runnable {

    private final Elevator elevator;
    private final Request request;

    AddJobWorker(Elevator elevator, Request request) {
        this.elevator = elevator;
        this.request = request;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        elevator.addJob(request);
    }

}
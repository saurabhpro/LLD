package elevator;

import elevator.models.Request;

public interface IElevator {
    void startElevator();

    void addJob(Request request);

    void addToPendingJobs(Request request);
}

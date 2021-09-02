package elevator.models;

public record Request(InternalRequest internalRequest,
                      ExternalRequest externalRequest) implements Comparable<Request> {

    @Override
    public int compareTo(Request req) {
        return Integer.compare(internalRequest.destinationFloor(), req.internalRequest.destinationFloor());
    }
}

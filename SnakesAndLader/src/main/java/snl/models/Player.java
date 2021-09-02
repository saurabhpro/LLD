package snl.models;

import java.util.UUID;

public record Player(String name, String id) {
    public Player(String name) {
        this(name, UUID.randomUUID().toString());
    }
}
package me.nunum.whereami.model.exceptions;

public class EntityAlreadyExists extends RuntimeException {
    public EntityAlreadyExists(Throwable cause) {
        super(cause);
    }
}

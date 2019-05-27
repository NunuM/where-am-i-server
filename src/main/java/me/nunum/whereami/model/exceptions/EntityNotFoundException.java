package me.nunum.whereami.model.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String s) {
        super(s);
    }

    public EntityNotFoundException(Throwable throwable) {
        super(throwable);
    }
}

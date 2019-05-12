package me.nunum.whereami.model.exceptions;

public class ForbiddenEntityAccessException extends RuntimeException {
    public ForbiddenEntityAccessException(String s) {
        super(s);
    }
}

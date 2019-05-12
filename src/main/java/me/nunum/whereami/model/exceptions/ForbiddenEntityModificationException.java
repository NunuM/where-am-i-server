package me.nunum.whereami.model.exceptions;

public class ForbiddenEntityModificationException extends RuntimeException {
    public ForbiddenEntityModificationException(String s) {
        super(s);
    }
}

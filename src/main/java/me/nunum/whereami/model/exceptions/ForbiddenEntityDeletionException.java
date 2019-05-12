package me.nunum.whereami.model.exceptions;

public class ForbiddenEntityDeletionException extends RuntimeException {
    public ForbiddenEntityDeletionException(String s) {
        super(s);
    }
}

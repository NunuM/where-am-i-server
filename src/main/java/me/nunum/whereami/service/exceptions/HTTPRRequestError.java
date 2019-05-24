package me.nunum.whereami.service.exceptions;

public class HTTPRRequestError extends RuntimeException {

    public HTTPRRequestError(String s) {
        super(s);
    }
}

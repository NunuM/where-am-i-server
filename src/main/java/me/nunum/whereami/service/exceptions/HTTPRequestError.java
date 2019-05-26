package me.nunum.whereami.service.exceptions;

public class HTTPRequestError extends RuntimeException {

    public HTTPRequestError(String s) {
        super(s);
    }
}

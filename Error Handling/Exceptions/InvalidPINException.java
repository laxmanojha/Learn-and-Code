package Exceptions;

import java.lang.Exception;

public class InvalidPINException extends Exception {
    public InvalidPINException(String message) {
        super(message);
    }
}

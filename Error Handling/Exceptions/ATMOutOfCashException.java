package Exceptions;

import java.lang.Exception;

public class ATMOutOfCashException extends Exception {
    public ATMOutOfCashException(String message) {
        super(message);
    }
}

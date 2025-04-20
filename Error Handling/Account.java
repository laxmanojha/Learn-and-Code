import Exceptions.InvalidPINException;
import Exceptions.CardBlockedException;

public class Account {
    private String cardNumber;
    private String pin;
    private double balance;
    private int invalidAttempts = 0;
    private boolean cardBlocked = false;
    private double dailyLimit = 5000;

    public Account(String cardNumber, String pin, double balance) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public boolean validatePIN(String inputPin) throws InvalidPINException, CardBlockedException {
        if (cardBlocked) return false;

        if (this.pin.equals(inputPin)) {
            invalidAttempts = 0;
            return true;
        } else {
            invalidAttempts++;
            if (invalidAttempts >= 3) {
                cardBlocked = true;
                throw new CardBlockedException("Card has been blocked after 3 invalid PIN attempts.");
            } else {
                throw new InvalidPINException("Invalid PIN. Attempts left: " + (3 - invalidAttempts));
            }
        }
    }

    public boolean isCardBlocked() {
        return cardBlocked;
    }

    public double getBalance() {
        return balance;
    }

    public void debit(double amount) {
        balance -= amount;
    }

    public double getDailyLimit() {
        return dailyLimit;
    }
}

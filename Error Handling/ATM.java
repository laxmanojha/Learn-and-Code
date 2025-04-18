import java.util.Random;
import Exceptions.CardBlockedException;
import Exceptions.DailyLimitExceededException;
import Exceptions.InsufficientFundsException;
import Exceptions.ATMOutOfCashException;

public class ATM {
    private double atmBalance = 10000;
    private boolean serverConnected = true;

    public boolean checkServerConnection() {
        // Randomly simulate server issue
        serverConnected = new Random().nextInt(10) != 0; // 10% chance of failure
        return serverConnected;
    }

    public void withdraw(Account account, double amount) throws Exception {
        if (!checkServerConnection()) {
            throw new Exception("Unable to connect with server. Please try again later.");
        }

        if (account.isCardBlocked()) {
            throw new CardBlockedException("Card is blocked due to multiple incorrect PIN attempts.");
        }

        if (amount > account.getDailyLimit()) {
            throw new DailyLimitExceededException("Daily withdrawal limit exceeded.");
        }

        if (amount > account.getBalance()) {
            throw new InsufficientFundsException("Insufficient balance in your account.");
        }

        if (amount > atmBalance) {
            throw new ATMOutOfCashException("ATM has insufficient cash.");
        }

        account.debit(amount);
        atmBalance -= amount;
        System.out.println("Withdrawal successful. New balance: " + account.getBalance());
    }
}

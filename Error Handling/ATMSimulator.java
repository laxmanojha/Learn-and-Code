import java.util.Scanner;
import Exceptions.InvalidPINException;
import Exceptions.CardBlockedException;

public class ATMSimulator {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            ATM atm = new ATM();
            Account account = new Account("12345678", "1234", 10000);

            System.out.print("Enter PIN: ");
            for (int index = 0; index < 3; index++) {
                try {
                    String enteredPin = sc.nextLine();
                    if (account.validatePIN(enteredPin)) {
                        System.out.println("PIN validated successfully.");
                        break;
                    }
                } catch (InvalidPINException | CardBlockedException e) {
                    System.out.println(e.getMessage());
                    if (account.isCardBlocked()) return;
                    if (index == 2) return;
                    System.out.print("Try again: ");
                }
            }

            System.out.print("Enter amount to withdraw: ");
            double amount = sc.nextDouble();

            atm.withdraw(account, amount);
        } catch (Exception e) {
            System.out.println("Transaction Failed: " + e.getMessage());
        }
    }
}

public class Customer {

    private String firstName;
    private String lastName;
    private Wallet myWallet;

    public Customer() {
        float defaultAmount = 100;
        this.firstName = "Default";
        this.lastName = "Default";
        this.myWallet = new Wallet(defaultAmount);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public float makePayment(float payableAmount) throws Exception {
        float paymentReceived = 0;

        if (myWallet.getTotalMoney() > payableAmount) {
            myWallet.subtractMoney(payableAmount);
            paymentReceived = payableAmount;
        }

        return paymentReceived;
    }
}

public class Main {

    public static float getPayment(Customer customer, float payableAmount) {
        float paymentReceived = 0;
        try {
            paymentReceived = customer.makePayment(payableAmount);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return paymentReceived;
    }
    
    public static void main(String[] args) {
        // code from some method inside the delivery boy class... payment = 2.00; //
        Customer customer = new Customer();
        float payableAmount = 2;

        float paymentReceived = getPayment(customer, payableAmount);

        if (paymentReceived == payableAmount)
            System.out.println("Payment received successfully.");
        else
            System.out.println("Payment unsuccessful!");
    }
}

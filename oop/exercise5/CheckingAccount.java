public class CheckingAccount implements IBankAccount {
    private double balance;
    private double overdraftLimit = 500;

    public double getBalance() {
        return balance;
    }

    @Override
    public void deposit(double amount) {
        balance += amount;
        System.out.println("Deposited: $" + amount + ", New Balance: $" + balance);
    }

    @Override
    public void withdraw(double amount) {
        if (amount > balance + overdraftLimit) {
            System.out.println("Not enough money to withdraw: $"+amount);
        } else {

            balance -= amount;

            System.out.println("Withdrawn: $" + amount + ", Remaining Balance: $" + balance);
        }
    }
}

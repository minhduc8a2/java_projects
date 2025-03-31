public class SavingAccount implements IBankAccount {
    private double balance;

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
        if (amount > balance) {
            System.out.println("Not enough money to withdraw");
        } else {
            balance -= amount;
            System.out.println("Withdrawn: $" + amount + ", Remaining Balance: $" + balance);
        }
    }
}

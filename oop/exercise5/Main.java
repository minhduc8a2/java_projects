public class Main {
    public static void main(String[] args) {
        IBankAccount account1 = new CheckingAccount();
        account1.deposit(100);
        account1.withdraw(200);
        account1.withdraw(500);
        account1.withdraw(400);
        account1.withdraw(100);
    }
}

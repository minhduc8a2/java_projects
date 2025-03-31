public class Main {
    public static void main(String[] args) {
        Notification sms = new SMSNotification();
        Notification email = new EmailNotification();

        sms.send("Your order has arrived!");
        email.send("Your order has arrived!");
    }
}

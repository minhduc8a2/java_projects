public class Main {
    public static void main(String[] args) {
        NotificationManager notificationManager = new NotificationManager();
        notificationManager.addListener(new EmailNotification());
        notificationManager.addListener(new SMSNotification());

        notificationManager.sendNotification("Payment has been done successfully!");
    }
}

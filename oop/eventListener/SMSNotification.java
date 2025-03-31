public class SMSNotification implements EventListener {
    @Override
    public void notify(String message){
        System.out.println("[SMS Notification] message: "+message);
    }
}

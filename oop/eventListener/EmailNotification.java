public class EmailNotification implements EventListener {
    @Override
    public void notify(String message){
        System.out.println("[Email Notification] message: "+message);
    }
}

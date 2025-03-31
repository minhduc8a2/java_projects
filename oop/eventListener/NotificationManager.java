import java.util.ArrayList;
import java.util.List;

public class NotificationManager {
    private List<EventListener> listeners;

    public NotificationManager() {
        listeners = new ArrayList<>();
    }

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    public void sendNotification(String message) {
        listeners.forEach(el -> el.notify(message));
    }
}

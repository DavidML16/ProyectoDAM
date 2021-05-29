package morales.david.desktop.managers.eventcallbacks;

import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private static EventManager INSTANCE = null;

    public static EventManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new EventManager();
        return INSTANCE;
    }

    private Map<String, List<ScheduleListener>> listeners = new HashMap<>();

    public void subscribe(String uuid, ScheduleListener listener) {
        if(!listeners.containsKey(uuid))
            listeners.put(uuid, new ArrayList<>());
        List<ScheduleListener> typeListeners = listeners.get(uuid);
        typeListeners.add(listener);
    }

    public void unsubscribe(String uuid, ScheduleListener listener) {
        List<ScheduleListener> typeListeners = listeners.get(uuid);
        typeListeners.remove(listener);
    }

    public void notify(String uuid, ScheduleListenerType scheduleListenerType) {
        Platform.runLater(() -> {
            List<ScheduleListener> typeListeners = listeners.get(uuid);
            for (ScheduleListener listener : typeListeners) {
                listener.callback(uuid, scheduleListenerType);
            }
            listeners.remove(uuid);
        });
    }

}

package morales.david.desktop.managers.eventcallbacks;

import javafx.application.Platform;
import morales.david.desktop.models.Schedule;

import java.util.*;

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

            if(!listeners.containsKey(uuid))
                return;

            List<ScheduleListener> typeListeners = new ArrayList<>(listeners.get(uuid));

            if (typeListeners == null)
                return;

            for (ScheduleListener listener : typeListeners)
                listener.callback(uuid, scheduleListenerType);

            listeners.remove(uuid);

        });

    }

}

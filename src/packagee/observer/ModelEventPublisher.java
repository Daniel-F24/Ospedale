package packagee.observer;

import java.util.ArrayList;
import java.util.List;


public class ModelEventPublisher implements ModelSubject {

    private final List<ModelObserver> observers = new ArrayList<>();

    @Override
    public synchronized void addObserver(ModelObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public synchronized void removeObserver(ModelObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(ModelEvent event) {
        List<ModelObserver> snapshot;
        synchronized (this) {
            snapshot = new ArrayList<>(observers);
        }
        for (ModelObserver observer : snapshot) {
            observer.onModelChanged(event);
        }
    }

    public void notifyChange(ModelEventType type, String source, String description) {
        notifyObservers(new ModelEvent(type, source, description));
    }
}

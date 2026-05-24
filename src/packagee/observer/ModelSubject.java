package packagee.observer;

public interface ModelSubject {
    void addObserver(ModelObserver observer);
    void removeObserver(ModelObserver observer);
    void notifyObservers(ModelEvent event);
}


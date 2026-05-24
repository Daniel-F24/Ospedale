package packagee.observer;


public class ModelEvent {

    private final ModelEventType type;
    private final String source;
    private final String description;

    public ModelEvent(ModelEventType type, String source, String description) {
        this.type = type;
        this.source = source;
        this.description = description;
    }

    public ModelEventType getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getDescription() {
        return description;
    }
}

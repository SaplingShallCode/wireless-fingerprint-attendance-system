package core;

public class EventData {
    private String current_event_name;
    private String current_event_location;

    public String getCurrentEventName() {
        return current_event_name;
    }

    public String getCurrentEventLocation() {
        return current_event_location;
    }

    public void setCurrentEventName(String new_event_name) {
        current_event_name = new_event_name;
    }
    public void setCurrentEventLocation(String new_event_location) {
        current_event_location = new_event_location;
    }
}

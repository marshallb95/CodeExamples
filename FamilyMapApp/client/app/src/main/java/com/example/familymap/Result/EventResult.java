package com.example.familymap.Result;

import com.example.familymap.Model.Event;

/**
 * Class that contains array of events associated with user
 */
public class EventResult extends Result {
    /**
     * Array of events associated with user
     */
    Event[] data;
    /**
     * Create result of events
     * @param events Events associated with user
     */
    public EventResult(Event[] events) {
        setEvents(events);
    }

    public Event[] getEvents() {
        return data;
    }

    public void setEvents(Event[] events) {
        this.data = events;
    }
}


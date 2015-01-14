package edu.aalto.emn;

import java.util.HashMap;
import java.util.Map;

public class Bus {

    private Map<String, BusStop> stops; // time - stop
    private int route;

    public Bus() {
        this.stops = new HashMap<String, BusStop>();
    }

    public void setRoute(int route) {
        this.route = route;
    }

    public void addStop(String time, BusStop stop) {
        this.stops.put(time, stop);
    }
}

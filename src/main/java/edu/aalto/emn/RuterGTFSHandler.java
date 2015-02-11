package edu.aalto.emn;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.onebusaway.csv_entities.EntityHandler;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.serialization.GtfsReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import edu.aalto.emn.dataobject.Bus;
import edu.aalto.emn.dataobject.BusStop;


public class RuterGTFSHandler {

    private GtfsReader reader;
    private GtfsDaoImpl store;
    private static int  BUS_ROUTE_TYPE = 3;
    
    public RuterGTFSHandler(String gtfsDir) throws IOException {
        this.reader = new GtfsReader();
        this.reader.setInputLocation(new File(gtfsDir));
        this.store = new GtfsDaoImpl();
        this.reader.setEntityStore(this.store);
    }
    
    public void parseAndDump() throws IOException {
        this.reader.run();
        DBCollection routesColl, tripsColl;
        
        routesColl = MongoUtils.getDB("ruter").getCollection("routes");
        routesColl.drop();
        routesColl = MongoUtils.getDB().getCollection("routes");
        
        tripsColl = MongoUtils.getDB("ruter").getCollection("trips");
        tripsColl.drop();
        tripsColl = MongoUtils.getDB().getCollection("trips");
        
        HashMap<String, Route> routes = new HashMap<String, Route>();
        
        for(Route r: this.store.getAllRoutes()) {
            if(r.getType() == BUS_ROUTE_TYPE) {
                BasicDBObject route = new BasicDBObject();
                route.append("id", r.getId().getId())
                .append("description", r.getDesc())
                .append("name", r.getShortName());
                
                routesColl.insert(route);
                routes.put(r.getId().getId().toString(), r);
            }
        }
        
        HashMap<String, Bus> trips = new HashMap<String, Bus>(store.getAllTrips().size());

        for(Trip t: store.getAllTrips()) {
            Bus bus = new Bus();
            bus.setServiceID(t.getId().getId());
            bus.setFootnoteId(t.getServiceId().getId());
            bus.setServiceNbr(t.getRoute().getId().getId());
            tripsColl.insert(bus.toMongoObj());
        }
    }
}

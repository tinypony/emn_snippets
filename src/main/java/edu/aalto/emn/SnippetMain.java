package edu.aalto.emn;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import edu.aalto.emn.route.DistanceRetriever;

public class SnippetMain {
    
   
    @Parameter(names = "-xml", description = "Path to XML database dump")
    private String xmlPath;
    
    @Parameter(names = "-gtfs", description = "Path to GTFS database dump")
    private String gtfsPath;
    
	
	public SnippetMain() throws UnknownHostException {

	}

    public static void main(String[] args) throws IOException, InterruptedException {
    	SnippetMain snippet = new SnippetMain();
    	new JCommander(snippet, args);


    	if(snippet.xmlPath != null) {
            try {
                InputStream xmlInput = new FileInputStream(snippet.xmlPath);
                HSLXmlHandler hslHandler = new HSLXmlHandler();
                hslHandler.dumpData(hslHandler.parse(xmlInput).getBuses());
            } catch (Throwable err) {
                err.printStackTrace ();
            }
    	} else if(snippet.gtfsPath != null) {
    	    RuterGTFSHandler ruterHandler = new RuterGTFSHandler(snippet.gtfsPath);
    	    ruterHandler.parseAndDump();
    	}
      
    	
    	System.out.println();
		snippet.retrieveRouteLength();
        System.out.println("Done");
    }
    
    public void retrieveRouteLength() throws IOException, InterruptedException {
    	DBCollection coll = MongoUtils.getDB().getCollection("buses");
		List<String> routes = this.getDistinctRoutes();
    	Iterator<String> iter = routes.iterator();
    	float i = 0;
    	
    	while(iter.hasNext()) {
    		String routeCode = iter.next();
    		DBObject bus = this.getBus(routeCode);
    		
    		//Skip already processed routes
    		if(bus.get("routeLength") == null || (Integer) bus.get("routeLength") == 0) {
    			
    			int lengthInMeters;
    			
    			try {
      	  			lengthInMeters = DistanceRetriever.getRouteLength(bus);
    			} catch(IllegalStateException e) { //results from exceeding google api request quota, try to use hashed distances instead
    				continue;
    			}
    			
      	  		System.out.println("Route:" + routeCode + ", length:" + lengthInMeters);
      	  		
      			BasicDBObject query = new BasicDBObject("serviceNbr", routeCode);
      			BasicDBObject update = new BasicDBObject("$set", 
      					new BasicDBObject("routeLength", lengthInMeters)
      			);
      			WriteResult result = coll.update(query, update, false, true);
      	  		System.out.println("Entries affected: " + result.getN());
    		}
    		
    		i += 1.0;
    		System.out.print("Done "+i+"/"+routes.size()+ "(" + (i/routes.size())*100 + "%)            \r");
    	}
    }
    
    
    public List<String> getDistinctRoutes() throws UnknownHostException {
    	DBCollection coll = MongoUtils.getDB().getCollection("buses");
    	return coll.distinct("serviceNbr");
    }
    
    public DBObject getBus(String route) throws UnknownHostException {
    	DBCollection coll = MongoUtils.getDB().getCollection("buses");
    	BasicDBObject query = new BasicDBObject();
    	query.append("serviceNbr", route);
    	DBObject bus = coll.findOne(query);
    	return bus;
    }

}

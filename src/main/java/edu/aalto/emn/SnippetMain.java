package edu.aalto.emn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import edu.aalto.emn.route.DistanceRetriever;

public class SnippetMain {
    
    @Parameter(names = { "-route" }, description = "Route number")
    private String route = "23";
    
    private String companyId = "15690";
   
    @Parameter(names = "-xml", description = "Path to XML database dump", required=true)
    private String path;
    
    @Parameter(names = "-out", required = true)
    private String out;
    
	private MongoClient mongoClient;
	private DB db;
	
	public SnippetMain() throws UnknownHostException {
    	this.mongoClient = new MongoClient( "localhost" );
    	this.db = mongoClient.getDB( "hsl" );
	}

    public static void main(String[] args) throws IOException {
    	SnippetMain snippet = new SnippetMain();
    	new JCommander(snippet, args);
        
    	  List<String> routes = snippet.getDistinctRoutes(); 
    	  snippet.retrieveRouteLength(routes.subList(0, 1));
      
//        try {
//            InputStream xmlInput = new FileInputStream(snippet.path);
//            snippet.dumpData(snippet.parse(xmlInput).getBuses());
//        } catch (Throwable err) {
//            err.printStackTrace ();
//        }
        System.out.println("Done");
    }
    
    public void retrieveRouteLength(List<String> routes) throws IOException {
    	DBCollection coll = db.getCollection("buses");
    	Iterator<String> iter = routes.iterator();
    	
    	while(iter.hasNext()) {
    		String routeCode = iter.next();
    		DBObject bus = this.getBus(routeCode);
    		System.out.println(bus);
    		
    		if(bus.get("routeLength") == null) {
      	  		int lengthInMeters = DistanceRetriever.getRouteLength(bus);
      	  		System.out.println("Route:"+routeCode + ", length:"+lengthInMeters);
      	  		
      	  		BasicDBObject query = new BasicDBObject("serviceNbr", route);
      	  		BasicDBObject update = new BasicDBObject("routeLength", lengthInMeters);
      	  		coll.findAndModify(query, update);
    		}
    	}
    }
    
    public DBHandler parse(InputStream xmlInput) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
    	SAXParser saxParser = factory.newSAXParser();
        DBHandler handler   = new DBHandler(this.route, this.companyId);
        saxParser.parse(xmlInput, handler);
        System.out.println("Got " + handler.getStops().keySet().size() + " stops");
        System.out.println("Got " + handler.getBuses().size() + " buses");
        return handler;
    }
    
    public void dumpData(List<Bus> buses) throws UnknownHostException {
    	DBCollection coll = db.getCollection("buses");
    	coll.drop();
    	coll = db.getCollection("buses");
    	
    	for(Bus bus : buses) {
        	coll.insert(bus.toMongoObj());
    	}
    }
    
    public List<String> getDistinctRoutes() throws UnknownHostException {
    	DBCollection coll = db.getCollection("buses");
    	return coll.distinct("serviceNbr");
    }
    
    public DBObject getBus(String route) {
    	DBCollection coll = db.getCollection("buses");
    	BasicDBObject query = new BasicDBObject();
    	query.append("serviceNbr", route);
    	DBObject bus = coll.findOne(query);
    	return bus;
    }

}

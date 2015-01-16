package edu.aalto.emn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.UnknownHostException;
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
import com.mongodb.MongoClient;

public class SnippetMain {
    
    @Parameter(names = { "-route" }, description = "Route number")
    private String route = "23";
    
    private String companyId = "15690";
   
    @Parameter(names = "-xml", description = "Path to XML database dump", required=true)
    private String path;
    
    @Parameter(names = "-out", required = true)
    private String out;

    public static void main(String[] args) throws UnknownHostException {
    	SnippetMain snippet = new SnippetMain();
    	new JCommander(snippet, args);
        
    	System.out.println("Got " + snippet.getDistinctRoutes().size() + " distinct routes");
      /*  
        try {
            InputStream xmlInput = new FileInputStream(snippet.path);
            //snippet.dumpData(snippet.parse(xmlInput).getBuses());
            
//            File outfile = new File(snippet.out);
//            outfile.createNewFile();
//            PrintWriter writer = new PrintWriter(outfile);
//            writer.print("");
//            writer.close();
//            
//            writer = new PrintWriter(outfile);
//            writer.print(db.toString());
//            writer.close();
//            System.out.println("Done!");
        } catch (Throwable err) {
            err.printStackTrace ();
        } */
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
    	MongoClient mongoClient = new MongoClient( "localhost" );
    	DB db = mongoClient.getDB( "hsl" );
    	DBCollection coll = db.getCollection("buses");
    	for(Bus bus : buses) {
        	coll.insert(bus.toMongoObj());
    	}
    }
    
    public List<BasicDBObject> getDistinctRoutes() throws UnknownHostException {
    	MongoClient mongoClient = new MongoClient( "localhost" );
    	DB db = mongoClient.getDB( "hsl" );
    	DBCollection coll = db.getCollection("buses");
    	return coll.distinct("serviceNbr");
    }

}

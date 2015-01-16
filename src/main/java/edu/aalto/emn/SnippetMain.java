package edu.aalto.emn;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class SnippetMain {
    
    @Parameter(names = { "-route" }, description = "Route number")
    private String route = "23";
    
    private String companyId = "15690";
   
    @Parameter(names = "-xml", description = "Path to XML database dump", required=true)
    private String path;
    
    @Parameter(names = "-out", required = true)
    private String out;

    public static void main(String[] args) {
    	SnippetMain snippet = new SnippetMain();
    	new JCommander(snippet, args);
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        
        try {
            InputStream xmlInput = new FileInputStream(snippet.path);

            SAXParser saxParser = factory.newSAXParser();
            DBHandler handler   = new DBHandler(snippet.route, snippet.companyId);
            saxParser.parse(xmlInput, handler);
            
            JSONObject db = new JSONObject();
            JSONArray busesAr = new JSONArray();
            

            System.out.println("Got " + handler.getStops().keySet().size() + " stops");
            System.out.println("Got " + handler.getBuses().size() + " buses");
            
            for(Bus bus : handler.getBuses()) {
            	busesAr.put(bus.toJson());
            }
            
            db.put("buses", busesAr);
            
            File outfile = new File(snippet.out);
            outfile.createNewFile();
            PrintWriter writer = new PrintWriter(outfile);
            writer.print("");
            writer.close();
            
            writer = new PrintWriter(outfile);
            writer.print(db.toString());
            writer.close();
            System.out.println("Done!");
        } catch (Throwable err) {
            err.printStackTrace ();
        }
    }

}

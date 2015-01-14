package edu.aalto.emn;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.beust.jcommander.Parameter;

public class SnippetMain {
    
    @Parameter(names = { "-route" }, description = "Route number")
    private static Integer route = 23;
   
    @Parameter(names = "-xml", description = "Path to XML database dump", required=true)
    private static String path;

    public static void main(String[] args) {
        File xmlFile = new File(args[0]);
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            InputStream xmlInput = new FileInputStream(args[0]);

            SAXParser saxParser = factory.newSAXParser();
            DBHandler handler   = new DBHandler(route);
            saxParser.parse(xmlInput, handler);

            for(Bus bus : handler.getBuses()){
            //    System.out.println(bus);
            }
            System.out.println("Done");
        } catch (Throwable err) {
            err.printStackTrace ();
        }
    }

}

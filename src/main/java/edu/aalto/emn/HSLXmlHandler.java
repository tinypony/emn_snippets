package edu.aalto.emn;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.mongodb.DBCollection;

import edu.aalto.emn.dataobject.Bus;

public class HSLXmlHandler {

    public DBHandler parse(InputStream xmlInput) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DBHandler handler   = new DBHandler("","");
        saxParser.parse(xmlInput, handler);
        System.out.println("Got " + handler.getStops().keySet().size() + " stops");
        System.out.println("Got " + handler.getBuses().size() + " buses");
        return handler;
    }
    
    
    public void dumpData(List<Bus> buses) throws UnknownHostException {
        DBCollection coll = MongoUtils.getDB().getCollection("buses");
        coll.drop();
        coll = MongoUtils.getDB().getCollection("buses");
        
        for(Bus bus : buses) {
            coll.insert(bus.toMongoObj());
        }
    }
}

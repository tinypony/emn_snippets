package edu.aalto.emn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DBHandler extends DefaultHandler {
    private Map<String, BusStop> stops;
    private List<Bus> buses;
    private int route;

    private Stack<String> elementStack = new Stack<String>();
    private Stack<Object> objectStack = new Stack<Object>();

    public DBHandler(int route) {
        this.route = route;
        this.stops = new HashMap();
        this.buses = new ArrayList<Bus>();
    }

    public List<Bus> getBuses() {
        return this.buses;
    }
    
    public boolean isReal(Attributes atts) {
        String type = atts.getValue("type");
        String isVirtual = atts.getValue("isVirtual");
        
        if(type == null && isVirtual == null) {
            return true;
        }
        
        return (type != null && "0".equals(type)) || (isVirtual != null && "false".equals(isVirtual));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        this.elementStack.push(qName);

        if ("station".equals(qName.toLowerCase())) {
            if(isReal(attributes)) {
                BusStop stop = new BusStop(attributes);
                this.objectStack.push(stop);
                this.stops.put(stop.getId(), stop);
            } else {
                this.elementStack.pop();
            }
        } else if ("service".equals(qName.toLowerCase())) {
            Bus bus = new Bus();
            this.objectStack.push(bus);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {

        this.elementStack.pop();
        String qNameLow = qName.toLowerCase();

        if ("station".equals(qNameLow) || "service".equals(qNameLow)) {
            Object object = this.objectStack.pop();
        }
    }
}

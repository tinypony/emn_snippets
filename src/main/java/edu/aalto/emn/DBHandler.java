package edu.aalto.emn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DBHandler extends DefaultHandler {
	private Map<String, BusStop> stops;
	private ArrayList<Bus> buses;

	private List<String> allowedModes = Arrays.asList("1", "3", "4", "5", "25");
	private String route, company;

	private Stack<String> elementStack = new Stack<String>();
	private Stack<Object> objectStack = new Stack<Object>();
	

	public DBHandler(String route, String company) {
		this.route = route;
		this.company = company;
		this.stops = new HashMap<String, BusStop>();
		this.buses = new ArrayList<Bus>();
	}

	public List<Bus> getBuses() {
		return this.buses;
	}

	public Map<String, BusStop> getStops() {
		return this.stops;
	}

	public boolean isReal(Attributes atts) {
		String type = atts.getValue("type");
		String isVirtual = atts.getValue("isVirtual");


		if (type == null && isVirtual == null) {
			return true;
		}

		return (type != null && "0".equals(type)) || (isVirtual != null && "false".equals(isVirtual));
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		String qNameLow = qName.toLowerCase();
		this.elementStack.push(qName);

		if ("station".equals(qName.toLowerCase())) {
			if (isReal(attributes) && attributes.getValue("X") != null && attributes.getValue("Y") != null) {
				BusStop stop = new BusStop(attributes);
				this.objectStack.push(stop);
				this.stops.put(stop.getId(), stop);
			}
		} else if ("service".equals(qName.toLowerCase())) {
			Bus bus = new Bus();
			bus.setServiceID(attributes.getValue("ServiceId"));
			this.objectStack.push(bus);
			
		} else if ("servicenbr".equals(qNameLow)) {
			String routeNbr = attributes.getValue("Variant");
			String serviceNbr = attributes.getValue("ServiceNbr");
			String companyId = attributes.getValue("CompanyId");
			
			
			if (routeNbr != null) {
				Bus bus = (Bus) this.objectStack.peek();
				bus.setCompany(companyId);
				bus.setRoute(routeNbr);
				bus.setServiceNbr(serviceNbr);
			}
			
		} else if ("stop".equals(qNameLow)) {
			Bus bus = (Bus) this.objectStack.peek();
			
			try {
				Stop stop = new Stop(attributes, this.getStops());
				bus.addStop(stop);
			} catch(Exception e) {
				System.out.println("Error parsing stop");
			}
		} else if ("servicetrnsmode".equals(qNameLow)) {
			Bus bus = (Bus) this.objectStack.peek();
			bus.setTrnsmode(attributes.getValue("TrnsmodeId"));
		}
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		this.elementStack.pop();
		String qNameLow = qName.toLowerCase();
		
		if ("service".equals(qNameLow)) {
			Bus bus = (Bus) this.objectStack.pop();
			
			if(filter(bus)) {
				this.buses.add(bus);
			}
		}
	}
	

	private boolean filter(Bus bus) {
		return allowedModes.contains(bus.getTrnsmode()); //bus.getRoute().equals(this.route) && this.company.equals(bus.getCompany());
	}
}
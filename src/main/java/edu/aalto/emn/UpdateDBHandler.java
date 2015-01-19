package edu.aalto.emn;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

public class UpdateDBHandler extends DefaultHandler {
	
	Map<String, BasicDBObject> validityDays;
	Stack<String> serviceStack;
	String deliveryStart="";
	
	public UpdateDBHandler() {
		serviceStack = new Stack<String>();
		this.validityDays = new HashMap<String, BasicDBObject>();
	}
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		if("footnote".equalsIgnoreCase(qName)) {
			BasicDBObject footnote = new BasicDBObject();
			footnote.append("vector", attributes.getValue("Vector"));
			
			if(attributes.getValue("Firstdate") != null) {
				footnote.append("firstDate", attributes.getValue("Firstdate"));
			} else {
				footnote.append("firstDate", deliveryStart);
			}
			
			validityDays.put(attributes.getValue("FootnoteId"), footnote);
		} else if("delivery".equalsIgnoreCase(qName)) {
			deliveryStart = attributes.getValue("Firstday");
		} else if("service".equalsIgnoreCase(qName)) {
			serviceStack.push(attributes.getValue("ServiceId"));
			
			
		} else if("servicevalidity".equalsIgnoreCase(qName)) {
			String serviceId = serviceStack.pop();
			DBCollection coll;
			
			try {
				coll = MongoUtils.getDB().getCollection("buses");
	
				BasicDBObject query = new BasicDBObject("serviceId", serviceId);
				BasicDBObject update = new BasicDBObject("$set",
						new BasicDBObject("validity",
								validityDays.get(attributes
										.getValue("FootnoteId"))));
				coll.update(query, update, false, false);
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void endElement(String uir, String localName, String qName){
		
	}
}

package edu.aalto.emn;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.beust.jcommander.converters.ISO8601DateConverter;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class Bus implements Jsonable, Mongoable {

	private String serviceID;
	private String serviceNbr;
    private ArrayList<Stop> stops; //stop
    private String route;
	private String companyId;
	private String trnsmode;
	private String footnoteId;
	private String firstDate;
	private String vector;

    public Bus() {
        this.stops = new ArrayList<Stop>();
    }

    public void setRoute(String route) {
        this.route = route;
    }
    
    public String getRoute() {
    	return this.route;
    }

    public void addStop(Stop stop) {
        this.stops.add(stop);
    }
    
    public ArrayList<Stop> getStops() {
    	return this.stops;
    }

	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public String getServiceNbr() {
		return serviceNbr;
	}

	public void setServiceNbr(String serviceNbr) {
		this.serviceNbr = serviceNbr;
	}

	public void setCompany(String companyId) {
		this.companyId = companyId;		
	}
	
	public String getCompany() {
		return this.companyId;		
	}

	@Override
	public JSONObject toJson() {
    	JSONObject jBus = new JSONObject();
    	jBus.put("serviceId", this.getServiceID());
    	jBus.put("companyId", this.getCompany());
    	jBus.put("serviceNbr", this.getServiceNbr());
    	jBus.put("route", this.getRoute());
    	JSONArray jstops = new JSONArray();
    	
    	for(Stop stop : this.getStops()) {
    		jstops.put(stop.toJson());
    	}
    	
    	jBus.put("stops", jstops);
    	
    	return jBus;
	}

	public String getTrnsmode() {
		return trnsmode;
	}

	public void setTrnsmode(String trnsmode) {
		this.trnsmode = trnsmode;
	}

	@Override
	public BasicDBObject toMongoObj() {
		BasicDBObject val = new BasicDBObject();
		val.append("firstDate", this.getFirstDate());
		val.append("vector", this.getVector());
		
		BasicDBList dates = new BasicDBList();
		Calendar cal = Calendar.getInstance();
		String[] tokens = firstDate.split("-");
		
		cal.set(Integer.parseInt(tokens[0]), 
				Integer.parseInt(tokens[1])-1, 
				Integer.parseInt(tokens[2])-1);
		
		
		for(int i =0; i < this.getVector().length(); i++) {
			char a = this.getVector().charAt(i);
			if(a == '1') {
				cal.set(Integer.parseInt(tokens[0]), 
						Integer.parseInt(tokens[1])-1, 
						Integer.parseInt(tokens[2]));
				cal.add(Calendar.DATE, i);
				dates.add(cal.getTime());
			}
		}
		
		val.append("dates", dates);
		
		BasicDBObject obj = new BasicDBObject();
    	obj.append("serviceId", this.getServiceID())
    	.append("companyId", this.getCompany())
    	.append("serviceNbr", this.getServiceNbr())
    	.append("route", this.getRoute())
    	.append("stops", this.getDBStops())
    	.append("footnodeId", this.getFootnoteId())
    	.append("validity", val);
    	
    	return obj;
	}

	private List<BasicDBObject> getDBStops() {
		ArrayList<BasicDBObject> stops = new ArrayList<BasicDBObject>();
		for(Stop stop: this.getStops()) {
			stops.add(stop.toMongoObj());
		}
		return stops;
	}

	public String getFootnoteId() {
		return footnoteId;
	}

	public void setFootnoteId(String footnoteId) {
		this.footnoteId = footnoteId;
	}

	public String getFirstDate() {
		return firstDate;
	}

	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}

	public String getVector() {
		return vector;
	}

	public void setVector(String vector) {
		this.vector = vector;
	}
}

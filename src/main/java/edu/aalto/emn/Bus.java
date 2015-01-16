package edu.aalto.emn;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Bus implements Jsonable {

	private String serviceID;
	private String serviceNbr;
    private ArrayList<Stop> stops; //stop
    private String route;
	private String companyId;
	private String trnsmode;

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
}

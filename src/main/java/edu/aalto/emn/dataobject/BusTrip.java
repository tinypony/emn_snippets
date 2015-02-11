package edu.aalto.emn.dataobject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onebusaway.gtfs.model.ServiceCalendarDate;

import com.beust.jcommander.converters.ISO8601DateConverter;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

public class BusTrip implements Jsonable, Mongoable {

	private String serviceID;
	private String serviceNbr;
    private ArrayList<ScheduleStop> stops; //stop
    private String route;
	private String companyId;
	private String trnsmode;
	private String footnoteId;
	private String firstDate;
	private String vector;
	private String dataSource;
	
	private Object auxilliary;

    public BusTrip() {
        this.stops = new ArrayList<ScheduleStop>();
    }
    
    public BusTrip(Object aux) {
        this();
        this.auxilliary = aux;
    }

    public void setRoute(String route) {
        this.route = route;
    }
    
    public String getRoute() {
    	return this.route;
    }

    public void addStop(ScheduleStop stop) {
        this.stops.add(stop);
    }
    
    public ArrayList<ScheduleStop> getStops() {
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
    	
    	for(ScheduleStop stop : this.getStops()) {
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
	
	public BasicDBList getDates() {
		BasicDBList dates = new BasicDBList();
		Calendar cal = Calendar.getInstance();
		
		if(dataSource == "hsl") {
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
					dates.add(this.getDateString(cal));
				}
			}
		} else if(dataSource == "ruter") {
			List<ServiceCalendarDate> calDates = (List<ServiceCalendarDate>) this.auxilliary;
			for(ServiceCalendarDate scd: calDates) {
				cal.set(scd.getDate().getYear(), 
				scd.getDate().getMonth()-1, 
				scd.getDate().getDay());
				dates.add(this.getDateString(cal));
			}
		}
		
		return dates;
	}
	
	public String getDateString(Calendar cal) {
		return 	cal.get(Calendar.YEAR) + "-" 
				+ (cal.get(Calendar.MONTH)+1) + "-" 
				+ cal.get(Calendar.DATE);
	}

	@Override
	public BasicDBObject toMongoObj() {
		Collections.sort(this.stops);
		
		BasicDBObject obj = new BasicDBObject();
    	obj.append("serviceId", this.getServiceID())
    	.append("companyId", this.getCompany())
    	.append("serviceNbr", this.getServiceNbr())
    	.append("route", this.getRoute())
    	.append("stops", this.getDBStops())
    	.append("footnodeId", this.getFootnoteId())
    	.append("dates", this.getDates());
    	
    	return obj;
	}

	private List<BasicDBObject> getDBStops() {
		ArrayList<BasicDBObject> stops = new ArrayList<BasicDBObject>();
		for(ScheduleStop stop: this.getStops()) {
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

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
}

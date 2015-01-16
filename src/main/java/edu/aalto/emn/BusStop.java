package edu.aalto.emn;

import org.json.JSONObject;
import org.xml.sax.Attributes;


public class BusStop{
    private String id;
    private String name;
    private String x;
    private String y;
    
    public BusStop() {
        
    }
    
    public BusStop(Attributes atts) {
        String name = atts.getValue("Name");
        String id = atts.getValue("StationId");
        String x = atts.getValue("X");
        String y = atts.getValue("Y");
        
        if(name != null) {
            this.setName(name);
        }
        
        if(id !=null ) {
            this.setId(id);
        }
        
        if(x !=null) {
            this.setX(x);
        }
        
        if(y !=null) {
            this.setY(y);
        }
    }
    
    public BusStop(String id, String name, String x, String y) {
        this.setId(id);
        this.setName(name);
        this.setX(x);
        this.setY(y);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
    
    
    
}

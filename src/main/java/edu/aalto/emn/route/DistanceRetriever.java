package edu.aalto.emn.route;

import java.io.IOException;
import java.util.List;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.mongodb.DBObject;

import edu.aalto.emn.Bus;

public class DistanceRetriever {

	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private static String getOrigins(DBObject bus) {
		StringBuilder sb = new StringBuilder();
		List<DBObject> stops = (List<DBObject>) bus.get("stops");
		for(int i=0; i<stops.size()-1; i++) {
			DBObject stop = stops.get(i);
			if(i == 0) {
				sb.append(stop.get("Y")+","+stop.get("X"));
			} else {
				sb.append("|"+stop.get("Y")+","+stop.get("X"));	
			}			
		}
		String retval = sb.toString();
		
		return retval;
	}
	
	private static String getDestinations(DBObject bus) {
		StringBuilder sb = new StringBuilder();
		List<DBObject> stops = (List<DBObject>) bus.get("stops");
		
		for(int i=1; i<stops.size(); i++) {
			DBObject stop = stops.get(i);
			if(i == 1) {
				sb.append(stop.get("Y")+","+stop.get("X"));
			} else {
				sb.append("|"+stop.get("Y")+","+stop.get("X"));	
			}			
		}
		String retval = sb.toString();
		
		return retval;
	}

	public static int getRouteLength(DBObject bus) throws IOException {
		HttpRequestFactory requestFactory = HTTP_TRANSPORT
				.createRequestFactory(new HttpRequestInitializer() {
					
					@Override
					public void initialize(HttpRequest request) {
						request.setParser(new JsonObjectParser(JSON_FACTORY));
					}
				});
		
		ApiClasses.DistanceUrl url = ApiClasses.DistanceUrl.url(getOrigins(bus), getDestinations(bus));
		url.put("fields", "items(id,url,object(content,plusoners/totalItems))");
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
		System.out.println(response.parseAsString());
		ApiClasses.DestinationAPIResponse apiResponse = response.parseAs(ApiClasses.DestinationAPIResponse.class);
		return apiResponse.calculateTotalDistance();
	}
}

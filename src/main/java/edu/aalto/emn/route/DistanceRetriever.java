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

	static final int MAX_MATRIX_SIDE = 10;
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();
	static final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
				@Override
				public void initialize(HttpRequest request) {
					request.setParser(new JsonObjectParser(JSON_FACTORY));
				}
			});

	private static String getOrigins(List<DBObject> stops) {
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<stops.size()-1; i++) {
			DBObject stop = stops.get(i);
			if(i == 0) {
				sb.append(stop.get("posY")+","+stop.get("posX"));
			} else {
				sb.append("|"+stop.get("posY")+","+stop.get("posX"));	
			}			
		}
		
		String retval = sb.toString();
		//System.out.println("Origins = "+retval);
		return retval;
	}
	
	private static String getDestinations(List<DBObject> stops) {
		StringBuilder sb = new StringBuilder();
		
		for(int i=1; i<stops.size(); i++) {
			DBObject stop = stops.get(i);
			//System.out.println(stop);
			if(i == 1) {
				sb.append(stop.get("posY")+","+stop.get("posX"));
			} else {
				sb.append("|"+stop.get("posY")+","+stop.get("posX"));	
			}			
		}
		String retval = sb.toString();
		//System.out.println("Destinations = "+retval);
		return retval;
	}
	
	public static int getRoutePartLength(List<DBObject> stops) throws IOException {
		ApiClasses.DistanceUrl url = ApiClasses.DistanceUrl.url(getOrigins(stops), getDestinations(stops));
		
		url.put("fields", "items(id,url,object(content,plusoners/totalItems))");
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse response = request.execute();
	//	System.out.println(response.parseAsString());
		ApiClasses.DestinationAPIResponse apiResponse = response.parseAs(ApiClasses.DestinationAPIResponse.class);
		if(apiResponse.getStatus().equals("OVER_QUERY_LIMIT")) {
			throw new RuntimeException("Too much queries");
		}
		return apiResponse.calculateTotalDistance();
	}

	public static int getRouteLength(DBObject bus) throws IOException, InterruptedException {
		List<DBObject> stopsTotal = (List<DBObject>) bus.get("stops");
		boolean hasMore = true;
		int retval = 0, i = 0;
		
		while(hasMore) {
			List<DBObject> stopsSubList;
			if(i + MAX_MATRIX_SIDE + 1 < stopsTotal.size()) {
				stopsSubList = stopsTotal.subList(i, i+MAX_MATRIX_SIDE + 1);
			} else {
				stopsSubList = stopsTotal.subList(i, stopsTotal.size());
				hasMore = false;
			}
			
			retval += getRoutePartLength(stopsSubList);
			if(hasMore) {
				Thread.sleep(1000);
			}
			i += MAX_MATRIX_SIDE;
		}
		//System.out.println("Total length = "+retval);
		return retval;
	}
}

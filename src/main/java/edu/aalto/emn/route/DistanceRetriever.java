package edu.aalto.emn.route;

import java.io.IOException;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import edu.aalto.emn.Bus;

public class DistanceRetriever {

	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private static String getOrigins(Bus bus) {
		String retval = "";
		
		return retval;
	}
	
	private static String getDestinations(Bus bus) {
		String retval = "";
		
		return retval;
	}

	public static float getRouteLength(Bus bus) throws IOException {
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

		ApiClasses.DestinationAPIResponse apiResponse = request.execute().parseAs(ApiClasses.DestinationAPIResponse.class);
		return apiResponse.calculateTotalDistance();
	}
}

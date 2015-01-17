package edu.aalto.emn.route;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Key;

public class ApiClasses {

	static final String API_KEY = "AIzaSyCx3auvl9pVjodHKFHIkZcgpwe7PWcHGio";

	public static class DistanceUrl extends GenericUrl {
		public DistanceUrl(String encodedUrl) {
			super(encodedUrl);
		}

		/**
		 * Lists the public activities for the given Google+ user ID.
		 * 
		 * @return
		 */
		public static DistanceUrl url(String origins, String destinations) {
			return new DistanceUrl(
					"https://maps.googleapis.com/maps/api/distancematrix/json?origins="
							+ origins + "&destinations=" + destinations
							+ "&mode=driving&key=" + API_KEY);
		}

	}

	public static class DestinationAPIResponse {
		@Key
		private String status;

		@Key
		private List<String> origin_addresses;

		@Key
		private List<String> destination_addresses;

		@Key
		private ArrayList<MatrixRow> rows;
		
		public DestinationAPIResponse(){};
		
		public int calculateTotalDistance() {
			int retval= 0;
			
			for(int i=0; i< rows.size(); i++) {
				retval += this.rows.get(i).getElements().get(i).distance.value;
			}
			
			return retval;
		}
	}

	public static class MatrixRow {
		@Key
		private ArrayList<MatrixElement> elements;

		public MatrixRow(){};
		
		public List<MatrixElement> getElements() {
			return this.elements;
		}
	}

	public static class MatrixElement {
		@Key
		private IntTextPair duration;

		@Key
		private IntTextPair distance;
		
		public MatrixElement(){};
	}

	public static class IntTextPair {
		@Key
		private Integer value;

		@Key
		private String text;
		
		public IntTextPair(){};
	}
}

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

	public class DestinationAPIResponse {
		@Key
		private String status;

		@Key
		private String origin_addresses;

		@Key
		private String destination_addresses;

		@Key
		private ArrayList<MatrixRow> rows;
		
		public float calculateTotalDistance() {
			return 0.0f;
		}
	}

	public class MatrixRow {
		@Key
		private ArrayList<MatrixElement> elements;

		public List<MatrixElement> getElements() {
			return this.elements;
		}
	}

	public class MatrixElement {
		@Key
		private IntTextPair duration;

		@Key
		private IntTextPair distance;
	}

	public class IntTextPair {
		@Key
		private Integer value;

		@Key
		private String text;
	}
}

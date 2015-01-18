package edu.aalto.emn;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoUtils {
	private static MongoClient mongoClient;
	private static DB db;
	
	public static DB getDB() throws UnknownHostException {
		if(mongoClient == null) {
			mongoClient = new MongoClient( "localhost" );
		}
		
		if(db == null) {
			db = mongoClient.getDB( "hsl" );
		}
		
		return db;
	}
}

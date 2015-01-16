package edu.aalto.emn;

import com.mongodb.BasicDBObject;

public interface Mongoable {
	public BasicDBObject toMongoObj();
}

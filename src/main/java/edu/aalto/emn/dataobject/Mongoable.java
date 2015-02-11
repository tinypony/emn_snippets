package edu.aalto.emn.dataobject;

import com.mongodb.BasicDBObject;

public interface Mongoable {
	public BasicDBObject toMongoObj();
}

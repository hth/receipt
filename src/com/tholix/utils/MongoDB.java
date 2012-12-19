/**
 * 
 */
package com.tholix.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * @author hitender Dec 18, 2012 11:22:33 PM
 * 
 */
public class MongoDB {

	private static final MongoOperations mongoOperations;

	static {
		ApplicationContext ctx = new GenericXmlApplicationContext("../mongo-config.xml");
		mongoOperations = (MongoOperations) ctx.getBean("mongoTemplate");
	}

	public static MongoOperations mo() {
		return mongoOperations;
	}

}

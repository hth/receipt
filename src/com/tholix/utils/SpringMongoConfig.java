/**
 * 
 */
package com.tholix.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.Mongo;

/**
 * @author hitender Dec 17, 2012 3:10:42 AM
 */

@Configuration
public class SpringMongoConfig {
	private static final String DB = "rm";

	public @Bean
	MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(new Mongo(), DB);
	}

	public @Bean
	MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;
	}
}

// For XML
// ApplicationContext ctx = new
// GenericXmlApplicationContext("mongo-config.xml");
// For Annotation
// ApplicationContext ctx = new
// AnnotationConfigApplicationContext(SpringMongoConfig.class);
// MongoOperations mongoOperation =
// (MongoOperations)ctx.getBean("mongoTemplate");
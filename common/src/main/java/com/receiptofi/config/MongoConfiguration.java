package com.receiptofi.config;

import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hitender
 * Date: 5/23/18 10:36 PM
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.receiptofi")
public class MongoConfiguration {

    @Value("${mongo.replica.set}")
    private String mongoReplicaSet;

    @Value("${mongo.database.name}")
    private String mongoDatabaseName;

    /**
     * Template ready to use to operate on the database
     *
     * @return Mongo Template ready to use
     */
    @Bean
    MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), mongoConverter());

        mongoTemplate.setReadPreference(ReadPreference.nearest());
        mongoTemplate.setWriteConcern(WriteConcern.JOURNALED);
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);

        return mongoTemplate;
    }

    /**
     * DB connection Factory
     *
     * @return a ready to use MongoDbFactory
     */
    @Bean
    MongoDbFactory mongoDbFactory() {
        // Mongo Client
        MongoClient mongoClient = new MongoClient(getMongoSeeds());

        // Mongo DB Factory
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient, mongoDatabaseName);

        return mongoDbFactory;
    }

    @Bean
    MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }

    @Bean
    MappingMongoConverter mongoConverter() {
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(mongoDbFactory()),
                mongoMappingContext());
        converter.setTypeMapper(mongoTypeMapper());
        return converter;
    }

    @Bean
    MongoTypeMapper mongoTypeMapper() {
        return new DefaultMongoTypeMapper(null);
    }

    private List<ServerAddress> getMongoSeeds() {
        List<ServerAddress> serverAddresses = new ArrayList<>();

        if (StringUtils.isNotBlank(mongoReplicaSet)) {
            String[] mongoInternetAddresses = mongoReplicaSet.split(",");
            for (String mongoInternetAddress : mongoInternetAddresses) {
                String[] mongoIpAndPort = mongoInternetAddress.split(":");
                ServerAddress serverAddress = new ServerAddress(mongoIpAndPort[0], Integer.valueOf(mongoIpAndPort[1]));
                serverAddresses.add(serverAddress);
            }
        } else {
            throw new RuntimeException("No Mongo Seed(s) listed");
        }

        return serverAddresses;
    }
}

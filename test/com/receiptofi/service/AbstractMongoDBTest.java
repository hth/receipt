package com.receiptofi.service;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.NullProcessor;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.DB;
import com.mongodb.Mongo;

/**
 * User: hitender
 * Date: 2/19/14 11:51 PM
 */
public abstract class AbstractMongoDBTest {
    private static final class BasicExecutableNaming implements ITempNaming {
        @Override
        public String nameFor(String prefix, String postfix) {
            return Command.MongoD.commandName();
        }
    }

    private static final String PROCESS_ADDRESS = "localhost";
    private static final int PROCESS_PORT = 12345;

    private static MongodExecutable mongodExecutable = null;
    private static MongodProcess mongodProcess = null;
    private static Mongo mongoClient = null;
    private MongoTemplate mongoTemplate;

    @BeforeClass
    public static void setUp() throws Exception {
        IStreamProcessor stream = new NullProcessor();
        MongodStarter runtime = MongodStarter.getInstance(new RuntimeConfigBuilder()
                .defaults(Command.MongoD)
                .processOutput(new ProcessOutput(stream, stream, stream))
                .artifactStore(new ArtifactStoreBuilder()
                        .defaults(Command.MongoD)
                        .executableNaming(new BasicExecutableNaming())
                        .build())
                .build());
        mongodExecutable = runtime.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(PROCESS_PORT, Network.localhostIsIPv6()))
                .build());

        mongodProcess = mongodExecutable.start();
        mongoClient = new Mongo(PROCESS_ADDRESS, PROCESS_PORT);
    }

    @AfterClass
    public static void tearDown() {
        mongodProcess.stop();
        mongodExecutable.stop();
    }

    protected Mongo getMongoClient() {
        return mongoClient;
    }

    protected DB getDb() {
        return mongoClient.getDB("receiptofi-test");
    }

    public MongoTemplate createDatastore(Class clazz, String databaseName) {
        if(mongoTemplate == null) {
            mongoTemplate = new MongoTemplate(mongoClient, databaseName);
        }
        if(!mongoTemplate.collectionExists(clazz)) {
            mongoTemplate.createCollection(clazz);
        }
        return mongoTemplate;
    }

    public MongoTemplate createDatastore(Class clazz) {
        return createDatastore(clazz, "receiptofi-test");
    }

    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}

package com.receiptofi;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import org.springframework.data.mongodb.core.MongoTemplate;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.DownloadConfigBuilder;
import de.flapdoodle.embed.mongo.config.ExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.extract.ITempNaming;
import de.flapdoodle.embed.process.extract.UUIDTempNaming;
import de.flapdoodle.embed.process.io.IStreamProcessor;
import de.flapdoodle.embed.process.io.NullProcessor;
import de.flapdoodle.embed.process.io.directories.FixedPath;
import de.flapdoodle.embed.process.io.directories.IDirectory;
import de.flapdoodle.embed.process.runtime.Network;
import de.flapdoodle.embed.process.store.IArtifactStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * User: hitender
 * Date: 2/19/14 11:51 PM
 */
public abstract class RealMongoForTests {
    private static MongodExecutable mongodExecutable = null;
    private static MongodProcess mongodProcess = null;
    private static MongoClient mongo = null;
    private static final String DATABASE_NAME = "rm-i-test";

    private MongoTemplate mongoTemplate;

    @BeforeClass
    public static void setUp() throws Exception {
        Command command = Command.MongoD;

        int port = Network.getFreeServerPort();
        String host = "localhost";

        IDirectory artifactStorePath = new FixedPath(System.getProperty("user.home") + "/.embeddedMongodbCustomPath");
        ITempNaming executableNaming = new UUIDTempNaming();
        IStreamProcessor stream = new NullProcessor();
        IArtifactStore artifactStore = new ExtractedArtifactStoreBuilder()
                .defaults(command)
                .download(
                        new DownloadConfigBuilder()
                                .defaultsForCommand(command)
                                .artifactStorePath(artifactStorePath)
                )
                .executableNaming(executableNaming)
                .build();

        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(command)
                .processOutput(new ProcessOutput(stream, stream, stream))
                .artifactStore(artifactStore)
                .build();

        MongodStarter runtime = MongodStarter.getInstance(runtimeConfig);

        mongodExecutable = runtime.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build());

        mongodProcess = mongodExecutable.start();
        mongo = new MongoClient(host, port);
    }

    @AfterClass
    public static void tearDown() {
        /** On stop and delete, jenkins test fails as its not able to delete these files. */
        //mongodProcess.stop();
        //mongodExecutable.stop();
    }

    protected MongoTemplate getMongoTemplate() {
        if (null == mongoTemplate) {
            mongoTemplate = new MongoTemplate(mongo, DATABASE_NAME);
        }
        return mongoTemplate;
    }

    protected DBCollection getCollection(String name) {
        return getDB().getCollection(name);
    }

    protected DB getDB() {
        return mongo.getDB(DATABASE_NAME);
    }

    protected MongoCollection<Document> getCollectionNew(String name) {
        return getMongoDatabase().getCollection(name);
    }

    protected MongoDatabase getMongoDatabase() {
        return mongo.getDatabase(DATABASE_NAME);
    }
}

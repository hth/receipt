package com.receiptofi.repository;

import com.receiptofi.domain.FileSystemEntity;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

import static com.receiptofi.repository.util.AppendAdditionalFields.update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.WriteResult;

/**
 * User: hitender
 * Date: 12/23/13 9:21 PM
 */
public final class FileSystemManagerImpl implements FileSystemManager {
    private static final Logger log = LoggerFactory.getLogger(FileSystemManagerImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<FileSystemEntity> getAllObjects() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void save(FileSystemEntity object) throws Exception {
        if(object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public FileSystemEntity findOne(String id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), FileSystemEntity.class, TABLE);
    }

    @Override
    public WriteResult updateObject(String id, String name) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void deleteHard(FileSystemEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    public void deleteHard(Collection<FileSystemEntity> fileSystemEntities) {
        for(FileSystemEntity fileSystemEntity : fileSystemEntities) {
            deleteHard(fileSystemEntity);
        }
    }

    private void deleteSoft(FileSystemEntity fileSystemEntity) {
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("id").is(new ObjectId(fileSystemEntity.getId()))),
                update(Update.update("DELETE", true)),
                FileSystemEntity.class
        );
    }

    public void deleteSoft(Collection<FileSystemEntity> fileSystemEntities) {
        for(FileSystemEntity fileSystemEntity : fileSystemEntities) {
            deleteSoft(fileSystemEntity);
        }
    }

    @Override
    public void createCollection() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public void dropCollection() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public long collectionSize() {
        throw new UnsupportedOperationException("Method not implemented");
    }
}

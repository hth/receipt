package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.FileSystemEntity;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * User: hitender
 * Date: 12/23/13 9:21 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public final class FileSystemManagerImpl implements FileSystemManager {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            FileSystemEntity.class,
            Document.class,
            "collection");

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void save(FileSystemEntity object) {
        if (object.getId() != null) {
            object.setUpdated();
        }
        mongoTemplate.save(object, TABLE);
    }

    @Override
    public FileSystemEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), FileSystemEntity.class, TABLE);
    }

    @Override
    public void deleteHard(FileSystemEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    public void deleteHard(Collection<FileSystemEntity> fileSystemEntities) {
        for (FileSystemEntity fileSystemEntity : fileSystemEntities) {
            deleteHard(fileSystemEntity);
        }
    }

    private void deleteSoft(FileSystemEntity fileSystemEntity) {
        mongoTemplate.updateMulti(
                query(where("id").is(new ObjectId(fileSystemEntity.getId()))),
                entityUpdate(update("D", true)),
                FileSystemEntity.class
        );
    }

    public void deleteSoft(Collection<FileSystemEntity> fileSystemEntities) {
        for (FileSystemEntity fileSystemEntity : fileSystemEntities) {
            deleteSoft(fileSystemEntity);
        }
    }
}

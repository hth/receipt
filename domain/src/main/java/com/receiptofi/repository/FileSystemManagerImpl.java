package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static com.receiptofi.repository.util.AppendAdditionalFields.isActive;
import static com.receiptofi.repository.util.AppendAdditionalFields.isNotDeleted;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.value.DiskUsageGrouped;

import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

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
    public FileSystemEntity getById(String id) {
        Assert.hasText(id, "Id is empty");
        return mongoTemplate.findOne(query(where("id").is(id)), FileSystemEntity.class, TABLE);
    }

    @Override
    public void deleteHard(FileSystemEntity object) {
        mongoTemplate.remove(object, TABLE);
    }

    @Override
    public void deleteHard(Collection<FileSystemEntity> fileSystemEntities) {
        fileSystemEntities.forEach(this::deleteHard);
    }

    private void deleteSoft(String id) {
        mongoTemplate.updateMulti(
                query(where("id").is(new ObjectId(id))),
                entityUpdate(update("D", true)),
                FileSystemEntity.class
        );
    }

    @Override
    public void deleteSoft(Collection<FileSystemEntity> fileSystemEntities) {
        for (FileSystemEntity fileSystemEntity : fileSystemEntities) {
            deleteSoft(fileSystemEntity.getId());
        }
    }

    /**
     * Note: DO NOT USE GroupBy as it affects performance really bad. Like 1_000_000 times for GroupBy takes
     * 1 hour 45 minutes versus 20 minutes for the query below.
     *
     * @param rid
     * @return
     */
    @Override
    public List<DiskUsageGrouped> diskUsage(String rid) {
        TypedAggregation<FileSystemEntity> agg = newAggregation(FileSystemEntity.class,
                match(where("RID").is(rid)
                        .andOperator(
                                isActive(),
                                isNotDeleted()
                        )),
                group("rid")
                        .first("rid").as("rid")
                        .sum("fileLength").as("totalLN")
                        .sum("scaledFileLength").as("totalSLN")
        );

        return mongoTemplate.aggregate(agg, TABLE, DiskUsageGrouped.class).getMappedResults();
    }

    @Override
    public List<FileSystemEntity> filesPending(String rid) {
        return mongoTemplate.find(
                query(where("RID").is(rid).and("SLN").is(0)
                        .andOperator(
                            isActive(),
                            isNotDeleted()
                        )
                ),
                FileSystemEntity.class,
                TABLE);
    }
}

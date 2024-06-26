package com.receiptofi.repository;

import static com.receiptofi.repository.util.AppendAdditionalFields.entityUpdate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import com.mongodb.WriteResult;

import com.receiptofi.domain.BaseEntity;
import com.receiptofi.domain.CouponEntity;
import com.receiptofi.domain.types.CouponUploadStatusEnum;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: hitender
 * Date: 4/28/16 12:00 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Repository
public class CouponManagerImpl implements CouponManager {
    private static final Logger LOG = LoggerFactory.getLogger(CouponManagerImpl.class);
    private static final String TABLE = BaseEntity.getClassAnnotationValue(
            CouponEntity.class,
            Document.class,
            "collection");

    private MongoTemplate mongoTemplate;

    @Autowired
    public CouponManagerImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(CouponEntity object) {
        if (StringUtils.isNotBlank(object.getRid())) {
            if (object.getId() != null) {
                object.setUpdated();
            }
            mongoTemplate.save(object, TABLE);
        } else {
            LOG.error("Cannot save Coupon without rid");
            throw new RuntimeException("Missing user info for coupon");
        }
    }

    @Override
    public CouponEntity findOne(String id) {
        return mongoTemplate.findOne(query(where("id").is(id)), CouponEntity.class, TABLE);
    }

    @Override
    public void deleteHard(CouponEntity object) {
        throw new UnsupportedOperationException("This method is not supported");
    }

    @Override
    public List<CouponEntity> findCouponToUpload(int limit) {
        return mongoTemplate.find(
                query(where("CU").is(CouponUploadStatusEnum.A)).limit(limit),
                /** If have to use FS exists true then there is a bug. */
                /* query(
                        where("CU").is(CouponUploadStatusEnum.A)
                        .and("FS").exists(true)
                ).limit(limit), */
                CouponEntity.class,
                TABLE
        );
    }

    @Override
    public void cloudUploadSuccessful(String id, String imagePathOnCloud) {
        mongoTemplate.updateFirst(
                query(where("id").is(id)),
                entityUpdate(
                        update("CU", CouponUploadStatusEnum.C)
                                .set("IP", imagePathOnCloud)
                                .unset("LID")
                ),
                CouponEntity.class,
                TABLE
        );
    }

    @Override
    public void markCampaignCouponsInactive(String campaignId) {
        WriteResult writeResult = mongoTemplate.updateMulti(
                query(where("IF").is(campaignId)),
                entityUpdate(update("A", false)),
                CouponEntity.class,
                TABLE
        );

        LOG.info("Marked inactive coupon count={} campaignId={}", writeResult.getN(), campaignId);
    }
}

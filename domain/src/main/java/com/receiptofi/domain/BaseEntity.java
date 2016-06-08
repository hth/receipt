/**
 *
 */
package com.receiptofi.domain;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Date;

/**
 * @author hitender
 * @since Dec 23, 2012 2:02:10 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
public abstract class BaseEntity implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(BaseEntity.class);

    @Id
    protected String id;

    @Version
    @Field ("V")
    private Integer version;

    @Field ("U")
    private Date updated = new Date();

    @Field ("C")
    private Date created = new Date();

    @Field ("A")
    private boolean active = true;

    @Field ("D")
    private boolean deleted = false;

    public BaseEntity() {
        super();
    }

    /**
     * http://thierrywasyl.wordpress.com/2011/05/12/get-annotations-fields-value-easily/
     *
     * @param classType
     * @param annotationType
     * @param attributeName
     * @return Collection Name
     */
    @SuppressWarnings ("rawtypes")
    public static String getClassAnnotationValue(Class<?> classType, Class annotationType, String attributeName) {
        String value = null;

        @SuppressWarnings ("unchecked")
        Annotation annotation = classType.getAnnotation(annotationType);
        if (null != annotation) {
            try {
                value = (String) annotation.annotationType().getMethod(attributeName).invoke(annotation);
            } catch (Exception annotationException) {
                LOG.error("annotation reading error={}", annotationException);
            }
        }

        return value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    private void setActive(boolean active) {
        this.active = active;
    }

    public void active() {
        setActive(true);
    }

    public void inActive() {
        setActive(false);
    }

    public boolean isDeleted() {
        return deleted;
    }

    private void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void markAsDeleted() {
        setDeleted(true);
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @DateTimeFormat (iso = ISO.NONE)
    public Date getUpdated() {
        return updated;
    }

    public void setUpdated() {
        this.updated = new Date();
    }

    @DateTimeFormat (iso = ISO.NONE)
    public Date getCreated() {
        return created;
    }

    /**
     * Can be switched to use new ObjectId(id).getDate(); to get the create date from ObjectId.
     *
     * @param created
     */
    @Deprecated
    public void setCreated(Date created) {
        this.created = created;
    }

    public void setCreateAndUpdate(Date created) {
        this.created = created;
        this.updated = created;
    }
}

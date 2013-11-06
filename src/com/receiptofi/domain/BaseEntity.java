/**
 *
 */
package com.receiptofi.domain;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import org.joda.time.DateTime;

import com.receiptofi.utils.DateUtil;

/**
 * @author hitender
 * @since Dec 23, 2012 2:02:10 AM
 *
 */
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -5848946567869042567L;

	@Id
	protected String id;

	@Version
    @Field("VERSION")
	private Integer version;

	@DateTimeFormat(iso = ISO.DATE_TIME)
    @Field("UPDATE")
	private Date updated = DateUtil.nowTime();

	@DateTimeFormat(iso = ISO.DATE_TIME)
    @Field("CREATE")
	private Date created = DateUtil.nowTime();

    @Field("ACTIVE")
    private boolean active = true;

    @Field("DELETE")
    private boolean deleted = false;

	public BaseEntity() {
		super();
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

	@DateTimeFormat(iso = ISO.NONE)
	public Date getUpdated() {
		return updated;
	}

	public void setUpdated() {
		this.updated = DateTime.now().toDate();
	}

	@DateTimeFormat(iso = ISO.NONE)
	public Date getCreated() {
		return created;
	}

    @Deprecated
	public void setCreated(Date created) {
		this.created = created;
	}

    public void setCreateAndUpdate(Date created) {
        this.created = created;
        this.updated = created;
    }

	/**
	 *
	 * http://thierrywasyl.wordpress.com/2011/05/12/get-annotations-fields-value-easily/
	 *
	 * @param classType
	 * @param annotationType
	 * @param attributeName
	 * @return Collection Name
	 */
	@SuppressWarnings("rawtypes")
	public static String getClassAnnotationValue(Class<?> classType, Class annotationType, String attributeName) {
		String value = null;

		@SuppressWarnings("unchecked")
		Annotation annotation = classType.getAnnotation(annotationType);
		if (annotation != null) {
			try {
				value = (String) annotation.annotationType().getMethod(attributeName).invoke(annotation);
			} catch (Exception ex) {
			}
		}

		return value;
	}

}

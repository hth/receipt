/**
 *
 */
package com.tholix.domain;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import org.joda.time.DateTime;

import com.tholix.utils.DateUtil;

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
	private Integer version;

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date updated = DateUtil.nowTime();

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date created = DateUtil.nowTime();

    private boolean active = true;

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

    public void setActive(boolean active) {
        this.active = active;
    }

    public void active() {
        setActive(true);
    }

    public void inActive() {
        setActive(false);
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

	public void setCreated(Date created) {
		this.created = created;
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

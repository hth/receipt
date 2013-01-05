/**
 * 
 */
package com.tholix.domain;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Version;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

/**
 * @author hitender
 * @when Dec 23, 2012 2:02:10 AM
 * 
 */
public abstract class BaseEntity implements Serializable {
	private static final long serialVersionUID = -5848946567869042567L;

	@Id
	protected String id;
	
	@Version
	private Long version;
	
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date updated = DateTime.now().toDate();

	@DateTimeFormat(iso = ISO.DATE_TIME)
	private Date created = DateTime.now().toDate();

	public BaseEntity() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}	

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated() {
		this.updated = DateTime.now().toDate();
	}

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

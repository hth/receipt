/**
 * 
 */
package com.tholix.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
 * @author hitender
 * @when Dec 23, 2012 2:02:10 AM
 * 
 */
public abstract class BaseEntity implements Serializable {
	
	private static final long serialVersionUID = -5848946567869042567L;
	
	@Id
	protected String id;
	private Long version;

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

}

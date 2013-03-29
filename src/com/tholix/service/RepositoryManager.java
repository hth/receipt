/**
 * 
 */
package com.tholix.service;

import java.io.Serializable;
import java.util.List;

import com.mongodb.WriteResult;

/**
 * @author hitender
 * @when Dec 22, 2012 8:56:01 PM
 * @link http://orangeslate.com/2012/07/11/step-by-step-guide-to-create-a-sample-crud-java-application-using-mongodb-and-spring-data-for-mongodb/
 */
public interface RepositoryManager<T> extends Serializable {

	/**
	 * Get all records.
	 */
	public List<T> getAllObjects();

	/**
	 * Saves a record.
	 * 
	 * @throws Exception
	 */
	public void saveObject(T object) throws Exception;

	/**
	 * Gets a record for a particular id.
	 */
	public T getObject(String id);

	/**
	 * Updates a record name for a particular id.
	 */
	public WriteResult updateObject(String id, String name);

	/**
	 * Delete a record for a particular id.
	 */
	public void deleteObject(String id);

	/**
	 * Create a collection if the collection does not already exists.
	 */
	public void createCollection();

	/**
	 * Drops the collection if the collection does already exists.
	 */
	public void dropCollection();
}

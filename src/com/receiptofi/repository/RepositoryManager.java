/**
 *
 */
package com.receiptofi.repository;

import java.io.Serializable;
import java.util.List;

import com.mongodb.WriteResult;

/**
 * @author hitender
 * @since Dec 22, 2012 8:56:01 PM
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
	public void save(T object) throws Exception;

	/**
	 * Gets a record for a particular id.
	 */
	public T findOne(String id);

	/**
	 * Updates a record name for a particular id.
	 */
	public WriteResult updateObject(String id, String name);

	/**
	 * Delete a record for a particular object.
	 */
	public void deleteHard(T object);

    /**
     * Collection size
     */
    public long collectionSize();
}

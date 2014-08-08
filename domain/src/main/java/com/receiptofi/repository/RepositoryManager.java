/**
 *
 */
package com.receiptofi.repository;

import java.io.Serializable;
import java.util.List;

/**
 * @author hitender
 * @link http://orangeslate.com/2012/07/11/step-by-step-guide-to-create-a-sample-crud-java-application-using-mongodb-and-spring-data-for-mongodb/
 * @since Dec 22, 2012 8:56:01 PM
 */
public interface RepositoryManager<T> extends Serializable {

    /** Get all records. */
    List<T> getAllObjects();

    /**
     * Saves a record.
     *
     * @throws Exception
     */
    void save(T object);

    /** Gets a record for a particular id. */
    T findOne(String id);

    /** Delete a record for a particular object. */
    void deleteHard(T object);

    /** Collection size */
    long collectionSize();
}

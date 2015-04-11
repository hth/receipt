/**
 *
 */
package com.receiptofi.repository;

import java.io.Serializable;

/**
 * @author hitender
 * @link http://orangeslate.com/2012/07/11/step-by-step-guide-to-create-a-sample-crud-java-application-using-mongodb-and-spring-data-for-mongodb/
 * @since Dec 22, 2012 8:56:01 PM
 */
public interface RepositoryManager<T> extends Serializable {

    /**
     * Saves a record.
     *
     * @throws Exception
     */
    void save(T object);

    /**
     * Delete a record for a particular object.
     */
    void deleteHard(T object);
}

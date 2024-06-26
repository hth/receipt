/**
 *
 */
package com.receiptofi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * @author hitender
 * @since Mar 31, 2013 2:24:33 AM
 * {@refer http://docs.spring.io/spring-data/data-mongo/docs/1.2.0.RELEASE/reference/htmlsingle/}
 */
public interface PagingAndSortingRepositoryManager<T extends Serializable> extends RepositoryManager<T> {

    Iterable<T> findAll(Sort sort);

    Page<T> findAll(Pageable pageable);
}

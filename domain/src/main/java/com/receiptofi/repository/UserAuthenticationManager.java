/**
 *
 */
package com.receiptofi.repository;

import com.receiptofi.domain.UserAuthenticationEntity;

import java.util.List;

/**
 * @author hitender
 * @since Dec 16, 2012 1:20:31 PM
 */
public interface UserAuthenticationManager extends RepositoryManager<UserAuthenticationEntity> {
    UserAuthenticationEntity getById(String id);

    List<UserAuthenticationEntity> getAll();
}

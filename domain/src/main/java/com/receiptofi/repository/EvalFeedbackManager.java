package com.receiptofi.repository;

import com.receiptofi.domain.EvalFeedbackEntity;

import java.util.List;

/**
 * User: hitender
 * Date: 7/20/13
 * Time: 5:37 PM
 */
public interface EvalFeedbackManager extends RepositoryManager<EvalFeedbackEntity> {

    List<EvalFeedbackEntity> latestFeedback(int limit);

    long collectionSize();
}

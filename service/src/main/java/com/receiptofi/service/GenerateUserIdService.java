package com.receiptofi.service;

import com.receiptofi.repository.GenerateUserIdManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: hitender
 * Date: 8/14/16 1:45 AM
 */
@Service
public class GenerateUserIdService {

    private GenerateUserIdManager generateUserIdManager;
    private SkippedRidsService skippedRidsService;

    @Autowired
    public GenerateUserIdService(GenerateUserIdManager generateUserIdManager, SkippedRidsService skippedRidsService) {
        this.generateUserIdManager = generateUserIdManager;
        this.skippedRidsService = skippedRidsService;
    }

    public String getNextAutoGeneratedUserId() {
        return skippedRidsService.hasFoundSkippedRids()
                ? skippedRidsService.getNextAutoGeneratedUserId() :
                generateUserIdManager.getNextAutoGeneratedUserId();
    }
}

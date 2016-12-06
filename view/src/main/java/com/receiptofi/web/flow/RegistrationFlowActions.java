package com.receiptofi.web.flow;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BizStoreEntity;
import com.receiptofi.domain.flow.Register;
import com.receiptofi.domain.shared.DecodedAddress;
import com.receiptofi.service.BizService;
import com.receiptofi.service.ExternalService;
import com.receiptofi.utils.CommonUtil;

/**
 * User: hitender
 * Date: 8/5/16 7:32 AM
 */
class RegistrationFlowActions {

    private ExternalService externalService;
    private BizService bizService;

    RegistrationFlowActions(ExternalService externalService, BizService bizService) {
        this.externalService = externalService;
        this.bizService = bizService;
    }

    @SuppressWarnings ("unused")
    public void updateProfile(Register register) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(register.getRegisterUser().getAddress()), register.getRegisterUser().getAddress());
        if (decodedAddress.isNotEmpty()) {
            register.getRegisterUser().setAddress(decodedAddress.getFormattedAddress());
            register.getRegisterUser().setCountryShortName(decodedAddress.getCountryShortName());
        }
        register.getRegisterUser().setPhone(CommonUtil.phoneCleanup(register.getRegisterUser().getPhone()));
    }

    @SuppressWarnings ("unused")
    public void updateBusiness(Register register) {
        DecodedAddress decodedAddress = DecodedAddress.newInstance(externalService.getGeocodingResults(register.getRegisterBusiness().getAddress()), register.getRegisterBusiness().getAddress());
        if (decodedAddress.isNotEmpty()) {
            register.getRegisterBusiness().setAddress(decodedAddress.getFormattedAddress());
            register.getRegisterBusiness().setCountryShortName(decodedAddress.getCountryShortName());
        }
        register.getRegisterBusiness().setPhone(CommonUtil.phoneCleanup(register.getRegisterBusiness().getPhone()));
    }

    private void validateAddress(BizStoreEntity bizStore) {
        if (null == bizStore.getId() || !bizStore.isValidatedUsingExternalAPI()) {
            externalService.decodeAddress(bizStore);
        }
    }

    BizNameEntity registerBusinessDetails(Register register) {
        BizNameEntity bizName = bizService.findMatchingBusiness(register.getRegisterBusiness().getName());
        if (null == bizName) {
            bizName = BizNameEntity.newInstance();
            bizName.setBusinessName(register.getRegisterBusiness().getName());
        }
        bizName.setBusinessTypes(register.getRegisterBusiness().getBusinessTypes());
        bizService.saveName(bizName);

        BizStoreEntity bizStore = bizService.findMatchingStore(
                register.getRegisterBusiness().getAddress(),
                register.getRegisterBusiness().getBusinessPhoneNotFormatted());
        if (bizStore == null) {
            bizStore = BizStoreEntity.newInstance();
            bizStore.setBizName(bizName);
            bizStore.setPhone(register.getRegisterBusiness().getPhone());
            bizStore.setAddress(register.getRegisterBusiness().getAddress());
            validateAddress(bizStore);
            bizService.saveStore(bizStore);
        }
        return bizName;
    }
}

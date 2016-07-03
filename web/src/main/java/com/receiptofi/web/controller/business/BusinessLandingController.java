package com.receiptofi.web.controller.business;

import static com.receiptofi.web.controller.access.LandingController.SUCCESS;

import com.google.gson.JsonObject;

import com.receiptofi.domain.BizNameEntity;
import com.receiptofi.domain.BusinessCampaignEntity;
import com.receiptofi.domain.BusinessUserEntity;
import com.receiptofi.domain.FileSystemEntity;
import com.receiptofi.domain.analytic.BizDimensionEntity;
import com.receiptofi.domain.shared.UploadDocumentImage;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.domain.types.FileTypeEnum;
import com.receiptofi.service.BusinessCampaignService;
import com.receiptofi.service.BusinessUserService;
import com.receiptofi.service.ImageSplitService;
import com.receiptofi.service.analytic.BizDimensionService;
import com.receiptofi.utils.Maths;
import com.receiptofi.web.form.business.BusinessLandingForm;
import com.receiptofi.web.form.business.CampaignListForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;

/**
 * For Businesses.
 * User: hitender
 * Date: 5/13/16 1:34 PM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/business")
public class BusinessLandingController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLandingController.class);

    private String nextPage;
    private String businessRegistrationFlow;

    private BusinessUserService businessUserService;
    private BizDimensionService bizDimensionService;
    private BusinessCampaignService businessCampaignService;
    private ImageSplitService imageSplitService;

    @Autowired
    public BusinessLandingController(
            @Value ("${nextPage:/business/landing}")
            String nextPage,

            @Value ("${businessRegistrationFlow:redirect:/business/registration.htm}")
            String businessRegistrationFlow,

            BusinessUserService businessUserService,
            BizDimensionService bizDimensionService,
            BusinessCampaignService businessCampaignService,
            ImageSplitService imageSplitService) {
        this.nextPage = nextPage;
        this.businessRegistrationFlow = businessRegistrationFlow;
        this.businessUserService = businessUserService;
        this.bizDimensionService = bizDimensionService;
        this.businessCampaignService = businessCampaignService;
        this.imageSplitService = imageSplitService;
    }

    /**
     * Loading landing page for business.
     *
     * @param businessLandingForm
     * @return
     */
    @PreAuthorize ("hasAnyRole('ROLE_BUSINESS')")
    @RequestMapping (value = "/landing", method = RequestMethod.GET)
    public String loadForm(@ModelAttribute ("businessLandingForm") BusinessLandingForm businessLandingForm) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LOG.info("Landed on business page rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
        BusinessUserEntity businessUser = businessUserService.findBusinessUser(receiptUser.getRid());
        return nextPage(receiptUser, businessUser, businessLandingForm);
    }

    private String nextPage(
            ReceiptUser receiptUser,
            BusinessUserEntity businessUser,
            BusinessLandingForm businessLandingForm) {
        switch (businessUser.getBusinessUserRegistrationStatus()) {
            case V:
                populateBusinessLandingForm(businessLandingForm, businessUser);
                return nextPage;
            case C:
            case I:
            case N:
                LOG.info("Business Registration rid={} level={}", receiptUser.getRid(), receiptUser.getUserLevel());
                return businessRegistrationFlow;
            default:
                LOG.error("Reached unsupported condition={}", businessUser.getBusinessUserRegistrationStatus());
                throw new UnsupportedOperationException("Reached unsupported condition " + businessUser.getBusinessUserRegistrationStatus());
        }
    }

    private void populateBusinessLandingForm(BusinessLandingForm businessLandingForm, BusinessUserEntity businessUser) {
        Assert.notNull(businessUser, "Business user should not be null");
        BizNameEntity bizName = businessUser.getBizName();
        String bizNameId = bizName.getId();
        LOG.info("Loading dashboard for bizName={} bizId={}", bizName.getBusinessName(), bizName.getId());

        BizDimensionEntity bizDimension = bizDimensionService.findBy(bizNameId);
        if (null != bizDimension) {
            businessLandingForm.setBizName(bizDimension.getBizName())
                    .setCustomerCount(bizDimension.getUserCount())
                    .setStoreCount(bizDimension.getStoreCount())
                    .setTotalCustomerPurchases(Maths.adjustScale(bizDimension.getBizTotal()))
                    .setVisitCount(bizDimension.getVisitCount())
                    .setCampaignListForm(new CampaignListForm().setBusinessCampaigns(businessCampaignService.findBy(bizNameId)));
        }
    }

    /**
     * For uploading campaign coupon.
     *
     * @throws IOException
     */
    @SuppressWarnings ("unused")
    @RequestMapping (
            method = RequestMethod.POST,
            value = "/upload")
    @ResponseBody
    public String uploadCoupon(
            @RequestParam ("campaignId")
            String campaignId,

            @RequestParam ("bizId")
            String bizId,

            @RequestPart ("qqfile")
            MultipartFile multipartFile
    ) throws IOException {
        String rid = ((ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRid();
        LOG.info("Uploading business coupon rid={} campaignId={}", rid, campaignId);

        if (multipartFile.isEmpty()) {
            LOG.error("Empty or no coupon uploaded");
            throw new RuntimeException("Empty or no coupon uploaded");
        }

        UploadDocumentImage image = UploadDocumentImage.newInstance(FileTypeEnum.C)
                .setFileData(multipartFile)
                .setRid(rid);

        JsonObject jsonObject = new JsonObject();
        BufferedImage bufferedImage = imageSplitService.bufferedImage(image.getFileData().getInputStream());
        if (bufferedImage.getWidth() > 600) {
            jsonObject.addProperty(SUCCESS, false);
            jsonObject.addProperty("reason", "<sup>*</sup> Uploaded image width greater than 600px");
            return jsonObject.toString();
        }

        BusinessCampaignEntity businessCampaign = businessCampaignService.findById(campaignId, bizId);
        Collection<FileSystemEntity> fileSystems = businessCampaignService.deleteAndCreateNewImage(
                bufferedImage,
                image,
                businessCampaign.getFileSystemEntities());

        businessCampaign.setFileSystemEntities(fileSystems);
        businessCampaignService.save(businessCampaign);

        LOG.info("Upload complete rid={}", rid);
        jsonObject.addProperty(SUCCESS, true);
        return jsonObject.toString();
    }
}

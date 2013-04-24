/**
 *
 */
package com.tholix.web;

import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import org.joda.time.DateTime;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.service.BizNameManager;
import com.tholix.service.BizStoreManager;
import com.tholix.service.ItemManager;
import com.tholix.service.ItemOCRManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.ReceiptOCRManager;
import com.tholix.service.routes.MessageManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;
import com.tholix.web.form.ReceiptForm;
import com.tholix.web.validator.ReceiptFormValidator;

/**
 * @author hitender
 * @when Jan 7, 2013 2:13:22 AM
 *
 */
@Controller
@RequestMapping(value = "/emp")
public class ReceiptUpdateController {
	private static final Logger log = Logger.getLogger(ReceiptUpdateController.class);

	private static final String nextPage = "receiptupdate";

	@Autowired private ReceiptManager receiptManager;
	@Autowired private ItemManager itemManager;
	@Autowired private ReceiptOCRManager receiptOCRManager;
	@Autowired private ItemOCRManager itemOCRManager;
	@Autowired private ReceiptFormValidator receiptFormValidator;
    @Autowired private MessageManager messageManager;
    @Autowired private BizNameManager bizNameManager;
    @Autowired private BizStoreManager bizStoreManager;
    @Autowired private AdminLandingController adminLandingController;

//	@InitBinder
//	public void initBinder(WebDataBinder binder) {
//	    binder.setDisallowedFields("administrator");
//	}

//  Refer http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/mvc.html
//	@RequestMapping(value="/owners/{ownerId}/pets/{petId}", method=RequestMethod.GET)
//	public String findPet(@PathVariable String ownerId, @PathVariable String petId, Model model) {
//	  Owner owner = ownerService.findOwner(ownerId);
//	  Pet pet = owner.getPet(petId);
//	  model.addAttribute("pet", pet);
//	  return "displayPet";
//	}

	@RequestMapping(value = "/receiptupdate", method = RequestMethod.GET)
	public ModelAndView loadForm(@RequestParam("id") String id, @ModelAttribute("receiptForm") ReceiptForm receiptForm) {
        DateTime time = DateUtil.now();
        ReceiptEntityOCR receipt = receiptOCRManager.findOne(id);
		receiptForm.setReceipt(receipt);

		List<ItemEntityOCR> items = itemOCRManager.getWhereReceipt(receipt);
		receiptForm.setItems(items);

        PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName());
		return  new ModelAndView(nextPage);
	}

	@RequestMapping(value = "/receiptupdate", method = RequestMethod.POST)
	public String post(@ModelAttribute("receiptForm") ReceiptForm receiptForm, BindingResult result) {
        DateTime time = DateUtil.now();
        log.info("Turk processing a receipt " + receiptForm.getReceipt().getId() + " ; Title : " + receiptForm.getReceipt().getBizName().getName());
		receiptFormValidator.validate(receiptForm, result);
		if (result.hasErrors()) {
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in result");
            return nextPage;
		} else {
			ReceiptEntity receipt = null;
			List<ItemEntity> items;
			try {
				receipt = receiptForm.getReceiptEntity();
                adminLandingController.saveNewBusinessAndOrStore(receipt);
				receiptManager.save(receipt);
				items = receiptForm.getItemEntity(receipt);
				itemManager.saveObjects(items);

				ReceiptEntityOCR receiptEntityOCR = receiptForm.getReceipt();
				receiptEntityOCR.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);
                adminLandingController.saveNewBusinessAndOrStore(receiptEntityOCR);
				receiptOCRManager.save(receiptEntityOCR);

                try {
                    messageManager.updateObject(receiptEntityOCR.getId());
                } catch(Exception exce) {
                    log.error(exce.getLocalizedMessage());
                    messageManager.updateObject(receiptEntityOCR.getId(), false);
                    throw exce;
                }

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "success");
                return "redirect:/emp/landing.htm";
			} catch(Exception exce) {
				log.error(exce.getLocalizedMessage());
				result.rejectValue("receipt.receiptDate", exce.getLocalizedMessage(), exce.getLocalizedMessage());

				int sizeReceiptInitial = receiptManager.getAllObjects().size();
				if(receipt != null) {
					receiptManager.delete(receipt);
					itemManager.deleteWhereReceipt(receipt);
				}
				int sizeReceiptFinal = receiptManager.getAllObjects().size();
				log.info("Initial size: " + sizeReceiptInitial + ", Final size: " + sizeReceiptFinal);

                PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error in receipt save");
                return nextPage;
			}
		}
	}

}

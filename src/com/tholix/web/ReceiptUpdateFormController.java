/**
 * 
 */
package com.tholix.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tholix.domain.ItemEntity;
import com.tholix.domain.ItemEntityOCR;
import com.tholix.domain.ReceiptEntity;
import com.tholix.domain.ReceiptEntityOCR;
import com.tholix.domain.UserSession;
import com.tholix.domain.types.ReceiptStatusEnum;
import com.tholix.service.ItemManager;
import com.tholix.service.ItemOCRManager;
import com.tholix.service.ReceiptManager;
import com.tholix.service.ReceiptOCRManager;
import com.tholix.service.validator.ReceiptFormValidator;
import com.tholix.web.form.ReceiptForm;

/**
 * @author hitender 
 * @when Jan 7, 2013 2:13:22 AM
 *
 */
@Controller
@RequestMapping(value = "/receiptupdate")
public class ReceiptUpdateFormController {
	private final Log log = LogFactory.getLog(getClass());

	private String nextPage = "receiptupdate";

	@Autowired private ReceiptManager receiptManager;
	@Autowired private ItemManager itemManager;
	@Autowired private ReceiptOCRManager receiptOCRManager;	
	@Autowired private ItemOCRManager itemOCRManager;
	@Autowired private ReceiptFormValidator receiptFormValidator;
	
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

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loadForm(@RequestParam("id") String id, @ModelAttribute("receiptForm") ReceiptForm receiptForm) {
		ReceiptEntityOCR receipt = receiptOCRManager.getObject(id);		
		receiptForm.setReceipt(receipt);
		
		List<ItemEntityOCR> items = itemOCRManager.getObjectWithRecipt(receipt);	
		receiptForm.setItems(items);
		
		return  new ModelAndView(nextPage);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(@ModelAttribute("receiptForm") ReceiptForm receiptForm, HttpSession session, BindingResult result, final RedirectAttributes redirectAttrs) {
		log.info("Turk processing a receipt " + receiptForm.getReceipt().getId() + " ; Title : " + receiptForm.getReceipt().getTitle());
		receiptFormValidator.validate(receiptForm, result);
		if (result.hasErrors()) {
			return nextPage;
		} else {
			try {
				ReceiptEntity receipt = receiptForm.getReceiptEntity();
				receiptManager.saveObject(receipt);
				List<ItemEntity> items = receiptForm.getItemEntity(receipt);			
				itemManager.saveObjects(items);
				
				ReceiptEntityOCR receiptEntityOCR = receiptForm.getReceipt();
				receiptEntityOCR.setReceiptStatus(ReceiptStatusEnum.TURK_PROCESSED);			
				receiptOCRManager.saveObject(receiptEntityOCR);
				
				UserSession userSession = (UserSession) session.getAttribute("userSession");
				redirectAttrs.addFlashAttribute("userSession", userSession);
	
				return "redirect:/landing.htm";
			} catch(Exception exce) {
				log.error(exce.getLocalizedMessage());
				result.rejectValue("receipt.receiptDate", exce.getLocalizedMessage(), exce.getLocalizedMessage());
				return nextPage;
			}
		}		
	}
	
}

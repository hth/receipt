/**
 *
 */
package com.tholix.web;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.gridfs.GridFSDBFile;
import org.joda.time.DateTime;

import com.tholix.domain.UserSession;
import com.tholix.repository.StorageManager;
import com.tholix.utils.DateUtil;
import com.tholix.utils.PerformanceProfiling;

/**
 * @author hitender
 * @when Jan 6, 2013 8:21:54 PM
 *
 */
@Controller
@RequestMapping(value = "/receiptimage")
@SessionAttributes({"userSession"})
public class ReceiptImageController {
	private static final Logger log = Logger.getLogger(ReceiptImageController.class);

	@Autowired private StorageManager storageManager;

	/**
	 * This is used only to serve images of Receipt
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getReceipt(@RequestParam("id") String id, @ModelAttribute("userSession") UserSession userSession, HttpServletRequest request, HttpServletResponse response) {
        DateTime time = DateUtil.now();

		try {
			GridFSDBFile gridFSDBFile = storageManager.get(id);
			if(gridFSDBFile == null) {
				response.setContentType("image/gif");
				String pathToWeb = request.getServletContext().getRealPath(File.separator);
				File file = FileUtils.getFile(pathToWeb + "/images/no_image.gif");
				BufferedImage bi = ImageIO.read(file);
				OutputStream out = response.getOutputStream();
				ImageIO.write(bi, "gif", out);
				out.close();
			} else {
				gridFSDBFile.writeTo(response.getOutputStream());
				response.setContentType(gridFSDBFile.getContentType());
			}

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(),  true);
			return null;
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
			log.error("Image retrieval error occurred: " + id + " for user : " + userSession.getEmailId());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error fetching receipt");
			return null;
		}
	}

	public void setStorageManager(StorageManager storageManager) {
		this.storageManager = storageManager;
	}


}

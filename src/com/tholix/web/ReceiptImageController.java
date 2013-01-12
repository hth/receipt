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
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.gridfs.GridFSDBFile;
import com.tholix.domain.UserSession;
import com.tholix.service.StorageManager;

/**
 * @author hitender 
 * @when Jan 6, 2013 8:21:54 PM
 *
 */
@Controller
@RequestMapping(value = "/receiptimage")
public class ReceiptImageController {
	private final Log log = LogFactory.getLog(getClass());	
	
	@Autowired private StorageManager storageManager;

	/**
	 * This is used only to serve images of Receipt
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getReceipt(@RequestParam("id") String id, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		UserSession userSession = (UserSession) session.getAttribute("userSession");
		
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

			return null;
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
			log.error("Image retrival error occured: " + id + " for user : " + userSession.getEmailId());
			return null;
		} 
	}
}

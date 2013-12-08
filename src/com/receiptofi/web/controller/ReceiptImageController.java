/**
 *
 */
package com.receiptofi.web.controller;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.UserSession;
import com.receiptofi.service.FileDBService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Formatter;
import com.receiptofi.utils.PerformanceProfiling;
import com.receiptofi.web.scheduledtasks.FileSystemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.joda.time.DateTime;

import com.mongodb.gridfs.GridFSDBFile;

/**
 * @author hitender
 * @since Jan 6, 2013 8:21:54 PM
 *
 */
@Controller
@RequestMapping(value = "/receiptimage")
@SessionAttributes({"userSession"})
public class ReceiptImageController {
	private static final Logger log = LoggerFactory.getLogger(ReceiptImageController.class);

	@Autowired private FileDBService fileDBService;
    @Autowired private FileSystemProcessor fileSystemProcessor;
    @Autowired private ReceiptService receiptService;

	/**
	 * This is used only to serve images of Receipt
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public void getReceipt(@RequestParam("id") String imageId, @ModelAttribute("userSession") UserSession userSession, HttpServletRequest request, HttpServletResponse response) {
        DateTime time = DateUtil.now();

		try {
			GridFSDBFile gridFSDBFile = fileDBService.getFile(imageId);
            log.debug("Length: " + gridFSDBFile.getLength() + ", MetaData: " + gridFSDBFile.getMetaData());

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
		} catch (IOException e) {
			log.error("Exception occurred during image retrieval" + e.getLocalizedMessage());
			log.error("Image retrieval error occurred: " + imageId + " for user : " + userSession.getEmailId());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error fetching receipt");
		}
	}

    @RequestMapping(method = RequestMethod.GET, value = "/exp/{receiptId}")
    public void getReport(@PathVariable String receiptId, @ModelAttribute("userSession") UserSession userSession, HttpServletResponse response) {
        DateTime time = DateUtil.now();

        try {
            ReceiptEntity receiptEntity = receiptService.findReceipt(receiptId, userSession.getUserProfileId());
            setHeaderForExcel(receiptEntity, response);

            InputStream inputStream = new FileInputStream(fileSystemProcessor.getExcelFile(receiptEntity.getExpenseReportInFS()));
            IOUtils.copy(inputStream, response.getOutputStream());

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(),  true);
        } catch (IOException e) {
            log.error("Exception occurred during excel retrieval" + e.getLocalizedMessage());
            log.error("Excel retrieval error occurred: " + receiptId + " for user : " + userSession.getEmailId());
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error fetching receipt");
        }
    }

    private void setHeaderForExcel(ReceiptEntity receiptEntity, HttpServletResponse response) {
        response.addHeader("Content-Disposition", "inline; filename=" + receiptEntity.getBizName().getName() + "-" + Formatter.dateSmall(receiptEntity.getReceiptDate()));
        response.setContentType("application/vnd.ms-excel");
    }
}

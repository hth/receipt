/**
 *
 */
package com.receiptofi.web.controller.access;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.FileDBService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.utils.DateUtil;
import com.receiptofi.utils.Formatter;
import com.receiptofi.loader.scheduledtasks.FileSystemProcess;
import com.receiptofi.web.util.PerformanceProfiling;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import org.joda.time.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hitender
 * @since Jan 6, 2013 8:21:54 PM
 */
@Controller
@RequestMapping (value = "/access/filedownload")
public final class FileDownloadController {
    private static final Logger LOG = LoggerFactory.getLogger(FileDownloadController.class);

    @Autowired private FileDBService fileDBService;
    @Autowired private FileSystemProcess fileSystemProcess;
    @Autowired private ReceiptService receiptService;

    @Value ("${imageNotFoundPlaceHolder:/static/images/no_image.gif}")
    private String imageNotFound;

    /**
     * Servers images
     *
     * @param imageId
     * @param request
     * @param response
     */
    @RequestMapping (method = RequestMethod.GET, value = "/receiptimage/{imageId}")
    public void getDocumentImage(@PathVariable String imageId, HttpServletRequest request, HttpServletResponse response) {
        DateTime time = DateUtil.now();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            GridFSDBFile gridFSDBFile = fileDBService.getFile(imageId);

            if (gridFSDBFile == null) {
                LOG.warn("GridFSDBFile failed to find image={}", imageId);
                File file = FileUtils.getFile(request.getServletContext().getRealPath(File.separator) + imageNotFound);
                BufferedImage bi = ImageIO.read(file);
                setContentType(file.getName(), response);
                response.setHeader("Content-Length", String.valueOf(file.length()));
                response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
                OutputStream out = response.getOutputStream();
                ImageIO.write(bi, getFormatForImageIO(file.getName()), out);
                out.close();
            } else {
                LOG.debug("Length={} MetaData={}", gridFSDBFile.getLength(), gridFSDBFile.getMetaData());
                setContentType(gridFSDBFile.getFilename(), response);
                response.setHeader("Content-Length", String.valueOf(gridFSDBFile.getLength()));
                response.setHeader("Content-Disposition", "inline; filename=" + imageId + "." + FilenameUtils.getExtension(gridFSDBFile.getFilename()));
                gridFSDBFile.writeTo(response.getOutputStream());
            }

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), true);
        } catch (IOException e) {
            LOG.error("Image retrieval error occurred for imageId={} rid={} reason={}", imageId, receiptUser.getRid(), e.getLocalizedMessage(), e);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error fetching receipt");
        }
    }

    @RequestMapping (method = RequestMethod.GET, value = "/expensofi/{receiptId}")
    public void getReport(@PathVariable String receiptId, HttpServletResponse response) {
        DateTime time = DateUtil.now();
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            ReceiptEntity receiptEntity = receiptService.findReceipt(receiptId, receiptUser.getRid());
            setHeaderForExcel(receiptEntity, response);

            InputStream inputStream = new FileInputStream(fileSystemProcess.getExcelFile(receiptEntity.getExpenseReportInFS()));
            IOUtils.copy(inputStream, response.getOutputStream());

            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), true);
        } catch (IOException e) {
            LOG.error("Excel retrieval error occurred Receipt={} for user={} reason={}", receiptId, receiptUser.getRid(), e.getLocalizedMessage(), e);
            PerformanceProfiling.log(this.getClass(), time, Thread.currentThread().getStackTrace()[1].getMethodName(), "error fetching receipt");
        }
    }

    private void setHeaderForExcel(ReceiptEntity receiptEntity, HttpServletResponse response) {
        response.addHeader("Content-Disposition", "inline; filename=" + receiptEntity.getBizName().getBusinessName() + "-" + Formatter.toSmallDate(receiptEntity.getReceiptDate()));
        response.setContentType("application/vnd.ms-excel");
    }

    private void setContentType(String filename, HttpServletResponse response) {
        String extension = FilenameUtils.getExtension(filename);
        if (extension.endsWith("jpg") || extension.endsWith("jpeg")) {
            response.setContentType("image/jpeg");
        } else if (extension.endsWith("gif")) {
            response.setContentType("image/gif");
        } else {
            response.setContentType("image/png");
        }
    }

    private String getFormatForImageIO(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        if (extension.endsWith("jpeg")) {
            return "jpg";
        }
        return extension;
    }
}

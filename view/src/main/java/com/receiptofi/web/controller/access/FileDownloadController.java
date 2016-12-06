/**
 *
 */
package com.receiptofi.web.controller.access;

import com.mongodb.gridfs.GridFSDBFile;

import com.receiptofi.domain.ReceiptEntity;
import com.receiptofi.domain.site.ReceiptUser;
import com.receiptofi.service.FileDBService;
import com.receiptofi.service.ReceiptService;
import com.receiptofi.service.ftp.FtpService;
import com.receiptofi.utils.FileUtil;
import com.receiptofi.utils.Formatter;
import com.receiptofi.utils.ScrubbedInput;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

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
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Controller
@RequestMapping (value = "/access/filedownload")
public class FileDownloadController {
    private static final Logger LOG = LoggerFactory.getLogger(FileDownloadController.class);

    private FileDBService fileDBService;
    private ReceiptService receiptService;
    private FtpService ftpService;

    @Value ("${imageNotFoundPlaceHolder:/static/images/no_image.gif}")
    private String imageNotFound;

    @Autowired
    public FileDownloadController(FileDBService fileDBService, ReceiptService receiptService, FtpService ftpService) {
        this.fileDBService = fileDBService;
        this.receiptService = receiptService;
        this.ftpService = ftpService;
    }

    /**
     * Servers images
     *
     * @param imageId
     * @param request
     * @param response
     */
    @RequestMapping (method = RequestMethod.GET, value = "/receiptimage/{imageId}")
    public void getDocumentImage(
            @PathVariable
            ScrubbedInput imageId,

            HttpServletRequest request,
            HttpServletResponse response
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            GridFSDBFile gridFSDBFile = fileDBService.getFile(imageId.getText());

            if (null == gridFSDBFile) {
                LOG.warn("GridFSDBFile failed to find image={}", imageId);
                File file = FileUtils.getFile(request.getServletContext().getRealPath(File.separator) + imageNotFound);
                BufferedImage bi = ImageIO.read(file);
                setContentType(file.getName(), response);
                response.setHeader("Content-Length", String.valueOf(file.length()));
                response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
                OutputStream outputStream = response.getOutputStream();
                /** imageNotFound is of type gif */
                ImageIO.write(bi, FileUtil.getFileExtension(file.getName()), outputStream);
                outputStream.close();
            } else {
                LOG.debug("Length={} MetaData={}", gridFSDBFile.getLength(), gridFSDBFile.getMetaData());
                setContentType(gridFSDBFile.getFilename(), response);
                response.setHeader("Content-Length", String.valueOf(gridFSDBFile.getLength()));
                response.setHeader("Content-Disposition", "inline; filename=" + imageId + "." + FilenameUtils.getExtension(gridFSDBFile.getFilename()));
                gridFSDBFile.writeTo(response.getOutputStream());
            }
        } catch (IOException e) {
            LOG.error("Image retrieval failure occurred for imageId={} rid={} reason={}",
                    imageId, receiptUser.getRid(), e.getLocalizedMessage(), e);
        }
    }

    @RequestMapping (method = RequestMethod.GET, value = "/expensofi/{receiptId}")
    public void getReport(
            @PathVariable
            ScrubbedInput receiptId,

            HttpServletResponse response
    ) {
        ReceiptUser receiptUser = (ReceiptUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        InputStream inputStream = null;
        try {
            ReceiptEntity receipt = receiptService.findReceipt(receiptId.getText(), receiptUser.getRid());
            setHeaderForExcel(receipt, response);

            inputStream = ftpService.getFile(receipt.getExpenseReportInFS());
            if (inputStream == null) {
                receiptService.removeExpensofiFilenameReference(receipt.getExpenseReportInFS());
                LOG.info("Could find file removed reference to receiptId={} filename={}", receipt.getId(), receipt.getExpenseReportInFS());
            }
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (IOException e) {
            LOG.error("Excel retrieval error occurred Receipt={} for user={} reason={}",
                    receiptId, receiptUser.getRid(), e.getLocalizedMessage(), e);
        } finally {
            if (inputStream != null) {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    private void setHeaderForExcel(ReceiptEntity receiptEntity, HttpServletResponse response) {
        response.addHeader("Content-Disposition",
                "inline; filename="
                        + receiptEntity.getBizName().getBusinessName()
                        + "_"
                        + Formatter.toSmallDate(receiptEntity.getReceiptDate())
                        + ".xls"
        );
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
}

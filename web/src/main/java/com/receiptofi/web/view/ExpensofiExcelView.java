package com.receiptofi.web.view;

import com.receiptofi.domain.ItemEntity;
import com.receiptofi.web.helper.AnchorFileInExcel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This view generates an Excel report from receipt item objects.
 * User: hitender
 * Date: 11/30/13 2:45 AM
 */
@SuppressWarnings ({
        "PMD.BeanMembersShouldSerialize",
        "PMD.LocalVariableCouldBeFinal",
        "PMD.MethodArgumentCouldBeFinal",
        "PMD.LongVariable"
})
@Component
public final class ExpensofiExcelView extends AbstractExcelView {
    private static final Logger LOG = LoggerFactory.getLogger(ExpensofiExcelView.class);
    // Columns - width is measured in 256ths of an el and 1330 equals 1 cm
    private static final int UNIT = 1300;
    private static final HSSFCellStyle NO_STYLE = null;

    @Value ("${expensofiReportLocation}")
    private String expensofiReportLocation;

    @Value ("${ExpensofiExcelView.heading:Description,Date,Quantity,Tax,Price,Expense Type}")
    private String heading;

    /**
     * Description,Date,Quantity,Tax,Price,Expense Type.
     */
    @Value ("${ExpensofiExcelView.columnSize:4,3,2,2,3,3}")
    private String columnSize;

    public void generateExcel(Map<String, Object> model, HSSFWorkbook workbook) throws IOException {
        buildExcelDocument(model, workbook, null, null);
        persistWorkbookToFileSystem(workbook, (String) model.get("file-name"));
    }

    @Override
    @SuppressWarnings ("unchecked")
    protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) {
        HSSFSheet sheet = workbook.createSheet();

        List<ItemEntity> items = (ArrayList) model.get("items");
        if (null == items) {
            HSSFRow row = sheet.createRow(0);
            addToCell(row, 0, "Error creating spreadsheet", NO_STYLE);
            return;
        }
        setColumnWidth(sheet);
        setHeadings(workbook, sheet);

        // Other styles
        HSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));

        HSSFCellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));

        int nAccounts = items.size();

        // Content
        for (int i = 0; i < nAccounts; i++) {
            ItemEntity item = items.get(i);
            HSSFRow row = sheet.createRow(i + 1);
            addToCell(row, 0, item.getName(), NO_STYLE);
            addToCell(row, 1, item.getReceipt().getReceiptDate(), dateStyle);
            addToCell(row, 2, item.getQuantity(), NO_STYLE);
            addToCell(row, 3, item.getTotalTax().doubleValue(), moneyStyle);
            addToCell(row, 4, item.getTotalPriceWithoutTax().doubleValue(), moneyStyle);

            String cc = null;
            if (item.getExpenseTag() != null) {
                cc = item.getExpenseTag().getTagName();
            }
            addToCell(row, 5, null == cc ? "N/A" : cc, NO_STYLE);
        }

        // Totals
        HSSFRow row = sheet.createRow(nAccounts + 2);
        addToCell(row, 2, "SUM", NO_STYLE);
        addToCell(row, 3, "=sum(D2:D" + (nAccounts + 1) + ')', moneyStyle);
        addToCell(row, 4, "=sum(E2:E" + (nAccounts + 1) + ')', moneyStyle);

        row = sheet.createRow(nAccounts + 3);
        addToCell(row, 3, "TOTAL", NO_STYLE);
        addToCell(row, 4, "=sum(D" + (nAccounts + 3) + ":E" + (nAccounts + 3) + ')', moneyStyle);

        Collection<AnchorFileInExcel> anchorFileInExcelCollection = (Collection) model.get("to_be_anchored_files");
        for (AnchorFileInExcel anchorFileInExcel : anchorFileInExcelCollection) {
            //Add receipt image
            byte[] imageBytes = anchorFileInExcel.getBytes();
            String imageContentType = anchorFileInExcel.getContentType();
            anchorReceiptImage(imageBytes, imageContentType, workbook, sheet, row);
        }
    }

    /**
     * Populate excel headings.
     *
     * @param workbook
     * @param sheet
     */
    private void setHeadings(HSSFWorkbook workbook, HSSFSheet sheet) {
        // Heading style and font
        HSSFCellStyle cellHeader = setHeadingStyle(workbook);
        setHeadingFont(workbook, cellHeader);

        // Headings
        setHeadingTitles(cellHeader, sheet.createRow(0));
    }

    /**
     * Sets excel header name.
     *
     * @param cellHeader
     * @param row
     */
    private void setHeadingTitles(HSSFCellStyle cellHeader, HSSFRow row) {
        String[] header = heading.split(",");
        for (int i = 0; i < header.length; i++) {
            addToCell(row, i, header[i], cellHeader);
        }
    }

    /**
     * Size set for of each column.
     *
     * @param sheet
     */
    private void setColumnWidth(HSSFSheet sheet) {
        String[] sizes = columnSize.split(",");
        for (int i = 0; i < sizes.length; i++) {
            sheet.setColumnWidth(i, UNIT * Integer.valueOf(sizes[i]));
        }
    }

    private void persistWorkbookToFileSystem(Workbook workbook, String filename) throws IOException {
        FileOutputStream out = null;
        try {
            Assert.notNull(expensofiReportLocation);
            out = new FileOutputStream(new File(expensofiReportLocation + File.separator + filename));
            workbook.write(out);
        } catch (IOException e) {
            LOG.error(
                    "Possible permission error while persisting file to file system={}{}{}, reason={}",
                    expensofiReportLocation,
                    File.separator,
                    filename,
                    e.getLocalizedMessage(),
                    e
            );
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    /**
     * Add picture data to this workbook.
     * @param imageBytes
     * @param imageContentType
     * @param workbook
     * @param sheet
     * @param row
     */
    private void anchorReceiptImage(
            byte[] imageBytes,
            String imageContentType,
            HSSFWorkbook workbook,
            HSSFSheet sheet,
            HSSFRow row
    ) {
        int pictureIdx = workbook.addPicture(
                imageBytes,
                "image/jpeg".equalsIgnoreCase(imageContentType) ? Workbook.PICTURE_TYPE_JPEG : Workbook.PICTURE_TYPE_PNG);

        CreationHelper helper = workbook.getCreationHelper();

        // Create the drawing patriarch. This is the top level container for all shapes.
        Drawing drawing = sheet.createDrawingPatriarch();

        //add a picture shape
        ClientAnchor anchor = helper.createClientAnchor();
        //set top-left corner of the picture,
        //subsequent call of Picture#resize() will operate relative to it
        anchor.setCol1(0);
        anchor.setRow1(row.getRowNum() + 2);
        Picture pict = drawing.createPicture(anchor, pictureIdx);

        //auto-size picture relative to its top-left corner
        pict.resize();
    }

    private HSSFCellStyle setHeadingStyle(HSSFWorkbook workbook) {
        HSSFCellStyle cellHeader = workbook.createCellStyle();
        cellHeader.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellHeader.setBottomBorderColor(HSSFColor.BLACK.index);
        cellHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellHeader.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
        cellHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        return cellHeader;
    }

    private void setHeadingFont(HSSFWorkbook workbook, HSSFCellStyle heading) {
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        heading.setFont(font);
    }

    private HSSFCell addToCell(HSSFRow row, int index, Object value, HSSFCellStyle style) {
        HSSFCell cell = row.createCell(index);

        if (null == style) {
            style = cell.getCellStyle();
        }

        if (value instanceof String) {
            String str = (String) value;
            LOG.debug("STRING: [{}]", str);
            if (str.startsWith("=")) {
                cell.setCellFormula(str.substring(1));
            } else {
                cell.setCellValue(new HSSFRichTextString(str));
            }

            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        } else if (value instanceof Date) {
            LOG.debug("DATE: {}", value);
            cell.setCellValue((Date) value);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        } else if (value instanceof Double) {
            LOG.debug("MONEY: {}", value);
            cell.setCellValue((Double) value);
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        } else {
            if (null == value) {
                LOG.debug("OTHER: {} ({})", "", StringUtils.EMPTY.getClass());
                cell.setCellValue(new HSSFRichTextString(""));
            } else {
                LOG.debug("OTHER: {} ({})", value, value.getClass());
                cell.setCellValue(new HSSFRichTextString(value.toString()));
            }
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        }

        cell.setCellStyle(style);
        LOG.debug(" ({})", style);
        return cell;
    }
}

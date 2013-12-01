package com.receiptofi.web.view;

import com.receiptofi.domain.ItemEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import org.springframework.web.servlet.view.document.AbstractExcelView;

/**
 * This view generates an Excel report from receipt item objects.
 */
public class ExpensofiExcelView extends AbstractExcelView {
    private static final Logger log = LoggerFactory.getLogger(ExpensofiExcelView.class);

    public final HSSFCellStyle NO_STYLE = null;

    @Override
    @SuppressWarnings("unchecked")
    protected void buildExcelDocument(Map<String, Object> model, HSSFWorkbook workbook,
                                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        HSSFSheet sheet = workbook.createSheet();

        List<ItemEntity> items = (ArrayList) model.get("items");
        if(items == null) {
            HSSFRow row = sheet.createRow(0);
            addToCell(row, 0, "Error creating spreadsheet", NO_STYLE);
            return;
        }

        // Columns - width is measured in 256ths of an el
        short unit = (short) 1300; // = 1cm
        sheet.setColumnWidth((short) 0, (short) (unit * 3.9));
        sheet.setColumnWidth((short) 1, (short) (unit * 2.8));
        sheet.setColumnWidth((short) 2, (short) (unit * 3.7));
        sheet.setColumnWidth((short) 3, (short) (unit * 3.6));
        sheet.setColumnWidth((short) 4, (short) (unit * 2.4));
        sheet.setColumnWidth((short) 5, (short) (unit * 4.0));

        // Heading style and font
        HSSFCellStyle heading = setHeadingStyle(workbook);
        setHeadingFont(workbook, heading);

        // Other styles
        HSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
        HSSFCellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));

        // Headings
        HSSFRow row = sheet.createRow(0);
        addToCell(row, 0, "Description", heading);
        addToCell(row, 1, "Date", heading);
        addToCell(row, 2, "Quantity", heading);
        addToCell(row, 3, "Tax", heading);
        addToCell(row, 4, "Price", heading);
        addToCell(row, 5, "Expense Type", heading);

        int nAccounts = items.size();

        // Content
        for(short i = 0; i < nAccounts; i++) {
            ItemEntity item = items.get(i);
            row = sheet.createRow(i + 1);
            addToCell(row, 0, item.getName(), NO_STYLE);
            addToCell(row, 1, item.getReceipt().getReceiptDate(), dateStyle);
            addToCell(row, 2, item.getQuantity(), NO_STYLE);
            addToCell(row, 3, item.getTax(), moneyStyle);
            addToCell(row, 4, item.getPrice(), moneyStyle);

            String cc = null;
            if(item.getExpenseType() != null) {
                cc = item.getExpenseType().getExpName();
            }
            addToCell(row, 5, cc == null ? "N/A" : cc, NO_STYLE);
        }

        // Totals
        row = sheet.createRow(nAccounts + 2);
        addToCell(row, 3, "TOTAL", NO_STYLE);
        addToCell(row, 4, "=sum(E2:E" + (nAccounts + 1) + ')', moneyStyle);

        //Add receipt image
        byte[] imageBytes = (byte[]) model.get("image");
        String imageContentType = (String) model.get("image-type");
        anchorReceiptImage(imageBytes, imageContentType, workbook, sheet, row);
    }

    //add picture data to this workbook.
    private void anchorReceiptImage(byte[] imageBytes, String imageContentType, HSSFWorkbook workbook, HSSFSheet sheet, HSSFRow row) {
        int pictureIdx = workbook.addPicture(imageBytes, !imageContentType.equalsIgnoreCase("image/jpeg") ? Workbook.PICTURE_TYPE_PNG : Workbook.PICTURE_TYPE_JPEG);

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
        HSSFCellStyle heading = workbook.createCellStyle();
        heading.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        heading.setBottomBorderColor(HSSFColor.BLACK.index);
        heading.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        heading.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
        heading.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        return heading;
    }

    private void setHeadingFont(HSSFWorkbook workbook, HSSFCellStyle heading) {
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setColor(HSSFColor.WHITE.index);
        heading.setFont(font);
    }

    private HSSFCell addToCell(HSSFRow row, int index, Object value, HSSFCellStyle style) {
        HSSFCell cell = row.createCell((short) index);

        if(style == null) {
            style = cell.getCellStyle();
        }

        if(value instanceof String) {
            String str = (String) value;
            log.info("STRING: [" + str + ']');
            if(str.startsWith("=")) {
                cell.setCellFormula(str.substring(1));
            } else {
                cell.setCellValue(new HSSFRichTextString(str));
            }

            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        } else if(value instanceof Date) {
            log.info("DATE:  " + value);
            cell.setCellValue((Date) value);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        } else if(value instanceof Double) {
            log.info("MONEY: " + value);
            cell.setCellValue(((Double) value));
            style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        } else {
            if(value == null) {
                value = "";   // Ignore
            }
            log.info("OTHER: " + value + " (" + value.getClass() + ")");
            cell.setCellValue(new HSSFRichTextString(value.toString()));
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        }

        cell.setCellStyle(style);
        log.info(" (" + style + ")");
        return cell;
    }

}
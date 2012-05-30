////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Astina AG, Zurich
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////////////////////////
package ch.astina.hesperid.util.jasper;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class JasperExcelStreamResponse extends JasperStreamResponse 
{
    public static final String EXCEL_CONTENT_TYPE = "application/vnd.ms-excel";
    private boolean removeEmptySpaceBetweenRows;
    private boolean removeEmptySpaceBetweenColumns;
    private boolean printWhitePageBackground;
    private boolean fixFontSize;
    private boolean ignoreGraphics;

    public JasperExcelStreamResponse(JasperPrint jasperPrint, String fileName) 
    {
        super(jasperPrint, fileName);
    }

    @Override
    public String getContentType() 
    {
        return EXCEL_CONTENT_TYPE;
    }

    public boolean isRemoveEmptySpaceBetweenRows() 
    {
        return removeEmptySpaceBetweenRows;
    }

    public void setRemoveEmptySpaceBetweenRows(boolean removeEmptySpaceBetweenRows) 
    {
        this.removeEmptySpaceBetweenRows = removeEmptySpaceBetweenRows;
    }

    public boolean isRemoveEmptySpaceBetweenColumns() 
    {
        return removeEmptySpaceBetweenColumns;
    }

    public void setRemoveEmptySpaceBetweenColumns(boolean removeEmptySpaceBetweenColumns) 
    {
        this.removeEmptySpaceBetweenColumns = removeEmptySpaceBetweenColumns;
    }

    public boolean isPrintWhitePageBackground() 
    {
        return printWhitePageBackground;
    }

    public void setPrintWhitePageBackground(boolean printWhitePageBackground) 
    {
        this.printWhitePageBackground = printWhitePageBackground;
    }

    public boolean isFixFontSize() 
    {
        return fixFontSize;
    }

    public void setFixFontSize(boolean fixFontSize) 
    {
        this.fixFontSize = fixFontSize;
    }

    public boolean isIgnoreGraphics() 
    {
        return ignoreGraphics;
    }

    public void setIgnoreGraphics(boolean ignoreGraphics) 
    {
        this.ignoreGraphics = ignoreGraphics;
    }

    @Override
    public void exportReportToStream(JasperPrint jasperPrint, OutputStream outputStream) throws Exception 
    {
        JRXlsExporter exporter = new JRXlsExporter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, baos);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, this.removeEmptySpaceBetweenRows);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, this.removeEmptySpaceBetweenColumns);
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, this.printWhitePageBackground);
        exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, this.fixFontSize);
        exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_GRAPHICS, this.ignoreGraphics);
        exporter.exportReport();

        HSSFWorkbook workbook = new HSSFWorkbook(new ByteArrayInputStream(baos.toByteArray()));
        workbook.getSheetAt(0).setAutobreaks(true);
        workbook.getSheetAt(0).getPrintSetup().setFitHeight((short) jasperPrint.getPages().size());
        workbook.getSheetAt(0).getPrintSetup().setFitWidth((short) 1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));

        for (Integer x = 0; x < workbook.getSheetAt(0).getPhysicalNumberOfRows(); x++) {
            HSSFRow row = workbook.getSheetAt(0).getRow(x);

            Iterator<Cell> ci = row.cellIterator();

            Cell c = null;
            Date d = null;

            while (ci.hasNext()) {
                c = ci.next();
                try {
                    d = sdf.parse(c.getStringCellValue().trim());
                    c.setCellValue(d);
                    c.setCellStyle(cellStyle);
                } catch (Exception e) {
                }
            }
        }

        workbook.write(outputStream);
    }
}

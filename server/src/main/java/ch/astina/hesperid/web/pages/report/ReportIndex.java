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
package ch.astina.hesperid.web.pages.report;

import ch.astina.hesperid.dao.HqlDAO;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

import ch.astina.hesperid.dao.ReportDAO;
import ch.astina.hesperid.model.internal.ReportType;
import ch.astina.hesperid.util.GenericSelectModel;
import ch.astina.hesperid.util.jasper.JasperExcelStreamResponse;
import ch.astina.hesperid.util.jasper.JasperPDFStreamResponse;
import ch.astina.hesperid.util.jasper.JasperStreamResponse;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.tapestry5.StreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 105 $, $Date: 2011-09-21 16:01:05 +0200 (Mi, 21 Sep 2011) $
 */
@Secured("ROLE_ADMIN")
public class ReportIndex
{
    @SuppressWarnings("unused")
    @Property
    private GenericSelectModel<ReportType> reportTypes;
    
    private Logger logger = LoggerFactory.getLogger(ReportIndex.class);

    @SuppressWarnings("unused")
    @Property
    private ReportType reportType;

    @Inject
    private ReportDAO reportDAO;
    
    @Inject
    private HqlDAO hqlDAO;

    @Inject
    private PropertyAccess propertyAccess;
    
    private JasperStreamResponse reportResponse;
    
    private boolean isExcel;

    @Secured("ROLE_ADMIN")
    public void onActivate()
    {
        reportTypes = new GenericSelectModel<ReportType>(reportDAO.getAllReportTypes(), ReportType.class, "name", "id", propertyAccess);
    }
    
    protected JasperPrint createReport() throws Exception
    {
        JasperDesign jasperDesign = JRXmlLoader.load(new ByteArrayInputStream(reportType.getJasperXmlCode().getBytes()));
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        Map<String, Object> parameters = new HashMap<String, Object>();
        Image logo = ImageIO.read(getClass().getClassLoader().getResourceAsStream("hesperid-logo-blue-big.jpeg"));
        parameters.put("logo", logo);
        parameters.put("reportTitle", reportType.getName());

        List hqlResult = hqlDAO.getExecuteHql(reportType.getHqlQuery());
        JRBeanCollectionDataSource hqlResultDataSource = new JRBeanCollectionDataSource(hqlResult);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, hqlResultDataSource);

        return jasperPrint;
    }
    
    public void onSelectedFromExcel()
    {
        isExcel = true;
    }

    public void onSelectedFromPDF()
    {
        isExcel = false;
    }
   
    private void generatePdf() throws Exception
    {
    	JasperPrint jasperPrint = createReport();
    	this.reportResponse = new JasperPDFStreamResponse(jasperPrint, reportType.getName() + ".pdf");
    }
    
    private void generateExcel() throws Exception
    {
    	JasperPrint jasperPrint = createReport();
    	JasperExcelStreamResponse response = new JasperExcelStreamResponse(jasperPrint, reportType.getName() + ".xls");
    	response.setRemoveEmptySpaceBetweenRows(true);
    	response.setRemoveEmptySpaceBetweenColumns(true);
    	response.setPrintWhitePageBackground(false);
    	response.setFixFontSize(true);
    	response.setIgnoreGraphics(false);
        
    	this.reportResponse = response;
    }
    
    public StreamResponse onSuccess()
    {
        try{
            if (isExcel) {
                generateExcel();
            } else {
                generatePdf();
            }
        } catch (Exception e) {
            logger.error("Errror while export", e);
        }
        
        return reportResponse;
    }
}

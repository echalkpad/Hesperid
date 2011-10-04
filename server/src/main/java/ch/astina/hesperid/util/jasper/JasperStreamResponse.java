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

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.net.URLEncoder;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public abstract class JasperStreamResponse implements StreamResponse 
{
    private JasperPrint jasperPrint;
    private String fileName;
    private final Logger logger = LoggerFactory.getLogger(JasperPrint.class);

    public JasperStreamResponse(JasperPrint jasperPrint, String fileName) 
    {
        this.jasperPrint = jasperPrint;
        this.fileName = fileName;
    }

    @Override
    public abstract String getContentType();

    public abstract void exportReportToStream(JasperPrint jasperPrint, OutputStream outputStream) throws Exception;

    @Override
    public InputStream getStream() 
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            exportReportToStream(jasperPrint, baos);
        } catch (Exception e) {
            logger.error("Unable to export JasperReport", e);
        }
        return new ByteArrayInputStream(baos.toByteArray());
    }

    @Override
    public void prepareResponse(Response response) 
    {
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(this.fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("Unable to set Content-Disposition", e);
        }
    }
}

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
package ch.astina.hesperid.web.pages.admin.report;

import ch.astina.hesperid.dao.ReportDAO;
import ch.astina.hesperid.model.internal.ReportType;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.access.annotation.Secured;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
@Secured({"ROLE_ADMIN"})
public class ManageReportType 
{
    @Property
    private ReportType reportType;

    @Inject
    private ReportDAO reportDAO;
    
    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    public void onActivate(Long reportTypeId)
    {
        reportType = reportDAO.getReportTypeForId(reportTypeId);
    }

    @Secured({"ROLE_ADMIN"})
    public void onActivate()
    {
        if (reportType == null) {
            reportType = new ReportType();
        }
    }

    public Long onPassivate()
    {
        if (reportType != null) {
            return reportType.getId();
        }

        return null;
    }

    @CommitAfter
    public Link onSuccess()
    {
        reportDAO.saveOrUpdateReportType(reportType);

        return pageRenderLinkSource.createPageRenderLink(ReportIndex.class);
    } 
}

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

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

import ch.astina.hesperid.dao.ReportDAO;
import ch.astina.hesperid.model.internal.ReportType;
import ch.astina.hesperid.util.GenericSelectModel;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 105 $, $Date: 2011-09-21 16:01:05 +0200 (Mi, 21 Sep 2011) $
 */
public class ReportIndex
{
    @SuppressWarnings("unused")
    @Property
    private GenericSelectModel<ReportType> reportTypes;

    @SuppressWarnings("unused")
    @Property
    private ReportType reportType;

    @Inject
    private ReportDAO reportDAO;

    @Inject
    private PropertyAccess propertyAccess;

    public void onActivate()
    {
        reportTypes = new GenericSelectModel<ReportType>(reportDAO.getAllReportTypes(), ReportType.class, "name", "id", propertyAccess);
    }

    public void onSelectedFromExcel()
    {

    }

    public void onSelectedFromCSV()
    {

    }
}

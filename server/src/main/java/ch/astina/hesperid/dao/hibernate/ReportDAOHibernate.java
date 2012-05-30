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
package ch.astina.hesperid.dao.hibernate;

import ch.astina.hesperid.dao.ReportDAO;
import ch.astina.hesperid.model.internal.ReportType;
import org.hibernate.Session;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 103 $, $Date: 2011-09-21 15:51:59 +0200 (Mi, 21 Sep 2011) $
 */
public class ReportDAOHibernate implements ReportDAO
{
    private Session session;

    public ReportDAOHibernate(Session session)
    {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    public List<ReportType> getAllReportTypes()
    {
        return session.createCriteria(ReportType.class).list();
    }

    public Session getSession()
    {
        return session;
    }

    @Override
    public ReportType getReportTypeForId(Long reportTypeId) 
    {
        return (ReportType) session.get(ReportType.class, reportTypeId);
    }

    @Override
    public void saveOrUpdateReportType(ReportType reportType) 
    {
        session.saveOrUpdate(reportType);
    }

    @Override
    public void deleteReportType(ReportType reportType) 
    {
        session.delete(reportType);
    }
}

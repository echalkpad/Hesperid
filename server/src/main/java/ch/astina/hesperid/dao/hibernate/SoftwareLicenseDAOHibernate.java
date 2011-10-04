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

import ch.astina.hesperid.dao.SoftwareLicenseDAO;
import ch.astina.hesperid.model.base.SoftwareLicense;
import java.util.List;
import org.hibernate.Session;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class SoftwareLicenseDAOHibernate implements SoftwareLicenseDAO
{
    private Session session;
    
    public SoftwareLicenseDAOHibernate(Session session)
    {
        this.session = session;
    }
    
    @Override
    public SoftwareLicense getSoftwareLicenseForId(Long softwareLicenseId) 
    {
        return (SoftwareLicense) session.get(SoftwareLicense.class, softwareLicenseId);
    }

    @Override
    public List<SoftwareLicense> getAllSoftwareLicenses() 
    {
        return session.createCriteria(SoftwareLicense.class).list();
    }

    @Override
    public void saveOrUpdateSoftwareLicense(SoftwareLicense softwareLicense) 
    {
        session.saveOrUpdate(softwareLicense);
    }

    @Override
    public void deleteSoftwareLicense(SoftwareLicense softwareLicense) 
    {
        session.createSQLQuery("DELETE FROM asset_software_license WHERE software_license = :softwareLicenseId").setLong("softwareLicenseId", softwareLicense.getId()).executeUpdate();
        session.delete(softwareLicense);
    }
}

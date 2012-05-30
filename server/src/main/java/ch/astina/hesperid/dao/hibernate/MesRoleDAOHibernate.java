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

import ch.astina.hesperid.dao.MesRoleDAO;
import ch.astina.hesperid.model.base.BusinessRole;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class MesRoleDAOHibernate implements MesRoleDAO
{
    private Session session;

    public MesRoleDAOHibernate(Session session)
    {
        this.session = session;
    }

    @Override
    public BusinessRole getMesRoleForId(Long mesRoleId)
    {
        return (BusinessRole) session.get(BusinessRole.class, mesRoleId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BusinessRole> getAllMesRoles()
    {
        return session.createCriteria(BusinessRole.class).list();
    }

    @Override
    public void saveOrUpdateMesRole(BusinessRole mesRole)
    {
        session.saveOrUpdate(mesRole);
    }

    @Override
    public void deleteMesRole(BusinessRole mesRole)
    {
        String sql = "UPDATE asset_contact SET business_role = NULL WHERE business_role = ?";
        Query query = session.createSQLQuery(sql);
        query.setLong(0, mesRole.getId());
        query.executeUpdate();

        session.delete(mesRole);
    }
}

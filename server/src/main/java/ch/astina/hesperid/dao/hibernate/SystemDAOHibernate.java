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

import ch.astina.hesperid.dao.SystemDAO;
import ch.astina.hesperid.model.base.System;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class SystemDAOHibernate implements SystemDAO
{
    private Session session;

    public SystemDAOHibernate(Session session)
    {
        this.session = session;
    }

    public System getById(Long id)
    {
        return (System) session.get(System.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<System> getSystems()
    {
        return session.createCriteria(System.class).list();
    }

    public void save(System system)
    {
        session.saveOrUpdate(system);
    }

    public void delete(System system)
    {
        String sql = "UPDATE asset SET system = NULL WHERE system = ?";
        Query query = session.createSQLQuery(sql);
        query.setLong(0, system.getId());
        query.executeUpdate();

        session.delete(system);
    }
}

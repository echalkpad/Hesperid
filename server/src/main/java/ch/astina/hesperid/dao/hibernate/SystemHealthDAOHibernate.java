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

import ch.astina.hesperid.dao.SystemHealthDAO;
import ch.astina.hesperid.model.internal.SystemHealth;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class SystemHealthDAOHibernate implements SystemHealthDAO
{
    private Session session;

    public SystemHealthDAOHibernate(Session session)
    {
        this.session = session;
    }

	@Override
	public List<SystemHealth> findByLog(String searchString)
	{
		return session.createCriteria(SystemHealth.class)
				.add(Restrictions.like("log", searchString, MatchMode.ANYWHERE))
				.addOrder(Order.desc("id"))
				.list();
	}

	@SuppressWarnings("unchecked")
    public List<SystemHealth> getAllSystemHealthEntries()
    {
        return session.createCriteria(SystemHealth.class).addOrder(Order.desc("id")).list();
    }

    public void deleteAllSystemHeathEntries()
    {
        String sql = "DELETE FROM system_health";
        session.createSQLQuery(sql).executeUpdate();
    }

    public void saveOrUpdateSystemHealth(SystemHealth systemHealth)
    {
        session.saveOrUpdate(systemHealth);
    }
}

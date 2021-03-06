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

import ch.astina.hesperid.dao.AgentBundleDAO;
import ch.astina.hesperid.model.base.AgentBundle;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class AgentBundleDAOHibernate implements AgentBundleDAO
{
    private Session session;

    public AgentBundleDAOHibernate(Session session)
    {
        this.session = session;
    }

    public AgentBundle getAgentBundleForId(Long agentBundleId)
    {
        return (AgentBundle) session.get(AgentBundle.class, agentBundleId);
    }

    @SuppressWarnings("unchecked")
    public List<AgentBundle> getAllAgentBundles()
    {
        return session.createCriteria(AgentBundle.class).list();
    }

    @SuppressWarnings("unchecked")
    public List<AgentBundle> getAllActiveAgentBundles()
    {
        return session.createCriteria(AgentBundle.class).add(Restrictions.eq("active", true)).addOrder(Order.desc("published")).list();
    }

    public void saveOrUpdateAgentBundle(AgentBundle agentBundle)
    {
        session.saveOrUpdate(agentBundle);
    }

    public void deleteAgentBundle(AgentBundle agentBundle)
    {
        session.delete(agentBundle);
    }
}

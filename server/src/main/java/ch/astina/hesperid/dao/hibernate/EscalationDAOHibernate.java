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

import ch.astina.hesperid.dao.EscalationDAO;
import ch.astina.hesperid.model.base.EscalationLevel;
import ch.astina.hesperid.model.base.EscalationScheme;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class EscalationDAOHibernate implements EscalationDAO
{
    private Session session;

    public EscalationDAOHibernate(Session session)
    {
        this.session = session;
    }

    @Override
    public EscalationScheme getEscalationSchemeById(Long id)
    {
        return (EscalationScheme) session.load(EscalationScheme.class, id);
    }

    @Override
    public void save(EscalationScheme escalationScheme)
    {
        session.saveOrUpdate(escalationScheme);
    }

    @Override
    public void delete(EscalationScheme escalationScheme)
    {
        session.createSQLQuery("UPDATE asset SET escalation_scheme = NULL WHERE escalation_scheme = :escalationSchemeId").setLong("escalationSchemeId", escalationScheme.getId()).executeUpdate();
        
        session.delete(escalationScheme);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<EscalationScheme> getEscalationSchemes()
    {
        return session.createCriteria(EscalationScheme.class).addOrder(Order.asc("name")).list();
    }

    @Override
    public EscalationLevel getEscalationLevelById(Long id)
    {
        return (EscalationLevel) session.load(EscalationLevel.class, id);
    }

    @Override
    public void save(EscalationLevel escalationLevel)
    {
        session.saveOrUpdate(escalationLevel);
    }

    @Override
    public void delete(EscalationLevel escalationLevel)
    {
        session.delete(escalationLevel);
    }
}

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

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import ch.astina.hesperid.dao.FailureDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.FailureEscalation;
import ch.astina.hesperid.model.base.FailureStatus;
import ch.astina.hesperid.model.base.Observer;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class FailureDAOHibernate implements FailureDAO
{
    private Session session;

    public FailureDAOHibernate(Session session)
    {
        this.session = session;
    }

    @Override
    public Failure getFailureById(Long id)
    {
        return (Failure) session.load(Failure.class, id);
    }

    @Override
    public void save(Failure failure)
    {
        if (failure.getFailureStatus() == null) {
            failure.setFailureStatus(FailureStatus.DETECTED);
        }
        session.saveOrUpdate(failure);
    }

    @Override
    public void delete(Failure failure)
    {
        session.delete(failure);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Failure> getFailures(FailureStatus status)
    {
        return session.createCriteria(Failure.class).add(Restrictions.eq("failureStatus", status)).addOrder(Order.asc("detected")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Failure> getFailures(Asset asset)
    {
        return session.createCriteria(Failure.class).add(Restrictions.eq("asset", asset)).addOrder(Order.asc("detected")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Failure> getFailures(Observer observer)
    {
        return session.createCriteria(Failure.class).add(Restrictions.eq("observer", observer)).addOrder(Order.asc("detected")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Failure> getUnresolvedFailures()
    {
        return session.createCriteria(Failure.class).add(Restrictions.ne("failureStatus", FailureStatus.RESOLVED)).addOrder(Order.asc("detected")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Failure> getUnresolvedFailures(Asset asset)
    {
        return session.createCriteria(Failure.class).add(Restrictions.ne("failureStatus", FailureStatus.RESOLVED)).add(Restrictions.eq("asset", asset)).addOrder(Order.asc("detected")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Failure> getUnresolvedFailures(Observer observer)
    {
        return session.createCriteria(Failure.class).add(Restrictions.ne("failureStatus", FailureStatus.RESOLVED)).add(Restrictions.eq("observer", observer)).addOrder(Order.asc("detected")).list();
    }

    @Override
    public FilterGridDataSource getFailureFilterGridDataSource()
    {
        return new FilterGridDataSource(session, Failure.class);
    }

    @Override
    public void save(FailureEscalation failureEscalation)
    {
        session.saveOrUpdate(failureEscalation);
    }

    @Override
    public void delete(FailureEscalation failureEscalation)
    {
        session.delete(failureEscalation);
    }
}

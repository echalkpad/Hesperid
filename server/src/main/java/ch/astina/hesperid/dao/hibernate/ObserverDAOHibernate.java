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

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.ObservationScope;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.model.base.ObserverStrategy;
import java.util.ArrayList;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;
import org.hibernate.criterion.Criterion;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public class ObserverDAOHibernate implements ObserverDAO
{
    private Session session;

    public ObserverDAOHibernate(Session session)
    {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObserverStrategy> getObserverStrategies()
    {
        return session.createCriteria(ObserverStrategy.class).addOrder(Order.asc("name")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObserverStrategy> getClientObserverStrategies()
    {
        return session.createCriteria(ObserverStrategy.class).add(Restrictions.eq("observationScope", ObservationScope.CLIENT)).addOrder(Order.asc("name")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObserverStrategy> getExternalObserverStrategies()
    {
        return session.createCriteria(ObserverStrategy.class).add(Restrictions.eq("observationScope", ObservationScope.EXTERNAL)).addOrder(Order.asc("name")).list();
    }

    @Override
    public void save(ObserverStrategy observerStrategy)
    {
        session.saveOrUpdate(observerStrategy);
    }

    @Override
    public void delete(ObserverStrategy observerStrategy)
    {
        for (Observer o : observerStrategy.getObservers()) {
            delete(o);
        }
        
        session.delete(observerStrategy);
    }

    @Override
    public Observer getObserver(Long id)
    {
        return (Observer) session.load(Observer.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Observer> getObservers()
    {
        return session.createCriteria(Observer.class).addOrder(Order.asc("name")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Observer> getObservers(Asset asset)
    {
        return session.createCriteria(Observer.class).add(Restrictions.eq("asset", asset)).addOrder(Order.asc("name")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Observer> getClientObservers()
    {
        Query query = session.createQuery("from Observer o "
                + "where o.observerStrategy.observationScope = :scope " + "order by o.name");
        query.setParameter("scope", ObservationScope.CLIENT);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Observer> getClientObservers(Asset asset)
    {
        Query query = session.createQuery("from Observer o "
                + "where o.observerStrategy.observationScope = :scope "
                + "and o.asset = :asset order by o.name");
        query.setParameter("scope", ObservationScope.CLIENT);
        query.setEntity("asset", asset);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Observer> getExternalObservers()
    {
        Query query = session.createQuery("from Observer o "
                + "where o.observerStrategy.observationScope = :scope " + "order by o.name");
        query.setParameter("scope", ObservationScope.EXTERNAL);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Observer> getExternalObservers(Asset asset)
    {
        Query query = session.createQuery("from Observer o "
                + "where o.observerStrategy.observationScope = :scope "
                + "and o.asset = :asset order by o.name");
        query.setParameter("scope", ObservationScope.EXTERNAL);
        query.setEntity("asset", asset);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Observer> getMonitoredObservers()
    {
        return session.createCriteria(Observer.class).add(Restrictions.eq("monitor", true)).addOrder(Order.asc("name")).list();
    }

    @Override
    public void save(Observer observer)
    {
        session.saveOrUpdate(observer);
    }

    @Override
    public void delete(Observer observer)
    {
        session.delete(observer);
    }

    @Override
    public ObserverParameter getLatestObserverParameter(Observer observer)
    {
        return (ObserverParameter) session.createCriteria(ObserverParameter.class).add(Restrictions.eq("observer", observer)).addOrder(Order.desc("updated")).setMaxResults(1).uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObserverParameter> getObserverParameters(Observer observer, int max)
    {
        return session.createCriteria(ObserverParameter.class).add(Restrictions.eq("observer", observer)).addOrder(Order.desc("updated")).setMaxResults(max).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObserverParameter> getObserverParameters(Observer observer, Date since)
    {
        return session.createCriteria(ObserverParameter.class).add(Restrictions.eq("observer", observer)).add(Restrictions.ge("updated", since)).addOrder(Order.asc("updated")).list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ObserverParameter> getObserverParameters(Observer observer, Date from, Date until)
    {
        return session.createCriteria(ObserverParameter.class).add(Restrictions.eq("observer", observer)).add(Restrictions.ge("updated", from)).add(Restrictions.le("updated", until)).addOrder(Order.asc("updated")).list();
    }

    @Override
    public void save(ObserverParameter observerParameter)
    {
        session.saveOrUpdate(observerParameter);
    }

    @Override
    public void delete(ObserverParameter observerParameter)
    {
        session.delete(observerParameter);
    }

    @Override
    public FilterGridDataSource getObserverGridDataSource()
    {
        return new FilterGridDataSource(session, Observer.class);
    }

    @Override
    public HibernateGridDataSource getObserverParameterGridDataSource(Observer observer)
    {
        List<Criterion> criterions = new ArrayList<Criterion>();
        criterions.add(Restrictions.eq("", observer));

        FilterGridDataSource filterGridDataSource = new FilterGridDataSource(session, ObserverParameter.class);
        filterGridDataSource.addFilter(Restrictions.eq("observer", observer));
        filterGridDataSource.setOrder(Order.desc("updated"));

        return filterGridDataSource;
    }
}

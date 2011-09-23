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
package ch.astina.hesperid.dao;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import ch.astina.hesperid.dao.hibernate.FilterGridDataSource;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.model.base.ObserverStrategy;
import org.apache.tapestry5.hibernate.HibernateGridDataSource;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 122 $, $Date: 2011-09-22 15:06:31 +0200 (Do, 22 Sep 2011) $
 */
public interface ObserverDAO
{
    public List<ObserverStrategy> getObserverStrategies();

    public List<ObserverStrategy> getClientObserverStrategies();

    public List<ObserverStrategy> getExternalObserverStrategies();

    public void save(ObserverStrategy observerStrategy);

    public void delete(ObserverStrategy observerStrategy);

    public Observer getObserver(Long id);

    public List<Observer> getObservers();

    public List<Observer> getObservers(Asset asset);

    public List<Observer> getClientObservers();

    public List<Observer> getClientObservers(Asset asset);

    public List<Observer> getExternalObservers();

    public List<Observer> getExternalObservers(Asset asset);

    public List<Observer> getMonitoredObservers();

    public void save(Observer observer);

    public void delete(Observer observer);

    public ObserverParameter getLatestObserverParameter(Observer observer);

    public HibernateGridDataSource getObserverParameterGridDataSource(Observer observer);

    public List<ObserverParameter> getObserverParameters(Observer observer, int max);

    public List<ObserverParameter> getObserverParameters(Observer observer, Date since);

    public List<ObserverParameter> getObserverParameters(Observer observer, Date from, Date until);

    @CommitAfter
    public void save(ObserverParameter observerParameter);

    public void delete(ObserverParameter observerParameter);

    public abstract FilterGridDataSource getObserverGridDataSource();
}

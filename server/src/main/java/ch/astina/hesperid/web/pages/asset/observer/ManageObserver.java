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
package ch.astina.hesperid.web.pages.asset.observer;

import java.util.Date;
import java.util.List;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.springframework.security.annotation.Secured;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.ObservationScope;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverResultType;
import ch.astina.hesperid.model.base.ObserverStrategy;
import ch.astina.hesperid.web.services.scheduler.SchedulerService;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class ManageObserver
{
    @Property
    private Observer observer;
    @Inject
    private ObserverDAO observerDAO;
    @Inject
    private AssetDAO assetDAO;
    @Inject
    private SchedulerService schedulerService;
    @Inject
    private PageRenderLinkSource linkSource;

    @Secured({"ROLE_ADMIN"})
    void onActivate(Asset asset, ObserverStrategy observerStrategy)
    {
        observer = new Observer();
        observer.setObserverStrategy(observerStrategy);
        observer.setAsset(asset);
        observer.setName(observerStrategy.getName());
    }

    @Secured({"ROLE_ADMIN"})
    void onActivate(Observer observer)
    {
        if (this.observer == null) {
            this.observer = observer;
        }
    }

    Object[] onPassivate()
    {
        if (observer.getId() == null) {
            Object[] context = {observer.getAsset(), observer.getObserverStrategy()};
            return context;
        }
        Object[] context = {observer};
        return context;
    }

    @CommitAfter
    Link onSuccess()
    {
        observer.getAsset().setLastUpdatedObserver(new Date());
        assetDAO.saveOrUpdateAsset(observer.getAsset());
        observerDAO.save(observer);

        if (observer.getObserverStrategy().getObservationScope().equals(ObservationScope.EXTERNAL)) {
            schedulerService.restartExternalObservers(observer.getAsset());
        }

        return linkSource.createPageRenderLinkWithContext(ViewObserver.class, observer);
    }

    public List<ObserverStrategy> getObserverStrategies()
    {
        return observerDAO.getObserverStrategies();
    }

    public boolean isResultTypeNumeric()
    {
        return observer.getObserverStrategy().getResultType().isNumeric();
    }

    public boolean isResultTypeString()
    {
        return observer.getObserverStrategy().getResultType().equals(ObserverResultType.STRING);
    }

    public boolean isResultTypeBoolean()
    {
        return observer.getObserverStrategy().getResultType().equals(ObserverResultType.BOOLEAN);
    }
}

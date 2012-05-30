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
package ch.astina.hesperid.web.components;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.FailureDAO;
import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.dao.hibernate.FilterGridDataSource;
import ch.astina.hesperid.global.GlobalConstants;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.ObservationScope;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.model.base.ObserverStrategy;
import ch.astina.hesperid.web.services.failures.FailureService;
import ch.astina.hesperid.web.services.scheduler.SchedulerService;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Mixins;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Import(stylesheet = {"context:styles/observerlist.css"})
public class ObserverList
{
    @Component(parameters = {"PeriodicUpdate.event=refresh", "PeriodicUpdate.period=10"})
    @Mixins({"PeriodicUpdate"})
    private Zone observersZone;

    @Parameter
    private Asset asset;

    @Parameter(defaultPrefix = "literal")
    private String scope;

    @SuppressWarnings("unused")
    @Parameter(value = "Observers", defaultPrefix = "literal")
    @Property
    private String title;

    @Property
    private Observer observer;

    @SuppressWarnings("unused")
    @Property
    private Failure failure;

    @Inject
    private FailureService failureService;

    @Inject
    private FailureDAO failureDAO;

    @Inject
    private ObserverDAO observerDAO;

    @Property
    @Persist
    private ObserverStrategy filterObserverStrategy;

    @Property
    @Persist
    private Boolean filterFailed;

    @Property
    @Persist
    private Boolean filterMonitored;

    @Inject 
    private SchedulerService schedulerService;

    @Inject
    private AssetDAO assetDAO;

    public FilterGridDataSource getObservers()
    {
        ObservationScope observationScope = null;
        if (scope.equals(ObservationScope.EXTERNAL.name().toLowerCase())) {
            observationScope = ObservationScope.EXTERNAL;
        } else if (scope.equals(ObservationScope.CLIENT.name().toLowerCase())) {
            observationScope = ObservationScope.CLIENT;
        }

        FilterGridDataSource dataSource = observerDAO.getObserverGridDataSource();

        dataSource.addFilter(Restrictions.eq("asset", asset));

        if (observationScope != null) {
            dataSource.addAlias("observerStrategy", "os");
            dataSource.addFilter(Restrictions.eq("os.observationScope", observationScope));
        }

        if (filterObserverStrategy != null) {
            dataSource.addFilter(Restrictions.eq("observerStrategy", filterObserverStrategy));
        }
        if (filterFailed != null && filterFailed == true) {
            dataSource.addFilter(Restrictions.eq("failed", true));
        }
        if (filterMonitored != null && filterMonitored == true) {
            dataSource.addFilter(Restrictions.eq("monitor", true));
        }

        return dataSource;
    }

    @CommitAfter
    public void onActionFromDelete(Observer observer)
    {
        observer.getAsset().setLastUpdatedObserver(new Date());
        assetDAO.saveOrUpdateAsset(observer.getAsset());
        
        if (observer.getObserverStrategy().getObservationScope().equals(ObservationScope.EXTERNAL)) {
            schedulerService.restartExternalObservers(observer.getAsset());
        }
        
        observerDAO.delete(observer);
    }

    @CommitAfter
    void onActionFromAcknowledgeFailure(Failure failure)
    {
        if (failure.isStatusDetected()) {
            failureService.acknowledge(failure);
        }
    }

    @CommitAfter
    void onActionFromResolveFailure(Failure failure)
    {
        if (failure.isStatusAcknowledged()) {
            failureService.resolve(failure);
        }
    }

    Object onRefresh()
    {
        return observersZone.getBody();
    }

    public String getObserverStatus()
    {
        if (!observer.isMonitor()) {
            return "none";
        }
        return observer.isFailed() ? "failed" : "ok";
    }

    public String getRowClass()
    {
        return String.format("status-%s", getObserverStatus());
    }

    public List<Failure> getUnresolvedFailures()
    {
        return failureDAO.getUnresolvedFailures(observer);
    }

    public String getLatestvalue()
    {
        ObserverParameter param = observerDAO.getLatestObserverParameter(observer);
        if (param == null) {
            return null;
        }
        String value = param.getValue();
        if (value == null) {
            return null;
        }
        if (value.length() > 15) {
            value = value.substring(0, 15) + " ...";
        }
        return value;
    }

    public String getLastCheck()
    {
        if (observer.getLastCheck() == null) {
            return null;
        }
        return GlobalConstants.DATETIME_FORMAT.format(observer.getLastCheck());
    }

    public List<ObserverStrategy> getObserverStrategies()
    {
        if (scope.equals(ObservationScope.EXTERNAL.name().toLowerCase())) {
            return observerDAO.getExternalObserverStrategies();
        } else if (scope.equals(ObservationScope.CLIENT.name().toLowerCase())) {
            return observerDAO.getClientObserverStrategies();
        }
        return observerDAO.getObserverStrategies();
    }

    public boolean isNoAgentInstalled()
    {
        return scope.equals(ObservationScope.CLIENT.name().toLowerCase()) && !asset.isAgentInstalled();
    }
}

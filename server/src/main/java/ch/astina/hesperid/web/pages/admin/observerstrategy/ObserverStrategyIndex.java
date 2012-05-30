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
package ch.astina.hesperid.web.pages.admin.observerstrategy;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.model.base.ObservationScope;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverStrategy;
import ch.astina.hesperid.web.services.scheduler.SchedulerService;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.access.annotation.Secured;

import java.util.Date;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
@Secured({"ROLE_ADMIN"})
public class ObserverStrategyIndex
{
    @Inject
    private ObserverDAO observerDAO;
    
    @SuppressWarnings("unused")
    @Property
    private ObserverStrategy observerStrategy;
    
    @Inject
    private AssetDAO assetDAO;
    
    @Inject
    private SchedulerService schedulerService;

    public List<ObserverStrategy> getObserverStrategies()
    {
        return observerDAO.getObserverStrategies();
    }
    
    @CommitAfter
    public void onActionFromDelete(ObserverStrategy os)
    {
        for (Observer o : os.getObservers()) {
            o.getAsset().setLastUpdatedObserver(new Date());
            assetDAO.saveOrUpdateAsset(o.getAsset());
        
            if (o.getObserverStrategy().getObservationScope().equals(ObservationScope.EXTERNAL)) {
                schedulerService.restartExternalObservers(o.getAsset());
            }
        }
        
        observerDAO.delete(os);
    }
}

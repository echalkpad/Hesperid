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
package ch.astina.hesperid.web.services.jobs.impl;

import java.util.Date;

import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.groovy.ParameterGatherer;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.web.services.SystemHealthService;
import ch.astina.hesperid.web.services.jobs.ExternalObserverJob;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class ExternalObserverJobImpl implements ExternalObserverJob
{
    private ObserverDAO observerDAO;
    private SystemHealthService systemHealthService;

    public ExternalObserverJobImpl(ObserverDAO observerDAO, SystemHealthService systemHealthService)
    {
        this.observerDAO = observerDAO;
        this.systemHealthService = systemHealthService;
    }

    @Override
    public void monitor(Observer observer)
    {
        String result = null;
        String error = null;
        try {

            // instantiate strategy
            ParameterGatherer parameterGatherer = observer.getObserverStrategy().getGroovyScriptInstance();

            // execute strategy code
            result = parameterGatherer.getResult(observer.getParameterMap());

        } catch (Exception e) {
            error = e.getMessage();
        }

        try {

            ObserverParameter param = new ObserverParameter();
            param.setObserver(observer);
            param.setValue(result);
            param.setError(error);
            param.setUpdated(new Date());

            observerDAO.save(param);

        } catch (Exception e) {
            systemHealthService.log("Error while executing external observer", e.getMessage(), e);
        }
    }
}

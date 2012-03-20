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

import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.web.services.SystemHealthService;
import ch.astina.hesperid.web.services.jobs.ExternalObserverJob;
import ch.astina.hesperid.worker.ParameterGathererRunner;

import java.util.Date;

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
        try {
	        ParameterGathererRunner runner = new ParameterGathererRunner(observer);
	        runner.execute();

            ObserverParameter param = new ObserverParameter();
            param.setObserver(observer);
            param.setValue(runner.getResult());
            param.setError(runner.getErrorMessage());
            param.setUpdated(new Date());

            observerDAO.save(param);

	        if (runner.hasUnknownError()) {
		        systemHealthService.log("Error while saving observer parameter", runner.getException().getClass().getName(), runner.getException());
	        }

        } catch (Exception e) {
            systemHealthService.log("Error while saving observer parameter", e.getMessage(), e);
        }
    }
}

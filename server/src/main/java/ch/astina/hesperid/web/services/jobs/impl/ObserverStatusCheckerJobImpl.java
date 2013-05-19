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
import ch.astina.hesperid.model.base.Failure;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.web.services.SystemHealthService;
import ch.astina.hesperid.web.services.failures.FailureService;
import ch.astina.hesperid.web.services.jobs.ObserverStatusCheckerJob;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class ObserverStatusCheckerJobImpl implements ObserverStatusCheckerJob
{
    private SystemHealthService systemHealthService;
    private ObserverDAO observerDAO;
    private FailureService failureService;
    private int PARAMETER_AGE_ALERT_WAIT = 300;

    public ObserverStatusCheckerJobImpl(SystemHealthService systemHealthService,
            ObserverDAO observerDAO, FailureService failureService)
    {
        this.systemHealthService = systemHealthService;
        this.observerDAO = observerDAO;
        this.failureService = failureService;
    }

    @Override
    public void checkObservers()
    {
        try {
            for (Observer observer : observerDAO.getMonitoredObservers()) {
                checkObserver(observer);
            }
        } catch (Exception e) {
            systemHealthService.log("Error during status check", e.getMessage(), e);
        }
    }

    private void checkObserver(Observer observer)
    {
        // check new parameters since last check
        List<ObserverParameter> parameters = observerDAO.getObserverParameters(observer, observer.getLastCheck());

	    for ( Iterator<ObserverParameter> parameterIter = parameters.iterator(); parameterIter.hasNext(); ) {

		    ObserverParameter parameter = parameterIter.next();

	        if (parameter.getValue() != null) {
		        checkObserverParameter(observer, parameter);
	        } else if (!parameterIter.hasNext()) {
		        Failure failure = generateFailure(observer, parameter, "Parameter delivery had an error: " + parameter.getError());
		        failureService.report(failure);
	        }
        }

        checkLatestParameterAge(observer);

        observer.setLastCheck(new Date());
        observerDAO.save(observer);
    }

    private void checkObserverParameter(Observer observer, ObserverParameter parameter)
    {
        try {
	        ObserverParameterValidator validator = new ObserverParameterValidator();
	        if (validator.isValid(parameter)) {
		        failureService.autoresolve(observer);
	        } else {
		        Failure failure = generateFailure(observer, parameter, validator.getValidationMessage());
		        failureService.report(failure);
	        }
        } catch (Exception e) {
	        systemHealthService.log("Error occurred during validation of parameter with id:"
			        + parameter.getId() + "\n"
			        + observer
			        , "", e);
        }
    }

    private Failure generateFailure(Observer observer, ObserverParameter parameter, String message)
    {
        Failure failure = new Failure();
        failure.setObserver(observer);
        failure.setObserverParameter(parameter);
        failure.setAsset(observer.getAsset());
        failure.setMessage(message);
        return failure;
    }

	/**
	 * Checks if the agent is still delivering the requested parameters.
	 */
    private void checkLatestParameterAge(Observer observer)
    {
        if (!observer.isFailed() && latestUpdateIsOutsideOfNotificationTimeRange(observer)) {
            Failure failure = generateFailure(observer, null, "No observer data received");
            failureService.report(failure);
        }
    }

	private boolean latestUpdateIsOutsideOfNotificationTimeRange(Observer observer)
	{
		long alertWaitTime = -(observer.getCheckInterval() + PARAMETER_AGE_ALERT_WAIT);

		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, (int) alertWaitTime);

		ObserverParameter latestParameter = observerDAO.getLatestObserverParameter(observer);

		return latestParameter == null || latestParameter.getUpdated().before(now.getTime());
	}
}

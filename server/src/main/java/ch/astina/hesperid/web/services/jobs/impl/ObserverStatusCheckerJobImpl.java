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
import ch.astina.hesperid.model.base.ObserverResultType;
import ch.astina.hesperid.web.services.SystemHealthService;
import ch.astina.hesperid.web.services.failures.FailureService;
import ch.astina.hesperid.web.services.jobs.ObserverStatusCheckerJob;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

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
        // check new paramaters since last check
        List<ObserverParameter> parameters = observerDAO.getObserverParameters(observer,
                observer.getLastCheck());
        for (ObserverParameter parameter : parameters) {
            checkObserverParameter(observer, parameter);
        }

        // check age of latest parameter
        checkLatestParameterAge(observer);

        observer.setLastCheck(new Date());
        observerDAO.save(observer);
    }

    private void checkObserverParameter(Observer observer, ObserverParameter parameter)
    {
        try {
            validateObserverParameter(parameter);
        } catch (Exception e) {
            Failure failure = generateFailure(observer, parameter, e.getMessage());
            failureService.report(failure);
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

    private void validateObserverParameter(ObserverParameter parameter)
    {
        Observer observer = parameter.getObserver();
        ObserverResultType resultType = observer.getObserverStrategy().getResultType();

        if (resultType.isNumeric()) {
			validateNumeric(observer, parameter);
        } else if (resultType.equals(ObserverResultType.BOOLEAN)) {
			validateBoolean(observer, parameter);
        } else if (resultType.equals(ObserverResultType.STRING)) {
			validateString(observer, parameter);
        }
    }

	private void validateNumeric(Observer observer, ObserverParameter parameter)
	{
		try {
			// no min/max?
			if (observer.getExpectedValueMin() == null
					&& observer.getExpectedValueMax() == null) {
				return;
			}

			float value = Float.parseFloat(parameter.getValue());

			if (observer.getExpectedValueMin() != null && value < observer.getExpectedValueMin()) {
				throw new RuntimeException("Parameter value is below expected minimum");
			}
			if (observer.getExpectedValueMax() != null && value > observer.getExpectedValueMax()) {
				throw new RuntimeException("Parameter value is above expected maximum");
			}

			// everything is ok. auto-resolve existing failures
			failureService.autoresolve(observer);

		} catch (RuntimeException rte) {
			throw rte;

		} catch (Exception e) {
			systemHealthService.log("Error in asset information check Numeric comparison", "",
					e);
		}
	}

	private void validateBoolean(Observer observer, ObserverParameter parameter)
	{
		if (!parameter.getValue().equals(observer.getExpectedValue())) {
			throw new RuntimeException("Parameter value does not match expected value");
		}

		// everything is ok. auto-resolve existing failures
		failureService.autoresolve(observer);
	}

	private void validateString(Observer observer, ObserverParameter parameter)
	{
		try {
			Pattern pattern = Pattern.compile(observer.getExpectedValue(), Pattern.DOTALL
					| Pattern.MULTILINE);

			if (!pattern.matcher(parameter.getValue()).matches()) {
				throw new RuntimeException("Parameter value does not match expected value");
			}

			// everything is ok. auto-resolve existing failures
			failureService.autoresolve(observer);

		} catch (RuntimeException rte) {
			throw rte;

		} catch (Exception e) {
			systemHealthService.log("Error in asset information check Result Regex Pattern",
					"", e);
		}
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

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
package ch.astina.hesperid.agentbundle.scheduler;

import ch.astina.hesperid.agentbundle.webservice.AgentFeedback;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.model.base.ObserverParameter;
import ch.astina.hesperid.worker.ParameterGathererRunner;
import org.apache.log4j.Logger;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 133 $, $Date: 2011-09-23 13:43:55 +0200 (Fr, 23 Sep 2011) $
 */
public class ParameterGathererExecutor 
{
    private AgentFeedback agentFeedback;
    private Observer observer;
    private Logger logger = Logger.getLogger(AgentFeedback.class);

    public ParameterGathererExecutor(AgentFeedback agentFeedback, Observer observer) 
    {
        this.agentFeedback = agentFeedback;
        this.observer = observer;
    }

    public void gatherParameterAndReply() 
    {
        try {
	        ParameterGathererRunner runner = new ParameterGathererRunner(observer);
	        runner.execute();

            ObserverParameter parameter = new ObserverParameter();
            parameter.setObserver(observer);
            parameter.setValue(runner.getResult());
            parameter.setError(runner.getErrorMessage());
            agentFeedback.deliverObserverParameter(parameter);

	        if (runner.hasUnknownError()) {
		        logger.warn("Observer parameter gathering recognized an unknown error.", runner.getException());
	        }

        } catch (Exception e) {
            logger.warn("Exception during parameter gathering", e);
        }
    }
}

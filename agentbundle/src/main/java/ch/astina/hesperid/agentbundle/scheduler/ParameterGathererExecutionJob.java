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

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.astina.hesperid.agentbundle.webservice.AgentFeedback;
import ch.astina.project.molo.model.base.Observer;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 133 $, $Date: 2011-09-23 13:43:55 +0200 (Fr, 23 Sep 2011) $
 */
public class ParameterGathererExecutionJob implements Job 
{
    private static final Logger logger = LoggerFactory.getLogger(ParameterGathererExecutionJob.class);

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException 
    {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();

        AgentFeedback agentFeedback = (AgentFeedback) jobDataMap.get("agentFeedback");
        Observer observer = (Observer) jobDataMap.get("observer");

        ParameterGathererExecutor executor = new ParameterGathererExecutor(agentFeedback, observer);
        executor.gatherParameterAndReply();

        logger.info("Parameter Gatherer Execution started: " + observer);
    }
}

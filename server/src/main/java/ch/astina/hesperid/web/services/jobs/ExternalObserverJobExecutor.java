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
package ch.astina.hesperid.web.services.jobs;

import ch.astina.hesperid.model.base.Observer;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class ExternalObserverJobExecutor implements Job
{
    private static final Logger logger = LoggerFactory.getLogger(ExternalObserverJobExecutor.class);

    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        PerthreadManager perthreadManager = (PerthreadManager) jobDataMap.get("perthreadManager");

        ExternalObserverJob externalObserverJob = (ExternalObserverJob) jobDataMap.get("externalObserverJob");

        Observer observer = (Observer) jobDataMap.get("observer");

        logger.info("External observer execution started: " + observer.toString());

        externalObserverJob.monitor(observer);

        perthreadManager.cleanup();
    }
}

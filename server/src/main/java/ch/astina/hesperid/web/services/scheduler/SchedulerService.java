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
package ch.astina.hesperid.web.services.scheduler;

import java.util.List;

import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.web.services.dbmigration.DbMigration;
import ch.astina.hesperid.web.services.jobs.ExternalObserverJob;
import ch.astina.hesperid.web.services.jobs.ExternalObserverJobExecutor;
import ch.astina.hesperid.web.services.jobs.FailureCheckerJob;
import ch.astina.hesperid.web.services.jobs.FailureCheckerJobExecutor;
import ch.astina.hesperid.web.services.jobs.ObserverStatusCheckerJob;
import ch.astina.hesperid.web.services.jobs.ObserverStatusCheckerJobExecutor;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 123 $, $Date: 2011-09-23 11:53:17 +0200 (Fr, 23 Sep 2011) $
 */
public class SchedulerService
{
    private static final int DEFAULT_INTERVAL = 60;
    private PerthreadManager perthreadManager;
    private ExternalObserverJob externalObserverJob;
    private ObserverStatusCheckerJob serviceStatusCheckerJob;
    private FailureCheckerJob failureCheckerJob;
    private ObserverDAO observerDAO;
    private AssetDAO assetDAO;
    private SchedulerFactory schedulerFactory;
    private DbMigration dbMigration;
    private Scheduler scheduler;
    private final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    public SchedulerService(PerthreadManager perthreadManager,
            ExternalObserverJob externalServiceMonitoringJob,
            ObserverStatusCheckerJob serviceStatusCheckerJob,
            FailureCheckerJob failureCheckerJob, ObserverDAO observerDAO,
            AssetDAO assetDAO, DbMigration dbMigration)
    {
        this.perthreadManager = perthreadManager;
        this.externalObserverJob = externalServiceMonitoringJob;
        this.serviceStatusCheckerJob = serviceStatusCheckerJob;
        this.failureCheckerJob = failureCheckerJob;
        this.observerDAO = observerDAO;
        this.assetDAO = assetDAO;

        schedulerFactory = new org.quartz.impl.StdSchedulerFactory();

        try {
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();

            startExternalObservers();
            startObserverStatusChecker();
            startFailureChecker();

        } catch (Exception e) {
            logger.error("Error while starting scheduler service", e);
        }
    }

    public void restartExternalObservers(Asset asset)
    {
        try {
            for (String s : scheduler.getJobNames(asset.getAssetIdentifier())) {
                scheduler.deleteJob(s, asset.getAssetIdentifier());
            }

            List<Observer> observers = observerDAO.getExternalObservers(asset);

            for (Observer observer : observers) {

                JobDetail jobDetail = new JobDetail(observer.getId().toString(),
                        asset.getAssetIdentifier(), ExternalObserverJobExecutor.class);
                jobDetail.getJobDataMap().put("perthreadManager", perthreadManager);
                jobDetail.getJobDataMap().put("externalObserverJob", externalObserverJob);
                jobDetail.getJobDataMap().put("observer", observer);

                Trigger trigger = TriggerUtils.makeSecondlyTrigger(observer.getCheckInterval() != null ? observer.getCheckInterval().intValue() : DEFAULT_INTERVAL);
                trigger.setName(observer.getId().toString() + "trigger");

                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (Exception e) {
            logger.error("Error while restarting external service checks", e);
        }
    }

    private void startExternalObservers()
    {
        for (Asset asset : assetDAO.getAllAssets()) {
            restartExternalObservers(asset);
        }
    }

    public void startObserverStatusChecker()
    {
        try {
            JobDetail jobDetail = new JobDetail("observerStatusChecker", "observerStatusChecker",
                    ObserverStatusCheckerJobExecutor.class);
            jobDetail.getJobDataMap().put("perthreadManager", perthreadManager);
            jobDetail.getJobDataMap().put("serviceStatusCheckerJob", serviceStatusCheckerJob);

            Trigger trigger = TriggerUtils.makeSecondlyTrigger(DEFAULT_INTERVAL);
            trigger.setName("serviceStatusCheck-trigger");

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.error("Error while starting observer status checker", e);
        }
    }

    private void startFailureChecker()
    {
        try {
            JobDetail jobDetail = new JobDetail("failureChecker", "failureChecker", FailureCheckerJobExecutor.class);
            jobDetail.getJobDataMap().put("perthreadManager", perthreadManager);
            jobDetail.getJobDataMap().put("failureCheckerJob", failureCheckerJob);

            Trigger trigger = TriggerUtils.makeSecondlyTrigger(DEFAULT_INTERVAL);
            trigger.setName("failureChecker-trigger");

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.error("Error while starting failure checker", e);
        }
    }
}

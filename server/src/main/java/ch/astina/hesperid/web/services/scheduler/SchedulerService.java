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

import ch.astina.hesperid.dao.AssetDAO;
import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.dao.SystemSettingsDAO;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.Observer;
import ch.astina.hesperid.web.services.dbmigration.DbMigration;
import ch.astina.hesperid.web.services.jobs.DbCleanupJob;
import ch.astina.hesperid.web.services.jobs.DbCleanupJobExecutor;
import ch.astina.hesperid.web.services.jobs.ExternalObserverJob;
import ch.astina.hesperid.web.services.jobs.ExternalObserverJobExecutor;
import ch.astina.hesperid.web.services.jobs.FailureCheckerJob;
import ch.astina.hesperid.web.services.jobs.FailureCheckerJobExecutor;
import ch.astina.hesperid.web.services.jobs.ObserverStatusCheckerJob;
import ch.astina.hesperid.web.services.jobs.ObserverStatusCheckerJobExecutor;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.quartz.*;
import org.quartz.impl.jdbcjobstore.TriggerPersistenceDelegate;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;


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
	private DbCleanupJob dbCleanupJob;

    private ObserverDAO observerDAO;
    private AssetDAO assetDAO;

    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;
    private final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    public SchedulerService(PerthreadManager perthreadManager,
            ExternalObserverJob externalServiceMonitoringJob,
            ObserverStatusCheckerJob serviceStatusCheckerJob,
            FailureCheckerJob failureCheckerJob,
            DbCleanupJob dbCleanupJob,
            ObserverDAO observerDAO,
            AssetDAO assetDAO, DbMigration dbMigration)
    {
            this.perthreadManager = perthreadManager;
            this.externalObserverJob = externalServiceMonitoringJob;
            this.serviceStatusCheckerJob = serviceStatusCheckerJob;
            this.failureCheckerJob = failureCheckerJob;
	        this.dbCleanupJob = dbCleanupJob;
            this.observerDAO = observerDAO;
            this.assetDAO = assetDAO;
            
            try {
                logger.error("DB Migration " + dbMigration);
                dbMigration.updateAllChangelogs();
            } catch (Exception e) {
                logger.error("Error while db fummel",e);
            }

            schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
            try {
                scheduler = schedulerFactory.getScheduler();
                scheduler.start();

                startExternalObservers();
                startObserverStatusChecker();
                startFailureChecker();
	            startDatabaseCleanupJob();

            } catch (Exception e) {
                logger.error("Error while starting scheduler service", e);
            }
    }

	public void stopObserverJobsFor(Asset asset) throws SchedulerException
	{
		for (JobKey jobKey : scheduler.getJobKeys( GroupMatcher.jobGroupEquals(asset.getAssetIdentifier())) ) {
			JobDetail job = scheduler.getJobDetail(jobKey);
			scheduler.deleteJob(job.getKey());
		}
	}

    public JobDetail getObserverJob(Observer observer)
    {
        try {
            return scheduler.getJobDetail(new JobKey(observer.getId().toString(), observer.getAsset().getAssetIdentifier()));
        } catch (SchedulerException e) {
            return null;
        }
    }

    public Trigger getObserverTrigger(Observer observer)
    {
        try {
            return scheduler.getTrigger(new TriggerKey(observer.getId().toString() + "trigger"));
        } catch (SchedulerException e) {
            return null;
        }
    }

    public void restartExternalObservers(Asset asset)
    {
        try {
	        stopObserverJobsFor(asset);

            List<Observer> observers = observerDAO.getExternalObservers(asset);

            for (Observer observer : observers) {

	            JobDetail jobDetail = JobBuilder.newJob(ExternalObserverJobExecutor.class)
			            .withIdentity(observer.getId().toString(), asset.getAssetIdentifier())
			            .build();

	            jobDetail.getJobDataMap().put("perthreadManager", perthreadManager);
	            jobDetail.getJobDataMap().put("externalObserverJob", externalObserverJob);
	            jobDetail.getJobDataMap().put("observer", observer);

	            int interval = observer.getCheckInterval() != null ? observer.getCheckInterval().intValue() : DEFAULT_INTERVAL;
                Trigger trigger = buildTrigger(
                        observer.getId().toString() + "trigger",
                        interval,
                        Trigger.DEFAULT_PRIORITY + 1 // higher priority for observer jobs
                );

                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (Exception e) {
            logger.error("Error while restarting external observers", e);
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
	        JobDetail jobDetail = JobBuilder.newJob(ObserverStatusCheckerJobExecutor.class)
			        .withIdentity("observerStatusChecker", "observerStatusChecker")
			        .build();

            jobDetail.getJobDataMap().put("perthreadManager", perthreadManager);
            jobDetail.getJobDataMap().put("serviceStatusCheckerJob", serviceStatusCheckerJob);

	        Trigger trigger = buildTrigger("serviceStatusCheck-trigger", DEFAULT_INTERVAL);

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.error("Error while starting observer status checker", e);
        }
    }

    private void startFailureChecker()
    {
        try {
	        JobDetail jobDetail = JobBuilder.newJob(FailureCheckerJobExecutor.class)
			        .withIdentity("failureChecker", "failureChecker")
			        .build();
            jobDetail.getJobDataMap().put("perthreadManager", perthreadManager);
            jobDetail.getJobDataMap().put("failureCheckerJob", failureCheckerJob);

	        // Failure checker is started in one and a half minute that auto resolving can run first.
	        Trigger trigger = TriggerBuilder.newTrigger()
			        .withIdentity("failureChecker-trigger")
			        .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(DEFAULT_INTERVAL))
			        .startAt(DateUtils.addSeconds(new Date(), 90))
			        .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.error("Error while starting failure checker", e);
        }
    }

	private void startDatabaseCleanupJob()
	{
		try {
			JobDetail jobDetail = JobBuilder.newJob(DbCleanupJobExecutor.class)
					.withIdentity("dbCleanup", "dbCleanup")
					.build();
			jobDetail.getJobDataMap().put("perthreadManager", perthreadManager);
			jobDetail.getJobDataMap().put("dbCleanupJob", dbCleanupJob);

			Trigger trigger = buildTrigger("dbCleanup-trigger", 3600);

			scheduler.scheduleJob(jobDetail, trigger);
		} catch (Exception e) {
			logger.error("Error while starting database cleanup job", e);
		}
	}

	private Trigger buildTrigger(String triggerName, int interval, int priority)
	{
		return TriggerBuilder.newTrigger()
				.withIdentity(triggerName)
				.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(interval))
                .withPriority(priority)
				.startNow()
				.build();
	}

    private Trigger buildTrigger(String triggerName, int interval)
    {
        return buildTrigger(triggerName, interval, Trigger.DEFAULT_PRIORITY);
    }
}

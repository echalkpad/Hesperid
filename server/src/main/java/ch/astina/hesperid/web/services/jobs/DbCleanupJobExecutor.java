package ch.astina.hesperid.web.services.jobs;

import org.apache.tapestry5.ioc.services.PerthreadManager;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks for data in the database which is older than the defined setting and deletes the
 * matching data sets.
 *
 * @author fharter
 */
public class DbCleanupJobExecutor implements Job
{
	private static final Logger logger = LoggerFactory.getLogger(DbCleanupJobExecutor.class);

	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		logger.info("Database cleanup job started");

		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		PerthreadManager perthreadManager = (PerthreadManager) jobDataMap.get("perthreadManager");

		DbCleanupJob dbCleanupJob = (DbCleanupJob) jobDataMap.get("dbCleanupJob");
		dbCleanupJob.cleanup();

		perthreadManager.cleanup();
	}
}

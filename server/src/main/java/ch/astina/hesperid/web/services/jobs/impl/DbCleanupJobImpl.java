package ch.astina.hesperid.web.services.jobs.impl;

import ch.astina.hesperid.dao.ObserverDAO;
import ch.astina.hesperid.dao.SystemSettingsDAO;
import ch.astina.hesperid.model.internal.SystemSettings;
import ch.astina.hesperid.web.services.jobs.DbCleanupJob;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * @author fharter
 */
public class DbCleanupJobImpl implements DbCleanupJob
{

	private ObserverDAO observerDAO;
	private SystemSettingsDAO systemSettingsDAO;
	private static final Logger logger = LoggerFactory.getLogger(DbCleanupJobImpl.class);

	public DbCleanupJobImpl(ObserverDAO observerDAO, SystemSettingsDAO systemSettingsDAO)
	{
		this.observerDAO = observerDAO;
		this.systemSettingsDAO = systemSettingsDAO;
	}

	@Override
	public void cleanup()
	{
		SystemSettings settings = systemSettingsDAO.getSystemSettingsForId(1l);

		Calendar cal = Calendar.getInstance();
		Date keepDataBarrier = DateUtils.addSeconds(cal.getTime(), (int) -settings.getDataDeletionBarrier());

		observerDAO.deleteObserverParameterBefore( keepDataBarrier );
	}
}

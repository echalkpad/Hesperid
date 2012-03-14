package ch.astina.hesperid.dao.hibernate;

import ch.astina.hesperid.dao.SystemSettingsDAO;
import ch.astina.hesperid.model.internal.SystemSettings;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import java.util.List;

/**
 * @author fharter
 */
public class SystemSettingsDAOHibernate implements SystemSettingsDAO
{

	private Session session;

	public SystemSettingsDAOHibernate(Session session)
	{
		this.session = session;
	}

	@Override
	public SystemSettings getSystemSettingsForId(Long systemSettingsId)
	{
		return (SystemSettings) session.get(SystemSettings.class, systemSettingsId);
	}

	@Override
	public List<SystemSettings> getAllSystemSettings()
	{
		return session.createCriteria(SystemSettings.class).addOrder(Order.asc("name")).list();
	}

	@Override
	public void saveOrUpdateSystemSettings(SystemSettings systemSettings)
	{
		session.saveOrUpdate(systemSettings);
	}


}

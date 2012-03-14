package ch.astina.hesperid.dao;



import ch.astina.hesperid.model.internal.SystemSettings;

import java.util.List;

/**
 * @author fharter
 */
public interface SystemSettingsDAO
{
	public SystemSettings getSystemSettingsForId(Long systemSettingsId);

	public List<SystemSettings> getAllSystemSettings();

	public void saveOrUpdateSystemSettings(SystemSettings systemSettings);

}

package ch.astina.hesperid.dao;

import ch.astina.hesperid.model.internal.JiraSettings;

public interface JiraSettingsDAO
{
    public JiraSettings findOne();

    public void save(JiraSettings jiraSettings);

    public void delete(JiraSettings jiraSettings);
}

package ch.astina.hesperid.dao.hibernate;

import ch.astina.hesperid.dao.JiraSettingsDAO;
import ch.astina.hesperid.model.internal.JiraSettings;
import org.hibernate.Session;

import java.util.List;

public class JiraSettingsDAOHibernate implements JiraSettingsDAO
{
    private Session session;

    public JiraSettingsDAOHibernate(Session session)
    {
        this.session = session;
    }

    @Override
    public JiraSettings findOne()
    {
        List<JiraSettings> jiraSettings = session.createCriteria(JiraSettings.class).list();

        return jiraSettings.size() == 0 ? new JiraSettings() : jiraSettings.get(0);
    }

    @Override
    public void save(JiraSettings jiraSettings)
    {
        session.saveOrUpdate(jiraSettings);
    }

    @Override
    public void delete(JiraSettings jiraSettings)
    {
        session.delete(jiraSettings);
    }
}

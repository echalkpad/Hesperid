package ch.astina.hesperid.web.services.jobs;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

/**
 * @author fharter
 */
public interface DbCleanupJob
{
	@CommitAfter
	public abstract void cleanup();
}

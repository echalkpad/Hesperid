package ch.astina.hesperid.worker;

import ch.astina.hesperid.groovy.ParameterGatherer;
import ch.astina.hesperid.model.base.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * @author fharter
 */
public final class ParameterGathererRunner
{
	private static final Logger logger = LoggerFactory.getLogger(ParameterGathererRunner.class);

	private Observer observer;

	private String result = null;
	private Exception exception;
	private int maxRetries = 3;

	public ParameterGathererRunner(Observer observer)
	{
		this.observer = observer;
	}


	/**
	 * Tries for maxRetries times the execution for the parameter gatherer.
	 */
	public void execute()
	{
		int retryCount = 0;
		while (!hasResult() && retryCount < maxRetries) {

			try {
				ParameterGatherer parameterGatherer = observer.getObserverStrategy().getGroovyScriptInstance();
				result = parameterGatherer.getResult(observer.getParameterMap());
			} catch ( Exception e ) {
				exception = e;

				try {
					Thread.sleep(observer.getCheckInterval() * 10); // e.g. 60 * 10 = 600 milliseconds
				} catch ( InterruptedException e1 ) {
					logger.error("Sleeping the thread for parameter gathering breaks.", e1);
					throw new RuntimeException("Sleeping the thread for parameter gathering breaks.", e1);
				}
				
				retryCount++;
			}
		}
	}

	private boolean hasResult()
	{
		return result != null && !result.isEmpty();
	}

	public boolean hasUnknownError()
	{
		return exception != null && exception.getMessage() == null;
	}
	
	public String getErrorMessage()
	{
		if (exception != null) {
			if(exception instanceof MalformedURLException) {
				return "The observer URL is not valid.";
			} else {
				if(exception.getMessage() != null) {
					return exception.getMessage();
				} else {
					return exception.getClass().getName();
				}
			}
		} else {
			return null;
		}
	}

	public String getResult()
	{
		return result;
	}

	public Exception getException()
	{
		return exception;
	}
}

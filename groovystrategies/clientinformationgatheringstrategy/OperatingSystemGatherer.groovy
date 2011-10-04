import ch.astina.hesperid.groovy.ParameterGatherer;

public class OperatingSystemGatherer implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{
		return System.getProperty("os.name") + " " + System.getProperty("os.version");
	}
}

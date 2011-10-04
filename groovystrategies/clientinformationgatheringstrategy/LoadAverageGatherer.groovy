import ch.astina.hesperid.groovy.ParameterGatherer;
import java.lang.management.ManagementFactory;

public class LoadAverageGatherer implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{
		return String.valueOf(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
	}
}

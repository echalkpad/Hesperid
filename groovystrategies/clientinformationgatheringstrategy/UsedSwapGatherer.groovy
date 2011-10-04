import ch.astina.hesperid.groovy.ParameterGatherer;
import java.lang.management.ManagementFactory;

public class UsedSwapGatherer implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{
		return String.valueOf((((ManagementFactory.getOperatingSystemMXBean()
				.getTotalSwapSpaceSize() - ManagementFactory.getOperatingSystemMXBean()
				.getFreeSwapSpaceSize()) / 1024) / 1024));
	}
}

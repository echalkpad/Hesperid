import ch.astina.hesperid.groovy.ParameterGatherer;
import java.lang.management.ManagementFactory;

public class UsedMemoryGatherer implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{
		return String.valueOf((((ManagementFactory.getOperatingSystemMXBean()
				.getTotalPhysicalMemorySize() - ManagementFactory.getOperatingSystemMXBean()
				.getFreePhysicalMemorySize()) / 1024) / 1024));
	}
}

import ch.astina.hesperid.groovy.ParameterGatherer;
import java.lang.management.ManagementFactory;

public class TotalSwapGatherer implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{
		return String.valueOf(((ManagementFactory.getOperatingSystemMXBean()
				.getTotalSwapSpaceSize() / 1024) / 1024));
	}
}

import java.util.Map;
import java.io.File;

import ch.astina.hesperid.groovy.ParameterGatherer;

public class TotalDiskSpaceGatherer implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{
		String path = parameters.get("path");
		File file = new File(path);
		return String.valueOf(file.getTotalSpace());
	}
}

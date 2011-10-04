import ch.astina.hesperid.groovy.ParameterGatherer;

import java.util.Map;
import java.io.File;

public class FileExistsGatherer implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{
		String path = parameters.get("path");
		File file = new File(path);
		return file.exists() ? "true" : "false";
	}
}

import ch.astina.hesperid.groovy.ParameterGatherer;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class JavaVersionGatherer implements ParameterGatherer
{	
	public String getResult(Map<String, String> parameters)
	{
		return System.getProperty("java.version");
	}
}

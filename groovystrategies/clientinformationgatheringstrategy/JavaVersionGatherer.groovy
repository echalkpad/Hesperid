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
		List<String> command = new ArrayList<String>();
		command.add("java");
		command.add("-version");
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Map<String, String> environ = builder.environment();
		
		builder.redirectErrorStream();
		
		final Process process = builder.start();
		
		BufferedReader inp = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		return inp.readLine();
	}
}

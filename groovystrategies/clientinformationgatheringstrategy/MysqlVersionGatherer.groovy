import ch.astina.hesperid.groovy.ParameterGatherer;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MysqlVersionGatherer implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{
		List<String> command = new ArrayList<String>();
		command.add("mysql");
		command.add("-V");
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Map<String, String> environ = builder.environment();
		
		builder.redirectErrorStream();
		
		final Process process = builder.start();
		
		OutputStream out = new ByteArrayOutputStream();
		
		int l;
		byte[] buffer = new byte[1024];
		while ((l = process.getInputStream().read(buffer)) != -1) {
			out.write(buffer, 0, l);
			out.flush();
		}
		out.close();
		
		return out.toString();
	}
}

import ch.astina.hesperid.groovy.ParameterGatherer;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class PhpVersionGatherer implements ParameterGatherer
{
    public String getResult(Map<String, String> parameters)
    {
        String result = "";
        final Process process = null
        
        try {
            List<String> command = new ArrayList<String>();
            command.add("php");
            command.add("-version");

            ProcessBuilder builder = new ProcessBuilder(command);
            Map<String, String> environ = builder.environment();

            builder.redirectErrorStream();

            process = builder.start();

            BufferedReader inp = new BufferedReader(new InputStreamReader(process.getInputStream()));

            result = inp.readLine();
            
        } catch (Exception e) {
            process.destroy();
            throw new RuntimeException(e);
        } finally {
            process.destroy();    
        }
        
        return result;
    }
}

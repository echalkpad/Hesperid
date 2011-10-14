import ch.astina.hesperid.groovy.ParameterGatherer;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SoftRAIDObserver implements ParameterGatherer
{
        public String getResult(Map<String, String> parameters)
        {
                try {
                        Runtime run = Runtime.getRuntime();
                        Process p = run.exec("cat /proc/mdstat");
                        p.waitFor();
                        BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String l;
                        while((l = buf.readLine()) != null)
                        {
                                if(l.matches(".*\\[[^\\[]*_[^\\]]*\\].*"))
                                {
                                        return "false";
                                }
                        }
                        return "true";
                }
                catch (Exception e)
                {
                        return "false";
                }
        }
}


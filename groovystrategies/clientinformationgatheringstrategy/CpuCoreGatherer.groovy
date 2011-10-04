import ch.astina.hesperid.groovy.ParameterGatherer;

public class CpuCoreGatherer implements ParameterGatherer
{
    public String getResult(Map<String, String> parameters)
    {
        return String.valueOf(Runtime.getRuntime().availableProcessors());
    }
}

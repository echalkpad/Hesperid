import java.io.File;
import ch.astina.hesperid.groovy.ParameterGatherer;

public class FreeDiskSpaceGatherer implements ParameterGatherer
{
    public String getResult(Map<String, String> parameters)
    {
        String path = parameters.get("path");
        String unit = parameters.get("unit");

        File file = new File(path);
        long freeSpace = file.getFreeSpace();

        if (unit != null) {
            if(unit.equals("GB")) {
                freeSpace = freeSpace / 1024 / 1024 / 1024;
            } else if(unit.equals("MB")) {
                freeSpace = freeSpace / 1024 / 1024;
            }
        }

        return String.valueOf(freeSpace);
    }
}
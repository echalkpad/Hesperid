import ch.astina.hesperid.groovy.ParameterGatherer;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

public class ExternalIpGatherer implements ParameterGatherer
{
    public String getResult(Map<String, String> parameters)
    {
	try {
	    String ip = "";
	    HttpClient client = new HttpClient();
	    HttpMethod method = new GetMethod("http://www.ksfx.de/ip.php");

	    client.executeMethod(method);

	    ip = method.getResponseBodyAsString();

	    method.releaseConnection();

            return ip;
        } catch (Exception e) {
            return "failed";
        }
	
	return "failed";
    }
}

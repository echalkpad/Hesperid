import java.util.Map;

import ch.astina.hesperid.groovy.ParameterGatherer;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

class HttpStatusCodeObserver implements ParameterGatherer
{
    public String getResult(Map<String, String> parameters)
    {
		URL url = new URL (parameters.get("url"));
		URLConnection connection = url.openConnection();
	
		HttpURLConnection httpConnection = (HttpURLConnection) connection;
	
		int code = httpConnection.getResponseCode();
	
		return String.valueOf(code);
    }
}

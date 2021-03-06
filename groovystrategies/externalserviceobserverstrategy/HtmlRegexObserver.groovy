import ch.astina.hesperid.groovy.ParameterGatherer;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

class HtmlRegexObserver implements ParameterGatherer
{
	public String getResult(Map<String, String> parameters)
	{			
		URL url = new URL(parameters.get("url"));
		String patternStr = parameters.get("pattern");
		int group = 1;
		if (parameters.containsKey("group")) {
			group = Integer.parseInt(parameters.get("group"));
		}
		
		URLConnection connection = url.openConnection();
		
		InputStreamReader isr = new InputStreamReader(connection.getInputStream());
		BufferedReader inp = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		
		String inputLine;
		while ((inputLine = inp.readLine()) != null) {
			sb.append(inputLine);
		}
		inp.close();
		
		Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL | Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(sb.toString());
		
		if (matcher.matches()) {
			return matcher.groupCount() == 0 ? matcher.group() : matcher.group(group);
		}
		
		return null;
	}
}

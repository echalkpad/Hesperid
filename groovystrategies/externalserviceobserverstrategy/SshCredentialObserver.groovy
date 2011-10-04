import java.util.Map;

import ch.astina.hesperid.groovy.ParameterGatherer;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.util.Properties;

class SshCredentialObserver implements ParameterGatherer
{
    public String getResult(Map<String, String> parameters)
    {
   		JSch jsch = new JSch();

   		String host = parameters.get("host");
   		String user = parameters.get("user");
   		String password = parameters.get("password");

		Properties config = new Properties(); 
		config.put("StrictHostKeyChecking", "no");

   		Session session = jsch.getSession(user, host, 22);
		session.setConfig(config);
   		session.setPassword(password);
   		session.connect();

		return "true";
    }
}

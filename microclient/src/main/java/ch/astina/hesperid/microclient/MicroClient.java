package ch.astina.hesperid.microclient;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 124 $, $Date: 2011-09-23 13:05:15 +0200 (Fr, 23 Sep 2011) $
 */
public class MicroClient extends Thread
{
    private final XMLConfiguration xmlConfiguration;
    private Date lastUpdated;
    private File cacheDir;

	private static Process process = null;

    public MicroClient(XMLConfiguration xmlConfiguration)
    {
        this.xmlConfiguration = xmlConfiguration;
        String clientBundleStopFile = this.xmlConfiguration.getString("clientInstallDir") + "/pid/stopped";

        File file = new File(clientBundleStopFile);

        if (file.exists()) {
            file.delete();
        }

        cacheDir = new File(this.xmlConfiguration.getString("clientInstallDir") + "/microclient");
    }

    @Override
    public void run()
    {
		startShutdownHook();
	    startAgentBundleObserver();
    }

	private void startShutdownHook()
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("stopping");
				try {
					File file = new File(xmlConfiguration.getString("clientInstallDir") + "/pid/stopped");
					OutputStream out = new FileOutputStream(file);
					out.write("stopped".getBytes());
					out.flush();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void startAgentBundleObserver()
	{
		boolean running = true;


		while (running) {
			try {
				String version = getVersionOnServer();
				AgentInfo agentInfo = new AgentInfo(version);

				// Download the agent jars if needed
				if(agentInfo.isValid() && isAgentUpdateNeeded(agentInfo)) {

					System.out.println("New agent library is available on the server.");
					String bundleLocation = xmlConfiguration.getString("hostBaseURL")
							+ "/microclient/latestagentversiondownload/"
							+ agentInfo.getAgentFile();

					retrieveClient(bundleLocation);
					quitProcess();

					lastUpdated = new Date();
				}

				startProcess();

			} catch (Exception e) {
				e.printStackTrace();
			}

			sleep();
		}

		quitProcess();
		stopChilds();
	}

	private boolean isAgentUpdateNeeded(AgentInfo agentInfo)
	{
		return lastUpdated == null || agentInfo.getNewestAgentDate().after(lastUpdated) || cacheDir.list().length == 0;
	}

    private String getVersionOnServer()
    {
        String version = "";

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(xmlConfiguration.getString("hostBaseURL") + "/microclient/latestagentversion");
        
        try {
            client.executeMethod(method);
            version = method.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                method.releaseConnection();
            } catch (Exception exx) {
                exx.printStackTrace();
            }
        }

        return version;
    }

    private void retrieveClient(String url)
    {
	    System.out.println("Retrieving new agent library from " + url);

        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(url);
        
        try {
            method.setFollowRedirects(true);

            client.executeMethod(method);

            InputStream is = method.getResponseBodyAsStream();
            File file = new File(cacheDir,"agent.jar");

            if (file.exists()) {
                file.delete();
                file = new File(cacheDir,"agent.jar");
            }

            OutputStream out = new FileOutputStream(file);
            byte buf[]=new byte[1024];
            int len;
            
            while((len=is.read(buf))>0) {
                System.out.println("xyz");
                out.write(buf,0,len);
            }
            
            out.close();
            is.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
    }


	/*************** Process handling ***************/

	private void sleep()
	{
		try {
			long sleepInterval = xmlConfiguration.getLong("updateCheckIntervalMillis");
			System.out.println("Sleeping for: " + sleepInterval + " (in Milliseconds)");
			Thread.sleep(sleepInterval);
		} catch (Exception ex) {
			stopChilds();
			System.err.println("Could not create framework: " + ex);
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	private void quitProcess()
	{
		if (process != null) {
			System.out.println("Stopping current agent process: " + process.toString());
			//stopChilds();
			//process.waitFor();
			process.destroy();
		}
		process = null;
	}

	private void startProcess() throws IOException, InterruptedException
	{
		if (process == null) {
			System.out.println("Starting agent process...");
			process = startJar(cacheDir.getPath() + "/agent.jar", true);
			System.out.println("New agent process started " + process.getOutputStream().toString());
		}
	}

    private Process startJar(String jarFile, boolean mergeStreams) throws InterruptedException, IOException
    {
        List<String> command = new ArrayList<String>();
        command.add("java");
        command.add("-jar");
        command.add(jarFile);
        command.add(xmlConfiguration.getString("clientInstallDir") + "/conf/client-config.xml");

        ProcessBuilder builder = new ProcessBuilder(command);
        Map<String, String> environ = builder.environment();

        if (mergeStreams) {
            builder.redirectErrorStream();
        }

        final Process process = builder.start();

        ClientOutputLogger clientOutputLogger = new ClientOutputLogger(process.getInputStream());
        clientOutputLogger.start();

        return process;
    }

    private void stopChilds()
    {
        System.out.println("stopping");
        try {
            File file = new File(xmlConfiguration.getString("clientInstallDir") + "/pid/stopped");
            OutputStream out = new FileOutputStream(file);
            out.write("stopped".getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



	/***************** Server Information *****************/
	
	private class AgentInfo {
		
		private String agentInformation;
		private Date newestAgentDate;
		private String agentFile;
		
		AgentInfo (String agentInformation) {
			this.agentInformation = agentInformation;
			extractAgentDate(agentInformation);
		}
		
		private void extractAgentDate(String agentInformation)
		{
			try {
				String agentDateString = agentInformation.substring(0, agentInformation.indexOf(";"));
				agentFile = agentInformation.replaceFirst(agentDateString + ";", "");

				System.out.println("Agent Date from server is: " + agentDateString);

				if (agentInformation.length() > 4) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					newestAgentDate = df.parse(agentDateString);
				}
			} catch (Exception ex) {
				System.out.println("Extracting agent information from server failed.");
				ex.printStackTrace();
			}
		}

		public boolean isValid()
		{
			//return agentInformation.length() > 4;
			return newestAgentDate != null && agentFile != null;
		}

		public Date getNewestAgentDate()
		{
			return newestAgentDate;
		}

		public String getAgentFile()
		{
			return agentFile;
		}
	}
}

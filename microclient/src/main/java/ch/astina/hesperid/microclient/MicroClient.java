package ch.astina.hesperid.microclient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 124 $, $Date: 2011-09-23 13:05:15 +0200 (Fr, 23 Sep 2011) $
 */
public class MicroClient extends Thread
{
    private final XMLConfiguration xmlConfiguration;
    private Date lastUpdated;
    private File cacheDir;

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

        boolean running = true;
        Process process = null;

        try
        {      
            while (running) {
                try {
                    String version = getVersionOnServer();
                    String agentDate = version.substring(0, version.indexOf(";"));
                    String agentFile = version.replaceFirst(agentDate + ";", "");

                    System.out.println("Agent Date " + agentDate);

                    if (version.length() > 4) {

                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Date compareDate = df.parse(agentDate);

                        if (lastUpdated == null || compareDate.after(lastUpdated) || cacheDir.list().length == 0) {

                            String bundleLocation = xmlConfiguration.getString("hostBaseURL") + "/microclient/latestagentversiondownload/" + agentFile;
                            retrieveClient(bundleLocation);
                            if (process != null) {
                                //stopChilds();
                                //process.waitFor();
                                process.destroy();
                            }
                            process = null;

                            System.out.println("Installing Bundle " + bundleLocation);

                            lastUpdated = new Date();
                        }
                    }

                    if (process == null) {
                        process = startJar(cacheDir.getPath() + "/agent.jar", true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Thread.sleep(xmlConfiguration.getLong("updateCheckIntervalMillis"));
            }
        }
        catch (Exception ex)
        {
            stopChilds();
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
            System.exit(-1);
        }

        stopChilds();
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
        String version = "";

        System.out.println("Retrieving client " + url);

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


    Process startJar(String jarFile, boolean mergeStreams) throws InterruptedException, IOException
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
}

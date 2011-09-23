package ch.astina.hesperid.microclient;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 124 $, $Date: 2011-09-23 13:05:15 +0200 (Fr, 23 Sep 2011) $
 */
public class AgentDaemon
{
    public static void main(String[] args)
    {
        if(args.length == 0) {
            System.out.println("Usage: java -server Infraasset Agent infraassetagent-conf.xml");
            return;
        }

        try {
            XMLConfiguration clientConfig = new XMLConfiguration(args[0]);
            MicroClient osgiMicroClient = new MicroClient(clientConfig);
            osgiMicroClient.start();
        } catch (ConfigurationException e) {
            System.out.println("Configuration error: cannot read config file");
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
// Copyright 2011 Astina AG, Zurich
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
////////////////////////////////////////////////////////////////////////////////////////////////////
package ch.astina.hesperid.agentbundle;

import ch.astina.hesperid.agentbundle.scheduler.MicroclientStopListener;
import ch.astina.hesperid.agentbundle.scheduler.ParameterGathererExecutionJob;
import ch.astina.hesperid.agentbundle.webservice.AgentFeedback;
import ch.astina.hesperid.agentbundle.webservice.AgentFeedbackService;
import ch.astina.hesperid.global.GlobalConstants;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.Observer;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 133 $, $Date: 2011-09-23 13:43:55 +0200 (Fr, 23 Sep 2011) $
 */
public class Agent
{
    public static final long TICK_TIME = 60000; // one minute

    private static Logger logger = Logger.getLogger(Agent.class);

	private XMLConfiguration xmlConfiguration;
	private Asset asset;
	private AgentFeedback agentFeedbackPort;
	private SchedulerFactory schedulerFactory;
	private Scheduler scheduler;

    public static void main(String[] args)
    {
        if(args.length == 0) {
            System.out.println("Usage: java -server agentbundle.jar configuration.xml");
            return;
        }

        try {
	        XMLConfiguration xmlConfiguration = new XMLConfiguration(args[0]);
            Agent agent = new Agent(xmlConfiguration);
	        agent.run();
        } catch (ConfigurationException e) {
	        logger.error("Error while creating agent", e);
        } catch (Exception e) {
            logger.error("Error while creating agent", e);
        }

        System.setProperty("http.maxConnections", "1");
        System.setProperty("http.keepAlive", "false");
    }

    public Agent(XMLConfiguration xmlConfiguration) throws Exception
    {
        logger.info("Creating agent...");
	    this.xmlConfiguration = xmlConfiguration;

		initializeAgentFeedbackPort();
        initializeScheduler();
        registerStartMicroclientStopListener();

        asset = new Asset();
        asset.setAssetIdentifier(xmlConfiguration.getString("assetIdentifier"));
    }
	
	private void initializeAgentFeedbackPort() throws MalformedURLException
	{
		logger.info("Initializing agent feedback port form service...");
		String wsdlLocation = xmlConfiguration.getString("hostBaseURL") + "/soap?wsdl";

		AgentFeedbackService service = new AgentFeedbackService(new URL(wsdlLocation), new QName(GlobalConstants.WEBSERVICE_NAMESPACE, "AgentFeedbackService"));
		agentFeedbackPort = service.getAgentFeedbackPort();

		((BindingProvider) agentFeedbackPort).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 1000);
		((BindingProvider) agentFeedbackPort).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 1000);

		((BindingProvider) agentFeedbackPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xmlConfiguration.getString("hostBaseURL") + "/soap");

		//((BindingProvider) agentFeedbackPort).getRequestContext().put(JAXWSProperties.CONNECT_TIMEOUT, 1000);
		//((BindingProvider) agentFeedbackPort).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 1000);		
	}

	private void initializeScheduler() throws SchedulerException
	{
		logger.info("Initializing scheduler...");
		schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
		scheduler = schedulerFactory.getScheduler();
		scheduler.start();
	}

	public void run() throws InterruptedException
	{
		logger.info("Agent started. Updating client information every " + TICK_TIME / 1000 + " seconds.");
		Date lastUpdatedInformationOnClient = null;
		boolean running = true;

		while(running) {
			logger.info("Checking for new Information on the server");

			try {
				Date lastObserverUpdate = agentFeedbackPort.lastUpdatedObserver(asset);

				// Reschedule all observer jobs when there are observer modifications on the server.
				if ( serverHasObserverModifications(lastObserverUpdate, lastUpdatedInformationOnClient) ) {
					logger.info("Change detected now updating scheduler");

					Observer[] observers = agentFeedbackPort.observers(asset);

					if (observers != null) {
						for (JobKey jobKey : scheduler.getJobKeys( GroupMatcher.jobGroupEquals("observerGatheringGroup")) ) {
							JobDetail job = scheduler.getJobDetail(jobKey);
							scheduler.deleteJob(job.getKey());
						}

						scheduleObserverJobs(observers);
						lastUpdatedInformationOnClient = new Date();
					}
				}
			} catch (Exception e) {
				logger.info("Error in checking asset information loop", e);
			}

			Thread.sleep(TICK_TIME);
		}
	}

	private boolean serverHasObserverModifications(Date lastUpdatedObserver, Date lastUpdatedInformationOnClient)
	{
		return lastUpdatedObserver != null
				&& (lastUpdatedInformationOnClient == null || lastUpdatedInformationOnClient.before(lastUpdatedObserver));
	}

	/**
	 * Schedules observer jobs for each defined observer interval.
	 */
	private void scheduleObserverJobs(Observer[] observers) throws SchedulerException
	{
		for (Observer observer : observers) {

			if (observer.getObserverStrategy() != null) {

				JobDetail jobDetail = JobBuilder.newJob(ParameterGathererExecutionJob.class)
						.withIdentity(observer.getObserverStrategy().getName(), "observerGatheringGroup")
						.build();
				jobDetail.getJobDataMap().put("agentFeedback", agentFeedbackPort);
				jobDetail.getJobDataMap().put("observer", observer);

				int interval = observer.getCheckInterval() != null ? observer.getCheckInterval().intValue() : 60;
				Trigger trigger = buildTrigger(observer.getObserverStrategy().getName() + "-trigger", interval);

				scheduler.scheduleJob(jobDetail, trigger);

				logger.info("Observer: " + observer);
			}
		}
	}

    private void registerStartMicroclientStopListener()
    {
        logger.info("registering stop listener");

	    String clientBundleStopFile = xmlConfiguration.getString("clientInstallDir") + "/pid/stopped";
        
        try {
            Scheduler microclientScheduler = schedulerFactory.getScheduler();

	        JobDetail jobDetail = JobBuilder.newJob(MicroclientStopListener.class)
			        .withIdentity("microclientStopListener", Scheduler.DEFAULT_GROUP)
			        .build();
            jobDetail.getJobDataMap().put("stopFile", clientBundleStopFile);

			Trigger trigger = buildTrigger("microclientStopListener-trigger", 1);
            microclientScheduler.scheduleJob(jobDetail, trigger);

	        microclientScheduler.start();
        } catch (Exception e) {
            logger.info("Error in registering stop listener", e);
        }
    }

	private Trigger buildTrigger(String triggerName, int interval)
	{
		return TriggerBuilder.newTrigger()
				.withIdentity(triggerName)
				.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(interval))
				.startNow()
				.build();
	}

 }

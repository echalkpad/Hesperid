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

import java.net.URL;
import java.util.Date;
import javax.xml.namespace.QName;
import org.apache.commons.configuration.XMLConfiguration;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import ch.astina.hesperid.agentbundle.scheduler.MicroclientStopListener;
import ch.astina.hesperid.agentbundle.scheduler.ParameterGathererExecutionJob;
import ch.astina.hesperid.agentbundle.webservice.AgentFeedback;
import ch.astina.hesperid.agentbundle.webservice.AgentFeedbackService;
import ch.astina.hesperid.global.GlobalConstants;
import ch.astina.hesperid.model.base.Asset;
import ch.astina.hesperid.model.base.Observer;

import javax.xml.ws.BindingProvider;
import org.apache.log4j.Logger;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 133 $, $Date: 2011-09-23 13:43:55 +0200 (Fr, 23 Sep 2011) $
 */
public class Agent
{
    public static final long TICK_TIME = 60000;

    private static Logger logger = Logger.getLogger(Agent.class);

    public static void main(String[] args)
    {
        if(args.length == 0) {
            System.out.println("Usage: java -server agentbundle.jar configuration.xml");
            return;
        }

        XMLConfiguration xmlConfiguration = null;

        try {
            xmlConfiguration = new XMLConfiguration(args[0]);
        } catch (Exception e) {
            logger.error("Error while reading configuration", e);
        }

        try {
            new Agent(xmlConfiguration);
        } catch (Exception e) {
            logger.error("Error while creating agent", e);
        }

        System.setProperty("http.maxConnections", "1");
        System.setProperty("http.keepAlive", "false");
    }

    public Agent(XMLConfiguration xmlConfiguration) throws Exception
    {
        logger.info("Agent started");

        Date lastUpdatedInformationOnClient = null;

        String wsdlLocation = xmlConfiguration.getString("hostBaseURL") + "/soap?wsdl";

        AgentFeedbackService service = new AgentFeedbackService(new URL(wsdlLocation), new QName(GlobalConstants.WEBSERVICE_NAMESPACE, "AgentFeedbackService"));
        AgentFeedback port = service.getAgentFeedbackPort();

        ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.request.timeout", 1000);
        ((BindingProvider) port).getRequestContext().put("com.sun.xml.internal.ws.connect.timeout", 1000);
        
        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, xmlConfiguration.getString("hostBaseURL") + "/soap");
        
        //((BindingProvider) port).getRequestContext().put(JAXWSProperties.CONNECT_TIMEOUT, 1000);
        //((BindingProvider) port).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, 1000);

        SchedulerFactory schedulerFactory = new org.quartz.impl.StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        scheduler.start();

        String clientBundleStopFile = xmlConfiguration.getString("clientInstallDir") + "/pid/stopped";
        registerStartMicroclientStopListener(clientBundleStopFile, schedulerFactory);

        Asset asset = new Asset();
        asset.setAssetIdentifier(xmlConfiguration.getString("assetIdentifier"));
        
        boolean running = true;

        while(running) {

            logger.info("Checking for new Information on the server");

            try {
                Date lastUpdatedObserver = port.lastUpdatedObserver(asset);

                if (lastUpdatedObserver != null && (lastUpdatedInformationOnClient == null || lastUpdatedInformationOnClient.before(lastUpdatedObserver))) {

                    logger.info("Change detected now updating scheduler");

                    Observer[] observers = port.observers(asset);

                    if (observers != null) {
                        for (String s : scheduler.getJobNames("observerGatheringGroup")) {
                            scheduler.deleteJob(s, "observerGatheringGroup");
                        }

                        for (Observer observer : observers) {

                            if (observer.getObserverStrategy() != null) {

                                JobDetail jobDetail = new JobDetail(observer.getObserverStrategy()
                                		.getName(), "observerGatheringGroup", 
                                		ParameterGathererExecutionJob.class);
                                jobDetail.getJobDataMap().put("agentFeedback", port);
                                jobDetail.getJobDataMap().put("observer", observer);

                                Trigger trigger = TriggerUtils.makeSecondlyTrigger(
                                		observer.getCheckInterval() != null ? observer.getCheckInterval().intValue() : 60);
                                trigger.setName(observer.getObserverStrategy().getName() + "-trigger");

                                scheduler.scheduleJob(jobDetail, trigger);

                                logger.info("Observer: " + observer);
                            }
                        }

                        lastUpdatedInformationOnClient = new Date();
                    }

                }

            } catch (Exception e) {
                logger.info("Error in checking asset information loop",e);
            }

            Thread.sleep(TICK_TIME);
        }
    }

    private void registerStartMicroclientStopListener(String stopFile, SchedulerFactory schedulerFactory)
    {
        logger.info("registering stop listener");
        
        try {
            Scheduler scheduler = schedulerFactory.getScheduler();

            JobDetail jobDetail = new JobDetail("microclientStopListener", Scheduler.DEFAULT_GROUP, MicroclientStopListener.class);
            jobDetail.getJobDataMap().put("stopFile", stopFile);

            Trigger trigger = TriggerUtils.makeSecondlyTrigger();
            trigger.setName("microclientStopListener-trigger");

            scheduler.scheduleJob(jobDetail, trigger);

            scheduler.start();
        } catch (Exception e) {
            logger.info("Error in registering stop listener", e);
        }
    }
 }

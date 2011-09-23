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
package ch.astina.hesperid.agentbundle.webservice;

import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import ch.astina.project.molo.global.GlobalConstants;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 133 $, $Date: 2011-09-23 13:43:55 +0200 (Fr, 23 Sep 2011) $
 */
@WebServiceClient(name = "AgentFeedbackService", targetNamespace = GlobalConstants.WEBSERVICE_NAMESPACE)
public class AgentFeedbackService extends Service 
{
    private final static URL AGENTFEEDBACKSERVICE_WSDL_LOCATION = null;
    private final static Logger logger = Logger.getLogger(AgentFeedbackService.class.getName());

    public AgentFeedbackService(URL wsdlLocation, QName serviceName) 
    {
        super(wsdlLocation, serviceName);
    }

    @WebEndpoint(name = "AgentFeedbackPort")
    public AgentFeedback getAgentFeedbackPort() 
    {
        return super.getPort(new QName(GlobalConstants.WEBSERVICE_NAMESPACE, "AgentFeedbackPort"),
                AgentFeedback.class);
    }

    @WebEndpoint(name = "AgentFeedbackPort")
    public AgentFeedback getAgentFeedbackPort(WebServiceFeature... features) 
    {
        return super.getPort(new QName(GlobalConstants.WEBSERVICE_NAMESPACE, "AgentFeedbackPort"),
                AgentFeedback.class, features);
    }
}

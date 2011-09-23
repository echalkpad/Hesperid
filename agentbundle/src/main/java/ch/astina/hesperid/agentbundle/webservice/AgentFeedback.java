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

import java.util.Date;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import ch.astina.project.molo.global.GlobalConstants;
import ch.astina.project.molo.model.base.Asset;
import ch.astina.project.molo.model.base.Observer;
import ch.astina.project.molo.model.base.ObserverParameter;


/**
 * @author $Author: kstarosta $
 * @version $Revision: 133 $, $Date: 2011-09-23 13:43:55 +0200 (Fr, 23 Sep 2011) $
 */
@WebService(name = "AgentFeedback", targetNamespace = GlobalConstants.WEBSERVICE_NAMESPACE)
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface AgentFeedback
{
    @WebMethod
    @WebResult(name = "deliverObserverObserverParameterResponse", 
    		targetNamespace = GlobalConstants.WEBSERVICE_NAMESPACE, 
    		partName = "deliverObserverParameterResponse")
    public boolean deliverObserverParameter(
        @WebParam(name = "deliverObserverParameter",
        targetNamespace = GlobalConstants.WEBSERVICE_NAMESPACE,
        partName = "deliverObserverParameter")
        ObserverParameter parameter);

    @WebMethod
    @WebResult(name = "lastUpdatedObserverResponse", 
    		targetNamespace = GlobalConstants.WEBSERVICE_NAMESPACE, 
    		partName = "lastUpdatedObserverResponse")
    public Date lastUpdatedObserver(
        @WebParam(name = "lastUpdatedObserver",
        targetNamespace = GlobalConstants.WEBSERVICE_NAMESPACE,
        partName = "lastUpdatedObserver")
        Asset asset);

    @WebMethod
    @WebResult(name = "observersResponse", 
    		targetNamespace = GlobalConstants.WEBSERVICE_NAMESPACE, 
    		partName = "observersResponse")
    public Observer[] observers(
        @WebParam(name = "observers",
        targetNamespace = GlobalConstants.WEBSERVICE_NAMESPACE,
        partName = "observers")
        Asset asset);
}

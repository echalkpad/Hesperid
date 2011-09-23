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
package ch.astina.hesperid.web.pages.admin.agentbundle;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;

import ch.astina.hesperid.dao.AgentBundleDAO;
import ch.astina.hesperid.model.base.AgentBundle;

/**
 * @author $Author: kstarosta $
 * @version $Revision: 118 $, $Date: 2011-09-21 16:33:28 +0200 (Mi, 21 Sep 2011) $
 */
@Secured({"ROLE_ADMIN"})
public class AgentBundleIndex
{
    @Inject
    private AgentBundleDAO agentBundleDAO;

    @SuppressWarnings("unused")
	@Property
    private AgentBundle agentBundle;

    public List<AgentBundle> getAllAgentBundles()
    {
        return agentBundleDAO.getAllAgentBundles();
    }

    @CommitAfter
    public void onActionFromDelete(Long agentBundleId)
    {
        AgentBundle a = agentBundleDAO.getAgentBundleForId(agentBundleId);
        agentBundleDAO.deleteAgentBundle(a);
    }
}
